package test.java;
/**
 * This class is responsible for returning the connection objects from MediaWiki API and Database.
 * This is a singleton class to restrict the creation of objects to a single object.
 */
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

public class Connections {
	
	 private static Connections singletonConnection = new Connections( );
	 private Connections(){ };
	 
	 //This method will return an the sole object already created.
	 public static Connections getInstance( ) {
	      return singletonConnection;
	   }
	
	//This method will return the MediaWWiki Connection object.
	public ApiConnection getApiConnection(){
		ApiConnection con=new ApiConnection(Propaccess.getPropaccess("API_URL"));
		return con;
	}
	
	//This method will return the OreintDB graph instance after connection.
	public OrientGraph getDbGraph(){
	
	OrientGraphFactory factory = new OrientGraphFactory(Propaccess.getPropaccess("DB_URL"),Propaccess.getPropaccess("USER"),Propaccess.getPropaccess("PASSWD"));
	OrientGraph graph = factory.getTx();
	
	 return graph;
	}
	
}
	
