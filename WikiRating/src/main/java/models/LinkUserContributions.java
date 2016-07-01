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


/**This class will link the user to all their respective contributions on the platform
 * 
 */

public class LinkUserContributions {

	/**
	 * This method will link all the Users to their work.
	 */
	
	public static void linkAll() {

		String result = "";
		OrientGraph graph = Connections.getInstance().getDbGraph();
		//graph.setUseLightweightEdges(false);
		for (Vertex userNode : graph.getVertices("@class", "User")) {

			// Fetching the user contribution on a particular page
			result = getUserContribution(userNode.getProperty("username").toString());
	
				// JSON interpretation
				try {

					JSONObject js = new JSONObject(result);
					JSONObject js2 = js.getJSONObject("query");
					JSONArray arr = js2.getJSONArray("usercontribs");
					JSONObject currentJsonObject;
					System.out.println(userNode.getProperty("username")+"      "+arr.length());
					System.out.println("*");

					for (int i = 0; i < arr.length(); i++) {

						currentJsonObject = arr.getJSONObject(i);

						try {
							// Check to avoid null links
							if (graph.getVertices("revid", currentJsonObject.getInt("revid")).iterator().hasNext() == false) {
								continue;
							} // Creating edges between the User and his contribution
							Vertex targetVersionNode = graph.getVertices("revid", currentJsonObject.getInt("revid")).iterator().next();
							Edge contributes = graph.addEdge("contribute", userNode, targetVersionNode, "Contribute");
							contributes.setProperty("contributionSize", Math.abs(currentJsonObject.getInt("sizediff")));
							//graph.commit();
							System.out.println(userNode.getProperty("username") + " Contributes to "
									+ currentJsonObject.getString("title") + " to " + targetVersionNode.getProperty("revid")
									+ " of size " + Math.abs(currentJsonObject.getInt("sizediff")));
						} catch (Exception e) {
							e.printStackTrace();
							graph.rollback();
						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			
			graph.commit();	
		}

		graph.shutdown();
	}
	
	
	/**
	 * This method will return the a JSON formatted string queried
	 * from the MediaWiki API to get all the user contributions.
	 * @param username	Username of the user whose contributions are to be fetched
	 * @return	A JSON formatted String containing all user contributions
	 */

	public static String getUserContribution(String username) {

		String result = "";
		ApiConnection con = Connections.getInstance().getApiConnection();
		InputStream in = WikiUtil.reqSend(con, WikiUtil.getUserContriParam(username));
		result = WikiUtil.streamToString(in);
		return result;

	}

}
