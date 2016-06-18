package main.java;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class LinkUserContributions {

	public static void linkAll() {

		String result = "";
		OrientGraph graph = Connections.getInstance().getDbGraph();
		for (Vertex userNode : graph.getVertices("@class", "User")) {

			// Fetching the user contribution on a particular page
			result = getUserContribution(userNode.getProperty("username").toString());

			// System.out.println(result);
			try {
				// JSON interpretation
				try {

					JSONObject js = new JSONObject(result);
					JSONObject js2 = js.getJSONObject("query");
					JSONArray arr = js2.getJSONArray("usercontribs");
					JSONObject dummy;
					//System.out.println("*");
					// System.out.println(arr.length());
					for (int i = 0; i < arr.length(); i++) {

						dummy = arr.getJSONObject(i);

						try {
							// Check to avoid null links
							if (graph.getVertices("revid", dummy.getInt("revid")).iterator().hasNext() == false) {
								continue;
							} // Creating edges between the User and his contribution
							Vertex targetVersionNode = graph.getVertices("revid", dummy.getInt("revid")).iterator().next();
							Edge contributes = graph.addEdge("contribute", userNode, targetVersionNode, "Contribute");
							contributes.setProperty("consize", Math.abs(dummy.getInt("sizediff")));
							graph.commit();
							System.out.println(userNode.getProperty("username") + " Contributes to "
									+ dummy.getString("title") + " to " + targetVersionNode.getProperty("revid")
									+ " of size " + Math.abs(dummy.getInt("sizediff")));
						} catch (Exception e) {
							e.printStackTrace();
							graph.rollback();
						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception ee) {
				ee.printStackTrace();
			}

		}
		graph.shutdown();
	}

	// This is the helper method to return the a JSON formatted string queried
	// from the MediaWiki API to get all the user contributions.
	public static String getUserContribution(String username) {

		String result = "";
		ApiConnection con = Connections.getInstance().getApiConnection();
		InputStream in = WikiUtil.reqSend(con, WikiUtil.getUserContriParam(username));
		result = WikiUtil.streamToString(in);
		return result;

	}

}
