package main.java.computations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.utilities.Connections;
import main.java.utilities.PropertiesAccess;

/** 
 * This class will calculate the Normalised Votes of all the revisions and hence the page by using the given
 * recursive formula that takes keeps scaling the votes on the previous versions with the new ones
 * 
 */

public class NormalisedVotes {
	static boolean latestVoteCheck=true;		//To check for cases where latest version is voted on without any change
	final static double PHI_POWER_PARAMETER=Double.parseDouble(PropertiesAccess.getParameterProperties("PHI_POWER_PARAMETER"));
	
	/**
	   *This method will calculate the Normalised Votes of all the pages in on the platform
	   *along with their respective revisions.
	   * @return void
	   */
	public static void calculatePageVotes(){
		
		OrientGraph graph = Connections.getInstance().getDbGraph();
		double currentPageVote=0;
		Vertex revisionNode=null;
		for (Vertex pageNode : graph.getVertices("@class","Page")) {
			latestVoteCheck=true;
			try{
			revisionNode = pageNode.getEdges(Direction.OUT, "@class", "PreviousVersionOfPage").iterator().next().getVertex(Direction.IN);
			currentPageVote=recursiveVotes(graph,(int)revisionNode.getProperty("revid"));
			pageNode.setProperty("currentPageVote",currentPageVote);
			graph.commit();
			}catch(Exception e){e.printStackTrace();}
		}
		//graph.commit();	
		graph.shutdown();
	}
	
	/**
	   * This method will calculate and store the Normalised votes for all the revisions of a particular page
	   * and then return the final Normalised vote for the page itself
	   * @param graph OrientGraph object
	   * @param revid Revision Id of the latest version connected to the Page 
	   * @return final vote of the latest version is computed and returned
	   */
public static double recursiveVotes(OrientGraph graph,int revid){
		
		double lastVote=0,phi=0,normalVote=0,currVote=0;
		Vertex revisionNode=graph.getVertices("revid", revid).iterator().next();
		
		if(latestVoteCheck==false&&(double)revisionNode.getProperty("previousVote")!=-1){
			System.out.println(revisionNode.getProperty("revid")+" of "+revisionNode.getProperty("Page")+" has--- "+revisionNode.getProperty("previousVote"));
			return (double)revisionNode.getProperty("previousVote");
		}
		
		latestVoteCheck=false;
		if((int)revisionNode.getProperty("parentid")==0){
			lastVote=simpleVote(graph,revid);
			revisionNode.setProperty("previousVote",lastVote);
			graph.commit();
			System.out.println(revisionNode.getProperty("revid")+" of "+revisionNode.getProperty("Page")+" has--- "+lastVote);
			return lastVote;
		}
		
		
		else{
			phi=getPhi(graph,revid);
			currVote=simpleVote(graph,revid);
			normalVote=((simpleVote(graph,revid)+phi*recursiveVotes(graph,(int)revisionNode.getProperty("parentid")))/(phi+1));
			revisionNode.setProperty("previousVote",normalVote);
			graph.commit();
			System.out.println(revisionNode.getProperty("revid")+" of "+revisionNode.getProperty("Page")+" has--- "+normalVote);
			return normalVote;
		}
		
	}
	

	/**This method will calculate the weighted average of votes of the current Revision Node 
	 * 
	 * @param graph	OrientGraph object
	 * @param revid	Revision Id for the revision node under the calculation
	 * @return	The calculated Simple weighted average. 
	 */
	
	
	public static double simpleVote(OrientGraph graph,int revid){
		double denominator=0,numerator=0,simpleVote=0;
		Vertex userNode=null;
		Vertex revisionNode=graph.getVertices("revid",revid).iterator().next();
		for(Edge reviewEdge:revisionNode.getEdges(Direction.IN,"@class","Review")){
			//userNode=reviewEdge.getVertex(Direction.OUT);
			numerator+=(double)reviewEdge.getProperty("voteCredibility")*(double)reviewEdge.getProperty("vote");
			denominator+=(double)reviewEdge.getProperty("vote");
		}
		if(denominator==0)denominator=1;
		simpleVote=numerator/denominator;
		return simpleVote;
	}
	
	
	/**
	 * This will calculate the parameter phi to scale the votes of the previous versions
	 * @param graph	OrientGraph object
	 * @param revid	Revision Id for the revision node under the calculation
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
