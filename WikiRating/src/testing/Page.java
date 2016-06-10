package testing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class Page {
	
	public static String getAllPages() {
		
		String result = Connections.getInstance().getAllPages();
 		
 		
		 
		 //Connecting to database using Tinkerpop Blueprints
		 
		 OrientGraph graph = Connections.getInstance().getDbGraph();
		 try {
			 
			//JSON interpretation
			 try {  
				 	JSONObject js=new JSONObject(result);
				 	JSONObject js2=js.getJSONObject("query");
				 	JSONArray arr=js2.getJSONArray("allpages");
					result="";
				 	JSONObject dummy;
				 	for(int i=0;i<arr.length();i++){
				 		dummy=arr.getJSONObject(i);
				 		result=result+dummy.get("title")+" \n";
				 					 			
				 		//Adding pages to database
				 		try{
				 			  Vertex ver = graph.addVertex("class:Page"); // 1st OPERATION: IMPLICITLY BEGINS TRANSACTION
				 			  ver.setProperty( "name", dummy.get("title"));
				 			  ver.setProperty("pid",dummy.get("pageid"));
				 			  graph.commit();
				 			} catch( Exception e ) {
				 			  graph.rollback();
				 			}
				 	
				 		
				 	}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			 
			 
		 } finally {
		   graph.shutdown();
		 }
		 return result;
		
	}

}
