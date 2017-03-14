/**
 * 
 */
package org.wikitolearn.dao;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import java.util.List;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.wikitolearn.models.Page;
import org.wikitolearn.utils.DbConnection;

/**
 * @author alessandro
 *
 */
@Repository
public class PageDAO {
	private static final Logger LOG = LoggerFactory.getLogger(PageDAO.class);

    @Autowired
    private DbConnection connection;

	/**
	 * This method is used to create the class on the DB.
	 */
	public void createDBClass() {
		LOG.info("Getting the connection...");
		OrientGraphNoTx graph = connection.getDbGraphNT();
		try{
			graph.createVertexType("class:Page");
		} catch( Exception e ) {
			LOG.error("Something went wrong during class creation. Operation will be rollbacked.", e.getMessage());
			graph.rollback();
		} finally {
            graph.shutdown();
        }
	}


    /**
     * Insert all the given pages in the database as vertexes. 
     * @param pages List<Page> The pages to be inserted
     * @return boolean True if insertion was committed, false otherwise
     */
    public Boolean insertPages(List<Page> pages){
    	LOG.info("Getting the connection...");
    	OrientGraph graph = connection.getGraph();
    	LOG.info("Starting to insert pages...");
		try{
			for(Page p : pages){
				// Implicitly begins the transaction and create the class too
				Vertex pageNode = graph.addVertex("class:Page");
				pageNode.setProperty( "title", p.getTitle());
				pageNode.setProperty("pid", p.getPageid());
				pageNode.setProperty("pageRank", 0.0);
				LOG.info("Page inserted " + pageNode.toString());
			}
			graph.commit();
			LOG.info("Pages insertion committed");
			return true;
		} catch( Exception e ) {
			LOG.error("Something went wrong during page insertion. Operation will be rollbacked.", e.getMessage());
			graph.rollback();
		}finally {
		    graph.shutdown();
        }
        return false;
    }
	
}
