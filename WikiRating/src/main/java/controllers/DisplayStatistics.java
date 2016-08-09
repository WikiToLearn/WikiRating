package main.java.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.glassfish.jersey.server.JSONP;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.utilities.Loggings;
import main.java.utilities.Connections;
import main.java.utilities.PropertiesAccess;

@Path("stats")

public class DisplayStatistics {
	static Class className=DisplayStatistics.class;
	@GET
	@Path("display")
	@JSONP(queryParam = "callback")
	@Produces({ "application/x-javascript" })
	public String getAllStats(@QueryParam("callback") String callback, @QueryParam("pageTitle") String pageTitle) {
		int nameSpace=0,badgeNumber=0;long totalVotes=0;
		double pageRank=0,currentPageVote=0,pageReliability=0,maxPageReliability=0,maxPageRank=0;
		try{
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
		Loggings.getLogs(className).info(nameSpace+" "+badgeNumber+" "+pageRank+" "+totalVotes+" "+currentPageVote+" "+pageReliability+" "+maxPageReliability+" "+maxPageRank);


		}catch(Exception e){
			Loggings.getLogs(className).error(e);
		}

		String sJson="{\"nameSpace\":"+nameSpace+",\"badgeNumber\":"+badgeNumber+",\"pageRank\":"+pageRank+",\"totalVotes\":"+totalVotes+",\"currentPageVote\":"+currentPageVote+",\"pageReliability\":"+pageReliability+",\"maxPageReliability\":"+maxPageReliability+",\"maxPageRank\":"+maxPageRank+"}";
		String result = callback + "(" + sJson + ");";
		return result;

	}
}
