package main.java;
/**This class will check for new pages, revisions made on previous pages and add them. Further it will link them to the corresponding User contributions
 * It will also link a page to other pages that has a back link towards it
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import main.java.compute.LinkPages;
import main.java.fetch.Page;
import main.java.fetch.Revision;
import main.java.utilities.Connections;
import main.java.utilities.WikiUtil;



public class AddNewPages {
	
	public static void chackForPages(){
		
		OrientGraph graph = Connections.getInstance().getDbGraph();
		String allPages="";
		final int NO_OF_NAMESPACES=15;
		
		try {
			//Now we will be iterating over all the namespaces to get all the pages in each og them.
			
			for(int ns=0;ns<=NO_OF_NAMESPACES;ns++){
				
				//JSON interpretation
				try {  
					allPages =Page.getAllPages(ns);							//Getting the JSON formatted String to process.
					JSONObject js=new JSONObject(allPages);
					JSONObject js2=js.getJSONObject("query");
					JSONArray arr=js2.getJSONArray("allpages");
					JSONObject dummy;
					
					//Storing all the pages in a particular namespace
					
					for(int i=0;i<arr.length();i++){
						dummy=arr.getJSONObject(i);
						
						if(WikiUtil.rCheck("pid",dummy.getInt("pageid"),graph)){	//This is a makeshift way to avoid duplicate insertion.
							
							insertNewPage(graph,dummy,ns);
							getNewRevisions("title",dummy.getString("title"),false);
							LinkPages.linkAll("title",dummy.getString("title"));
							
							
						}
						else{
							getNewRevisions("title",dummy.getString("title"),true);
							
							
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		} finally {
			//graph.commit();
			graph.shutdown();
			//LinkPages.linkAll();
		}
		
	}
	
	public static void insertNewPage(OrientGraph graph,JSONObject dummy,int ns){
		
		//Adding pages to database
		try{
			System.out.println("++Adding this new Page++  "+dummy.getString("title"));
			Vertex ver = graph.addVertex("class:Page"); // 1st OPERATION: will implicitly begin the transaction and this command will create the class too.
			ver.setProperty( "title", dummy.getString("title"));
			ver.setProperty("pid",dummy.getInt("pageid"));
			ver.setProperty("ns", ns);
			graph.commit();
		} catch( Exception e ) {
			e.printStackTrace();
			graph.rollback();
		}
		
	}
	
	
	public static void getNewRevisions(String key,String value,boolean update){
		
		String result="";
		int sizeDiff=0;
		boolean flag=true;
		OrientGraph graph=Connections.getInstance().getDbGraph();
		System.out.println("=====Checkpoint for Revisions==========");
		for (Vertex pageNode : graph.getVertices(key,value)) {
			
			result=Revision.getRevision(pageNode.getProperty("pid").toString());	//Fetching the revision string for a particular page.
				
			try {
				//JSON interpretation
				 try {  
					 	JSONObject js=new JSONObject(result);
					 	JSONObject js2=js.getJSONObject("query").getJSONObject("pages").getJSONObject(pageNode.getProperty("pid").toString());
					 	JSONArray arr=js2.getJSONArray("revisions");
						JSONObject dummy;
						
						
					 	for(int i=0;i<arr.length();i++){
					 		dummy=arr.getJSONObject(i);
					 		
					 		
					 		//Adding pages to database without the duplicates
					 		if(WikiUtil.rCheck("revid", dummy.getInt("revid"), graph)){
					 			
					 			System.out.println(pageNode.getProperty("title").toString()+dummy.getInt("revid"));
					 			//Code to remove the Link of 'outdated'latest version from the parent page to make the room for the new one.
					 			if(update==true&&flag){
						 			System.out.println(pageNode.getProperty("title").toString()+" is getting updated");

									graph.removeEdge(pageNode.getEdges(Direction.OUT,"@class","PreviousVersionOfPage").iterator().next());
									//Remove old backlinks
									 pageNode=graph.getVertices("pid",dummy.getInt("pageid")).iterator().next();
									for(Edge backlinkEdge:pageNode.getEdges(Direction.IN,"@class","Backlink"))
									graph.removeEdge(backlinkEdge);
									LinkPages.linkAll("title",dummy.getString("title"));
								}
					 			flag=false;
					 			
					 		try{
					 			  Vertex revisionNode = graph.addVertex("class:Revision"); // 1st OPERATION: IMPLICITLY BEGINS TRANSACTION
					 			  revisionNode.setProperty( "Page", pageNode.getProperty("title").toString());
					 			  revisionNode.setProperty("revid",dummy.getInt("revid"));
					 			  revisionNode.setProperty("parentid",dummy.getInt("parentid"));
					 			  revisionNode.setProperty("user",dummy.getString("user"));
					 			  revisionNode.setProperty("userid",dummy.getInt("userid"));
					 			  revisionNode.setProperty("size",dummy.getInt("size"));
					 			  
					 			  //Code to link the user contributions
					 			  
					 			  if((i==0)&&(WikiUtil.rCheck("userid", dummy.getInt("userid"), graph)==false)){
					 				  
					 				 Vertex userNode= graph.getVertices("userid", dummy.getInt("userid")).iterator().next();
					 				 Edge contributes = graph.addEdge("contribute", userNode, revisionNode, "Contribute");
					 				 contributes.setProperty("contributionSize", dummy.getInt("size"));
					 			  }
					 			  
					 			 if((i!=0)&&(WikiUtil.rCheck("userid", dummy.getInt("userid"), graph)==false)){
					 				  
					 				 Vertex userNode= graph.getVertices("userid", dummy.getInt("userid")).iterator().next();
					 				 Vertex parentVersionNode=graph.getVertices("revid",dummy.getInt("parentid")).iterator().next();
					 				 sizeDiff=Math.abs((int)parentVersionNode.getProperty("size")-dummy.getInt("size"));
					 				 Edge contributes = graph.addEdge("contribute", userNode, revisionNode, "Contribute");
					 				 contributes.setProperty("contributionSize", sizeDiff);
					 			  }
					 			  
					 			  //All the versions are connected to each other like (Page)<-(Latest)<-(Latest-1)<-...<-(Last)
					 			  
					 			  if(i==arr.length()-1){//The latest version will be connected to the Page itself and to the previous revision too
					 				  
					 				  Vertex parentPage=graph.getVertices("pid",pageNode.getProperty("pid")).iterator().next();
					 				  Edge isRevision = graph.addEdge("PreviousVersionOfPage", parentPage,revisionNode,"PreviousVersionOfPage");
					 			  }
					 			  
					 			  if(i!=0){//To link the current revision to the previous versions
					 				Vertex parentVersionNode=graph.getVertices("revid",dummy.getInt("parentid")).iterator().next();
					 				Edge isRevision = graph.addEdge("PreviousRevision", revisionNode,parentVersionNode,"PreviousRevision");
					 			  }
					 			  
					 			  
					 			  graph.commit();
					 			} catch( Exception e ) {
					 				e.printStackTrace();
					 			  graph.rollback();
					 			}
					 	}
					 	
					 		}
					} catch (JSONException e) {
						e.printStackTrace();
					}
			 
				 
			 } catch(Exception ee) {
			   ee.printStackTrace();
			 }
			
}		//graph.commit();
		//graph.shutdown();
//Pagerank.pageRankCompute();
		
			
		
	}

}
