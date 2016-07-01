package main.java.utilities;

import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

/**This singleton class deals with the handling of all the connections to Database and MediaWiki API
 * 
 */

public class Connections {

	private static Connections singletonConnection = new Connections();

	/**
	 * This method return sole object created of the class
	 * @return	Connections object
	 */
	public static Connections getInstance() {
		return singletonConnection;
	}

	 /**
	 * This method creates a MediaWWiki Connection object
	 * @return	ApiConnection object
	 */
	
	public ApiConnection getApiConnection() {
		ApiConnection con = new ApiConnection(PropertiesAccess.getConfigProperties("API_URL"));
		return con;
	}

	 /**
	 * This method will return the OreintDB graph instance after connection.
	 * @return	Transaction enabled OrientGraph object 
	 */
	public OrientGraph getDbGraph() {

		OrientGraphFactory factory = new OrientGraphFactory(PropertiesAccess.getConfigProperties("DB_URL"),
		PropertiesAccess.getConfigProperties("USER"), PropertiesAccess.getConfigProperties("PASSWD"));
		OrientGraph graph = factory.getTx();

		return graph;
	}
	
	/**
	 * This method will return the OreintDB graph instance after connection,
	 * for massive inserts to improve performance,no transaction method 
	 * @return	Transaction disabled OrientGraph object
	 */
	
	public OrientGraphNoTx getDbGraphNT() {

		OrientGraphFactory factory = new OrientGraphFactory(PropertiesAccess.getConfigProperties("DB_URL"),
		PropertiesAccess.getConfigProperties("USER"), PropertiesAccess.getConfigProperties("PASSWD"));
		factory.declareIntent(new OIntentMassiveInsert());
		OrientGraphNoTx graph =factory.getNoTx();

		return graph;
	}
	

}
