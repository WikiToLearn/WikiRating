package testing;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class Revision {
	
	public static String getAllRevisions(){
		
		String result="";
		OrientGraph graph=Connections.getInstance().getDbGraph();
		String res="==";int totalInsertions=0;
		System.out.println("=====Checkpoint for Revisions==========");
		for (Vertex v : graph.getVertices("@class","Page")) {
			result=getRevision(v.getProperty("pid").toString());
			//System.out.println(result);
			//res=res+result+" \n";
			 //Connecting to database using Tinkerpop Blueprints
			 
			 try {
				 
				
				//JSON interpretation
				 try {  
					 	JSONObject js=new JSONObject(result);
					 	JSONObject js2=js.getJSONObject("query").getJSONObject("pages").getJSONObject(v.getProperty("pid").toString());
					 	JSONArray arr=js2.getJSONArray("revisions");
						JSONObject dummy;
						/*dummy=arr.getJSONObject(0);
						System.out.println("=====Checkpoint10==========");
						System.out.println(dummy.getInt("revid"));*/
						//System.out.println(v.getProperty("name").toString()+arr.length());
						
					 	for(int i=0;i<arr.length();i++){
					 		dummy=arr.getJSONObject(i);
					 		//res=res+dummy.getInt("revid")+" \n";
					 		System.out.println(v.getProperty("name").toString()+dummy.getInt("revid"));
					 		//Adding pages to database
					 		if(WikiUtil.rCheck("revid", dummy.getInt("revid"), graph)){
					 			totalInsertions++;
					 		try{
					 			  Vertex ver = graph.addVertex("class:Revision"); // 1st OPERATION: IMPLICITLY BEGINS TRANSACTION
					 			  ver.setProperty( "Page", v.getProperty("name").toString());
					 			  ver.setProperty("revid",dummy.getInt("revid"));
					 			  ver.setProperty("parentid",dummy.getInt("parentid"));
					 			  ver.setProperty("user",dummy.getString("user"));
					 			  ver.setProperty("size",dummy.getInt("size"));
					 			 					 			  
					 			  if(i!=0){
					 				Vertex parent=graph.getVertices("revid",dummy.getInt("parentid")).iterator().next();
					 				Edge isRevision = graph.addEdge("link", parent, ver, "version");
					 			  }
					 			  else{
					 				  Vertex parentPage=graph.getVertices("pid",v.getProperty("pid")).iterator().next();
					 				  Edge isRevision = graph.addEdge("link", parentPage, ver, "Pversion");
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
			 
				 
			 } finally {
			   
			 }
			
}		graph.shutdown();
		res=totalInsertions+"";
		return res;		
		
	}

	
	public static String getRevision(String pid ){
		String result = "";
		  
		  ApiConnection con=Connections.getInstance().getApiConnection();
		  InputStream in = null;
		 
			  in=WikiUtil.reqSend(con,WikiUtil.getRevisionParam(pid));
			  result=WikiUtil.streamToString(in);
			 
			  return result;
	}
	
	
	
	
	
	
	
	
	
	
}
