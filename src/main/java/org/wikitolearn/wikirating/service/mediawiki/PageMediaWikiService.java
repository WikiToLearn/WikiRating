/**
 * 
 */
package org.wikitolearn.wikirating.service.mediawiki;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikitolearn.wikirating.model.CourseTree;
import org.wikitolearn.wikirating.model.Page;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 
 * @author aletundo, valsdav
 *
 */
@Service
public class PageMediaWikiService extends MediaWikiService<Page>{
	
	/**
	 * Get all the pages from a specified namespace of MediaWiki instance through its API.
	 * @param apiUrl String The MediaWiki API url
	 * @return pages List<Page> A list that contains all the fetched pages
	 */
	@Override
	public List<Page> getAll(String apiUrl){
		ApiConnection connection = mediaWikiApiUtils.getApiConnection(apiUrl);
		Map<String, String> parameters = mediaWikiApiUtils.getListAllPagesParams(namespace);
		InputStream response;
		boolean morePages = true;
		JSONArray pagesJson = new JSONArray();
		List<JSONArray> toBeConcat = new ArrayList<>();
		List<Page> pages = new ArrayList<>();
		
		try {
			while(morePages){
				response = mediaWikiApiUtils.sendRequest(connection, "GET", parameters);
				JSONObject responseJson = mediaWikiApiUtils.streamToJson(response);
				
				toBeConcat.add(responseJson.getJSONObject("query").getJSONArray("allpages"));

				if(responseJson.has("continue")){
					String continueFrom = responseJson.getJSONObject("continue").getString("apcontinue");
					parameters.put("apfrom", continueFrom);
				}else{
					morePages = false;
					pagesJson = concatArrays(toBeConcat);
				}
			}
			pages = mapper.readValue(pagesJson.toString(), new TypeReference<List<Page>>(){});
			return pages;
		} catch (JSONException e){
			LOG.error("An error occurred while a JSONObject or JSONArray. {}", e.getMessage());
		} catch(IOException e){
			LOG.error("An error occurred while converting an InputStream to JSONObject. {}", e.getMessage());
		}
		return pages;
	}
	
	/**
	 * 
	 * @param apiUrl the MediaWiki API url
	 * @param pageTitle the title of the root course page
	 * @return
	 */
	public void getCourseTree(String apiUrl, String pageTitle) {
		ApiConnection connection = mediaWikiApiUtils.getApiConnection(apiUrl);
		Map<String, String> parameters = mediaWikiApiUtils.getCourseTreeParams(pageTitle);
		InputStream response;
		response = mediaWikiApiUtils.sendRequest(connection, "GET", parameters);
		JSONObject responseJson = mediaWikiApiUtils.streamToJson(response);
		try {
			JSONObject jsonTree = responseJson.getJSONObject("coursetree").getJSONObject("response");
			List<List<String>> levelsThree = new ArrayList<>();
			JSONArray levelsThreeJson = jsonTree.getJSONArray("levelsThree");
			levelsThree = mapper.readValue(levelsThreeJson.toString(), new TypeReference<List<List<String>>>(){});

			// Build course tree manually cause difficulties with serialization of nested JSON arrays
			CourseTree courseTree = new CourseTree();
			courseTree.setRoot(jsonTree.getString("root"));
			courseTree.setLevelsTwo(
					mapper.readValue(
						jsonTree.getJSONArray("levelsTwo").toString(), 
						new TypeReference<List<String>>(){}
					)
			);
			courseTree.setLevelsTree(levelsThree);

			LOG.info(courseTree.toString());
		} catch (JSONException | IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
	}
}
