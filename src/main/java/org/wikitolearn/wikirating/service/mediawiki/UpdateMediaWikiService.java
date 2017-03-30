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

    public List<UpdateInfo> getPagesUpdateInfo(String apiUrl, String namespace, Date start, Date end){
        List<UpdateInfo> editsAndNewPages = getRecentChangesBetweenDates(apiUrl, namespace, start, end);
        List<UpdateInfo> deletedPages = getLogEventsBetweenDates(apiUrl, "delete", start, end);
        List<UpdateInfo> movedPages = getLogEventsBetweenDates(apiUrl, "move", start, end);
        List<UpdateInfo> allUpdates = Stream.of(editsAndNewPages,deletedPages,movedPages)
                .flatMap(List::stream).collect(Collectors.toList());
        return allUpdates;
    }

    public List<UpdateInfo> getNewUsers(String apiUrl, Date start, Date end){
        return getLogEventsBetweenDates(apiUrl, "newusers", start, end);
    }


    public List<UpdateInfo> getRecentChangesBetweenDates( String apiUrl, String namespace, Date start, Date end){
        ApiConnection connection = mediaWikiApiUtils.getApiConnection(apiUrl);
        InputStream response;
        boolean moreRc = true;
        JSONArray rcJson = new JSONArray();
        List<JSONArray> toBeConcat = new ArrayList<>();
        List<UpdateInfo> rcs = new ArrayList<>();
        Map<String, String> parameters = mediaWikiApiUtils.getRecentChangesParam(namespace, start,end);
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
            rcs = mapper.readValue(rcJson.toString(), new TypeReference<List<UpdateInfo>>(){});
            return rcs;
        } catch (JSONException e){
            LOG.error("An error occurred while a JSONObject or JSONArray. {}", e.getMessage());
        } catch(IOException e){
            LOG.error("An error occurred while converting an InputStream to JSONObject. {}", e.getMessage());
        }
        return rcs;
    }


    public List<UpdateInfo> getLogEventsBetweenDates( String apiUrl, String logtype, Date start, Date end){
        ApiConnection connection = mediaWikiApiUtils.getApiConnection(apiUrl);
        InputStream response;
        JSONArray leJson = new JSONArray();
        List<JSONArray> toBeConcat = new ArrayList<>();
        List<UpdateInfo> levents = new ArrayList<>();

        try {
            Map<String, String> parameters = mediaWikiApiUtils.getLogEventsParam(logtype, start,end);
            boolean moreLe = true;

            while(moreLe){
                response = mediaWikiApiUtils.sendRequest(connection, "GET", parameters);
                JSONObject responseJson = mediaWikiApiUtils.streamToJson(response);

                toBeConcat.add(responseJson.getJSONObject("query").getJSONArray("recentchanges"));

                if(responseJson.has("continue")){
                    String continueFrom = responseJson.getJSONObject("continue").getString("lecontinue");
                    parameters.put("lecontinue", continueFrom);
                }else{
                    moreLe = false;
                    leJson = concatArrays(toBeConcat);
                }
            }
            levents = mapper.readValue(leJson.toString(), new TypeReference<List<UpdateInfo>>(){});
            return levents;
        } catch (JSONException e){
            LOG.error("An error occurred while a JSONObject or JSONArray. {}", e.getMessage());
        } catch(IOException e){
            LOG.error("An error occurred while converting an InputStream to JSONObject. {}", e.getMessage());
        }
        return levents;
    }


    @Override
    public List<UpdateInfo> getAll(String apiUrl) {
        return null;
    }
}
