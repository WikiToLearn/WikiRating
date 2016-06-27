package main.java;

/**This is the chief class will call all other methods
 * 
 */
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import main.java.compute.LinkPages;
import main.java.compute.LinkUserContributions;
import main.java.compute.NormalisedVotes;
import main.java.compute.PageRating;
import main.java.compute.Pagerank;
import main.java.compute.RandomVoteGenerator;
import main.java.compute.Reliability;
import main.java.compute.UserCredibility;
import main.java.fetch.*;

@Path("do")
public class Firstrun {

	@GET
	@Produces("application/json")
	// Warning: If you add revisions before the Users, only those users who have
	// not contributed to Wiki will be added.
	// However this behaviour can be inverted tool
	public Response pCompute() {
		long startTime = System.currentTimeMillis();
		
		Page.insertPages();
		System.out.println("==================Page insertion over=====================");
		
		LinkPages.linkAll("@class","Page");
		System.out.println("==================Page linking over=====================");
		
		User.insertAllUsers();
		System.out.println("==================All Users inserted=====================");
		
		Revision.getAllRevisions("@class","Page");
		System.out.println("==================Page Revisions over=====================");
		
		Pagerank.pageRankCompute();
		System.out.println("==================Page rank over=====================");
		
		LinkUserContributions.linkAll();
		System.out.println("==================All Users Linked=====================");
		
		RandomVoteGenerator.generateVotes();
		System.out.println("==================All Versions voted=====================");
		
		NormalisedVotes.calculatePageVotes();
		System.out.println("==================All Page Votes computed=====================");
		
		UserCredibility.getUserCredibility();
		System.out.println("==================User Credibility computed=====================");
		
		Reliability.calculateReliability();
		System.out.println("==================Vote Reliability computed=====================");
		
		PageRating.computePageRatings();
		System.out.println("==================Page Ratings computed=====================");
		//EdgesTest.testMe();
		//Contribution.getPageEdits();
		
		// String result1="Nothing to show here!";
		// String result1=Page.insertPages();
		// String result1=Dataret.printVertex();
		// result1=Dataret.printVertex();
		// String result1=WikiUtil.testInsert("","");
		/*
		 * result1=Revision.getAllRevisions(); result1=result1+" "
		 * +estimatedTime;
		 */
		// LinkPages.linkAll();
		long estimatedTime = System.currentTimeMillis() - startTime;
		estimatedTime = estimatedTime / 60000;

		return Response.status(200).entity("Successful and took" + estimatedTime + "Minutes").build();
	}

}
