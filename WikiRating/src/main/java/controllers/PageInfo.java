package main.java.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.glassfish.jersey.server.JSONP;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import main.java.utilities.Connections;
import main.java.utilities.PropertiesAccess;
/**
 * This class deals with the basic info requested by the platform's pages
 */
@Path("display")
public class PageInfo {

	@GET
	@Path("pageRating")
	@JSONP(queryParam = "callback")
	@Produces({ "application/x-javascript" })
	/**
	 * This method returns all the parameters requested by the Wiki page during the page load.
	 * @param callback The function name for bypassing SOP
	 * @param pageTitle Page title for which the information is being requested
	 * @return	JSON string consisting of requesting parameters.
	 */
	public String getAllTestData(@QueryParam("callback") String callback, @QueryParam("pageTitle") String pageTitle) {

		double currentPageRating=0,maxPageRating=0;
		int badgeNumber=0;
		OrientGraph graph = Connections.getInstance().getDbGraph();
		try{
		Vertex currentPage=graph.getVertices("title",pageTitle).iterator().next();
		currentPageRating=currentPage.getProperty("PageRating");
		maxPageRating=PropertiesAccess.getParameter("maxRating");
		badgeNumber=currentPage.getProperty("badgeNumber");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		graph.shutdown();
		System.out.println(pageTitle);
		System.out.println(currentPageRating);
		System.out.println(maxPageRating);
		System.out.println(badgeNumber);
	
		String sJson="{\"pageTitle\":\""+pageTitle+"\",\"currentPageRating\":"+currentPageRating+",\"maxPageRating\":"+maxPageRating+",\"badgeNumber\":"+badgeNumber+"}";
		
		
		String result = callback + "(" + sJson + ");";
		return result;

	}

}
