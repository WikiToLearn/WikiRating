package main.java.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import main.java.utilities.Connections;

@Path("wipe")
public class Wipe {
	
	@GET
	  @Produces("application/json")
	 
	public Response wipeDatabase() {
		  long startTime = System.currentTimeMillis();
		  OrientGraphNoTx graph = Connections.getInstance().getDbGraphNT();
		  
		  try{
			  graph.dropVertexType("Page");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  System.out.println("=============Page deleted=========");
		  
		  try{
			  graph.dropVertexType("Revision");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  System.out.println("=============Revision deleted=========");
		  try{
			  graph.dropVertexType("User");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  System.out.println("=============User deleted=========");
		  try{
			  graph.dropEdgeType("Backlink");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  System.out.println("=============Backlink deleted=========");
		  try{
			  graph.dropEdgeType("PreviousRevision");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  System.out.println("=============PreviousRevision deleted=========");
		  try{
			  graph.dropEdgeType("PreviousVersionOfPage");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  System.out.println("=============PreviousVersionOfPage deleted=========");
		  
		  try{
			  graph.dropEdgeType("Contribute");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  System.out.println("=============Contribute deleted=========");
		  try{
			  graph.dropEdgeType("Review");
		  }catch(Exception e){
			  e.printStackTrace();
			  }
		  System.out.println("=============Review deleted=========");
		  //graph.commit();
		  graph.shutdown();
		  
		  long estimatedTime = System.currentTimeMillis() - startTime;
		  estimatedTime=estimatedTime/60000;
		  	
			return Response.status(200).entity("Successful and took"+estimatedTime+"Minutes").build();	 
		  }

}
