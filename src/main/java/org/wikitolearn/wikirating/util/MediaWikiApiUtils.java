/**
 * 
 */
package org.wikitolearn.wikirating.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.LoginFailedException;
import org.wikitolearn.wikirating.exception.GenericException;

/**
 * @author aletundo, valsdav
 *
 */
@Component
public class MediaWikiApiUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(MediaWikiApiUtils.class);
	@Value("${mediawiki.api.user}")
	private String apiUser;
	@Value("${mediawiki.api.password}")
	private String apiPassword;
	
	 /**
	 * This method creates a MediaWiki Connection object
	 * @param apiUrl the url of the API
	 * @return	ApiConnection the API connection object
	 */

	public ApiConnection getApiConnection(String apiUrl) {
		ApiConnection connection = new ApiConnection(apiUrl);
		if(connection.isLoggedIn()){
			return connection;
		}else{
			try {
				connection.login(apiUser, apiPassword);
			} catch (LoginFailedException e) {
				LOG.error("MediaWiki login failed. {}", e.getMessage());
				// TODO change exception
				throw new GenericException(e.getMessage());
			}
			return connection;
		}
	}
	
	/**
	 * This method constructs the Map of parameters to attach with the MediaWiki Query to fetch all the pages
	 * in the specified namespace
	 * @param namespace	The namespace whose pages are requested
	 * @return Map having parameters
	 */

	public Map<String, String> getListAllPagesParamsMap(String namespace) {
		Map<String, String> queryParameterMap = new HashMap<String, String>();
		queryParameterMap.put("action", "query");
		queryParameterMap.put("list", "allpages");
		queryParameterMap.put("aplimit", "max");
		queryParameterMap.put("apnamespace", namespace);
		queryParameterMap.put("apfilterredir", "nonredirects");
		queryParameterMap.put("format", "json");
		return queryParameterMap;
	}

	/**
	 * This method constructs the MAP of parameters to attach with the MediaWiki Query to fetch all the revisions
	 * of the given page
	 * @param pid	The PageID of the page for which revisions are requested
	 * @return	Map having parameters
	 */
	public Map<String, String> getRevisionParam(int pid) {
		Map<String, String> queryParameterMap = new HashMap<String, String>();
		queryParameterMap.put("action", "query");
		queryParameterMap.put("prop", "revisions");
		queryParameterMap.put("pageids", Integer.toString(pid));
		queryParameterMap.put("rvprop", "userid|ids|timestamp|flags|size");
		queryParameterMap.put("rvlimit", "max");
		queryParameterMap.put("rvdir", "newer");
		queryParameterMap.put("format", "json");
		return queryParameterMap;
	}

	/**
	 * This method constructs the MAP of parameters to attach with the MediaWiki Query to get
	 * all the users.
	 * @return	Map having parameters
	 */
	public Map<String, String> getUserParam() {
		Map<String, String> queryParameterMap = new HashMap<String, String>();
		queryParameterMap.put("action", "query");
		queryParameterMap.put("list", "allusers");
		queryParameterMap.put("aulimit", "max");
		queryParameterMap.put("format", "json");
		return queryParameterMap;
	}

	/**
	 * This method constructs the MAP of parameters to attach to the Mediawiki Query to get
	 * all the recent changes in one namespace between two dates.
	 * @param namespace namespace to user
	 * @param begin start of the recentchanges
	 * @param end end of the changes
	 * @return the map with the parameters
	 */
	public Map<String, String> getRecentChangesParam(String namespace, Date begin, Date end){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Map<String, String> queryParameterMap = new HashMap<>();
		queryParameterMap.put("action", "query");
		queryParameterMap.put("list", "recentchanges");
		queryParameterMap.put("rclimit", "max");
		queryParameterMap.put("format", "json");
		queryParameterMap.put("rcnamespace", namespace);
		queryParameterMap.put("rcshow", "!bot|!redirect");
		queryParameterMap.put("rcprop", "title|userid|timestamp|ids|sizes|flags");
		queryParameterMap.put("rctype", "new|edit");
		queryParameterMap.put("rcstart", dateFormat.format(begin));
		queryParameterMap.put("rcend", dateFormat.format(end));
		queryParameterMap.put("rcdir", "newer");
		return queryParameterMap;
	}

	/**
	 * This method constructs the MAP of parameters to attach to the Mediawiki Query to get
	 * all the log entries between two dates. We need the logs about moved pages, new users, deleted pages.
	 * @param logtype Type of log to fetch: newusers|delete|move
	 * @param begin start of the recentchanges
	 * @param end end of the changes
	 * @return the map with the parameters
	 */
	public Map<String, String> getLogEventsParam(String logtype, Date begin, Date end){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Map<String, String> queryParameterMap = new HashMap<>();
		queryParameterMap.put("action", "query");
		queryParameterMap.put("list", "logevents");
		queryParameterMap.put("lelimit", "max");
		queryParameterMap.put("format", "json");
		queryParameterMap.put("lestart", dateFormat.format(begin));
		queryParameterMap.put("leend", dateFormat.format(end));
		queryParameterMap.put("ledir", "newer");
		return queryParameterMap;
	}
	
	/**
	 * This method converts an InputStream object to String
	 * @param inputStream InputStream object to be converted
	 * @return String result the converted stream into a string
	 */
	public JSONObject streamToJson(InputStream inputStream) {

		String result = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			LOG.error("Unsupported enconding. {}", e1.getMessage());
		}

		StringBuilder builder = new StringBuilder();
		String line;
		try {
			
			if(reader!=null){
				
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		}
			result = builder.toString();
			inputStream.close();
		} catch (IOException e) {

			LOG.error("An error occurs while reading the inputStream. {}", e.getMessage());
		}
		
		JSONObject jsonResponse = null;
		try {
			jsonResponse = new JSONObject(result);
		} catch (JSONException e) {
			LOG.error("An error occurs while converting string to JSONObject. {}", e.getMessage());
		}
		
		return jsonResponse;
	}
	
	/**
	 * This method sends a request to MediaWiki API and then gets back an InputStream
	 * @param connection ApiConnection The ApiConnection object
	 * @param requestMethod RequestMethod The request method (ex: GET, POST, ...)
	 * @param queryParametersMap Map<String, String> The HashMap having all the query parameters
	 * @return response InputStream The result data
	 */

	public InputStream sendRequest(ApiConnection connection, String requestMethod, Map<String, String> queryParametersMap) {
		InputStream response = null;
		try {
			response = connection.sendRequest(requestMethod, queryParametersMap);
		} catch (IOException e) {

			LOG.error("Failed to send a request to MediaWiki API. {}", e.getMessage());
		}

		return response;
	}
}
