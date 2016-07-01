package main.java.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.controllers.WikiUtil;
import main.java.utilities.Connections;
import main.java.utilities.PropertiesAccess;


/**
 * This class will check for new pages, revisions made on previous pages and add them. 
 * Further it will link them to the corresponding User contributions.
 * It will also link a page to other pages that has a back link towards it.
 */

public class AddNewPages {
	
	static int TEMPLATE=Integer.parseInt(PropertiesAccess.getParameterProperties("TEMPLATE")); 
	static int MEDIAWIKI=Integer.parseInt(PropertiesAccess.getParameterProperties("MEDIAWIKI"));
	/**
	 * This method will check for all the changes and then call suitable methods to handle them.
	 */
	public static void checkForPages(){
		
		OrientGraph graph = Connections.getInstance().getDbGraph();
		String allPages="";
		final int NO_OF_NAMESPACES=15;
		
		try {
			//Now we will be iterating over all the namespaces to get all the pages in each og them.
			
			for(int ns=0;ns<=NO_OF_NAMESPACES;ns++){
				
				//To filter unwanted namespaces
				if(ns==TEMPLATE||ns==MEDIAWIKI)
					continue;
				
				//JSON interpretation
				try {  
					allPages =Page.getAllPages(ns);							//Getting the JSON formatted String to process.
					JSONObject js=new JSONObject(allPages);
					JSONObject js2=js.getJSONObject("query");
					JSONArray arr=js2.getJSONArray("allpages");
					JSONObject currentJsonObject;
					
					//Storing all the pages in a particular namespace
					
					for(int i=0;i<arr.length();i++){
						currentJsonObject=arr.getJSONObject(i);
						
						if(WikiUtil.rCheck("pid",currentJsonObject.getInt("pageid"),graph)){	//This is a makeshift way to avoid duplicate insertion.
							
							insertNewPage(graph,currentJsonObject,ns);
							getNewRevisions(graph,"title",currentJsonObject.getString("title"),false);
							linkAllBacklinks(graph,"title",currentJsonObject.getString("title"));
							
						}
						else{
							getNewRevisions(graph,"title",currentJsonObject.getString("title"),true);
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
	
	/**
	 * This method will add new pages to the database
	 * @param graph Orient Graph object
	 * @param currentJsonObject	JSONObject having Page object
	 * @param ns	Namespace of the page
	 */
	
	public static void insertNewPage(OrientGraph graph,JSONObject currentJsonObject,int ns){
		
		//Adding pages to database
		try{
			System.out.println("++Adding this new Page++  "+currentJsonObject.getString("title"));
			Vertex ver = graph.addVertex("class:Page"); // 1st OPERATION: will implicitly begin the transaction and this command will create the class too.
			ver.setProperty( "title", currentJsonObject.getString("title"));
			ver.setProperty("pid",currentJsonObject.getInt("pageid"));
			ver.setProperty("ns", ns);
			ver.setProperty("currentPageVote",-1.0);
			ver.setProperty("currentPageReliability", -1.0);
			graph.commit();
		} catch( Exception e ) {
			e.printStackTrace();
			graph.rollback();
		}
		
	}
	

		/**
		 * This method links all the backLinks of a particular page
		 * @param graph OrientGraph object
		 * @param key	Name of the key here '@class'
		 * @param value	Value of the key here 'Page'
		 */
	
		public static void linkAllBacklinks(OrientGraph graph,String key,String value){

			String result="";
			int inLinks;
			
			//Iterating on every vertex to check it's backlinks
			
			for (Vertex v : graph.getVertices(key,value)) {
				
				result=LinkPages.getBacklinks((int)(v.getProperty("pid")));	//Getting the JSON formatted String to process.
				inLinks=0;
				
				//JSON interpretation of the fetched String
				
				 try {  
					 	JSONObject js=new JSONObject(result);
					 	JSONObject js2=js.getJSONObject("query");
					 	JSONArray arr=js2.getJSONArray("backlinks");	//This array has all the backlinks the page has.
					 	JSONObject currentJsonObject;							
					 	inLinks=arr.length();
					 	System.out.println(v.getProperty("title").toString()+" has inLinks = "+inLinks);
					 	
					 	
					 	//Iterating to get all the backlinks of a particular node(Page)
					 	
					 	for(int i=0;i<arr.length();i++){
					 		currentJsonObject=arr.getJSONObject(i);
					 		
					 		try{	
					 			
					 			Vertex backLink=graph.getVertices("pid",currentJsonObject.getInt("pageid")).iterator().next();	//Getting the node linked to the current page.
					 			Edge isbackLink = graph.addEdge("Backlink", backLink, v, "Backlink");				//Creating Edge in between the 2 vertices.
					 			System.out.println(v.getProperty("title").toString()+" is linked to "+backLink.getProperty("title").toString());
					 			
					 			
					 		graph.commit();														
					 		} catch( Exception e ) {
					 			e.printStackTrace();
					 			graph.rollback();																	//In case the transaction fails we will rollback.
					 		}
					 		
					 	}
				 } catch (JSONException e) { 
					 e.printStackTrace();
				 }
				 
			}
			
		}
	
		/**
		 * This method will link the revisions to the new as well as modified pages
		 * @param graph OrientGraph object
		 * @param key	Name of the class here 'Page'
		 * @param value	Value of the page
		 * @param update	Set this true is the Page exsts already and needs to be updated
		 */
	public static void getNewRevisions(OrientGraph graph,String key,String value,boolean update){
		
		String result="";
		int sizeDiff=0;
		boolean flag=true;
		
		System.out.println("=====Checkpoint for Revisions==========");
		for (Vertex pageNode : graph.getVertices(key,value)) {
			
			result=Revision.getRevision(pageNode.getProperty("pid").toString());	//Fetching the revision string for a particular page.
				
			try {
				//JSON interpretation
				 try {  
					 	JSONObject js=new JSONObject(result);
					 	JSONObject js2=js.getJSONObject("query").getJSONObject("pages").getJSONObject(pageNode.getProperty("pid").toString());
					 	JSONArray arr=js2.getJSONArray("revisions");
						JSONObject currentJsonObject;
						
						
					 	for(int i=0;i<arr.length();i++){
					 		currentJsonObject=arr.getJSONObject(i);
					 		
					 		
					 		//Adding pages to database without the duplicates
					 		if(WikiUtil.rCheck("revid", currentJsonObject.getInt("revid"), graph)){
					 			
					 			System.out.println(pageNode.getProperty("title").toString()+currentJsonObject.getInt("revid"));
					 			//Code to remove the Link of 'outdated'latest version from the parent page to make the room for the new one.
					 			if(update==true&&flag){

					 				System.out.println(pageNode.getProperty("title").toString()+" is getting updated");
									graph.removeEdge(pageNode.getEdges(Direction.OUT,"@class","PreviousVersionOfPage").iterator().next());
									//Remove old backlinks
									for(Edge backlinkEdge:pageNode.getEdges(Direction.IN,"@class","Backlink")){
										graph.removeEdge(backlinkEdge);
									}
									linkAllBacklinks(graph,"title",pageNode.getProperty("title").toString());
					 			}
					 			flag=false;
									
					 			
					 			System.out.println("Adding some new revisions");
					 			
					 		try{
					 			  Vertex revisionNode = graph.addVertex("class:Revision"); // 1st OPERATION: IMPLICITLY BEGINS TRANSACTION
					 			  revisionNode.setProperty( "Page", pageNode.getProperty("title").toString());
					 			  revisionNode.setProperty("revid",currentJsonObject.getInt("revid"));
					 			  revisionNode.setProperty("parentid",currentJsonObject.getInt("parentid"));
					 			  revisionNode.setProperty("user",currentJsonObject.getString("user"));
					 			  revisionNode.setProperty("userid",currentJsonObject.getInt("userid"));
					 			  revisionNode.setProperty("size",currentJsonObject.getInt("size"));
					 			  revisionNode.setProperty("previousVote",-1.0);
					 			  revisionNode.setProperty("previousReliability", -1.0);

					 			  //Code to link the user contributions
					 			  
					 			  if((i==0)&&(WikiUtil.rCheck("userid", currentJsonObject.getInt("userid"), graph)==false)){
					 				  
					 				 Vertex userNode= graph.getVertices("userid", currentJsonObject.getInt("userid")).iterator().next();
					 				 Edge contributes = graph.addEdge("contribute", userNode, revisionNode, "Contribute");
					 				 contributes.setProperty("contributionSize", currentJsonObject.getInt("size"));
					 			  }
					 			  
					 			 if((i!=0)&&(WikiUtil.rCheck("userid", currentJsonObject.getInt("userid"), graph)==false)){
					 				  
					 				 Vertex userNode= graph.getVertices("userid", currentJsonObject.getInt("userid")).iterator().next();
					 				 Vertex parentVersionNode=graph.getVertices("revid",currentJsonObject.getInt("parentid")).iterator().next();
					 				 sizeDiff=Math.abs((int)parentVersionNode.getProperty("size")-currentJsonObject.getInt("size"));
					 				 Edge contributes = graph.addEdge("contribute", userNode, revisionNode, "Contribute");
					 				 contributes.setProperty("contributionSize", sizeDiff);
					 			  }
					 			  
					 			  //All the versions are connected to each other like (Page)<-(Latest)<-(Latest-1)<-...<-(Last)
					 			  
					 			  if(i==arr.length()-1){//The latest version will be connected to the Page itself and to the previous revision too
					 				  
					 				  Vertex parentPage=pageNode;
					 				  Edge isRevision = graph.addEdge("PreviousVersionOfPage", parentPage,revisionNode,"PreviousVersionOfPage");
					 			  }
					 			  
					 			  if(i!=0){//To link the current revision to the previous versions
					 				Vertex parentVersionNode=graph.getVertices("revid",currentJsonObject.getInt("parentid")).iterator().next();
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
			
}		
		
	}

}
