/**
 * 
 */
package org.wikitolearn.wikirating.service.mediawiki;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.wikitolearn.wikirating.util.MediaWikiApiUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author aletundo
 * @param <T>
 */
public abstract class MediaWikiService<T> {
	
	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Autowired
	protected MediaWikiApiUtils mediaWikiApiUtils;
	@Autowired
	protected ObjectMapper mapper;
	@Value("${mediawiki.namespace}")
	protected String namespace;
	
	/**
	 * This method is an utility. It concatenates the given JSONArrays into one. 
	 * @param arrays the arrays to be concatenated
	 * @return result the resulted JSONArray 
	 * @throws JSONException
	 */
	protected JSONArray concatArrays(List<JSONArray> arrays) throws JSONException{
		JSONArray result = new JSONArray();
	    for (JSONArray arr : arrays) {
	        for (int i = 0; i < arr.length(); i++) {
	            result.put(arr.get(i));
	        }
	    }
	    return result;
	} 

}
