/**
 * 
 */
package org.wikitolearn.dao;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.wikitolearn.models.User;
import org.wikitolearn.utils.DbConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Moreover it creates a unique index on the userid property to avoid duplication.
     */
	public void createDBClass() {
        LOG.info("Creating DB classes for RevisionDAO...");
        OrientGraphNoTx graph = connection.getGraphNT();
	    try{
            OrientVertexType vertex = graph.createVertexType("User");
            vertex.createProperty("userid", OType.INTEGER).setMandatory(true);
            vertex.createIndex("userid", OClass.INDEX_TYPE.UNIQUE, "userid");
	    } catch( Exception e ) {
            LOG.error("Something went wrong during class creation. Operation will be rollbacked.", e.getMessage());
            graph.rollback();
        } finally {
	        graph.shutdown();
        }
    }


    /**
     * Insert all the given users in the database as vertexes.
     * If there are duplicates all the insertion is rolled back.
     * @param users List<User> The pages to be inserted
     * @return boolean True if insertion was committed, false otherwise
     */
    public Boolean insertUsers(List<User> users){
    	OrientGraph graph = connection.getGraph();
    	LOG.info("Starting to insert users...");
		try{
			for(User p : users) {
                try {
                    Map<String, Object> props = new HashMap<>();
                    props.put("userid", p.getUserid());
                    props.put("username", p.getUsername());
                    props.put("votesReliability", p.getVotesReliability());
                    props.put("contributesReliability", p.getContributesReliability());
                    props.put("totalReliability", p.getTotalReliability());

                    Vertex userNode = graph.addVertex("class:User", props);
                    graph.commit();
                    LOG.info("User inserted " + userNode.toString());
                } catch (ORecordDuplicatedException or) {
                    LOG.error("The user is already in the DB. Operation will be rollbacked.", or.getMessage());
                    graph.rollback();
                }
            }
			LOG.info("Users insertion ended");
			graph.shutdown();
			return true;
        } catch( Exception e ) {
            LOG.error("Something went wrong during user insertion. Operation will be rollbacked.", e.getMessage());
            graph.rollback();
            graph.shutdown();
        }
        return false;
    }
	
}
