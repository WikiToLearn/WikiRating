package main.java.computations;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.json.JSONObject;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import main.java.utilities.Connections;

@Path("credit")
@Produces("application/json")
public class CreditSystem {
	
	@GET
	@Path("users")
	@Produces("application/json")
	public static String generateCredits(@QueryParam("name") List<String> pageList,@QueryParam("join") boolean join){
		OrientGraph graph = Connections.getInstance().getDbGraph();
		
		if(!join){
			
			Vertex pageNode=null;
			for(int i=0;i<pageList.size();i++){
				pageNode=graph.getVertices("title",pageList.get(i)).iterator().next();
				HashMap<String,Integer> userContributions=getUserContributions(graph,pageNode);
				
				
			}
			
			
			graph.shutdown();
			return "Success";
			
		}
		
		
		else{
			
			Vertex pageNode=null;
			int contributionSize=0;
			String username="";
			HashMap<String,Integer> totalUserContributions=new HashMap<String,Integer>();
			for(int i=0;i<pageList.size();i++){
			
				pageNode=graph.getVertices("title",pageList.get(i)).iterator().next();
				HashMap<String,Integer> userContributions=getUserContributions(graph,pageNode);
				
				Iterator it = userContributions.entrySet().iterator();
				while (it.hasNext()) {
					
					Map.Entry pair = (Map.Entry) it.next();
					
					username=(String)pair.getKey();
					contributionSize=(int)pair.getValue();
					
					if(totalUserContributions.containsKey(username)){
						totalUserContributions.put(username, totalUserContributions.get(username)+contributionSize);
					}
					else{
						totalUserContributions.put(username, contributionSize);
					}
						
				}
			}
			
			System.out.println("===Printing bulk contributions===");
			Iterator it = totalUserContributions.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				System.out.println(pair.getKey()+"   "+pair.getValue());
			}
			
			
			graph.shutdown();
			return "Success";
			
	}

}
	
	/**
	 * @param graph
	 * @param userContributions
	 * @param pageNode
	 */
	public static HashMap<String,Integer> getUserContributions(OrientGraph graph,Vertex pageNode){
		Vertex revisionNode=null,userNode=null;
		revisionNode=pageNode.getEdges(Direction.OUT, "@class", "PreviousVersionOfPage").iterator().next().getVertex(Direction.IN);
		Edge contribute=null;
		int contributionSize=0;
		String username="";
		HashMap<String,Integer> userContributions=new HashMap<String,Integer>();
		try{
		while(true){
			//System.out.println("Hello");
			if(revisionNode.getEdges(Direction.IN, "@class", "Contribute").iterator().hasNext()){
				//System.out.println("insider");
			contribute=revisionNode.getEdges(Direction.IN, "@class", "Contribute").iterator().next();
			userNode=contribute.getVertex(Direction.OUT);
			 
			 //Get the required bytes and the corresponding username
			contributionSize=contribute.getProperty("contributionSize");
			username=userNode.getProperty("username"); //Putting the data into the HashMap
			 
			 if(userContributions.containsKey(username)){
			 
				 userContributions.put(username, userContributions.get(username)+contributionSize);
			 
			 }
			 else{
			
				 userContributions.put(username, contributionSize);
			 
			 }
		}
	 
			 if((int)revisionNode.getProperty("parentid")==0)
				 break;
			 
			revisionNode=graph.getVertices("revid", (int)revisionNode.getProperty("parentid")).iterator().next();
			
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println(pageNode.getProperty("title"));
		Iterator it = userContributions.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(pair.getKey()+"   "+pair.getValue());
		}
		
		return userContributions;
	}
}
