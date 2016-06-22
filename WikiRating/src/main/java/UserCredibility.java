package main.java;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
/**This class will deal with the calculations of User Credibility
 *  
 */
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
public class UserCredibility {
	
	//Main method that will compute all credibility for all the Users
	public static void getUserCredibility(){
		OrientGraph graph = Connections.getInstance().getDbGraph();
		final double S1=1,S2=1;
		double alpha=0,a=0,b=0,credibility=0;
		HashMap<Integer,Integer> pageEditMap=Contribution.getPageEdits();
		//To iterate over all the Users for getting their respective Credibility
		for(Vertex userNode:graph.getVertices("@class", "User")){ 
			a=geta(userNode,graph,pageEditMap);
			b=getb(userNode,graph);
			alpha=(S1*a+S2*b)/(S1+S2);
			credibility=alpha;
			userNode.setProperty("credibility",credibility);
			System.out.println(userNode.getProperty("username")+" has "+credibility);
			graph.commit();
		}
		graph.shutdown();
		
	}

	public static double geta(Vertex userNode,OrientGraph graph,HashMap<Integer,Integer> pageEditMap){
		HashMap<Integer,Integer> userPageContributions=new HashMap<Integer,Integer>();  
		int contpid=0,countContribution=0; 
		double userEdits=0,totalEdits=1,finalPageVote=0;
		double aTemp=0,aTotal=0;
		int contributionSize=0;int randc=0;
		for(Edge contributeEdge:userNode.getEdges(Direction.OUT,"@class","Contribute")){
			//randc++;
			//System.out.println("================#######++++++++++++++");
			contpid=(int)graph.getVertices("name",contributeEdge.getVertex(Direction.IN).getProperty("Page").toString()).iterator().next().getProperty("pid");
			contributionSize=contributeEdge.getProperty("consize");
			//System.out.println(userNode.getProperty("username")+"  ====  "+contributionSize+"");
			if(userPageContributions.containsKey(contpid)){
				//System.out.println("second time");
				contributionSize+=(int)userPageContributions.get(contpid);
				userPageContributions.put(contpid,(Integer)contributionSize);
			}
			else
			{
				//System.out.println("first time");
				userPageContributions.put(contpid,(Integer)contributionSize);
			}
	}
		//System.out.println("No to contri "+randc);
		Iterator it = userPageContributions.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			contpid=(int)pair.getKey();
			userEdits=(int)userPageContributions.get(contpid);
			totalEdits=(int)pageEditMap.get(contpid);
			finalPageVote=graph.getVertices("pid",contpid).iterator().next().getProperty("finalvote");
			if(totalEdits==0)totalEdits=1;
			/*System.out.println("======Printing the results======");
			System.out.println("userEdits  "+userEdits);
			System.out.println("totalEdits  "+totalEdits);
			System.out.println("finalPageVote  "+finalPageVote);*/
			aTemp=(finalPageVote*userEdits/totalEdits);
			aTotal+=aTemp;
		/*	System.out.println("aTemp  "+aTemp);
			System.out.println("aTotal  "+aTotal);
			System.out.println("aTemp  "+aTemp);*/
			countContribution++;
			//System.out.println("countContribution  "+countContribution);
		}
		if(countContribution==0)countContribution=1;
		return aTotal/countContribution;
}
	
	
	public static double getb(Vertex userNode,OrientGraph graph){
		double bTemp=0,bTotal=0,userVote,versionVote;
		int countReview=0;
		try{
		for(Edge reviewEdge:userNode.getEdges(Direction.OUT,"@class","Review")){
/*			System.out.println("bTemp------===="+bTemp);
			System.out.println("bTotal------===="+bTotal);*/
			userVote=reviewEdge.getProperty("vote");
			versionVote=reviewEdge.getVertex(Direction.IN).getProperty("normalvote");
			//System.out.println("userVote===="+userVote);
			bTemp=1-Math.abs(userVote-versionVote);
			//System.out.println("versionVote------===="+versionVote);
			bTotal+=bTemp;
			countReview++;
		}
		}catch(Exception e){e.printStackTrace();}
		if(countReview==0)countReview=1;
		//System.out.println("fffffffffffbTotal------===="+bTotal);
		return bTotal/countReview;
		
	}
}
