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
 * This class will deal with the procedures to links the pages which points to some other pages.
 * So this will interconnect all the Backlinks. 
 */

public class LinkPages {
	
	/**
	 * This method will link all the Pages in the database
	 * @param key	Name of the key here '@class'
	 * @param value	Value of the key here 'Page'
	 */
	public static void linkAll(String key,String value){

		OrientGraph graph = Connections.getInstance().getDbGraph();
		String result="";
		int inLinks;
		
		//Iterating on every vertex to check it's backlinks
		
		for (Vertex pageNode : graph.getVertices(key,value)) {
			
			//Getting the JSON formatted String to process.
			result=getBacklinks((int)(pageNode.getProperty("pid")));	
			inLinks=0;
			
			//JSON interpretation of the fetched String
			
			 try {  
				 	JSONObject js=new JSONObject(result);
				 	JSONObject js2=js.getJSONObject("query");
				 	//This array has all the backlinks the page has.
				 	JSONArray arr=js2.getJSONArray("backlinks");	
				 	JSONObject currentJsonObject;							
				 	inLinks=arr.length();
				 	
				 	System.out.println(pageNode.getProperty("title").toString()+" has inLinks = "+inLinks);
				 	
				 	//Iterating to get all the backlinks of a particular node(Page)
				 	
				 	for(int i=0;i<arr.length();i++){
				 		currentJsonObject=arr.getJSONObject(i);
				 		
				 		try{	
				 			
				 			//Getting the node linked to the current page.
				 			Vertex backLink=graph.getVertices("pid",currentJsonObject.getInt("pageid")).iterator().next();	
				 			//Creating Edge in between the 2 vertices.
				 			Edge isbackLink = graph.addEdge("Backlink", backLink, pageNode, "Backlink");				
				 			
				 			System.out.println(pageNode.getProperty("title").toString()+" is linked to "+backLink.getProperty("title").toString());
				 			
				 		graph.commit();														
				 		} catch( Exception e ) {
				 			e.printStackTrace();
				 			//In case the transaction fails we will rollback.
				 			graph.rollback();																	
				 		}
				 		
				 	}
			 } catch (JSONException e) { 
				 e.printStackTrace();
			 }
			 
		}
		//graph.commit();	
		graph.shutdown();
		//Revision.getAllRevisions();
	}
					
	
	
	
	/**
	 * This method will return the a JSON formatted string queried
	 * from the MediaWiki API to get all the backlinking pages
	 * @param pid	PageID of the page whose backlinks are to be fetched
	 * @return	A JSON formatted String containing all the backlinks  
	 */
	public static String getBacklinks(int pid) {
		  
		
		String result = "";
		ApiConnection con=Connections.getInstance().getApiConnection();
		//Getting the InputStream object having JSON form MediaWiki API
		InputStream in=WikiUtil.reqSend(con,WikiUtil.getLinkParam(pid+""));	
		//Converting the InputStream to String
		result=WikiUtil.streamToString(in);		  							
		return result;
		  
	}
	
}
