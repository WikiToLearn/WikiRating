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
import main.java.utilities.Loggings;
import main.java.utilities.Connections;
/**
 * This class is used to fetch the votes given by the user and add that to the database.
 * Duplicate votes aren't added but previous votes are updated during a revote
 */
@Path("votePage")
public class UserVoteFetch {
	static Class className=UserVoteFetch.class;
	@GET
	@Path("userVote")
	@JSONP(queryParam = "callback")
	@Produces({ "application/x-javascript" })
	/**
	 * This method adds the user vote to the the latest version of the page
	 * @param callback The function name for bypassing SOP
	 * @param pageTitle	The title of the page whose latest revision is being voted
	 * @param userName	The name of the user who is voting
	 * @param userVote	The value of the vote
	 * @return	A string stating the process is successful
	 */
	public String getAllTestData(@QueryParam("callback") String callback, @QueryParam("pageTitle") String pageTitle,@QueryParam("userName") String userName,@QueryParam("userVote") int userVote) {

		try{
		OrientGraph graph = Connections.getInstance().getDbGraph();
		Vertex userNode=graph.getVertices("username",userName).iterator().next();
		Vertex pageNode=graph.getVertices("title",pageTitle).iterator().next();
		Vertex revisionNode = pageNode.getEdges(Direction.OUT, "@class", "PreviousVersionOfPage").iterator().next().getVertex(Direction.IN);
		Vertex revisionNodeContributor=null;

		if(revisionNode.getEdges(Direction.IN, "@class", "Contribute").iterator().hasNext()){

			revisionNodeContributor=revisionNode.getEdges(Direction.IN, "@class", "Contribute").iterator().next().getVertex(Direction.OUT);
			//Code to prevent contributor from rating their own contributions
			if(((String)revisionNodeContributor.getProperty("username")).equals(userName)){

				Loggings.getLogs(className).info("Users can't vote their own work");
				graph.shutdown();
				String sJson="{\"pageTitle\":\"User cant vote thier own work\"}";
				String result = callback + "(" + sJson + ");";
				return result;
			}

		}

		//Removes the old vote if exists
		if(IsNotDuplicateVote(userNode, revisionNode)==false){
			Vertex votedRevisionNode=null;
			for(Edge votedRevisionEdge:userNode.getEdges(Direction.OUT, "@class", "Review")){
				votedRevisionNode=votedRevisionEdge.getVertex(Direction.IN);
				if(votedRevisionNode.getId()==revisionNode.getId()){
					Loggings.getLogs(className).info("Vote removed with value = "+votedRevisionEdge.getProperty("vote")+" having id = "+votedRevisionEdge.getProperty("@RID"));
					graph.removeEdge(votedRevisionEdge);

					}
			}

		}

		//Creates the new vote
		Edge review = graph.addEdge("review", userNode, revisionNode, "Review");
		review.setProperty("vote", userVote/10.0);
		review.setProperty("voteCredibility",userNode.getProperty("credibility"));
		graph.commit();

		Loggings.getLogs(className).info(pageNode.getProperty("title"));
		Loggings.getLogs(className).info(userNode.getProperty("username"));
		Loggings.getLogs(className).info(userVote);
		Loggings.getLogs(className).info("New Vote added successfully");





		graph.shutdown();
		}catch(Exception e){
			e.printStackTrace();
		}

		String sJson="{\"pageTitle\":\"Successful\"}";

		String result = callback + "(" + sJson + ");";
		return result;

	}

	/**
	 * This method checks whether the user already voted for the current version or not
	 * @param userNode	The user Vertex
	 * @param revisionNode The latest revision of the Page being voted
	 * @return Either true of false indicating the presence of a duplicate edge
	 */
	public boolean IsNotDuplicateVote(Vertex userNode,Vertex revisionNode){
		Vertex votedRevisionNode=null;
		for(Edge votedRevisionEdge:userNode.getEdges(Direction.OUT, "@class", "Review")){
			votedRevisionNode=votedRevisionEdge.getVertex(Direction.IN);
			if(votedRevisionNode.getId()==revisionNode.getId()){
				Loggings.getLogs(className).info("Already voted");
				return false;
				}
		}
		return true;

	}


}
