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

@Path("stats")

public class DisplayStatistics {

	@GET
	@Path("display")
	@JSONP(queryParam = "callback")
	@Produces({ "application/x-javascript" })
	public String getAllStats(@QueryParam("callback") String callback, @QueryParam("pageTitle") String pageTitle) {
		int nameSpace,badgeNumber;long totalVotes=0;
		double pageRank,currentPageVote,pageReliability,maxPageReliability,maxPageRank;
		OrientGraph graph = Connections.getInstance().getDbGraph();
		Vertex pageNode=graph.getVertices("title",pageTitle).iterator().next();
		
		nameSpace=pageNode.getProperty("namespace");
		badgeNumber=pageNode.getProperty("badgeNumber");
		pageRank=pageNode.getProperty("Pagerank");
		totalVotes=pageNode.getProperty("totalVotes");
		currentPageVote=pageNode.getProperty("currentPageVote");
		pageReliability=pageNode.getProperty("currentPageReliability");
		maxPageReliability=PropertiesAccess.getParameter("maxPageReliability");
		maxPageRank=PropertiesAccess.getParameter("maxPageRank");
		
		graph.shutdown();
		System.out.println(nameSpace+" "+badgeNumber+" "+pageRank+" "+totalVotes+" "+currentPageVote+" "+pageReliability+" "+maxPageReliability+" "+maxPageRank);
		
		//String sJson="{\"pageTitle\":\"dd\",\"currentPageRating\":2,\"maxPageRating\":55,\"badgeNumber\":4}";
		
		String sJson="{\"nameSpace\":"+nameSpace+",\"badgeNumber\":"+badgeNumber+",\"pageRank\":"+pageRank+",\"totalVotes\":"+totalVotes+",\"currentPageVote\":"+currentPageVote+",\"pageReliability\":"+pageReliability+",\"maxPageReliability\":"+maxPageReliability+",\"maxPageRank\":"+maxPageRank+"}";
		String result = callback + "(" + sJson + ");";
		return result;

	}
}
