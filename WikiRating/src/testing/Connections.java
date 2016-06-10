package testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import resources.Propaccess;

public class Connections {
	
	 private static Connections singletonConnection = new Connections( );
	 private Connections(){ };
	 
	 public static Connections getInstance( ) {
	      return singletonConnection;
	   }
	
	public  String getAllPages() throws JSONException {
		  
		  
		  //Accessing MediaWiki API using Wikidata Toolkit
		  
		  String result = "fail";
		  
		  ApiConnection comm=new ApiConnection(Propaccess.getPropaccess("API_URL"));
		  Map<String,String> mm=new HashMap<String, String>();
		  InputStream in = null;
		  mm.put("action", "query");
		  mm.put("list","allpages");
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
	        return result;
	}
	
	public OrientGraph getDbGraph(){
	
	OrientGraphFactory factory = new OrientGraphFactory(Propaccess.getPropaccess("DB_URL"),Propaccess.getPropaccess("USER"),Propaccess.getPropaccess("PASSWD"));
	OrientGraph graph = factory.getTx();
	
	 return graph;
	}
	
}
	
