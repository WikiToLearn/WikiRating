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
		
		if(join){
			Vertex pageNode=null;
			for(int i=0;i<pageList.size();i++){
				pageNode=graph.getVertices("title",pageList.get(i)).iterator().next();
				HashMap<Integer,Integer> userContributions=new HashMap<Integer,Integer>();
				getUserContributions(graph,userContributions,pageNode);
				
				
			}
			
			
			graph.shutdown();
			return "Success";
			
		}
		
		
		else{
			graph.shutdown();
			return "Success";
			
	}

}
	
	/**
	 * @param graph
	 * @param userContributions
	 * @param pageNode
	 */
	public static void getUserContributions(OrientGraph graph,HashMap<Integer,Integer> userContributions,Vertex pageNode){
		Vertex revisionNode=null,userNode=null;
		revisionNode=pageNode.getEdges(Direction.OUT, "@class", "PreviousVersionOfPage").iterator().next().getVertex(Direction.IN);
		Edge contribute=null;
		int contributionSize=0,userid=0;
		try{
		while(true){
			 
			contribute=revisionNode.getEdges(Direction.IN, "@class", "Contribute").iterator().next();
			userNode=contribute.getVertex(Direction.OUT);
			 
			 //Get the required bytes and the corresponding userid
			contributionSize=contribute.getProperty("contributionSize");
			userid=userNode.getProperty("userid"); //Putting the data into the HashMap
			 
			 if(userContributions.containsKey(userid)){
			 
				 userContributions.put(userid, userContributions.get(userid)+contributionSize);
			 
			 }
			 else{
			
				 userContributions.put(userid, contributionSize);
			 
			 }
			 
			 if((int)revisionNode.getProperty("parentid")!=0)
				 break;
			 
			revisionNode=graph.getVertices("revid", (int)revisionNode.getProperty("parentid")).iterator().next();
			
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Iterator it = userContributions.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(pair.getKey()+"   "+pair.getValue());
		}
	}
}
