/**
 * 
 */
package org.wikitolearn.controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aletundo
 * RESTController for pages resources. It handles all the requests to /pages.
 */
@RestController
@RequestMapping("/pages/")
public class PageController {
	private static final Logger LOG = LoggerFactory.getLogger(PageController.class);
	
	/**
	 * Handle POST requests on /pages/{pageId}/revisions/{revId}/votes URI. It expects vote and userId as request
	 * parameters.
	 * @param pageId int
	 * @param revId int
	 * @param vote double
	 * @param userId int
	 * @return response String
	 */
	@RequestMapping(value = "{pageId}/revisions/{revId}/votes", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String vote(@PathVariable("pageId") int pageId, @PathVariable("revId") int revId,
			@RequestParam("vote") double vote, @RequestParam("userId") int userId) {
		JSONObject response = new JSONObject();
		try {
			response.put("pageId", pageId);
			response.put("revId", revId);
			response.put("vote", vote);
			response.put("user", userId);
		} catch (JSONException e) {
			LOG.error("{}.", e.getMessage());
		}
		return response.toString();
	}
}
