package main.java.computations;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.glassfish.jersey.server.JSONP;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import main.java.utilities.Connections;

@Path("votePage")
public class UserVoteFetch {

	@GET
	@Path("userVote")
	@JSONP(queryParam = "callback")
	@Produces({ "application/x-javascript" })
	public String getAllTestData(@QueryParam("callback") String callback, @QueryParam("pageTitle") String pageTitle,@QueryParam("userName") String userName,@QueryParam("userVote") int userVote) {
		
		try{
		OrientGraph graph = Connections.getInstance().getDbGraph();
		Vertex userNode=graph.getVertices("username",userName).iterator().next();
		Vertex pageNode=graph.getVertices("title",pageTitle).iterator().next();
		Vertex revisionNode = pageNode.getEdges(Direction.OUT, "@class", "PreviousVersionOfPage").iterator().next().getVertex(Direction.IN);
		
		if(IsNotDuplicateVote(userNode, revisionNode)){
		
		
		Edge review = graph.addEdge("review", userNode, revisionNode, "Review");
		review.setProperty("vote", userVote/10.0);
		review.setProperty("voteCredibility",userNode.getProperty("credibility"));
		graph.commit();
		
		System.out.println(pageNode.getProperty("title"));
		System.out.println(userNode.getProperty("username"));
		System.out.println(userVote);
		System.out.println("New Vote added successfully");
		
		}
		
		

		graph.shutdown();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		String sJson="{\"pageTitle\":\"dd\",\"currentPageRating\":2,\"maxPageRating\":55,\"badgeNumber\":4}";
		
		String result = callback + "(" + sJson + ");";
		return result;

	}

	public boolean IsNotDuplicateVote(Vertex userNode,Vertex revisionNode){
		Vertex votedRevisionNode=null;
		for(Edge votedRevisionEdge:userNode.getEdges(Direction.OUT, "@class", "Review")){
			votedRevisionNode=votedRevisionEdge.getVertex(Direction.IN);
			if(votedRevisionNode.getId()==revisionNode.getId()){
				System.out.println("Already voted");
				return false;				
				}
		}
		return true;
		
	}
	
	
}
