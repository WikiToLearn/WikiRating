package main.java;
/** This class deals with the computation of Normalised votes for all the revisions and finally the page
 * 
 */
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class NormalisedVotes {
	
	//This method will calculate the finalVotes of all the Pages on the plaform
	public static void calculatePageVotes(){
		
		OrientGraph graph = Connections.getInstance().getDbGraph();
		double finalPageVote=0;
		Vertex revisionNode=null;
		for (Vertex pageNode : graph.getVertices("@class","Page")) {
			try{
			revisionNode = pageNode.getEdges(Direction.OUT, "@class", "Pversion").iterator().next().getVertex(Direction.IN);
			finalPageVote=recursiveVotes(graph,(int)revisionNode.getProperty("revid"));
			pageNode.setProperty("finalvote",finalPageVote);
			graph.commit();
			}catch(Exception e){e.printStackTrace();}
		}
		graph.shutdown();
	}
	
public static double recursiveVotes(OrientGraph graph,int revid){
		
		double lastVote=0,phi=0,normalVote=0,currVote=0;
		if((int)graph.getVertices("revid", revid).iterator().next().getProperty("parentid")==0){
			lastVote=simpleVote(graph,revid);
			Vertex currRevision=graph.getVertices("revid", revid).iterator().next();
			currRevision.setProperty("normalvote",lastVote);
			graph.commit();
			System.out.println(currRevision.getProperty("revid")+" of "+currRevision.getProperty("Page")+" has--- "+lastVote);
			return lastVote;
		}
		
		else{
			phi=getPhi(graph,revid);
			currVote=simpleVote(graph,revid);
			normalVote=((simpleVote(graph,revid)+phi*recursiveVotes(graph,(int)graph.getVertices("revid", revid).iterator().next().getProperty("parentid")))/(phi+1));
			Vertex currRevision=graph.getVertices("revid", revid).iterator().next();
			currRevision.setProperty("normalvote",normalVote);
			graph.commit();
			System.out.println(currRevision.getProperty("revid")+" of "+currRevision.getProperty("Page")+" has--- "+normalVote);
			return normalVote;
		}
		
	}
	
	public static double simpleVote(OrientGraph graph,int revid){
		double denominator=0,numerator=0,simpleVote=0;
		Vertex userNode=null;
		Vertex revisionNode=graph.getVertices("revid",revid).iterator().next();
		for(Edge reviewEdge:revisionNode.getEdges(Direction.IN,"@class","Review")){
			userNode=reviewEdge.getVertex(Direction.OUT);
			numerator+=(double)userNode.getProperty("credibility")*(double)reviewEdge.getProperty("vote");
			denominator+=(double)reviewEdge.getProperty("vote");
		}
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
