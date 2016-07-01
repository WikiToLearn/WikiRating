package main.java.models;

import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.controllers.WikiUtil;
import main.java.utilities.Connections;


/**
 * This class will link the revisions to the corresponding pages
 */

public class Revision {
	
	//This 
	/**
	 * This method will compute the revisions for all the pages and link them
	 * @param key	Name of the key here '@class'
	 * @param value	Value of the key here 'Revision'
	 */
	public static void getAllRevisions(String key,String value){
		
		String result="";
		OrientGraph graph=Connections.getInstance().getDbGraph();
		System.out.println("=====Checkpoint for Revisions==========");
		for (Vertex pageNode : graph.getVertices(key,value)) {
			
			result=getRevision(pageNode.getProperty("pid").toString());	//Fetching the revision string for a particular page.
							
			try {
				//JSON interpretation
				 try {  
					 	JSONObject js=new JSONObject(result);
					 	JSONObject js2=js.getJSONObject("query").getJSONObject("pages").getJSONObject(pageNode.getProperty("pid").toString());
					 	JSONArray arr=js2.getJSONArray("revisions");
						JSONObject currentJsonObject;
						
						
					 	for(int i=0;i<arr.length();i++){
					 		currentJsonObject=arr.getJSONObject(i);
					 		
					 		System.out.println(pageNode.getProperty("title").toString()+currentJsonObject.getInt("revid"));
					 		
					 		//Adding pages to database without the duplicates
					 		if(WikiUtil.rCheck("revid", currentJsonObject.getInt("revid"), graph)){
					 			
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
					 			  
					 			  //All the versions are connected to each other like (Page)<-(Latest)<-(Latest-1)<-...<-(Last)
					 			  
					 			  if(i==arr.length()-1){//The latest version will be connected to the Page itself and to the previous revision too
					 				  
					 				  Vertex parentPage=graph.getVertices("pid",pageNode.getProperty("pid")).iterator().next();
					 				  Edge isRevision = graph.addEdge("PreviousVersionOfPage", parentPage,revisionNode,"PreviousVersionOfPage");
					 			  }
					 			  
					 			  if(i!=0){//To link the current revision to the previous versions
					 				Vertex parent=graph.getVertices("revid",currentJsonObject.getInt("parentid")).iterator().next();
					 				Edge isRevision = graph.addEdge("PreviousRevision", revisionNode,parent,"PreviousRevision");
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
		graph.shutdown();
//Pagerank.pageRankCompute();
		
			
		
	}

	//This is the helper method to return the a JSON formatted string queried from the MediaWiki API to get all the revisions for a particular pid.
	
	/**
	 * This method will return the a JSON formatted string queried
	 * from the MediaWiki API get all the pages in the particular Namespace
	 * @param pid	PageID of the Page whose revisions are to be returned
	 * @return	A JSON formatted String containing all the revisions 
	 */
	
	public static String getRevision(String pid ){

		String result = "";
		ApiConnection con=Connections.getInstance().getApiConnection();
		InputStream in=WikiUtil.reqSend(con,WikiUtil.getRevisionParam(pid));
		result=WikiUtil.streamToString(in);
		return result;

	}
	
	
	
	
}
