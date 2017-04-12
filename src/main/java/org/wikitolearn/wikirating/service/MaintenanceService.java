package org.wikitolearn.wikirating.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.exception.GetPagesUpdateInfoException;
import org.wikitolearn.wikirating.exception.RevisionNotFoundException;
import org.wikitolearn.wikirating.exception.TemporaryVoteValidationException;
import org.wikitolearn.wikirating.exception.UpdateGraphException;
import org.wikitolearn.wikirating.exception.UpdatePagesAndRevisionsException;
import org.wikitolearn.wikirating.exception.UpdateUsersException;
import org.wikitolearn.wikirating.exception.UserNotFoundException;
import org.wikitolearn.wikirating.model.Process;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.model.TemporaryVote;
import org.wikitolearn.wikirating.model.UpdateInfo;
import org.wikitolearn.wikirating.model.User;
import org.wikitolearn.wikirating.model.Vote;
import org.wikitolearn.wikirating.repository.TemporaryVoteRepository;
import org.wikitolearn.wikirating.service.mediawiki.UpdateMediaWikiService;
import org.wikitolearn.wikirating.util.enums.ProcessStatus;
import org.wikitolearn.wikirating.util.enums.ProcessType;
//import org.wikitolearn.wikirating.util.enums.UpdateType;

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
	private UpdateMediaWikiService updateMediaWikiService;
	@Autowired
	private TemporaryVoteRepository temporaryVoteRepository;
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
		processService.addProcess(ProcessType.INIT);

		CompletableFuture<Boolean> initFuture = CompletableFuture
				.allOf(buildUsersAndPagesFutersList().toArray(new CompletableFuture[langs.size() + 1]))
				.thenCompose(result -> CompletableFuture
						.allOf(buildRevisionsFuturesList().toArray(new CompletableFuture[langs.size()])))
				.thenCompose(result -> CompletableFuture
						.allOf(buildApplyCourseStructureFuturesList().toArray(new CompletableFuture[langs.size()])))
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
	 * Build a list of CompletableFuture. The elements are the fetches of pages'
	 * revisions from each domain language.
	 * 
	 * @return a list of CompletableFuture
	 */
	private List<CompletableFuture<Boolean>> buildApplyCourseStructureFuturesList() {
		List<CompletableFuture<Boolean>> parallelApplyCourseStructureFutures = new ArrayList<>();
		// Add course structure for each domain language
		for (String lang : langs) {
			String url = protocol + lang + "." + apiUrl;
			parallelApplyCourseStructureFutures.add(pageService.applyCourseStructure(url, lang));
		}
		return parallelApplyCourseStructureFutures;
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

	/**
	 * Build a list of CompletableFuture. The first one is the fetch of the
	 * users from the first domain in mediawiki.langs list. The rest of the
	 * elements are the fetches of the pages for each language. This
	 * implementation assumes that the users are shared among domains.
	 * 
	 * @return a list of CompletableFuture
	 */
	private List<CompletableFuture<Boolean>> buildUsersAndPagesFutersList() {
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
	
	/**
	 * Entry point for the scheduled graph updated
	 * @return true if the update succeed
	 */
	@Scheduled(cron = "${maintenance.update.cron}")
	public void updateGraph() {
		
		// Get start timestamp of the latest FETCH Process before opening a new process
		Date startTimestampLatestFetch = processService.getLastProcessStartDateByType(ProcessType.FETCH);
		if(startTimestampLatestFetch == null){
			startTimestampLatestFetch = processService.getLastProcessStartDateByType(ProcessType.INIT);
		}
		// Create a new FETCH process
		Process currentFetchProcess = processService.addProcess(ProcessType.FETCH);
		metadataService.updateLatestProcess();
		
		Date startTimestampCurrentFetch = currentFetchProcess.getStartOfProcess();
		
		try {
			userService.updateUsers(protocol + langs.get(0) + "." + apiUrl, startTimestampLatestFetch,
					startTimestampCurrentFetch);
			updatePagesAndRevisions(startTimestampLatestFetch, startTimestampCurrentFetch);
			/*for (String lang : langs) {
				String url = protocol + lang + "." + apiUrl;
				pageService.applyCourseStructure(url, lang);
			}*/
			validateTemporaryVotes(startTimestampCurrentFetch);
			// Save the result of the process, closing the current one
			processService.closeCurrentProcess(ProcessStatus.DONE);
		} catch (TemporaryVoteValidationException | UpdateUsersException | UpdatePagesAndRevisionsException e) {
			processService.closeCurrentProcess(ProcessStatus.EXCEPTION);
			LOG.error("An error occurred during a scheduled graph update procedure");
			throw new UpdateGraphException();
		}
	}

	/**
	 * Update the pages and the revisions querying the MediaWiki API
	 * @param start
	 * @param end
	 */
	private void updatePagesAndRevisions(Date start, Date end) throws UpdatePagesAndRevisionsException{
		try{
			// First of all, get the RecentChangeEvents from MediaWiki API
			for (String lang : langs) {
				String url = protocol + lang + "." + apiUrl;
				// Fetching pages updates
				List<UpdateInfo> updates = updateMediaWikiService.getPagesUpdateInfo(url, namespace, start, end);
				
				for(UpdateInfo update : updates){
					switch (update.getType()) {
					case "new":
						// Create the new revision
						Revision newRev = revisionService.addRevision(update.getRevid(), lang, update.getUserid(),
								update.getOld_revid(), update.getNewlen(), update.getTimestamp());
						// Then create a new Page and link it with the revision
						pageService.addPage(update.getPageid(), update.getTitle(), lang, newRev);
						userService.setAuthorship(newRev);
						break;
					case "edit":
						// Create a new revision
						Revision updateRev = revisionService.addRevision(update.getRevid(), lang, update.getUserid(),
								update.getOld_revid(), update.getNewlen(), update.getTimestamp());
						// Then add it to the page
						pageService.addRevisionToPage(lang + "_" + update.getPageid(), updateRev);
						userService.setAuthorship(updateRev);
						break;
					case "move":
						// Move the page to the new title
						pageService.movePage(update.getTitle(), update.getNewTitle(), lang);
						break;
					case "delete":
						// Delete the page and all its revisions
						pageService.deletePage(update.getTitle(), lang);
						break;
					default:
						break;
					}
				}
			}
		}catch(GetPagesUpdateInfoException e){
			LOG.error("An error occurred while updating pages and revisions: {}", e.getMessage());
			throw new UpdatePagesAndRevisionsException();
		}
	}
	
	/**
	 * Validate temporary votes added before the given timestamp
	 * @param timestamp the timestamp used for comparison
	 * @throws TemporaryVoteValidationException
	 */
	private void validateTemporaryVotes(Date timestamp) throws TemporaryVoteValidationException{
		List<TemporaryVote> temporaryVotes = temporaryVoteRepository.findByTimestamp(timestamp);
		for(TemporaryVote temporaryVote: temporaryVotes){
			try{
				User user = userService.getUser(temporaryVote.getUserId());
				Revision revision = revisionService.getRevision(temporaryVote.getLangRevId());
				Vote vote = new Vote(temporaryVote.getValue(), temporaryVote.getReliability(), temporaryVote.getTimestamp());
				vote.setRevision(revision);
				vote.setUser(user);
				revision.addVote(vote);
				revisionService.updateRevision(revision);
			}catch(UserNotFoundException | RevisionNotFoundException e){
				LOG.error("An error occurred during temporary vote validation: {}", temporaryVote);
				throw new TemporaryVoteValidationException(e.getMessage());
			}
		}
	}
	
}
