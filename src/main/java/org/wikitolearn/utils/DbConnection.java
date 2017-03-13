/**
 * 
 */
package org.wikitolearn.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

/**
 * @author alessandro
 *
 */
@Service
public class DbConnection {
	
	private OrientGraphFactory factory;
	
	@Autowired
	public DbConnection(@Value("${DB_URL}") String dbUrl, @Value("${DB_USER}")
	String dbUser, @Value("${DB_PWD}") String dbPwd){

		factory = new OrientGraphFactory(dbUrl, dbUser, dbPwd).setupPool(1, 20);
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
		factory.declareIntent(new OIntentMassiveInsert());
        return factory.getNoTx();
	}
}
