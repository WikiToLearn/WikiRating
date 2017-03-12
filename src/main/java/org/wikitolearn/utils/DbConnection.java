/**
 * 
 */
package org.wikitolearn.utils;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

/**
 * @author alessandro
 *
 */
public class DbConnection {
	/**
	 * This method will return the OreintDB graph instance after connection.
	 * @return	Transaction enabled OrientGraph object
	 */
	public OrientGraph getDbGraph() {

		OrientGraphFactory factory = new OrientGraphFactory("remote:localhost/wikirate",
		"root", "wikitolearn");
		OrientGraph graph = factory.getTx();

		return graph;
	}

	/**
	 * This method will return the OreintDB graph instance after connection,
	 * for massive inserts to improve performance,no transaction method
	 * @return	Transaction disabled OrientGraph object
	 */

	public OrientGraphNoTx getDbGraphNT() {

		OrientGraphFactory factory = new OrientGraphFactory("remote:localhost/wikirate", "root", "wikitolearn");
		factory.declareIntent(new OIntentMassiveInsert());
		OrientGraphNoTx graph =factory.getNoTx();

		return graph;
	}
}
