/**
 * 
 */
package org.wikitolearn.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.wikitolearn.dao.UserDAO;
/**
 * @author alessandro
 *
 */
@Component
public class DbConnection {

	private static final Logger LOG = LoggerFactory.getLogger(UserDAO.class);
	private OrientGraphFactory factory;
	
	@Autowired
	public DbConnection(@Value("${db.url}") String dbUrl, @Value("${db.user}")
	String dbUser, @Value("${db.password}") String dbPwd){

		factory = new OrientGraphFactory(dbUrl, dbUser, dbPwd).setupPool(1, 20);
	}
	/**
	 * This method will return the OreintDB graph instance after connection.
	 * @return	Transaction enabled OrientGraph object
	 */
	public OrientGraph getGraph(){
	    LOG.info("Getting an instance of OrientDB....");
        return factory.getTx();
	}

	/**
	 * This method will return the OreintDB graph instance after connection,
	 * for massive inserts to improve performance,no transaction method
	 * @return	Transaction disabled OrientGraph object
	 */

	public OrientGraphNoTx getGraphNT() {
		LOG.info("Getting a NoTX instance of OrientDB....");
		factory.declareIntent(new OIntentMassiveInsert());
        return factory.getNoTx();
	}
}
