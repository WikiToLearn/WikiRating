package org.wikitolearn.services.mediawiki;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikitolearn.models.User;
import org.wikitolearn.utils.MediaWikiApiUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class used to query MediaWiki about Users
 * Created by valsdav on 14/03/17.
 */
@Service
public class UserMediaWikiService {
    private static final Logger LOG = LoggerFactory.getLogger(UserMediaWikiService.class);

    @Autowired
    private MediaWikiApiUtils mediaWikiApiUtils;
    @Autowired
    private ObjectMapper mapper;

    /**
     * Get all the users from MediaWiki instance through its API.
     * @param apiUrl the MediaWiki API url
     * @return users A list that contains all the fetched users
     */
    public List<User> getAllUsers(String apiUrl){
        ApiConnection connection = mediaWikiApiUtils.getApiConnection(apiUrl);
        Map<String, String> parameters = mediaWikiApiUtils.getUserParam();
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

    /**
     * This method is an utility. It concatenates the given JSONArrays into one.
     * @param arrays The arrays to be concatenated
     * @return result The resulted JSONArray
     * @throws JSONException
     */
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

