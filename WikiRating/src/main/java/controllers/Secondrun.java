package main.java.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import main.java.computations.BadgeGenerator;
import main.java.computations.NormalisedVotes;
import main.java.computations.Reliability;
import main.java.models.AddNewPages;
import main.java.models.User;

/**
 * This class will be used for detecting and storing any new changes like 
 * Page addition, Page modification and User Addition to the platform.
 */

@Path("secondRun")


public class Secondrun {

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
		System.out.println("==================Checked for new User's insertion=====================");
		
		AddNewPages.checkForPages();
		System.out.println("==================Checked for any new pages,revisions and linked the user contributions and made backlinks=====================");
		
		NormalisedVotes.calculatePageVotes();
		System.out.println("==================Calculated new page votes=====================");
		
		Reliability.calculateReliability();
		System.out.println("==================Calculated new reliabilities=====================");
		
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		estimatedTime = estimatedTime / 60000;

		return Response.status(200).entity("Successful and took" + estimatedTime + "Minutes").build();
	}
	
}
