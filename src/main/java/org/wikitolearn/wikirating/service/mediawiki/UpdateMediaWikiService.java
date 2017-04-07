package org.wikitolearn.wikirating.service.mediawiki;

import com.fasterxml.jackson.core.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikitolearn.wikirating.model.UpdateInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by valsdav on 29/03/17.
 */
@Service
public class UpdateMediaWikiService extends MediaWikiService<UpdateInfo>{

	/**
	 * 
	 * @param apiUrl
	 * @param namespace
	 * @param start
	 * @param end
	 * @return
	 */
    public List<UpdateInfo> getPagesUpdateInfo(String apiUrl, String namespace, Date start, Date end){
        List<UpdateInfo> editsAndNewPages = getRecentChangesBetweenDates(apiUrl, namespace, start, end);
        List<UpdateInfo> deletedPages = getLogEventsBetweenDates(apiUrl, "delete", start, end);
        List<UpdateInfo> movedPages = getLogEventsBetweenDates(apiUrl, "move", start, end);
        List<UpdateInfo> allUpdates = Stream.of(editsAndNewPages,deletedPages,movedPages)
                .flatMap(List::stream).collect(Collectors.toList());
        return allUpdates;
    }
    
    /**
     * 
     * @param apiUrl
     * @param start
     * @param end
     * @return
     */
    public List<UpdateInfo> getNewUsers(String apiUrl, Date start, Date end){
        return getLogEventsBetweenDates(apiUrl, "newusers", start, end);
    }

    /**
     * 
     * @param apiUrl
     * @param namespace
     * @param start
     * @param end
     * @return
     */
    public List<UpdateInfo> getRecentChangesBetweenDates( String apiUrl, String namespace, Date start, Date end){
        ApiConnection connection = mediaWikiApiUtils.getApiConnection(apiUrl);
        InputStream response;
        boolean moreRecentChanges = true;
        JSONArray recentChangesJson = new JSONArray();
        List<JSONArray> toBeConcat = new ArrayList<>();
        List<UpdateInfo> recentChanges = new ArrayList<>();
        Map<String, String> parameters = mediaWikiApiUtils.getRecentChangesParams(namespace, start,end);
        try {
            while(moreRecentChanges){
                response = mediaWikiApiUtils.sendRequest(connection, "GET", parameters);
                JSONObject responseJson = mediaWikiApiUtils.streamToJson(response);

                toBeConcat.add(responseJson.getJSONObject("query").getJSONArray("recentchanges"));

                if(responseJson.has("continue")){
                    String continueFrom = responseJson.getJSONObject("continue").getString("rccontinue");
                    parameters.put("rccontinue", continueFrom);
                }else{
                    moreRecentChanges = false;
                    recentChangesJson = concatArrays(toBeConcat);
                }
            }
            recentChanges = mapper.readValue(recentChangesJson.toString(), new TypeReference<List<UpdateInfo>>(){});
            return recentChanges;
        } catch (JSONException e){
            LOG.error("An error occurred while a JSONObject or JSONArray. {}", e.getMessage());
        } catch(IOException e){
            LOG.error("An error occurred while converting an InputStream to JSONObject. {}", e.getMessage());
        }
        return recentChanges;
    }

    /**
     * 
     * @param apiUrl
     * @param logtype
     * @param start
     * @param end
     * @return
     */
    public List<UpdateInfo> getLogEventsBetweenDates( String apiUrl, String logtype, Date start, Date end){
        ApiConnection connection = mediaWikiApiUtils.getApiConnection(apiUrl);
        InputStream response;
        JSONArray logEventsJson = new JSONArray();
        List<JSONArray> toBeConcat = new ArrayList<>();
        List<UpdateInfo> logEvents = new ArrayList<>();

        try {
            Map<String, String> parameters = mediaWikiApiUtils.getLogEventsParams(logtype, start,end);
            boolean moreLogEvents = true;

            while(moreLogEvents){
                response = mediaWikiApiUtils.sendRequest(connection, "GET", parameters);
                JSONObject responseJson = mediaWikiApiUtils.streamToJson(response);
                LOG.info("{}", responseJson.toString());
                toBeConcat.add(responseJson.getJSONObject("query").getJSONArray("logevents"));

                if(responseJson.has("continue")){
                    String continueFrom = responseJson.getJSONObject("continue").getString("lecontinue");
                    parameters.put("lecontinue", continueFrom);
                }else{
                    moreLogEvents = false;
                    logEventsJson = concatArrays(toBeConcat);
                }
            }
            if(logtype.equals("move")){
                for (int i = 0; i < logEventsJson.length(); i++){
                    JSONObject element = logEventsJson.getJSONObject(i);
                    String newTitle = (String) element.getJSONObject("params").get("target_title");
                    element.put("newTitle", newTitle);
                }
            }
            logEvents = mapper.readValue(logEventsJson.toString(), new TypeReference<List<UpdateInfo>>(){});
            return logEvents;
        } catch (JSONException e){
            LOG.error("An error occurred while a JSONObject or JSONArray. {}", e.getMessage());
        } catch(IOException e){
            LOG.error("An error occurred while converting an InputStream to JSONObject. {}", e.getMessage());
        }
        return logEvents;
    }
}
