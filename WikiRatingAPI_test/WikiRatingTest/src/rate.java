import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.HttpsURLConnection;
import com.orientechnologies.orient.jdbc.OrientJdbcConnection;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Path("/rate")
public class rate {
 
	  @GET
	  @Produces("application/json")
	  public Response welcome() {
 		String result = "You reached the homepage please add 'allpages' in the URL to get all the pages in https://en.wikitolearn.org";
		return Response.status(200).entity(result).build();	 
	  }
 
	  
	  @Path("{name}")
	  @GET
	  @Produces("application/json")
	  public Response allPages(@PathParam("name") String name) throws JSONException {
		  
		  
		  //Accessing MediaWiki API using Wikidata Toolkit
		  
		  String result = "fail";
		  
		  ApiConnection comm=new ApiConnection("https://en.wikitolearn.org/api.php");
		  Map<String,String> mm=new HashMap<String, String>();
		  InputStream in = null;
		  mm.put("action", "query");
		  mm.put("list",name);
		  mm.put("apfrom","a");
		  mm.put("aplimit","max");
		  mm.put("apnamespace","0");
		  mm.put("format","json");
		  
		  try {
			in= comm.sendRequest("POST", mm);
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		  
		  // Converting InputStream object to String
		  
		  BufferedReader reader=null;
		try {
			reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			
			e1.printStackTrace();
		}
			
	        StringBuilder builder = new StringBuilder();
	        String line;
	        try {
				while ((line = reader.readLine()) != null) {
				    builder.append(line);
				}
				result=builder.toString();
				in.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		  
		  
		 
		 //Connecting to database using Tinkerpop Blueprints
		 
		 OrientGraphFactory factory = new OrientGraphFactory("remote:localhost/wikirate","root","admin123");
		 OrientGraph graph = factory.getTx();
		 try {
			 
			//JSON interpretation
			 try {  
				 	JSONObject js=new JSONObject(result);
				 	JSONObject js2=js.getJSONObject("query");
				 	JSONArray arr=js2.getJSONArray("allpages");
					result="";
				 	JSONObject dummy;
				 	for(int i=0;i<arr.length();i++){
				 		dummy=arr.getJSONObject(i);
				 		result=result+dummy.get("title")+" \n";
				 					 			
				 		//Adding pages to database
				 		try{
				 			  Vertex ver = graph.addVertex(null); // 1st OPERATION: IMPLICITLY BEGINS TRANSACTION
				 			  ver.setProperty( "name", dummy.get("title") );
				 			  ver.setProperty("pid",dummy.get("pageid"));
				 			   graph.commit();
				 			} catch( Exception e ) {
				 			  graph.rollback();
				 			}
				 	
				 		
				 	}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			 
			 
		 } finally {
		   graph.shutdown();
		 }
		 
		 
		return Response.status(200).entity(result).build();
	  }
}