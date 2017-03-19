/**
 * 
 */
package org.wikitolearn.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wikitolearn.utils.DbConnection;

/**
 * @author aletundo
 *
 */
public abstract class GenericDAO {

	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	protected DbConnection connection;

	/**
	 * This method is used to create the class on the DB.
	 * It creates an unique index on the id to avoid duplicated.
	 * @return void
	 */
	public abstract void createDatabaseClass();

}