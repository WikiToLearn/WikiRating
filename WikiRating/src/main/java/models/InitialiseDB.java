package main.java.models;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import main.java.utilities.Connections;


/**
 * This class will be used only for the creation of the classes in the database.
  */

public class InitialiseDB {


	/**
	 * This method will create  various classes in the database 
	 */
	public static void createClass() {
		// No transaction to avoid warnings
		OrientGraphNoTx graph = Connections.getInstance().getDbGraphNT();
		graph.createVertexType("Page");
		System.out.println("====Created Class Page====");
		
		graph.createVertexType("User");
		System.out.println("====Created Class User====");
		
		graph.createVertexType("Revision");
		System.out.println("====Created Class Revision====");
		
		graph.createEdgeType("Backlink");
		System.out.println("====Created Class Backlink====");
		
		graph.createEdgeType("PreviousRevision");
		System.out.println("====Created Class PreviousRevision====");
		
		graph.createEdgeType("PreviousVersionOfPage");
		System.out.println("====Created Class PreviousVersionOfPage====");
		
		graph.createEdgeType("Contribute");
		System.out.println("====Created Class Contribute====");
		
		graph.createEdgeType("Review");
		System.out.println("====Created Class Review====");
		
		graph.shutdown();
	}

}
