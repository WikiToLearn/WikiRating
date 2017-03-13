/**
 * 
 */
package org.wikitolearn.controllers;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikitolearn.controllers.mediawikiClient.PageMediaWikiController;
import org.wikitolearn.models.Page;
import org.wikitolearn.utils.MediaWikiApiUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author alessandro
 *
 */
@RestController
public class InitializerController {
	
	private static final Logger LOG = LoggerFactory.getLogger(InitializerController.class);

	@Autowired
	private PageMediaWikiController pageController;
	//@Autowired
	//private MediaWikiApiUtils mwUtil;
	
	@RequestMapping(value = "/init", method = RequestMethod.GET, produces = "application/json")
	public List<Page> initialize(){

		List<Page> allPages =  pageController.getAllPages();

        return allPages;
	}
	
	/*@RequestMapping(value = "/init", method = RequestMethod.GET, produces = "application/json")
	public String initialize(){
		ApiConnection connection = mwUtil.getApiConnection("https://it.wikitolearn.org/api.php");
		
		Map<String, String> parameters =  new HashMap<>();
		parameters.put("action", "query");
		parameters.put("list", "allpages");
		parameters.put("apnamespace", "2800");
		parameters.put("aplimit", "max");
		parameters.put("format", "json");
		InputStream response = mwUtil.sendRequest(connection, "GET", parameters);
		
		ObjectMapper mapper = new ObjectMapper();
		return mwUtil.streamToJson(response).toString();
	}*/

	
	/*@RequestMapping(value = "/init", method = RequestMethod.GET, produces = "application/json")
	public Page[] initialize(){
		ApiConnection connection = mwUtil.getApiConnection("https://de.wikitolearn.org/api.php");
		
		Map<String, String> parameters =  new HashMap<>();
		parameters.put("action", "query");
		parameters.put("list", "allpages");
		parameters.put("apnamespace", "2800");
		parameters.put("aplimit", "max");
		parameters.put("format", "json");
		InputStream response = mwUtil.sendRequest(connection, "GET", parameters);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JSONArray pagesJson = mwUtil.streamToJson(response).getJSONObject("query").getJSONArray("allpages");
			return mapper.readValue(pagesJson.toString(), Page[].class);
		} catch (JSONException e) {
			LOG.error("An error occurred while a JSONObject or JSONArray", e.getMessage());
		} catch(IOException e){
			LOG.error("An error occurred while converting an InputStream to JSONObject", e.getMessage());
		}
		return null;
	}*/

}
