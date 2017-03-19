/**
 * 
 */
package org.wikitolearn.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;

/**
 * @author aletundo, valsdav
 *
 */
@Component
public class MediaWikiApiUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(MediaWikiApiUtils.class);
	
	 /**
	 * This method creates a MediaWiki Connection object
	 * @param apiUrl the url of the API
	 * @return	ApiConnection the API connection object
	 */

	public ApiConnection getApiConnection(String apiUrl) {
		ApiConnection connection = new ApiConnection(apiUrl);
		return connection;
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
			LOG.error("Unsupported enconding.", e1.getMessage());
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

			LOG.error("An error occurs while reading the inputStraem", e.getMessage());
		}
		
		JSONObject jsonResponse = null;
		try {
			jsonResponse = new JSONObject(result);
		} catch (JSONException e) {
			LOG.error("An error occurs while converting string to JSONObject", e.getMessage());
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

			LOG.error("Failed to send a request to MediaWiki API", e.getMessage());
		}

		return response;
	}
}
