package main.java.computations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.json.JSONArray;
import org.json.JSONObject;

import com.orientechnologies.orient.core.Orient;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.utilities.Connections;


/**
 * This class is used to return User Contributions for the passed pages collectively or individually.
 * Only major contributions done by registered users are considered.
 */
@Path("credit")
@Produces("application/json")
public class CreditSystem {
	
	@GET
	@Path("users")
	@Produces("application/json")
	
	/**
	 * This is the chief method that will return a JSON string having all the contributions
	 * @param pageList	List having all the Page Titles
	 * @param join	If set true collective user contributions for all the pages will be returned
	 * @return	JSON string having all the contributions and current version sizes.
	 */
	public static String generateCredits(@QueryParam("titles") List<String> pageList,@QueryParam("join") boolean join){
		
		OrientGraph graph = Connections.getInstance().getDbGraph();
		
		
		if(!join){
			//This part will be executed when individual contributions are asked for
			
			Vertex pageNode=null;
			
			JSONArray responseJson=new JSONArray();
			
			for(int i=0;i<pageList.size();i++){
				
				pageNode=graph.getVertices("title",pageList.get(i)).iterator().next();
				HashMap<String,Integer> userContributions=getUserContributions(graph,pageNode);
				
				JSONArray authorList=new JSONArray();
				JSONObject pageNodeObject=new JSONObject();
				
				Iterator it = userContributions.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					JSONObject authorNode=new JSONObject();
					authorNode.put("username", (String)pair.getKey());
					authorNode.put("bytes_changed", (int)pair.getValue());
					authorList.put(authorNode);
					
				}
				
				
				pageNodeObject.put("title",pageList.get(i) );
				pageNodeObject.put("authors", authorList);
				pageNodeObject.put("bytes", pageNode.getEdges(Direction.OUT, "@class", "PreviousVersionOfPage").iterator().next().getVertex(Direction.IN).getProperty("size"));
				
				responseJson.put(pageNodeObject);
				
			}
			
			JSONObject creditObject=new JSONObject();
			creditObject.put("credits", responseJson);
			graph.shutdown();
			return creditObject.toString();
			
		}
		
		
		else{
			//This part will be executed when collective contributions are asked for
			
			Vertex pageNode=null;
			int contributionSize=0;int totalBytes=0;
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
				totalBytes+=(int)pageNode.getEdges(Direction.OUT, "@class", "PreviousVersionOfPage").iterator().next().getVertex(Direction.IN).getProperty("size");
			}
			
			JSONArray authorList=new JSONArray();
			Iterator it = totalUserContributions.entrySet().iterator();

			while (it.hasNext()) {
				
				Map.Entry pair = (Map.Entry) it.next();
				JSONObject authorNode=new JSONObject();
				authorNode.put("username", (String)pair.getKey());
				authorNode.put("bytes_changed", (int)pair.getValue());
				authorList.put(authorNode);
				
			}
			
			JSONObject authorListObject=new JSONObject();
			authorListObject.put("totalBytes", totalBytes);
			authorListObject.put("authors", authorList);
						
			graph.shutdown();
			
			return authorListObject.toString();
			
	}
		
		
}
	
	/**
	 * This method returns a HashMap filled with the user contributions of the passed pageNode
	 * @param graph	OrientGraph
	 * @param pageNode	The Page Node of whose contribution you want
	 * @return	HashMap having username as key and contributions as corresponding value
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
			
			if(revisionNode.getEdges(Direction.IN, "@class", "Contribute").iterator().hasNext()){
				
			contribute=revisionNode.getEdges(Direction.IN, "@class", "Contribute").iterator().next();
			userNode=contribute.getVertex(Direction.OUT);
			 
			 //Get the required bytes and the corresponding username
			contributionSize=contribute.getProperty("contributionSize");
			username=userNode.getProperty("username"); 
			 
			//Putting the data into the HashMap
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
