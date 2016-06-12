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
	
	
	public ApiConnection getApiConnection(){
		ApiConnection comm=new ApiConnection(Propaccess.getPropaccess("API_URL"));
		return comm;
	}
	
	public OrientGraph getDbGraph(){
	
	OrientGraphFactory factory = new OrientGraphFactory(Propaccess.getPropaccess("DB_URL"),Propaccess.getPropaccess("USER"),Propaccess.getPropaccess("PASSWD"));
	OrientGraph graph = factory.getTx();
	
	 return graph;
	}
	
}
	
