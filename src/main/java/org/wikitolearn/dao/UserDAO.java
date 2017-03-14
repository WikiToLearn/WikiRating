/**
 * 
 */
package org.wikitolearn.dao;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.wikitolearn.models.Page;
import org.wikitolearn.models.User;
import org.wikitolearn.utils.DbConnection;

import java.util.List;

/**
 * @author alessandro
 *
 */
@Repository
public class UserDAO {
	private static final Logger LOG = LoggerFactory.getLogger(UserDAO.class);

    @Autowired
    private DbConnection connection;

    /**
     * This method is used to create the class on the DB.
     */
	public void createDBClass() {
        LOG.info("Getting the connection...");
        OrientGraphNoTx graph = connection.getDbGraphNT();
	    try{
            graph.createVertexType("class:User");
	    } catch( Exception e ) {
            LOG.error("Something went wrong during class creation. Operation will be rollbacked.", e.getMessage());
            graph.rollback();
        } finally {
	        graph.shutdown();
        }
    }


    /**
     * Insert all the given users in the database as vertexes.
     * @param users List<User> The pages to be inserted
     * @return boolean True if insertion was committed, false otherwise
     */
    public Boolean insertUsers(List<User> users){
    	LOG.info("Getting the connection...");
    	OrientGraph graph = connection.getGraph();
    	LOG.info("Starting to insert users...");
		try{
			for(User p : users){
				// Implicitly begins the transaction and create the class too
				Vertex userNode = graph.addVertex("class:User");
				userNode.setProperty( "username", p.getUsername());
				userNode.setProperty("userid", p.getUserid());
				userNode.setProperty("votesReliability", 0.0);
				userNode.setProperty("contributesReliability", 0.0);
				userNode.setProperty("totalReliability", 0.0);
				LOG.info("User inserted " + userNode.toString());
			}
			graph.commit();
			LOG.info("Users insertion committed");
			return true;
		} catch( Exception e ) {
			LOG.error("Something went wrong during user insertion. Operation will be rollbacked.", e.getMessage());
			graph.rollback();
		} finally {
		    graph.shutdown();
        }
        return false;
    }
	
}
