package main.java.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.orientechnologies.orient.core.Orient;

import main.java.computations.BadgeGenerator;
import main.java.computations.NormalisedVotes;
import main.java.computations.PageRating;
import main.java.computations.Pagerank;
import main.java.computations.RandomVoteGenerator;
import main.java.computations.Reliability;
import main.java.computations.UserCredibility;
import main.java.models.InitialiseDB;
import main.java.models.LinkPages;
import main.java.models.LinkUserContributions;
import main.java.models.Page;
import main.java.models.Revision;
import main.java.models.User;


/**
 * This class will be used to initialize the engine. 
 * 
 */
@Path("firstRun")
public class Firstrun {
	

	@GET
	@Produces("application/json")
	
	// Warning: If you add revisions before the Users, only those users who have
	// not contributed to Wiki will be added.
	// However this behaviour can be inverted too
	
	/**
	 * This method will call different other methods that will initialize the engine
	 * @return Response object showing time taken to run the computation
	 */
	public Response pCompute() {
		long startTime = System.currentTimeMillis();
		
		InitialiseDB.createClass();
		System.out.println("==================Classes creation over=====================");
		
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
		
		new BadgeGenerator().generateBadges();
		System.out.println("==================Badges given=====================");
		
		Orient.instance().shutdown();
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		estimatedTime = estimatedTime / 60000;

		return Response.status(200).entity("Successful and took" + estimatedTime + "Minutes").build();
	}

}
