package test.java;
/**This is the chief class will call all other methods
 * 
 */
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


@Path("do")
public class Firstrun {

	  @GET
	  @Produces("application/json")
	  
	  public Response pCompute() {
		  long startTime = System.currentTimeMillis();
		  	Page.insertPages();
		  	System.out.println("==================Page insertion over=====================");
		  	LinkPages.linkAll();
		  	System.out.println("==================Page linking over=====================");
		  	Revision.getAllRevisions();
		  	System.out.println("==================Page Revisions over=====================");
		  	Pagerank.pageRankCompute();
		  	System.out.println("==================Page rank over=====================");
		  	
		  	//String result1="Nothing to show here!";
	 		//String result1=Page.insertPages();
		  	//String result1=Dataret.printVertex();
	 		//result1=Dataret.printVertex();
		  	//String result1=WikiUtil.testInsert("","");
		  	/* result1=Revision.getAllRevisions();
		  	result1=result1+" "+estimatedTime;*/
		  	//LinkPages.linkAll();
		  	long estimatedTime = System.currentTimeMillis() - startTime;
		  	estimatedTime=estimatedTime/60000;
		  	
			return Response.status(200).entity("Successful and took"+estimatedTime+"Minutes").build();	 
		  }
	
}
