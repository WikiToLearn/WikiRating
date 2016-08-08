package main.java.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import main.java.utilities.Connections;
import main.java.utilities.Loggings;

/**
 * This class will wipe the database clean
 *
 */


@Path("wipe")
public class Wipe {
	static Class className=Wipe.class;
	@GET
	  @Produces("application/json")

	/**
	 * This method will delete all the added classes,Vertices,Edges from the database
	 * @return	Response object showing time taken to run the computation
	 */
	public Response wipeDatabase() {
		  long startTime = System.currentTimeMillis();
		  OrientGraphNoTx graph = Connections.getInstance().getDbGraphNT();

		  try{
			  graph.dropVertexType("Page");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  Loggings.getLogs(className).info("=============Page deleted=========");

		  try{
			  graph.dropVertexType("Revision");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  Loggings.getLogs(className).info("=============Revision deleted=========");
		  try{
			  graph.dropVertexType("User");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  Loggings.getLogs(className).info("=============User deleted=========");
		  try{
			  graph.dropEdgeType("Backlink");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  Loggings.getLogs(className).info("=============Backlink deleted=========");
		  try{
			  graph.dropEdgeType("PreviousRevision");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  Loggings.getLogs(className).info("=============PreviousRevision deleted=========");
		  try{
			  graph.dropEdgeType("PreviousVersionOfPage");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  Loggings.getLogs(className).info("=============PreviousVersionOfPage deleted=========");

		  try{
			  graph.dropEdgeType("Contribute");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  Loggings.getLogs(className).info("=============Contribute deleted=========");
		  try{
			  graph.dropEdgeType("Review");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  Loggings.getLogs(className).info("=============Review deleted=========");
		  //graph.commit();
		  graph.shutdown();

		  long estimatedTime = System.currentTimeMillis() - startTime;
		  estimatedTime=estimatedTime/60000;

			return Response.status(200).entity("Successful and took"+estimatedTime+"Minutes").build();
		  }

}
