package main.java.computations;

import java.util.ArrayList;
import java.util.Random;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.utilities.Connections;
import main.java.utilities.Loggings;


/**
 * This class will generate random votes for the platform to simulate the actual review activities of the user
 */

public class RandomVoteGenerator {
	static Class className=RandomVoteGenerator.class;
	/**
	 * This method generates a random int between low and high (both inclusive)
	 * @param low	The lower limit
	 * @param high	The upper limit
	 * @return	A random integer between low and high
	 */
	public static int  getRandom(int low, int high){

		Random r = new Random();
		int Low = low;
		int High = high+1;
		int Result=0;

		Result=r.nextInt(High-Low) + Low;

		return Result;

	}

	/**
	 * This method returns an ArrayList containing the revid of all the Revisions
	 * @param graph	OrientGraph object
	 * @return	return an ArrayList containing revid
	 */
public static ArrayList<Integer> getRevisionList(OrientGraph graph){
	ArrayList<Integer> revisionList=new ArrayList<Integer>();
	for(Vertex revisionNode:graph.getVertices("@class","Revision")){
		revisionList.add((Integer) revisionNode.getProperty("revid"));
	}
	return revisionList;
}

	/**
	 * This method will generate all the votes by choosing random users and then voting
	 * randomly for arbitrary pages
	 */
	public static void generateVotes(){
		int noOfVotes=0;
		int revisionToVote=0;
		int revid=0,currVote=0;
		Vertex revisionNode=null;
		OrientGraph graph = Connections.getInstance().getDbGraph();
		ArrayList revisionList=getRevisionList(graph);

		for (Vertex userNode : graph.getVertices("@class", "User")) {
			int[] voteTrack=new int[revisionList.size()];

			//HashMap<Integer,Integer> voteTrack=new HashMap<Integer,Integer>();
			if(getRandom(1,10)>4){

				//To get how many votes a user will vote for
				noOfVotes=(int)getRandom(1,50);

				//Loop to cast votes on the behalf of User
				for(int i=0;i<=noOfVotes;i++){
					revisionToVote=getRandom(0, revisionList.size()-1);
					revid=(int) revisionList.get(revisionToVote);
					if(voteTrack[revisionToVote]!=1){//To avoid voting for a same version
						revisionNode=graph.getVertices("revid",revid).iterator().next();
						if(((int)revisionNode.getProperty("userid"))!=((int)userNode.getProperty("userid"))){
							voteTrack[revisionToVote]=1;
							currVote=getRandom(1, 10);
							Loggings.getLogs(className).info(userNode.getProperty("username")+" gives "+currVote+" to "+revisionNode.getProperty("Page")+" == "+revid);
							Edge review = graph.addEdge("review", userNode, revisionNode, "Review");
							review.setProperty("vote", currVote/10.0);
							review.setProperty("voteCredibility",userNode.getProperty("credibility"));
						}
						//graph.commit();
					}
				}
				graph.commit();
			}
		}
		//graph.commit();
		graph.shutdown();
	}
}
