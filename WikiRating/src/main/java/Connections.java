package main.java;

/**This class deals with the handling of all the connections to Database and MediaWiki API
 * 
 */
import org.wikidata.wdtk.wikibaseapi.ApiConnection;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class Connections {

	private static Connections singletonConnection = new Connections();

	// This method will return an the sole object already created.
	public static Connections getInstance() {
		return singletonConnection;
	}

	// This method will return the MediaWWiki Connection object.
	public ApiConnection getApiConnection() {
		ApiConnection con = new ApiConnection(Propaccess.getPropaccess("API_URL"));
		return con;
	}

	// This method will return the OreintDB graph instance after connection.
	public OrientGraph getDbGraph() {

		OrientGraphFactory factory = new OrientGraphFactory(Propaccess.getPropaccess("DB_URL"),
				Propaccess.getPropaccess("USER"), Propaccess.getPropaccess("PASSWD"));
		OrientGraph graph = factory.getTx();

		return graph;
	}

}
