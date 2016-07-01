package main.java.computations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.utilities.Connections;
import main.java.utilities.PropertiesAccess;

/**
 *  This class will calculate the reliability of the vote given by the users.
 * The structure and the methods of this class are very similar to class NormalisedVotes.java
 */

public class Reliability {
	static boolean latestVoteCheck=true;		//To check for cases where latest version is voted on without any change
	final static double PHI_POWER_PARAMETER=Double.parseDouble(PropertiesAccess.getParameterProperties("PHI_POWER_PARAMETER"));

	/**
	   *This method will calculate the reliability of the votes given by the user
	   *to the versions.
	   * @return void
	   */
	public static void calculateReliability(){
		
		OrientGraph graph = Connections.getInstance().getDbGraph();
		double currentPageReliability=0;
		Vertex revisionNode=null;
		for (Vertex pageNode : graph.getVertices("@class","Page")) {
			try{
				
			revisionNode = pageNode.getEdges(Direction.OUT, "@class", "PreviousVersionOfPage").iterator().next().getVertex(Direction.IN);
			currentPageReliability=recursiveReliability(graph,(int)revisionNode.getProperty("revid"));
			pageNode.setProperty("currentPageReliability",currentPageReliability);
			graph.commit();
			}catch(Exception e){e.printStackTrace();}
		}
		//graph.commit();	
		graph.shutdown();
	}
	
	/**
	 * This method will calculate and store the reliability of  votes for all the revisions of a particular page
	 * and then return the final reliability of vote for the page itself
	   * @param graph OrientGraph object
	   * @param revid Revision Id of the latest version connected to the Page 
	 * @return final reliabiity of the latest version is computed and returned
	 */
	public static double recursiveReliability(OrientGraph graph,int revid){
		
		double lastReliability=0,phi=0,normalReliability=0,currReliability=0;
		Vertex revisionNode=graph.getVertices("revid", revid).iterator().next();
		
		if(latestVoteCheck==false&&(double)revisionNode.getProperty("previousReliability")!=-1){
			System.out.println(revisionNode.getProperty("revid")+" of "+revisionNode.getProperty("Page")+" has--- "+revisionNode.getProperty("previousReliability"));
			return (double)revisionNode.getProperty("previousReliability");
		}
		
		
		latestVoteCheck=false;
		if((int)revisionNode.getProperty("parentid")==0){
			lastReliability=simpleReliability(graph,revid);
			revisionNode.setProperty("previousReliability",lastReliability);
			graph.commit();
			System.out.println(revisionNode.getProperty("revid")+" of "+revisionNode.getProperty("Page")+" has--- "+lastReliability);
			return lastReliability;
		}
		
		else{
			phi=getPhi(graph,revid);
			currReliability=simpleReliability(graph,revid);
			normalReliability=((simpleReliability(graph,revid)+phi*recursiveReliability(graph,(int)graph.getVertices("revid", revid).iterator().next().getProperty("parentid")))/(phi+1));
			revisionNode.setProperty("previousReliability",normalReliability);
			graph.commit();
			System.out.println(revisionNode.getProperty("revid")+" of "+revisionNode.getProperty("Page")+" has--- "+normalReliability);
			return normalReliability;
		}
		
	}
	
	/**
	 * This method will calculate the  average of reliabilities of the current Revision Node 
	 * 
	 * @param graph	OrientGraph object
	 * @param revid	Revision Id for the revision node under the calculation
	 * @return	The calculated Simple weighted average. 
	 */
	public static double simpleReliability(OrientGraph graph,int revid){
		
		double denominator=0,numerator=0,simpleVote=0,globalVote=0,userVote=0;
		int noOfVotes=0;
		Vertex revisionNode=graph.getVertices("revid",revid).iterator().next();
		for(Edge reviewEdge:revisionNode.getEdges(Direction.IN,"@class","Review")){
			userVote=reviewEdge.getProperty("vote");
			globalVote=revisionNode.getProperty("previousVote");
			numerator+=(double)reviewEdge.getProperty("voteCredibility")*(1-Math.abs(userVote-globalVote));
			noOfVotes++;
		}
		denominator=noOfVotes;
		if(denominator==0)denominator=1;
		simpleVote=numerator/denominator;
		return simpleVote;
	}
	
	/**
	 * This will calculate the parameter phi to scale the reliabilities of the previous versions
	 * @param graph
	 * @param revid
	 * @return The parameter phi
	 */
	public static double getPhi(OrientGraph graph,int revid){
		
		double phi=0;
		double sizePrev=0,newEdits=0,currSize=0;
		Vertex revisionNode=graph.getVertices("revid",revid).iterator().next();
		Vertex parentNode =graph.getVertices("revid",(int)revisionNode.getProperty("parentid")).iterator().next();
		sizePrev=(int)parentNode.getProperty("size");
		currSize=(int)revisionNode.getProperty("size");
		newEdits=Math.abs(sizePrev-currSize);
		if(sizePrev==0)sizePrev=1;
		phi=Math.pow(Math.E,-1*(Math.pow(newEdits/sizePrev, PHI_POWER_PARAMETER)));
		return phi;
	}
	
}
