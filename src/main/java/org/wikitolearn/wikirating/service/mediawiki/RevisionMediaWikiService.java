package org.wikitolearn.wikirating.service.mediawiki;

import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikitolearn.wikirating.model.Revision;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author aletundo, valsdav
 *
 */
@Service
public class RevisionMediaWikiService extends MediaWikiService<Revision>{  
	
    /**
     * Get all the revisions for a specific page querying MediaWiki API
     * @param apiUrl the MediaWiki API url
     * @param pageId the id the page of which getting the revisions
     * @return revisions  a list that contains all the fetched revisions
     */
	public List<Revision> getAllRevisionByPageId(String apiUrl, int pageId) {
		Map<String, String> parameters = mediaWikiApiUtils.getRevisionParams(pageId);
		ApiConnection connection = mediaWikiApiUtils.getApiConnection(apiUrl);
        InputStream response;
        boolean moreRevs = true;
        JSONArray revsJson = new JSONArray();
        List<JSONArray> toBeConcat = new ArrayList<>();
        List<Revision> revs = new ArrayList<>();

        try {
            while(moreRevs){
                response = mediaWikiApiUtils.sendRequest(connection, "GET", parameters);
                JSONObject responseJson = mediaWikiApiUtils.streamToJson(response);
                toBeConcat.add(responseJson.getJSONObject("query").getJSONObject("pages").
                        getJSONObject(parameters.get("pageids")).getJSONArray("revisions"));

                if(responseJson.has("continue")){
                    String continueFrom = responseJson.getJSONObject("continue").getString("rvcontinue");
                    parameters.put("rvcontinue", continueFrom);
                }else{
                    moreRevs = false;
                    revsJson = concatArrays(toBeConcat);
                }
            }
            revs = mapper.readValue(revsJson.toString(), new TypeReference<List<Revision>>(){});
            return revs;
        } catch (JSONException e){
            LOG.error("An error occurred while a JSONObject or JSONArray. {}", e.getMessage());
        } catch(IOException e){
            LOG.error("An error occurred while converting an InputStream to JSONObject. {}", e.getMessage());
        }
        return revs;
	}
}
