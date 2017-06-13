/**
 * 
 */
package org.wikitolearn.wikirating.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikitolearn.wikirating.util.MediaWikiApiUtils;

/**
 * @author aletundo
 *
 */
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.properties")
public class MediaWikiApiUtilsTest {
	
	@InjectMocks
	private MediaWikiApiUtils mediaWikiApiUtils;
	@Autowired
	private Config config;
	
	@Before
	public void setup(){
		ReflectionTestUtils.setField(mediaWikiApiUtils, "apiUser", this.config.apiUser);
		ReflectionTestUtils.setField(mediaWikiApiUtils, "apiPassword", this.config.apiPassword);
	}
	
	@Test
	public void testGetListAllPagesParamsMap(){
		Map<String, String> queryParameterMap = new HashMap<String, String>();
		queryParameterMap.put("action", "query");
		queryParameterMap.put("list", "allpages");
		queryParameterMap.put("aplimit", "max");
		queryParameterMap.put("apnamespace", "2800");
		queryParameterMap.put("apfilterredir", "nonredirects");
		queryParameterMap.put("format", "json");
		
		Map<String, String> result = mediaWikiApiUtils.getListAllPagesParams("2800");
		assertEquals(queryParameterMap.get("action"), result.get("action"));
		assertEquals(queryParameterMap.get("list"), result.get("list"));
		assertEquals(queryParameterMap.get("aplimit"), result.get("aplimit"));
		assertEquals(queryParameterMap.get("apnamespace"), result.get("apnamespace"));
		assertEquals(queryParameterMap.get("apfiltereedir"), result.get("apfiltereedir"));
		assertEquals(queryParameterMap.get("format"), result.get("format"));
	}
	
	@Test
	public void testGetRevisionParam(){
		Map<String, String> queryParameterMap = new HashMap<String, String>();
		queryParameterMap.put("action", "query");
		queryParameterMap.put("prop", "revisions");
		queryParameterMap.put("pageids", "1");
		queryParameterMap.put("rvprop", "userid|ids|timestamp|flags|size");
		queryParameterMap.put("rvlimit", "max");
		queryParameterMap.put("rvdir", "newer");
		queryParameterMap.put("format", "json");
		
		Map<String, String> result = mediaWikiApiUtils.getRevisionParams(1);
		assertEquals(queryParameterMap.get("action"), result.get("action"));
		assertEquals(queryParameterMap.get("prop"), result.get("prop"));
		assertEquals(queryParameterMap.get("pageids"), result.get("pageids"));
		assertEquals(queryParameterMap.get("rvprop"), result.get("rvprop"));
		assertEquals(queryParameterMap.get("rvlimit"), result.get("rvlimit"));
		assertEquals(queryParameterMap.get("rvdir"), result.get("rvdir"));
		assertEquals(queryParameterMap.get("format"), result.get("format"));
	}
	
	@Test
	public void testGetUserparam(){
		Map<String, String> queryParameterMap = new HashMap<String, String>();
		queryParameterMap.put("action", "query");
		queryParameterMap.put("list", "allusers");
		queryParameterMap.put("aulimit", "max");
		queryParameterMap.put("format", "json");
		
		Map<String, String> result = mediaWikiApiUtils.getUserParams();
		assertEquals(queryParameterMap.get("action"), result.get("action"));
		assertEquals(queryParameterMap.get("list"), result.get("list"));
		assertEquals(queryParameterMap.get("aulimit"), result.get("aulimit"));
		assertEquals(queryParameterMap.get("format"), result.get("format"));
	}
	
	@Test
	public void testGetApiConnection(){
		ApiConnection connection = mediaWikiApiUtils.getApiConnection("https://en.wikitolearn.org/api.php");
		assertTrue(connection.isLoggedIn());
	}
	
	@Test
	public void testSendRequest(){
		ApiConnection connection = mediaWikiApiUtils.getApiConnection("https://en.wikitolearn.org/api.php");
		Map<String, String> queryParameterMap = new HashMap<String, String>();
		queryParameterMap.put("action", "query");
		queryParameterMap.put("list", "allusers");
		queryParameterMap.put("aulimit", "max");
		queryParameterMap.put("format", "json");
		assertNotNull(mediaWikiApiUtils.sendRequest(connection, "GET", queryParameterMap));
	}
	
	@Test
	public void testStreamToJson(){
		String JSONString = "{\"id\" : \"1\"}";
		String JSONStringMalformed = "{\"id : \"1\"}";
		InputStream inputStream = new ByteArrayInputStream(JSONString.getBytes());
		InputStream inputStream2 = new ByteArrayInputStream(JSONStringMalformed.getBytes());
		JSONObject result = mediaWikiApiUtils.streamToJson(inputStream);
		JSONObject result2 = mediaWikiApiUtils.streamToJson(inputStream2);
		assertNotNull(result);
		assertNull(result2);
	}
	
	@Configuration
	static class Config {

		@Value("${mediawiki.api.user}")
		private String apiUser;
		@Value("${mediawiki.api.password}")
		private String apiPassword;

		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyPlaceholder() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}
}
