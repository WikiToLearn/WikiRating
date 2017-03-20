/**
 * 
 */
package org.wikitolearn.services.mediawiki;

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
import org.wikitolearn.models.Page;

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
		Map<String, String> parameters = mediaWikiApiUtils.getListAllPagesParamsMap("2800");
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
}
