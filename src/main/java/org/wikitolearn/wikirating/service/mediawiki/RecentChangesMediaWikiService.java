package org.wikitolearn.wikirating.service.mediawiki;

import com.fasterxml.jackson.core.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikitolearn.wikirating.model.UpdateEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by valsdav on 29/03/17.
 */
@Service
public class RecentChangesMediaWikiService extends MediaWikiService<UpdateEvent>{

    public List<UpdateEvent> getRecentChangesBetweenDates(String apiUrl, String namespace, Date begin, Date end){
        ApiConnection connection = mediaWikiApiUtils.getApiConnection(apiUrl);
        InputStream response;
        boolean moreRc = true;
        JSONArray rcJson = new JSONArray();
        List<JSONArray> toBeConcat = new ArrayList<>();
        List<UpdateEvent> rcs = new ArrayList<>();
        Map<String, String> parameters = mediaWikiApiUtils.getRecentChangesParam(namespace, begin,end);
        try {
            while(moreRc){
                response = mediaWikiApiUtils.sendRequest(connection, "GET", parameters);
                JSONObject responseJson = mediaWikiApiUtils.streamToJson(response);

                toBeConcat.add(responseJson.getJSONObject("query").getJSONArray("recentchanges"));

                if(responseJson.has("continue")){
                    String continueFrom = responseJson.getJSONObject("continue").getString("rccontinue");
                    parameters.put("rccontinue", continueFrom);
                }else{
                    moreRc = false;
                    rcJson = concatArrays(toBeConcat);
                }
            }
            rcs = mapper.readValue(rcJson.toString(), new TypeReference<List<UpdateEvent>>(){});
            return rcs;
        } catch (JSONException e){
            LOG.error("An error occurred while a JSONObject or JSONArray. {}", e.getMessage());
        } catch(IOException e){
            LOG.error("An error occurred while converting an InputStream to JSONObject. {}", e.getMessage());
        }
        return rcs;

    }

    @Override
    public List<UpdateEvent> getAll(String apiUrl) {
        return null;
    }
}
