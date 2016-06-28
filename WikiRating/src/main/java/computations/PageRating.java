package main.java.computations;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import main.java.utilities.Connections;

/**This class will compute the final Page rating
 *
 */
public class PageRating{

	public static void computePageRatings(){
		double currentPageReliability=0,pageRank=0,currentPageVote=0,sigma=1,pageRating=0;
		OrientGraph graph = Connections.getInstance().getDbGraph();
		for(Vertex pageNode:graph.getVertices("@class","Page")){
			try{
			currentPageReliability=pageNode.getProperty("currentPageReliability");
			pageRank=pageNode.getProperty("Pagerank");
			currentPageVote=pageNode.getProperty("currentPageVote");
			pageRating=((currentPageReliability*currentPageVote)+(sigma*pageRank))/(sigma+1);
			pageNode.setProperty("PageRating", pageRating);
			System.out.println(pageNode.getProperty("title")+"   ======has rating====   "+pageRating);
			}catch(Exception e){e.printStackTrace();}
		}
		graph.commit();
		graph.shutdown();
	}
}
