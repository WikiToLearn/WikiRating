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

	private OrientGraphFactory factory ;
	private OrientGraphFactory factoryNT;

	public DbConnection(){
		factory = new OrientGraphFactory("remote:localhost/wikirate",
				"root", "wikitolearn");
		factoryNT = new OrientGraphFactory("remote:localhost/wikirate", "root", "wikitolearn");
		factoryNT.declareIntent(new OIntentMassiveInsert());
	}
	/**
	 * This method will return the OreintDB graph instance after connection.
	 * @return	Transaction enabled OrientGraph object
	 */
	public OrientGraph getGraph() {
        return factory.getTx();
	}

	/**
	 * This method will return the OreintDB graph instance after connection,
	 * for massive inserts to improve performance,no transaction method
	 * @return	Transaction disabled OrientGraph object
	 */

	public OrientGraphNoTx getDbGraphNT() {
        return factoryNT.getNoTx();
	}
}
