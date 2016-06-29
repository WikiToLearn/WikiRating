package main.java.models;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import main.java.utilities.Propaccess;

/**This class will be used only for the creation of the classes in the database.
 * This is a singleton class
 * However the classes can be created on the fly too while inserting so this class is not used.
 */

public class Initialise {

	private static Initialise initial = new Initialise();

	public static Initialise getInstance() {
		return initial;
	}

	
	/**
	 * This method will create the various classes in the database 
	 */
	public void setClass() {
		OrientGraphFactory factory = new OrientGraphFactory(Propaccess.getPropaccess("DB_URL"),
				Propaccess.getPropaccess("USER"), Propaccess.getPropaccess("PASSWD"));
		OrientGraph graph = factory.getTx();
		graph.createVertexType("Page");
		graph.createVertexType("User");
		graph.createVertexType("Revision");
	}

}
