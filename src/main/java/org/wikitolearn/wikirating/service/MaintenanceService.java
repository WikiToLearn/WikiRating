package org.wikitolearn.wikirating.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.exception.*;
import org.wikitolearn.wikirating.model.graph.CourseLevelThree;
import org.wikitolearn.wikirating.model.graph.Process;
import org.wikitolearn.wikirating.util.enums.ProcessStatus;
import org.wikitolearn.wikirating.util.enums.ProcessType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author aletundo
 * @author valsdav
 */
@Service
public class MaintenanceService {
	private static final Logger LOG = LoggerFactory.getLogger(MaintenanceService.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private PageService pageService;
	@Autowired
	private RevisionService revisionService;
	@Autowired
	private MetadataService metadataService;
	@Autowired
	private ProcessService processService;
	@Autowired
	private VoteService voteService;
	@Autowired
    private ComputeService computeService;
	@Value("#{'${mediawiki.langs}'.split(',')}")
	private List<String> langs;
	@Value("${mediawiki.protocol}")
	private String protocol;
	@Value("${mediawiki.api.url}")
	private String apiUrl;
	@Value("${mediawiki.namespace}")
	private String namespace;
	
	/**
	 * Initialize the graph for the first time using parallel threads for each domain language
	 * @return true if initialization succeed
	 */
	public boolean initializeGraph() {
		// Initialize Metadata service
        metadataService.initMetadata();
		// Start a new Process
		Process initProcess = processService.addProcess(ProcessType.INIT);
		metadataService.addFirstProcess(initProcess);
		
		CompletableFuture<Boolean> initFuture = CompletableFuture
                // Users and pages
				.allOf(buildUsersAndPagesFuturesList().toArray(new CompletableFuture[langs.size() + 1]))
				// CourseStructure
				.thenCompose(result -> CompletableFuture
						.allOf(buildApplyCourseStructureFuturesList().toArray(new CompletableFuture[langs.size()])))
				// Revisions
				.thenCompose(result -> CompletableFuture
						.allOf(buildRevisionsFuturesList().toArray(new CompletableFuture[langs.size()])))
				// Change Coefficients
				.thenCompose(result -> CompletableFuture
                        .allOf(buildChangeCoefficientFuturesList().toArray(new CompletableFuture[langs.size()])))
				// Users authorship
				.thenCompose(result -> userService.initAuthorship());

		try {
            boolean result = initFuture.get();
            // Save the result of the process
            if (result){
                processService.closeCurrentProcess(ProcessStatus.DONE);
            }else{
                processService.closeCurrentProcess(ProcessStatus.EXCEPTION);
            }
            return result;
		} catch (InterruptedException | ExecutionException e) {
			LOG.error("Something went wrong. {}", e.getMessage());
			return false;
		}
	}
	
	/**
	 * Entry point for the scheduled graph updated
	 * @return true if the update succeed
	 */
	@Scheduled(cron = "${maintenance.update.cron}")
	public boolean updateGraph() throws UpdateGraphException{
		Process currentFetchProcess;
		Date startTimestampCurrentFetch, startTimestampLatestFetch;
		// Get start timestamp of the latest FETCH Process before opening a new process
		startTimestampLatestFetch = 
				(processService.getLastProcessStartDateByType(ProcessType.FETCH) != null) 
				? processService.getLastProcessStartDateByType(ProcessType.FETCH) 
				: processService.getLastProcessStartDateByType(ProcessType.INIT);

		// Create a new FETCH process
		try {
			currentFetchProcess = processService.addProcess(ProcessType.FETCH);
			metadataService.updateLatestProcess();
			startTimestampCurrentFetch = currentFetchProcess.getStartOfProcess();
		} catch (PreviousProcessOngoingException e){
			LOG.error("Cannot start Update process because the previous process is still ONGOING."
					+ "The update will be aborted.");
			return false;
		}
		
		try {
			CompletableFuture<Boolean> updateFuture = CompletableFuture
					.allOf(userService.updateUsers(protocol + langs.get(0) + "." + apiUrl, startTimestampLatestFetch,
							startTimestampCurrentFetch))
					.thenCompose(result -> CompletableFuture
							.allOf(buildUpdatePagesFuturesList(startTimestampLatestFetch, startTimestampCurrentFetch)
									.toArray(new CompletableFuture[langs.size()])))
					.thenCompose(result -> CompletableFuture
							.allOf(buildApplyCourseStructureFuturesList().toArray(new CompletableFuture[langs.size()])))
					.thenCompose(result -> voteService.validateTemporaryVotes(startTimestampCurrentFetch));

			boolean result = updateFuture.get();
			// Save the result of the process, closing the current one
			if (result) {
				processService.closeCurrentProcess(ProcessStatus.DONE);
			} else {
				processService.closeCurrentProcess(ProcessStatus.ERROR);
			}
			return result;
		} catch (TemporaryVoteValidationException | UpdateUsersException | UpdatePagesAndRevisionsException
				| InterruptedException | ExecutionException e) {
			processService.closeCurrentProcess(ProcessStatus.EXCEPTION);
			LOG.error("An error occurred during a scheduled graph update procedure");
			throw new UpdateGraphException();
		}
	}

	public boolean computePageRanking(){
       List<CourseLevelThree> pages = pageService.getCourseLevelThreePages("es");
       for (CourseLevelThree page : pages ){
           computeService.computeRevisionsRating(page.getLangPageId());
       }
       return true;
	}

	/**
	 * Build a list of CompletableFuture.
	 * 
	 * @return a list of CompletableFuture
	 */
	private List<CompletableFuture<Boolean>> buildApplyCourseStructureFuturesList() {
		List<CompletableFuture<Boolean>> parallelUpdateCourseStructureFutures = new ArrayList<>();
		// Add course structure for each domain language
		for (String lang : langs) {
			String url = protocol + lang + "." + apiUrl;
			parallelUpdateCourseStructureFutures.add(pageService.applyCourseStructure(lang, url));
		}
		return parallelUpdateCourseStructureFutures;
	}
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	private List<CompletableFuture<Boolean>> buildUpdatePagesFuturesList(Date start, Date end) {
		List<CompletableFuture<Boolean>> futures = new ArrayList<>();
		// Add update pages for each domain language
		for (String lang : langs) {
			String url = protocol + lang + "." + apiUrl;
			futures.add(pageService.updatePages(lang, url, start, end));
		}
		return futures;
	}
	
	/**
	 * Build a list of CompletableFuture. The elements are the fetches of pages'
	 * revisions from each domain language.
	 * 
	 * @return a list of CompletableFuture
	 */
	private List<CompletableFuture<Boolean>> buildRevisionsFuturesList() {
		List<CompletableFuture<Boolean>> parallelRevisionsFutures = new ArrayList<>();
		// Add revisions fetch for each domain language
		for (String lang : langs) {
			String url = protocol + lang + "." + apiUrl;
			parallelRevisionsFutures.add(revisionService.initRevisions(lang, url));
		}
		return parallelRevisionsFutures;
	}

	private List<CompletableFuture<Boolean>> buildChangeCoefficientFuturesList(){
		List<CompletableFuture<Boolean>> parallelCCFutures = new ArrayList<>();
		// Add revisions fetch for each domain language
		for (String lang : langs) {
			String url = protocol + lang + "." + apiUrl;
			parallelCCFutures.add(revisionService.calculateChangeCoefficientAllRevisions(lang, url));
		}
		return parallelCCFutures;
	}

	/**
	 * Build a list of CompletableFuture. The first one is the fetch of the
	 * users from the first domain in mediawiki.langs list. The rest of the
	 * elements are the fetches of the pages for each language. This
	 * implementation assumes that the users are shared among domains.
	 * 
	 * @return a list of CompletableFuture
	 */
	private List<CompletableFuture<Boolean>> buildUsersAndPagesFuturesList() {
		List<CompletableFuture<Boolean>> usersAndPagesInsertion = new ArrayList<>();
		// Add users fetch as fist operation
		usersAndPagesInsertion.add(userService.initUsers(protocol + langs.get(0) + "." + apiUrl));
		// Add pages fetch for each domain language
		for (String lang : langs) {
			String url = protocol + lang + "." + apiUrl;
			usersAndPagesInsertion.add(pageService.initPages(lang, url));
		}
		return usersAndPagesInsertion;
	}
	
	/*@SuppressWarnings("unchecked")
	private List<CompletableFuture<Boolean>> buildFuturesList(Object obj, String methodPrefix) {
		List<CompletableFuture<Boolean>> futures = new ArrayList<>();
		for (String lang : langs) {
			String url = protocol + lang + "." + apiUrl;
			Method[] methods = obj.getClass().getMethods();
			for (int i = 0; i < methods.length; i++) {
				try {
					if (methods[i].getName().startsWith(methodPrefix) && methods[i].getParameterCount() == 1) {

						futures.add((CompletableFuture<Boolean>) methods[i].invoke(obj, url));
					} else if (methods[i].getName().startsWith(methodPrefix) && methods[i].getParameterCount() == 2) {
						futures.add((CompletableFuture<Boolean>) methods[i].invoke(obj, lang, url));
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return futures;
	}*/
}
