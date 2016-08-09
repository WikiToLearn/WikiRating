package main.java.computations;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.utilities.Connections;
import main.java.utilities.Loggings;
import main.java.utilities.PropertiesAccess;
import main.java.utilities.Loggings;

/**
 * This class will compute the final Page rating combining all the parameter in the Master Equation
 */

public class PageRating{
	static Class className=PageRating.class;
	final static double PAGERANK_IMPORTANCE_PARAMETER=Double.parseDouble(PropertiesAccess.getParameterProperties("PAGERANK_IMPORTANCE_PARAMETER"));

	/**
	 * This method combines all the parameters are calculate and store the final Page Rating of the Page.
	 * @return void
	 */
	public static void computePageRatings(){

		double currentPageReliability=0,pageRank=0,currentPageVote=0,pageRating=0;
		OrientGraph graph = Connections.getInstance().getDbGraph();
		for(Vertex pageNode:graph.getVertices("@class","Page")){
			try{
			currentPageReliability=pageNode.getProperty("currentPageReliability");
			pageRank=pageNode.getProperty("Pagerank");
			currentPageVote=pageNode.getProperty("currentPageVote");
			pageRating=((currentPageReliability*currentPageVote)+(PAGERANK_IMPORTANCE_PARAMETER*pageRank));
			pageNode.setProperty("PageRating", pageRating);
			Loggings.getLogs(className).info(pageNode.getProperty("title")+"   ======has rating====   "+pageRating);
			}catch(Exception e){
				Loggings.getLogs(className).error(e);
			}
		}
		graph.commit();
		graph.shutdown();
	}
}
