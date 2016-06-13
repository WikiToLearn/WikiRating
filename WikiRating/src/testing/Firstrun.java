package testing;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import resources.Propaccess;

//This class will add all the pages with their revisions
@Path("do")
public class Firstrun {

	  @GET
	  @Produces("application/json")
	  public Response pCompute() {
		  long startTime = System.currentTimeMillis();
		// ... do something ... 
		
		  	//String result1="Nothing to show here!";
	 		String result1=Page.insertPages();
		  	//String result1=Dataret.printVertex();
	 		//result1=Dataret.printVertex();
		  	//String result1=WikiUtil.testInsert("","");
		  	 result1=Revision.getAllRevisions();
		  	long estimatedTime = System.currentTimeMillis() - startTime;
		  	estimatedTime=estimatedTime/1000;
		  	result1=result1+" "+estimatedTime;
			return Response.status(200).entity(result1).build();	 
		  }
	
}
