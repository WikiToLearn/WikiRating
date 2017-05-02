/**
 * 
 */
package org.wikitolearn.wikirating.controller;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.wikitolearn.wikirating.exception.PageNotFoundException;
import org.wikitolearn.wikirating.exception.RevisionNotFoundException;
import org.wikitolearn.wikirating.model.api.ApiResponseSuccess;
import org.wikitolearn.wikirating.model.graph.Page;
import org.wikitolearn.wikirating.model.graph.Revision;
import org.wikitolearn.wikirating.model.graph.TemporaryVote;
import org.wikitolearn.wikirating.model.graph.Vote;
import org.wikitolearn.wikirating.service.PageService;
import org.wikitolearn.wikirating.service.RevisionService;
import org.wikitolearn.wikirating.service.VoteService;

/**
 * RESTController for pages resources. It handles all the requests to {lang}/pages
 * @author aletundo 
 */
@RestController
@RequestMapping("{lang}/pages/")
public class PageController {
	private static final Logger LOG = LoggerFactory.getLogger(PageController.class);
	@Autowired
	private PageService pageService;
	@Autowired
	private RevisionService revisionService;
	@Autowired
	private VoteService voteService;
	
	/**
	 * Handle GET requests on {lang}/pages/{pageId} URI. The last revision of the page is returned.
	 * 
	 * @param pageId
	 *            int The id of the page
	 * @return the last revision of the requested page
	 * @throws PageNotFoundException
	 */
	@RequestMapping(value = "{pageId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ApiResponseSuccess getLastRevisionByPageId(@PathVariable("lang") String lang, @PathVariable("pageId") int pageId) throws PageNotFoundException{
		try{
			Page page = pageService.getPage(pageId, lang);
			ApiResponseSuccess body = new ApiResponseSuccess(page.getLastRevision(), Instant.now().toEpochMilli());
			return body;
		}catch(PageNotFoundException e){
			LOG.error("Impossible to get the last revision of {}_{}: {}",lang, pageId, e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Handle GET requests on {lang}/pages/{pageId}/revisions URI. All the revision for the requested page are returned.
	 * 
	 * @param pageId
	 *            int The id of the page
	 * @return a list with all revisions of the requested page
	 */
	@RequestMapping(value = "{pageId}/revisions", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ApiResponseSuccess getAllPageRevisions(@PathVariable("lang") String lang, @PathVariable("pageId") int pageId) {
		String langPageId = lang + "_" + pageId;
		try{
			Set<Revision> revisions = revisionService.getRevisionsOfPage(langPageId);
			ApiResponseSuccess body = new ApiResponseSuccess(revisions, Instant.now().toEpochMilli());
			return body;
		}catch(RevisionNotFoundException e){
			LOG.error("Impossible to get revisions of page {}", langPageId);
			throw e;
		}
	}

	/**
	 * Handle GET requests on {lang}/pages/{pageId}/revisions/{revId} URI. The requested page revision is returned.
	 * 
	 * @param pageId
	 *            int The id of the page
	 * @param revId
	 *            int The id of the revision
	 * @return the requested revision of the page
	 */
	@RequestMapping(value = "{pageId}/revisions/{revId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ApiResponseSuccess getRevisionById(@PathVariable("lang") String lang, @PathVariable("pageId") int pageId, @PathVariable("revId") int revId) {
		String langRevId = lang + "_" + revId;
		try{
			Revision revision = revisionService.getRevision(langRevId);
			ApiResponseSuccess body = new ApiResponseSuccess(revision, Instant.now().toEpochMilli());
			return body;
		}catch(RevisionNotFoundException e){
			String langPageId = lang + "_" + pageId;
			LOG.error("Impossible to get revision {} of page {}", langRevId, langPageId);
			throw e;
		}
	}
	
	/**
	 * Handle GET requests on {lang}/pages/{pageId}/revisions/{revId}/votes URI. All
	 * the votes for the requested page revision are returned.
	 * 
	 * @param pageId
	 *            int The id of the page
	 * @param revId
	 *            int The id of the revision
	 * @return all the votes of the requested revision
	 */
	@RequestMapping(value = "{pageId}/revisions/{revId}/votes", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ApiResponseSuccess showVotes(@PathVariable("lang") String lang, @PathVariable("pageId") int pageId, @PathVariable("revId") int revId) {
		// TODO: Work in progress
		String langRevId = lang + "_" + revId;
		List<Vote> votes =  voteService.getAllVotesOfRevision(langRevId);
		ApiResponseSuccess body = new ApiResponseSuccess(votes, Instant.now().toEpochMilli());
		return body;
	}

	/**
	 * Handle POST requests on {lang}/pages/{pageId}/revisions/{revId}/votes URI. It
	 * expects vote and userId as request parameters.
	 * 
	 * @param pageId
	 *            int The id of the page
	 * @param revId
	 *            int The id of the revision
	 * @param vote
	 *            double The value of the vote
	 * @param userId
	 *            int The id of the user who submit the request
	 * @return response Vote
	 */
	@RequestMapping(value = "{pageId}/revisions/{revId}/votes", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public TemporaryVote addVote(@PathVariable("lang") String lang, @PathVariable("pageId") int pageId, @PathVariable("revId") int revId,
			@RequestParam("vote") double vote, @RequestParam("userId") int userId) {
		//TODO
		return null;
	}
}
