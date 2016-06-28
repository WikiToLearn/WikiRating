package main.java.models;

/**This class will be used only for the creation of the classes in the database.
 * However the classes can be created on the fly too while inserting so this class is not used.
 */
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import main.java.utilities.Propaccess;

public class Initialise {

	private static Initialise initial = new Initialise();

	public static Initialise getInstance() {
		return initial;
	}

	public void setClass() {
		OrientGraphFactory factory = new OrientGraphFactory(Propaccess.getPropaccess("DB_URL"),
				Propaccess.getPropaccess("USER"), Propaccess.getPropaccess("PASSWD"));
		OrientGraph graph = factory.getTx();
		graph.createVertexType("Page");
		graph.createVertexType("User");
		graph.createVertexType("Revision");
	}

}
