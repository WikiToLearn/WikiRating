/**
 * 
 */
package org.wikitolearn.controllers.mediawikiClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikitolearn.models.Page;
import org.wikitolearn.utils.MediaWikiApiUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author alessandro
 *
 */
@Service
public class PageMediaWikiController {
	private static final Logger LOG = LoggerFactory.getLogger(PageMediaWikiController.class);
	
	@Autowired
	private MediaWikiApiUtils mediaWikiApiUtils;
	@Autowired
	private ObjectMapper mapper;
	
	public List<Page> getAllPages(){
		ApiConnection connection = mediaWikiApiUtils.getApiConnection("https://it.wikitolearn.org/api.php");
		Map<String, String> parameters = mediaWikiApiUtils.getListAllPagesParamsMap("2800");
		InputStream response;
		boolean morePages = true;
		JSONArray pagesJson = new JSONArray();
		List<JSONArray> toBeConcat = new ArrayList<>();
		try {
			while(morePages){
				response = mediaWikiApiUtils.sendRequest(connection, "GET", parameters);
				JSONObject responseJson = mediaWikiApiUtils.streamToJson(response);
				//JSONArray toBeConcatArray = responseJson.getJSONObject("query").getJSONArray("allpages");
				toBeConcat.add(responseJson.getJSONObject("query").getJSONArray("allpages"));
				/*for(int i = 0; i < toBeConcatArray.length(); i++){
					pagesJson.put(toBeConcatArray.get(i));
				}*/
				if(responseJson.has("continue")){
					String continueFrom = responseJson.getJSONObject("continue").getString("apcontinue");
					parameters.put("apfrom", continueFrom);
				}else{
					morePages = false;
					pagesJson = concatArrays(toBeConcat);
				}
			}
			return mapper.readValue(pagesJson.toString(), new TypeReference<List<Page>>(){});
		} catch (JSONException e){
			LOG.error("An error occurred while a JSONObject or JSONArray", e.getMessage());
		} catch(IOException e){
			LOG.error("An error occurred while converting an InputStream to JSONObject", e.getMessage());
		}
		return null;
	}
	
	private JSONArray concatArrays(List<JSONArray> arrays) throws JSONException{
		JSONArray result = new JSONArray();
	    for (JSONArray arr : arrays) {
	        for (int i = 0; i < arr.length(); i++) {
	            result.put(arr.get(i));
	        }
	    }
	    return result;
	}
}
