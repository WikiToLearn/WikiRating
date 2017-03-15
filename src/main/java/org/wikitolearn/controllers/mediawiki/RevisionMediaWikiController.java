package org.wikitolearn.controllers.mediawiki;

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
import org.wikitolearn.models.Revision;
import org.wikitolearn.utils.MediaWikiApiUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class will handle the query on MediaWiki about revisions.
 * Created by valsdav on 14/03/17.
 */
@Service
public class RevisionMediaWikiController {

    private static final Logger LOG = LoggerFactory.getLogger(RevisionMediaWikiController.class);

    @Autowired
    private MediaWikiApiUtils mediaWikiApiUtils;
    @Autowired
    private ObjectMapper mapper;

    /**
     * Get all the revisions for a specific page querying MediaWiki API
     * @param apiUrl the MediaWiki API url
     * @param pageid Pageid of the page of which getting the revisions.      *
     * @return revisions A list that contains all the fetched revisions
     */
    public List<Revision> getAllRevisionForPage(String apiUrl, int pageid){
        ApiConnection connection = mediaWikiApiUtils.getApiConnection(apiUrl);
        Map<String, String> parameters = mediaWikiApiUtils.getRevisionParam(pageid);
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
                        getJSONObject(Integer.toString(pageid)).getJSONArray("revisions"));

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
            LOG.error("An error occurred while a JSONObject or JSONArray", e.getMessage());
        } catch(IOException e){
            LOG.error("An error occurred while converting an InputStream to JSONObject", e.getMessage());
        }
        return revs;
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
