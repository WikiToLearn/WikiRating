package main.java;

/** This class will populate the database with all the pages available on the WikiPlatform.
 */
import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class Page {
	
	
	//This method will insert all the pages from all the available Namespaces in database.
	public static void insertPages(){
	
		OrientGraph graph = Connections.getInstance().getDbGraph();
		String allPages="";
		final int NO_OF_NAMESPACES=15;
		
		try {
			//Now we will be iterating over all the namespaces to get all the pages in each og them.
			
			for(int ns=0;ns<=NO_OF_NAMESPACES;ns++){
				
				//JSON interpretation
				try {  
					allPages = getAllPages(ns);							//Getting the JSON formatted String to process.
					JSONObject js=new JSONObject(allPages);
					JSONObject js2=js.getJSONObject("query");
					JSONArray arr=js2.getJSONArray("allpages");
					JSONObject dummy;
					
					//Storing all the pages in a particular namespace
					
					for(int i=0;i<arr.length();i++){
						dummy=arr.getJSONObject(i);
						
						if(WikiUtil.rCheck("pid",dummy.getInt("pageid"),graph)){	//This is a makeshift way to avoid duplicate insertion.
							
							//Adding pages to database
							try{
								System.out.println(dummy.getString("title"));
								Vertex ver = graph.addVertex("class:Page"); // 1st OPERATION: will implicitly begin the transaction and this command will create the class too.
								ver.setProperty( "name", dummy.getString("title"));
								ver.setProperty("pid",dummy.getInt("pageid"));
								ver.setProperty("ns", ns);
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
			//LinkPages.linkAll();
		}
		
	}
							
							
		
 		
	
	//This is the helper method to return the a JSON formatted string queried from the MediaWiki API to get all the pages in the particular Namespace
	
	public static String getAllPages(int ns) {
		  
		  
		  //Accessing MediaWiki API using Wikidata Toolkit
		  
		  String result = "";
		  
		  ApiConnection con=Connections.getInstance().getApiConnection();
		   
		 
		   InputStream in=WikiUtil.reqSend(con,WikiUtil.getPageParam(ns+""));
			  result=WikiUtil.streamToString(in);
		  
	        return result;
	}

}
