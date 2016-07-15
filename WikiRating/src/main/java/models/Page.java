package main.java.models;

import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.controllers.WikiUtil;
import main.java.utilities.AllowedNamespaces;
import main.java.utilities.Connections;
import main.java.utilities.PropertiesAccess;


/** 
 * This class will populate the database with all the pages available on the WikiPlatform.
 */

public class Page {
	

	/**
	 * This method will insert all the pages from all the available Namespaces in database.
	 */
	
	public static void insertPages(){
	
		OrientGraph graph = Connections.getInstance().getDbGraph();
		String allPages="";
		
		try {
			//Now we will be iterating over all the namespaces to get all the pages in each og them.
			
			for(AllowedNamespaces namespace:AllowedNamespaces.values()){
				
				
				//JSON interpretation
				try {  
					//Getting the JSON formatted String to process.
					allPages = getAllPages(namespace.getValue());							
					JSONObject js=new JSONObject(allPages);
					JSONObject js2=js.getJSONObject("query");
					JSONArray arr=js2.getJSONArray("allpages");
					JSONObject dummy;
					
					//Storing all the pages in a particular namespace
					
					for(int i=0;i<arr.length();i++){
						dummy=arr.getJSONObject(i);
						
						//This is a makeshift way to avoid duplicate insertion.
						if(WikiUtil.rCheck("pid",dummy.getInt("pageid"),graph)){	
							
							//Adding pages to database
							try{
								System.out.println(dummy.getString("title"));
								// 1st OPERATION: will implicitly begin the transaction and this command will create the class too.
								Vertex pageNode = graph.addVertex("class:Page"); 
								pageNode.setProperty( "title", dummy.getString("title"));
								pageNode.setProperty("pid",dummy.getInt("pageid"));
								pageNode.setProperty("namespace", namespace.getValue());
								pageNode.setProperty("currentPageVote",-1.0);
								pageNode.setProperty("currentPageReliability", -1.0);
								pageNode.setProperty("PageRating", 0.0);
								pageNode.setProperty("badgeNumber",4);
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
			}
			
		} finally {
			//graph.commit();
			graph.shutdown();
		}
		
	}
							
 		
	
	
	/**
	 * This method will return the a JSON formatted string queried
	 * from the MediaWiki API get all the pages in the particular Namespace
	 * @param ns	Namespace whose pages are to be fetched
	 * @return	A JSON formatted String containing all the pages  
	 */
	
	public static String getAllPages(int ns) {
		
		//Accessing MediaWiki API using Wikidata Toolkit
		
		String result = "";
		ApiConnection con=Connections.getInstance().getApiConnection();
		InputStream in=WikiUtil.reqSend(con,WikiUtil.getPageParam(Integer.toString(ns)));
		result=WikiUtil.streamToString(in);
		return result;
		  
	}

}
