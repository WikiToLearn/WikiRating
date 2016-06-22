package main.java;

import java.util.ArrayList;
import java.util.Random;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

/**This class will generate random votes for the platform to simulate the actual review activities of the user
 * 
 */
public class RandomVoteGenerator {
	
	public static int  getRandom(int low, int high){
		
		Random r = new Random();
		int Low = low;
		int High = high+1;
		int Result=0;
		
		Result=r.nextInt(High-Low) + Low;
		
		return Result;
		
	}
public static ArrayList<Integer> getRevisionList(OrientGraph graph){
	ArrayList<Integer> revisionList=new ArrayList<Integer>();
	for(Vertex revisionNode:graph.getVertices("@class","Revision")){
		revisionList.add((Integer) revisionNode.getProperty("revid"));
	}
	return revisionList;
}
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
				//System.out.println("Inside Random ");

				//To get how many votes a user will vote for
				noOfVotes=(int)getRandom(1,50);
				
				//Loop to cast votes on the behalf of User
				for(int i=0;i<=noOfVotes;i++){
					//System.out.println("Inside votes ");	
					revisionToVote=getRandom(0, revisionList.size()-1);
					revid=(int) revisionList.get(revisionToVote);
					if(voteTrack[revisionToVote]!=1){//To avoid voting for a same version
						revisionNode=graph.getVertices("revid",revid).iterator().next();
						//System.out.println("Passed redundency check");	
						if(((int)revisionNode.getProperty("userid"))!=((int)userNode.getProperty("userid"))){
							voteTrack[revisionToVote]=1;
							currVote=getRandom(1, 10);
							System.out.println(userNode.getProperty("username")+" gives "+currVote+" to "+revisionNode.getProperty("Page")+" == "+revid);
							Edge review = graph.addEdge("review", userNode, revisionNode, "Review");
							review.setProperty("vote", currVote/10.0);
						}
						graph.commit();
					}
				}
			}
		}
		graph.shutdown();
	}
}
