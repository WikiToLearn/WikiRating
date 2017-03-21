/**
 * 
 */
package org.wikitolearn.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.wikitolearn.utils.DbConnection;

/**
 * @author aletundo
 *
 */
public abstract class GenericDAO {

	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	protected DbConnection connection;
	@Value("#{'${mediawiki.langs}'.split(',')}")
	protected List<String> langs;

	/**
	 * This method is used to create the class on the DB.
	 * It creates an unique index on the id to avoid duplicated.
	 * @return void
	 */
	public abstract void createDatabaseClass();

}