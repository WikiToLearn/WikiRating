package main.java;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;


public class Pagerank {
	
	//This is a test method used for printing all the edges linking to a page for all the pages.
	public static void edgeList(){
		
	OrientGraph graph = Connections.getInstance().getDbGraph();
	for (Vertex v :graph.getVertices("@class","Page")) {
		OrientVertex vv=(OrientVertex)v;
		long backLinks=vv.countEdges(Direction.IN, "@class","Backlink");
		System.out.println("======= "+v.getProperty("name").toString()+"========"+backLinks);
		for(Edge e:vv.getEdges(Direction.IN,"@class","Backlink")){
			System.out.println(e.getVertex(Direction.OUT).getProperty("title"));
		}
		
	}
	
	}
	
	//This is the method that will calculate the ranks of all the pages.
	
	public static void pageRankCompute(){
		
		OrientGraph graph = Connections.getInstance().getDbGraph();
		HashMap<Integer,Double> pageRankMap=new HashMap<Integer,Double>();
		long maxLink=0,currLink=0;
		double tempSum=0;double finalPageRank;
		
		//Iterating for the calculation of number of backLinks and the maximum of them.
		for (Vertex v :graph.getVertices("@class","Page")) {
			OrientVertex ov=(OrientVertex)v;
			currLink=ov.countEdges(Direction.IN, "@class","Backlink");
			pageRankMap.put((Integer)ov.getProperty("pid"),(double)currLink);
			maxLink=(maxLink<currLink)?currLink:maxLink;
		}
		
		 //Scaling the values by dividing with the maxlinks
		
		 Iterator it = pageRankMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        pageRankMap.put((Integer) pair.getKey(), (Double)((double)pair.getValue()/maxLink));
		        		        
		    } 
		    
		    System.out.println(maxLink+" is the max ");
		
		    //Now calculating the weighted Page Rank for every page.
		    
		    for (Vertex v :graph.getVertices("@class","Page")) {
		    	
				OrientVertex vv=(OrientVertex)v;			//Casting required to count the edges from a node.
				tempSum=0;
				//Summing the contributions of different backLinks
				for(Edge e:vv.getEdges(Direction.IN,"@class","Backlink")){
					tempSum+=pageRankMap.get((Integer)e.getVertex(Direction.OUT).getProperty("pid"));
					
				}
		        
				finalPageRank=tempSum/maxLink;				//Final page rank
				pageRankMap.put((Integer)v.getProperty("pid"),finalPageRank);
				v.setProperty("Pagerank", finalPageRank);	//Adding the PageRank to Database
				graph.commit();
	
			}
		    //To print out the final results.
		    Iterator it2 = pageRankMap.entrySet().iterator();
		    while (it2.hasNext()) {
		        Map.Entry pair = (Map.Entry)it2.next();
		        System.out.println("===========Finals===========");
		        System.out.println(pair.getKey()+"  "+pair.getValue());
		       } 
		    //graph.commit();
		    graph.shutdown();
	}

}
