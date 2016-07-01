package main.java.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.utilities.Connections;



/**This class contains various utilities methods for the other classes
 * 
 */

public class WikiUtil {


	/**
	 * This method converts an InputStream object to String
	 * @param in	InputStream object to be converted
	 * @return Converted String
	 */
	public static String streamToString(InputStream in) {

		String result = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {

			e1.printStackTrace();
		}

		StringBuilder builder = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			result = builder.toString();
			in.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return result;
	}

	/**
	 * This method constructs the MAP of parameters to attach with the MediaWiki Query to fetch all the pages
	 * residing in all the namespaces
	 * @param ns	The namespace whose pages are requested
	 * @return	Map having parameters
	 */
	
	public static Map<String, String> getPageParam(String ns) {
		Map<String, String> mm = new HashMap<String, String>();
		mm.put("action", "query");
		mm.put("list", "allpages");
		mm.put("apfrom", "a");
		mm.put("aplimit", "max");
		mm.put("apnamespace", ns);
		mm.put("format", "json");
		return mm;
	}

	/**
	 * This method constructs the MAP of parameters to attach with the MediaWiki Query to fetch all the revisions
	 * of the given page
	 * @param pid	The PageID of the page for which revisions are requested
	 * @return	Map having parameters
	 */
	public static Map<String, String> getRevisionParam(String pid) {
		Map<String, String> mm = new HashMap<String, String>();
		mm.put("action", "query");
		mm.put("prop", "revisions");
		mm.put("pageids", pid);
		mm.put("rvprop", "userid|ids|timestamp|user|flags|size");
		mm.put("rvlimit", "max");
		mm.put("rvdir", "newer");
		mm.put("format", "json");
		return mm;
	}

	/**
	 * This method constructs the MAP of parameters to attach with the MediaWiki Query to get
	 * all the backlinks for the specified page
	 * @param pid	The PageID of the page for which backlinks are requested
	 * @return	Map having parameters
	 */
	
	public static Map<String, String> getLinkParam(String pid) {
		Map<String, String> mm = new HashMap<String, String>();
		mm.put("action", "query");
		mm.put("list", "backlinks");
		mm.put("blpageid", pid);
		mm.put("blfilterredir", "all");
		mm.put("bllimit", "max");
		mm.put("format", "json");
		return mm;
	}


	/**
	 * This method constructs the MAP of parameters to attach with the MediaWiki Query to get
	 * all the users
	 * @param username	username to continue from in case the results are more than 500
	 * @return	Map having parameters
	 */
	public static Map<String, String> getUserParam(String username) {
		Map<String, String> mm = new HashMap<String, String>();
		mm.put("action", "query");
		mm.put("list", "allusers");
		mm.put("aulimit", "max");
		mm.put("aufrom", username);
		mm.put("rawcontinue", "");
		mm.put("format", "json");
		return mm;
	}


	/**
	 * This method constructs the MAP of parameters to attach with the MediaWiki Query to get
	 * all the contibutions by the specified User
	 * @param username Username for whom the contributions have to be fetched
	 * @return	Map having parameters
	 */
	public static Map<String, String> getUserContriParam(String username) {
		Map<String, String> mm = new HashMap<String, String>();
		mm.put("action", "query");
		mm.put("list", "usercontribs");
		mm.put("uclimit", "max");
		mm.put("ucdir", "newer");
		mm.put("ucuser", username);
		mm.put("ucshow", "!minor");
		mm.put("ucprop", "sizediff|title|ids|flags");
		mm.put("format", "json");
		return mm;
	}

	/**
	 * This method sends a POST request to MediaWiki API and then gets back an InputStream
	 * @param con	The ApiConnection object
	 * @param mm	The Map having all the query parameters
	 * @return	InputStream object having the requested data
	 */

	public static InputStream reqSend(ApiConnection con, Map<String, String> mm) {
		InputStream in = null;
		try {
			in = con.sendRequest("POST", mm);
		} catch (IOException e) {

			e.printStackTrace();
		}

		return in;
	}

	/**
	 * This method will check for the duplicate entities in the database
	 * @param key	The name of the class for which redundancy needs to be checked
	 * @param value	The value to be checked
	 * @param graph	OrientGraph object
	 * @return	true or false depending on whether entity is absent or present respectively
	 */
	public static boolean rCheck(String key, int value, OrientGraph graph) {

		Iterable<Vertex> vv = graph.getVertices(key, value);
		Iterator it = vv.iterator();

		if (it.hasNext()) {
			return false;
		} else
			return true;

	}

	

	
	/**
	 * This method prints all the pages
	 * @return A formatted string containing all the Page names
	 */
	public static String printVertex() {
		String result = "";
		OrientGraph graph = Connections.getInstance().getDbGraph();
		for (Vertex v : graph.getVertices("@class", "Page")) {
			result = result + " \n" + v.getProperty("title");
		}

		return result;
	}

}