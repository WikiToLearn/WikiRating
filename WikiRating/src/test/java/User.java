package test.java;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class User {


	public static void insertAllUsers(){
		
		OrientGraph graph = Connections.getInstance().getDbGraph();
		String allUsers="";
		String nextPageUsername="";
		boolean loopCounter=true;				//To re query till we get all the users on the platform
		while(loopCounter){

			try {  
				allUsers=getAllUsers(nextPageUsername);
				
				//Getting the JSON formatted String to process.
				JSONObject js=new JSONObject(allUsers);
				//Detecting the page end
				if(js.has("query-continue")){
					nextPageUsername=js.getJSONObject("query-continue").getJSONObject("allusers").getString("aufrom");
				}
				else
					loopCounter=false;
				
					
				JSONObject js2=js.getJSONObject("query");
				JSONArray arr=js2.getJSONArray("allusers");
				JSONObject dummy;
				
				//Storing all the Users in a particular namespace
				
				for(int i=0;i<arr.length();i++){
					dummy=arr.getJSONObject(i);
					
					if(WikiUtil.rCheck("userid",dummy.getInt("userid"),graph)){	//This is a makeshift way to avoid duplicate insertion.
						
						//Adding Users to database
						try{
							System.out.println(dummy.getString("name"));
							Vertex ver = graph.addVertex("class:User"); // 1st OPERATION: will implicitly begin the transaction and this command will create the class too.
							ver.setProperty( "username", dummy.getString("name"));
							ver.setProperty("userid",dummy.getInt("userid"));
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
			
			
			
		}graph.shutdown();
	}
	
	
	
	
	//This is the helper method to return the a JSON formatted string queried from the MediaWiki API to get all the users on the platform
	
	public static String getAllUsers(String username) {
		
		
		//Accessing MediaWiki API using Wikidata Toolkit
		String result = "";
		ApiConnection con=Connections.getInstance().getApiConnection();
		InputStream in=WikiUtil.reqSend(con,WikiUtil.getUserParam(username));
		result=WikiUtil.streamToString(in);
		return result;
	}
		
		
		
	
}
