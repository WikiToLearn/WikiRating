/**
 * 
 */
package org.wikitolearn.wikirating.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.model.Vote;

/**
 * @author aletundo RESTController for pages resources. It handles all the
 *         requests to /pages.
 */
@RestController
@RequestMapping("/pages/")
public class PageController {
	private static final Logger LOG = LoggerFactory.getLogger(PageController.class);
	
	/**
	 * Handle GET requests on /pages/{pageId} URI. The last revision of the page is returned.
	 * 
	 * @param pageId
	 *            int The id of the page
	 * @return response Revision
	 */
	@RequestMapping(value = "{pageId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Revision getLastRevisionByPageId(@PathVariable("pageId") int pageId) {
		// TODO Get the last available revision for the requested page
		return new Revision();
	}
	
	/**
	 * Handle GET requests on /pages/{pageId}/revisions URI. All the revision for the requested page are returned.
	 * 
	 * @param pageId
	 *            int The id of the page
	 * @return response The list with all the revisions
	 */
	@RequestMapping(value = "{pageId}/revisions", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Revision> getAllPageRevisions(@PathVariable("pageId") int pageId) {
		// TODO Get the requested revision
		return new ArrayList<Revision>();
	}

	/**
	 * Handle GET requests on /pages/{pageId}/revisions/{revId} URI. The requested page revision is returned.
	 * 
	 * @param pageId
	 *            int The id of the page
	 * @param revId
	 *            int The id of the revision
	 * @return response Revision
	 */
	@RequestMapping(value = "{pageId}/revisions/{revId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Revision getRevisionById(@PathVariable("pageId") int pageId, @PathVariable("revId") int revId) {
		// TODO Get the requested revision
		return new Revision();
	}
	
	/**
	 * Handle GET requests on /pages/{pageId}/revisions/{revId}/votes URI. All
	 * the votes for the requested page revision are returned.
	 * 
	 * @param pageId
	 *            int The id of the page
	 * @param revId
	 *            int The id of the revision
	 * @return response Vote
	 */
	@RequestMapping(value = "{pageId}/revisions/{revId}/votes", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Vote> showVotes(@PathVariable("pageId") int pageId, @PathVariable("revId") int revId) {

		// TODO Get votes from PageService/PageDAO
		return new ArrayList<Vote>();
	}

	/**
	 * Handle POST requests on /pages/{pageId}/revisions/{revId}/votes URI. It
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
	@ResponseBody
	public Vote addVote(@PathVariable("pageId") int pageId, @PathVariable("revId") int revId,
			@RequestParam("vote") double vote, @RequestParam("userId") int userId) {
		// TODO Insert the vote into the graph
		return new Vote();
	}
}