/**
 * 
 */
package org.wikitolearn.dao;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.wikitolearn.models.Page;
import org.wikitolearn.utils.DbConnection;

/**
 *
 * This class will handles the operations on DB handling the Pages
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
     * It creates an unique index on pageid to avoid duplicated.
	 */
	public void createDBClass() {
		LOG.info("Creating DB classes for PageDAO...");
		OrientGraphNoTx graph = connection.getDbGraphNT();
		try{
            OrientVertexType vertex = graph.createVertexType("Page");
            vertex.createProperty("pageid", OType.INTEGER).setMandatory(true);
            vertex.createIndex("pageid", OClass.INDEX_TYPE.UNIQUE, "pageid");
		} catch( Exception e ) {
			LOG.error("Something went wrong during class creation. Operation will be rollbacked.", e.getMessage());
			graph.rollback();
		} finally {
            graph.shutdown();
        }
	}


    /**
     * Insert all the given pages in the database as vertexes.
     * If there are duplicates all the insertion is rolled back.
     * @param pages List<Page> The pages to be inserted
     * @return boolean True if insertion was committed, false otherwise
     */
    public Boolean insertPages(List<Page> pages, String lang){
    	OrientGraph graph = connection.getGraph();
    	LOG.info("Starting to insert pages...");
		try{
			for(Page p : pages){
				Map<String, Object> props = new HashMap<>();
				props.put("pageid", p.getPageid());
				props.put( "title", p.getTitle());
				props.put("lang", lang);
				props.put("pageRank", p.getPageRank());

				OrientVertex pageNode = graph.addVertex("class:Page", props);
				LOG.info("Page inserted " + pageNode.toString());
			}
			graph.commit();
			LOG.info("Pages insertion committed");
			return true;
		} catch (ORecordDuplicatedException or) {
            LOG.error("Some of the pages are duplicates. Operation will be rollbacked.", or.getMessage());
            graph.rollback();
		} catch( Exception e ) {
			LOG.error("Something went wrong during page insertion. Operation will be rollbacked.", e.getMessage());
			graph.rollback();
		}finally {
		    graph.shutdown();
        }
        return false;
    }
	
}
