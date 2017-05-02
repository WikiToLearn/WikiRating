package org.wikitolearn.wikirating.service.mediawiki;

import com.fasterxml.jackson.core.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikitolearn.wikirating.model.graph.User;

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
public class UserMediaWikiService extends MediaWikiService<User>{
    /**
     * Get all the users from MediaWiki instance through its API.
     * @param apiUrl the MediaWiki API url
     * @return users a list that contains all the fetched users
     */
    public List<User> getAll(String apiUrl){
        ApiConnection connection = mediaWikiApiUtils.getApiConnection(apiUrl);
        Map<String, String> parameters = mediaWikiApiUtils.getUserParams();
        InputStream response;
        boolean moreUsers = true;
        JSONArray usersJson = new JSONArray();
        List<JSONArray> toBeConcat = new ArrayList<>();
        List<User> users = new ArrayList<>();

        try {
            while(moreUsers){
                response = mediaWikiApiUtils.sendRequest(connection, "GET", parameters);
                JSONObject responseJson = mediaWikiApiUtils.streamToJson(response);

                toBeConcat.add(responseJson.getJSONObject("query").getJSONArray("allusers"));

                if(responseJson.has("continue")){
                    String continueFrom = responseJson.getJSONObject("continue").getString("aufrom");
                    parameters.put("aufrom", continueFrom);
                }else{
                    moreUsers = false;
                    usersJson = concatArrays(toBeConcat);
                }
            }
            users = mapper.readValue(usersJson.toString(), new TypeReference<List<User>>(){});
            return users;
        } catch (JSONException e){
            LOG.error("An error occurred while a JSONObject or JSONArray. {}", e.getMessage());
        } catch(IOException e){
            LOG.error("An error occurred while converting an InputStream to JSONObject. {}", e.getMessage());
        }
        return users;
    }
}