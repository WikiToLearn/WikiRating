package main.java;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import main.java.fetch.User;
import main.java.utilities.Connections;

@Path("sec")


public class Secondrun {

@GET
@Produces("application/json")

	public Response pCompute() {
		long startTime = System.currentTimeMillis();
		//Code here
				
		User.insertAllUsers();
		System.out.println("==================Checked for new User's insertion=====================");
		
		AddNewPages.chackForPages();
		System.out.println("==================Checked for any new pages,revisions and linked the user contributions and made backlinks=====================");

		
		//Now we will check for new pages and add revisions to them too.
		//Make links to the user contributions too
		//calculate the user votes and then calculate the recursive votes too.
		
		//We will calculate the backlinks too
		
		//Drop backlinks and then create the new ones again
		
		//Calculate the votes
		//calculate the user reliability
		
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		estimatedTime = estimatedTime / 60000;

		return Response.status(200).entity("Successful and took" + estimatedTime + "Minutes").build();
	}
	
}
