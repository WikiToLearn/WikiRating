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

@Path("display")
public class PageInfo {

	@GET
	@Path("pageRating")
	@JSONP(queryParam = "callback")
	@Produces({ "application/x-javascript" })
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
		
		//{\"pageTitle\":ccc,\"currentPageRating\":xxc,\"maxPageRating\":vvv}
		// String sJson="{\"data\":{\"id\":1}}";
		// String sJson="{\"id\":1}";
		//String sJson = "{\"PageName\":78,\"Ratings\":2.02}";
		String sJson="{\"pageTitle\":\""+pageTitle+"\",\"currentPageRating\":"+currentPageRating+",\"maxPageRating\":"+maxPageRating+",\"badgeNumber\":"+badgeNumber+"}";
		//String sJson="{\"pageTitle\":22,\"currentPageRating\":12.0,\"maxPageRating\":20.4}";
		
		String result = callback + "(" + sJson + ");";
		return result;

	}

}
