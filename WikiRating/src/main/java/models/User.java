package main.java.models;

import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.controllers.WikiUtil;
import main.java.utilities.Connections;
import main.java.utilities.Loggings;

/**This class inserts all the users available on the WikitoLearn Platform
 */

public class User {
	static Class className=User.class;
	final static double INITIAL_USER_CREDIBILTY=0.4;
	/**
	 * This method will insert all the users into the database
	 */
	public static void insertAllUsers(){

		OrientGraph graph = Connections.getInstance().getDbGraph();
		String allUsers="";
		String nextPageUsername="";
		//To re query till we get all the users on the platform
		boolean loopCounter=true;
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
				JSONObject currentJsonObject;

				//Storing all the Users in a particular namespace

				for(int i=0;i<arr.length();i++){
					currentJsonObject=arr.getJSONObject(i);
					//This is a makeshift way to avoid duplicate insertion.
					if(WikiUtil.rCheck("userid",currentJsonObject.getInt("userid"),graph)){
						Loggings.getLogs(className).info(currentJsonObject.getString("name"));

						//Adding Users to database
						try{
							Loggings.getLogs(className).info(currentJsonObject.getString("name"));
							// 1st OPERATION: will implicitly begin the transaction and this command will create the class too.
							Vertex userNode = graph.addVertex("class:User");
							userNode.setProperty( "username", currentJsonObject.getString("name"));
							userNode.setProperty("userid",currentJsonObject.getInt("userid"));
							//Initial credibility of the user
							userNode.setProperty("credibility",INITIAL_USER_CREDIBILTY);
							userNode.setProperty("totalContributedBytes", 0);
							graph.commit();
						} catch( Exception e ) {
							Loggings.getLogs(className).error(e);
							graph.rollback();
						}

					}
				}
			} catch (JSONException e) {
				Loggings.getLogs(className).error(e);
			}



		}//graph.commit();
		graph.shutdown();
	}



	/**
	 * This method will return the a JSON formatted string queried
	 * from the MediaWiki API to get all the users.
	 * @param username	Username of the user whose information has to be fetched
	 * @return	A JSON formatted String containing the user information
	 */
	public static String getAllUsers(String username) {

		//Accessing MediaWiki API using Wikidata Toolkit
		String result = "";
		ApiConnection con=Connections.getInstance().getApiConnection();
		InputStream in=WikiUtil.reqSend(con,WikiUtil.getUserParam(username));
		result=WikiUtil.streamToString(in);
		return result;

	}

	/**
	 * Function to expose the total bytes contributed by a certain user
	 * @param username	Username of the user whose contributions are to be fetched
	 * @return	The total contributed bytes
	 */
	public static int getTotalContributionBytes(String username){

		int totalContributionBytes=0;
		OrientGraph graph = Connections.getInstance().getDbGraph();
		Vertex userNode=graph.getVertices("username",username).iterator().next();
		totalContributionBytes=userNode.getProperty("totalContributedBytes");
		graph.shutdown();
		return totalContributionBytes;

	}


}
