package main.java.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import main.java.utilities.Loggings;
import main.java.computations.BadgeGenerator;
import main.java.computations.NormalisedVotes;
import main.java.computations.PageRating;
import main.java.computations.Pagerank;
import main.java.computations.Reliability;
import main.java.computations.UserCredibility;
import main.java.models.AddNewPages;
import main.java.models.User;
import main.java.utilities.PropertiesAccess;

/**
 * This class will be used for detecting and storing any new changes like
 * Page addition, Page modification and User Addition to the platform.
 */

@Path("secondRun")


public class Secondrun {
	static Class className=Secondrun.class;

@GET
@Produces("application/json")

	/**
	 * This method will call suitable methods to insert all the new users and
	 * update the database for any new changes
	 * @return	Response object showing time taken to run the computation
	 */
	public Response pCompute() {
		long startTime = System.currentTimeMillis();

		/*Now we will check for new pages and add revisions to them too.
		 *Make links to the user contributions too
		 *Calculate the user votes and then calculate the recursive votes too.
		 *We will calculate the backlinks too
		 *Drop backlinks and then create the new ones again
		 *Calculate the votes
		 *Calculate the user reliability
		 */

		User.insertAllUsers();
		Loggings.getLogs(className).info("==================Checked for new User's insertion=====================");

		AddNewPages.checkForPages();
		Loggings.getLogs(className).info("==================Checked for any new pages,revisions and linked the user contributions and made backlinks=====================");

		NormalisedVotes.calculatePageVotes();
		Loggings.getLogs(className).info("==================Calculated new page votes=====================");

		Reliability.calculateReliability();
		Loggings.getLogs(className).info("==================Calculated new reliabilities=====================");

		Pagerank.pageRankCompute();
		Loggings.getLogs(className).info("==================Page rank over=====================");

		UserCredibility.getUserCredibility();
		Loggings.getLogs(className).info("==================User Credibility computed=====================");

		PageRating.computePageRatings();
		Loggings.getLogs(className).info("==================Page Ratings computed=====================");

		new BadgeGenerator().generateBadges();
		Loggings.getLogs(className).info("==================Badges given=====================");


		long estimatedTime = System.currentTimeMillis() - startTime;
		estimatedTime = estimatedTime / 60000;

		return Response.status(200).entity("Successful and took" + estimatedTime + "Minutes").build();
	}

}
