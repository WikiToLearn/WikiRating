package main.java.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.orientechnologies.orient.core.Orient;
import main.java.utilities.Loggings;
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
import main.java.utilities.Loggings;

/**
 * This class will be used to initialize the engine.
 *
 */
@Path("firstRun")
public class Firstrun {
	static Class className=Firstrun.class;

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
		Loggings.getLogs(className).info("==================Classes creation over=====================");

		Page.insertPages();
		Loggings.getLogs(className).info("==================Page insertion over=====================");

		LinkPages.linkAll("@class","Page");
		Loggings.getLogs(className).info("==================Page linking over=====================");


		User.insertAllUsers();
		Loggings.getLogs(className).info("==================All Users inserted=====================");

		Revision.getAllRevisions("@class","Page");
		Loggings.getLogs(className).info("==================Page Revisions over=====================");

		Pagerank.pageRankCompute();
		Loggings.getLogs(className).info("==================Page rank over=====================");

		LinkUserContributions.linkAll();
		Loggings.getLogs(className).info("==================All Users Linked=====================");

		RandomVoteGenerator.generateVotes();
		Loggings.getLogs(className).info("==================All Versions voted=====================");

		NormalisedVotes.calculatePageVotes();
		Loggings.getLogs(className).info("==================All Page Votes computed=====================");

		UserCredibility.getUserCredibility();
		Loggings.getLogs(className).info("==================User Credibility computed=====================");

		Reliability.calculateReliability();
		Loggings.getLogs(className).info("==================Vote Reliability computed=====================");


		PageRating.computePageRatings();
		Loggings.getLogs(className).info("==================Page Ratings computed=====================");

		new BadgeGenerator().generateBadges();
		Loggings.getLogs(className).info("==================Badges given=====================");

		//Orient.instance().shutdown();

		long estimatedTime = System.currentTimeMillis() - startTime;
		estimatedTime = estimatedTime / 60000;

		return Response.status(200).entity("Successful and took" + estimatedTime + "Minutes").build();
	}

}
