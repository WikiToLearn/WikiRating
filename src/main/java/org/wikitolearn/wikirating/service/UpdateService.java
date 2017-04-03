package org.wikitolearn.wikirating.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.model.Process;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.model.UpdateInfo;
import org.wikitolearn.wikirating.model.User;
import org.wikitolearn.wikirating.service.mediawiki.UpdateMediaWikiService;
import org.wikitolearn.wikirating.util.enums.ProcessStatus;
import org.wikitolearn.wikirating.util.enums.ProcessType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by valsdav on 29/03/17.
 */
@Service
public class UpdateService {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateService.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private PageService pageService;
	@Autowired
	private RevisionService revisionService;
	@Autowired
	private ProcessService processService;
	@Autowired
	private UpdateMediaWikiService updateMediaWikiService;
	@Value("#{'${mediawiki.langs}'.split(',')}")
	private List<String> langs;
	@Value("${mediawiki.protocol}")
	private String protocol;
	@Value("${mediawiki.api.url}")
	private String apiUrl;
	@Value("${mediawiki.namespace}")
	private String namespace;
	
	/**
	 * 
	 * @return
	 */
	public boolean updateGraph() {
		// Get start timestamp of the latest FETCH Process before opening a new process
		Date startTimestampLatestFetch = processService.getLastProcessStartDateByType(ProcessType.FETCH);
		// Create a new FETCH process
		Process currentFetchProcess = processService.createNewProcess(ProcessType.FETCH);
		LOG.info("Created new fetch process {}", currentFetchProcess.toString());
		
		Date startTimestampCurrentFetch = currentFetchProcess.getStartOfProcess();
		
		updateUsers(startTimestampLatestFetch, startTimestampCurrentFetch);
		updatePagesAndRevisions(startTimestampLatestFetch, startTimestampCurrentFetch);

		// Save the result of the process, closing the current one
		processService.closeCurrentProcess(ProcessStatus.DONE);
		return true;
	}
	
	/**
	 * @param start
	 * @param end
	 */
	private void updateUsers(Date start, Date end) {
		String url = protocol + langs.get(0) + "." + apiUrl;
		List<UpdateInfo> usersUpdateInfo = updateMediaWikiService.getNewUsers(url, start, end);
		List<User> newUsers = new ArrayList<>();
		
		// Build a list with new users to be added to the graph
		for(UpdateInfo updateInfo : usersUpdateInfo){
			User user = new User();
			user.setUserId(updateInfo.getUserid());
			user.setUsername(updateInfo.getUser());
			newUsers.add(user);
		}
		
		userService.addUsers(newUsers);
	}

	/**
	 * 
	 * @param start
	 * @param end
	 */
	private void updatePagesAndRevisions(Date start, Date end) {
		// First of all, get the RecentChangeEvents from MediaWiki API
		for (String lang : langs) {
			String url = protocol + lang + "." + apiUrl;
			// Fetching pages updates
			List<UpdateInfo> updates = updateMediaWikiService.getPagesUpdateInfo(url, namespace, start, end);

			updates.forEach(update -> {
				switch (update.getType()) {
				case NEW:
					// Create the new revision
					Revision newRev = revisionService.addRevision(update.getRevid(), lang, update.getUserid(),
							update.getOld_revid(), update.getNewlen(), update.getTimestamp());
					// Then create a new Page and link it with the revision
					pageService.addPage(update.getPageid(), update.getTitle(), lang, newRev);
					break;
				case EDIT:
					// Create a new revision
					Revision updateRev = revisionService.addRevision(update.getRevid(), lang, update.getUserid(),
							update.getOld_revid(), update.getNewlen(), update.getTimestamp());
					// Then add it to the page
					pageService.addRevisionToPage(lang + "_" + update.getPageid(), updateRev);
					break;
				case MOVE:
					// Move the page to the new title
					pageService.movePage(update.getTitle(), update.getNewTitle(), lang);
					break;
				case DELETE:
					// Delete the page and all its revisions
					pageService.deletePage(update.getTitle(), lang);
					break;
				default:
					break;
				}
			});
		}
	}
	
}
