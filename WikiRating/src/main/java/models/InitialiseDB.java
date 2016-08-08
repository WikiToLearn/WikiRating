package main.java.models;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import main.java.utilities.Connections;
import main.java.utilities.Loggings;

/**
 * This class will be used only for the creation of the classes in the database.
  */

public class InitialiseDB {
	static Class className=InitialiseDB.class;

	/**
	 * This method will create  various classes in the database
	 */
	public static void createClass() {
		// No transaction to avoid warnings
		OrientGraphNoTx graph = Connections.getInstance().getDbGraphNT();
		graph.createVertexType("Page");
		Loggings.getLogs(className).info("====Created Class Page====");

		graph.createVertexType("User");
		Loggings.getLogs(className).info("====Created Class User====");

		graph.createVertexType("Revision");
		Loggings.getLogs(className).info("====Created Class Revision====");

		graph.createEdgeType("Backlink");
		Loggings.getLogs(className).info("====Created Class Backlink====");

		graph.createEdgeType("PreviousRevision");
		Loggings.getLogs(className).info("====Created Class PreviousRevision====");

		graph.createEdgeType("PreviousVersionOfPage");
		Loggings.getLogs(className).info("====Created Class PreviousVersionOfPage====");

		graph.createEdgeType("Contribute");
		Loggings.getLogs(className).info("====Created Class Contribute====");

		graph.createEdgeType("Review");
		Loggings.getLogs(className).info("====Created Class Review====");

		graph.shutdown();
	}

}
