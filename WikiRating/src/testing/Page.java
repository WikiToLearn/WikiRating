package testing;

import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class Page {
	
	
	public static String getAllPages(int ns) {
		  
		  
		  //Accessing MediaWiki API using Wikidata Toolkit
		  
		  String result = "";
		  
		  ApiConnection con=Connections.getInstance().getApiConnection();
		  InputStream in = null;
		 
			  in=WikiUtil.reqSend(con,WikiUtil.getPageParam(ns+""));
			  result=WikiUtil.streamToString(in);
		  
	        return result;
	}
	
	public static String insertPages() throws JSONException{
		
		
 		
		 //Connecting to database using Tinkerpop Blueprints
		 
		 OrientGraph graph = Connections.getInstance().getDbGraph();
		 String result="no insertions",allPages="";
		 try {
			 for(int ns=0;ns<=15;ns++){
			 
			//JSON interpretation
			 try {  
				 	allPages = getAllPages(ns);
				 	JSONObject js=new JSONObject(allPages);
				 	JSONObject js2=js.getJSONObject("query");
				 	JSONArray arr=js2.getJSONArray("allpages");
					JSONObject dummy;
				 	for(int i=0;i<arr.length();i++){
				 		dummy=arr.getJSONObject(i);
				 		
				 		if(WikiUtil.rCheck("pid",dummy.getInt("pageid"),graph)){
				 		result=result+dummy.get("title")+" \n";
				 					 			
				 		//Adding pages to database
				 		try{
				 			  System.out.println(dummy.getString("title"));
				 			  Vertex ver = graph.addVertex("class:Page"); // 1st OPERATION: IMPLICITLY BEGINS TRANSACTION
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
		   graph.shutdown();
		 }
		 
		 return result;
		
	}

}
