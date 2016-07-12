package main.java.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.JSONP;


@Path("employee")
public class PageInfo {

	@GET
    @Path("search1")
	@Produces("application/json")
	
    public Response getEmployeeDetails(@QueryParam("param1") int employeeId,@QueryParam("param2") String employeeName)
               {
					System.out.println(employeeName+"  is a good person having an ID of "+employeeId);
					
					String m=employeeName+"  is a good person having an ID of "+employeeId;
					

					return Response.status(200).entity(m).build();
					
               }

	@GET  
	@Path("search")
	@JSONP(queryParam="callback")
	@Produces({"application/x-javascript"})
	public String getAllTestData(@QueryParam("callback") String callback,@QueryParam("param1") int PageId,@QueryParam("param2") String PageName) {  
	        

	        System.out.println("I got executed!");
	        System.out.println(callback);
	        System.out.println(PageId);
	        System.out.println(PageName);
	        //String sJson="{\"data\":{\"id\":1}}";
	        //String sJson="{\"id\":1}";
	        String sJson="{\"PageName\":78,\"Ratings\":1.0}";
	        String result=callback+"("+sJson+");";
		//return Response.status(200).entity("{ foo: 'bar' }").build();
	        return result;
	        //return Response.ok("mycallback({status: 'success'})").header("Access-Control-Allow-Origin", true).build();
	    }
	
}
