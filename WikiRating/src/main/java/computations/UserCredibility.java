package main.java.computations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.utilities.Connections;

/**
 * This class will deal with the calculations of User Credibility 
 */

public class UserCredibility {
	
	//
	/**
	 *This method will compute the credibility for all the Users 
	 */
	public static void getUserCredibility(){
		OrientGraph graph = Connections.getInstance().getDbGraph();
		final double S1=1,S2=1;
		double alpha=0,a=0,b=0,credibility=0;
		HashMap<Integer,Integer> pageEditMap=Contribution.getPageEdits();
		//To iterate over all the Users for getting their respective Credibility
		try{
		for(Vertex userNode:graph.getVertices("@class", "User")){ 
			a=geta(userNode,graph,pageEditMap);
			b=getb(userNode,graph);
			alpha=(S1*a+S2*b)/(S1+S2);
			credibility=alpha;
			userNode.setProperty("credibility",credibility);
			System.out.println(userNode.getProperty("username")+" has "+credibility);
			graph.commit();
		}
		}catch(Exception e){e.printStackTrace();}
		//graph.commit();
		graph.shutdown();
		
	}

	/**
	 * This method calculates the parameter 'a' for credibility calculation
	 * @param userNode	The Vertex of the User class whose credibility is being calculated
	 * @param graph	OrientGraph object
	 * @param pageEditMap	HashMap containing all the edits and their corresponding pid
	 * @return	The value of parameter 'a'
	 */
	public static double geta(Vertex userNode,OrientGraph graph,HashMap<Integer,Integer> pageEditMap){
		HashMap<Integer,Integer> userPageContributions=new HashMap<Integer,Integer>();  
		int contpid=0,countContribution=0; 
		double userEdits=0,totalEdits=1,finalPageVote=0;
		double aTemp=0,aTotal=0;
		int contributionSize=0;int randc=0;
		for(Edge contributeEdge:userNode.getEdges(Direction.OUT,"@class","Contribute")){
			
			contpid=(int)graph.getVertices("title",contributeEdge.getVertex(Direction.IN).getProperty("Page").toString()).iterator().next().getProperty("pid");
			contributionSize=contributeEdge.getProperty("contributionSize");
			if(userPageContributions.containsKey(contpid)){
				contributionSize+=(int)userPageContributions.get(contpid);
				userPageContributions.put(contpid,(Integer)contributionSize);
			}
			else
			{
				userPageContributions.put(contpid,(Integer)contributionSize);
			}
	}
		Iterator it = userPageContributions.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			contpid=(int)pair.getKey();
			userEdits=(int)userPageContributions.get(contpid);
			totalEdits=(int)pageEditMap.get(contpid);
			finalPageVote=graph.getVertices("pid",contpid).iterator().next().getProperty("currentPageVote");
			if(totalEdits==0)totalEdits=1;

			aTemp=(finalPageVote*userEdits/totalEdits);
			aTotal+=aTemp;
			countContribution++;
		}
		if(countContribution==0)countContribution=1;
		return aTotal/countContribution;
}
	
	/**
	 * This method calculates the parameter 'b' for credibility calculation
	 * @param userNode	The Vertex of the User class whose credibility is being calculated
	 * @param graph	OrientGraph object
	 * @return	The value of parameter 'b'
	 */
	
	public static double getb(Vertex userNode,OrientGraph graph){
		double bTemp=0,bTotal=0,userVote,versionVote;
		int countReview=0;
		try{
		for(Edge reviewEdge:userNode.getEdges(Direction.OUT,"@class","Review")){

			userVote=reviewEdge.getProperty("vote");
			versionVote=reviewEdge.getVertex(Direction.IN).getProperty("previousVote");
			bTemp=1-Math.abs(userVote-versionVote);
			bTotal+=bTemp;
			countReview++;
		}
		}catch(Exception e){e.printStackTrace();}
		if(countReview==0)countReview=1;
		return bTotal/countReview;
		
	}
}
