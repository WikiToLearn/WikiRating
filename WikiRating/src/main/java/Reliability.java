package main.java;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/** This class will calculate the reliability of the vote given by the users
 * 
 */
public class Reliability {

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
	
	
public static double recursiveReliability(OrientGraph graph,int revid){
		
		double lastReliability=0,phi=0,normalReliability=0,currReliability=0;
		if((int)graph.getVertices("revid", revid).iterator().next().getProperty("parentid")==0){
			lastReliability=simpleReliability(graph,revid);
			Vertex currRevision=graph.getVertices("revid", revid).iterator().next();
			currRevision.setProperty("previousReliability",lastReliability);
			graph.commit();
			System.out.println(currRevision.getProperty("revid")+" of "+currRevision.getProperty("Page")+" has--- "+lastReliability);
			return lastReliability;
		}
		
		else{
			phi=getPhi(graph,revid);
			currReliability=simpleReliability(graph,revid);
			normalReliability=((simpleReliability(graph,revid)+phi*recursiveReliability(graph,(int)graph.getVertices("revid", revid).iterator().next().getProperty("parentid")))/(phi+1));
			Vertex currRevision=graph.getVertices("revid", revid).iterator().next();
			currRevision.setProperty("previousReliability",normalReliability);
			graph.commit();
			System.out.println(currRevision.getProperty("revid")+" of "+currRevision.getProperty("Page")+" has--- "+normalReliability);
			return normalReliability;
		}
		
	}
	
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
	
	public static double getPhi(OrientGraph graph,int revid){
		final double P=1.0;
		double phi=0;
		double sizePrev=0,newEdits=0,currSize=0;
		Vertex revisionNode=graph.getVertices("revid",revid).iterator().next();
		Vertex parentNode =graph.getVertices("revid",(int)revisionNode.getProperty("parentid")).iterator().next();
		sizePrev=(int)parentNode.getProperty("size");
		currSize=(int)revisionNode.getProperty("size");
		newEdits=Math.abs(sizePrev-currSize);
		if(sizePrev==0)sizePrev=1;
		phi=Math.pow(Math.E,-1*(Math.pow(newEdits/sizePrev, P)));
		return phi;
	}
	
}
