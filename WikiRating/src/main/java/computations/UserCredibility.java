package main.java.computations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.utilities.Connections;
import main.java.utilities.PropertiesAccess;
import main.java.utilities.Loggings;


/**
 * This class will deal with the calculations of User Credibility
 */

public class UserCredibility {
	static Class className=CreditSystem.class;
	final static double USER_CONTRI_IMPORTANCE_PARAMETER=Double.parseDouble(PropertiesAccess.getParameterProperties("USER_CONTRI_IMPORTANCE_PARAMETER"));
	final static double USER_VOTE_IMPORTANCE_PARAMETER=Double.parseDouble(PropertiesAccess.getParameterProperties("USER_VOTE_IMPORTANCE_PARAMETER"));

	/**
	 *This method will compute the credibility for all the Users
	 */

	public static void getUserCredibility(){
		OrientGraph graph = Connections.getInstance().getDbGraph();
		double alpha=0,relativeUserContribution=0,voteDeviation=0,credibility=0;
		Map<Integer,Integer> pageEditMap=Contribution.getPageEdits();
		//To iterate over all the Users for getting their respective Credibility
		try{
		for(Vertex userNode:graph.getVertices("@class", "User")){
			relativeUserContribution=getRelativeUserContribution(userNode,graph,pageEditMap);
			voteDeviation=getVoteDeviation(userNode,graph);
			alpha=(USER_CONTRI_IMPORTANCE_PARAMETER*relativeUserContribution+USER_VOTE_IMPORTANCE_PARAMETER*voteDeviation)/(USER_CONTRI_IMPORTANCE_PARAMETER+USER_VOTE_IMPORTANCE_PARAMETER);
			credibility=alpha;
			userNode.setProperty("credibility",credibility);
			Loggings.getLogs(className).info(userNode.getProperty("username")+" has "+credibility);
			graph.commit();
		}
		}catch(Exception e){Loggings.getLogs(className).error(e);}
		//graph.commit();
		graph.shutdown();

	}

	/**
	 * This method calculates the parameter 'a'(relativeUserContribution) for credibility calculation
	 * @param userNode	The Vertex of the User class whose credibility is being calculated
	 * @param graph	OrientGraph object
	 * @param pageEditMap	HashMap containing all the edits and their corresponding pid
	 * @return	The value of parameter 'a'
	 */
	public static double getRelativeUserContribution(Vertex userNode,OrientGraph graph,Map<Integer,Integer> pageEditMap){
		HashMap<Integer,Integer> userPageContributions=new HashMap<Integer,Integer>();
		int contpid=0,countContribution=0,totalEdits;
		double userEdits=0,finalPageVote=0;
		double userPageContributionsTemp=0,userPageContributionsTotal=0;
		int contributionSize=0;
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
			if(totalEdits==0)totalEdits=1;	//Not a float comparison

			userPageContributionsTemp=(finalPageVote*userEdits/totalEdits);
			userPageContributionsTotal+=userPageContributionsTemp;
			countContribution++;
		}
		if(countContribution==0)countContribution=1;
		return userPageContributionsTotal/countContribution;
}

	/**
	 * This method calculates the parameter 'b'(voteDeviation) for credibility calculation
	 * @param userNode	The Vertex of the User class whose credibility is being calculated
	 * @param graph	OrientGraph object
	 * @return	The value of parameter 'b'
	 */

	public static double getVoteDeviation(Vertex userNode,OrientGraph graph){
		double voteDeviationTemp=0,voteDeviationTotal=0,userVote,versionVote;
		int countReview=0;
		try{
		for(Edge reviewEdge:userNode.getEdges(Direction.OUT,"@class","Review")){

			userVote=reviewEdge.getProperty("vote");
			versionVote=reviewEdge.getVertex(Direction.IN).getProperty("previousVote");
			voteDeviationTemp=1-Math.abs(userVote-versionVote);
			voteDeviationTotal+=voteDeviationTemp;
			countReview++;
		}
		}catch(Exception e){Loggings.getLogs(className).error(e);}
		if(countReview==0)countReview=1;
		return voteDeviationTotal/countReview;

	}
}
