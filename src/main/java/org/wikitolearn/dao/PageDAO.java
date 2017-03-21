/**
 * 
 */
package org.wikitolearn.dao;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

import org.springframework.stereotype.Repository;
import org.wikitolearn.models.Page;
/**
 * 
 * @author aletundo, valsdav
 *
 */
@Repository
public class PageDAO extends GenericDAO{

	/**
	 * This method is used to create the class on the DB.
     * It creates an unique index on pageid to avoid duplicated.
     * @return void
	 */
    @Override
	public void createDatabaseClass() {
		LOG.info("Creating database Page class...");
		OrientGraphNoTx graph = connection.getGraphNT();
		try{
            OrientVertexType vertex = graph.createVertexType("Page",1);
            vertex.createProperty("pageid", OType.INTEGER).setMandatory(true);
            vertex.createProperty("lang", OType.STRING).setMandatory(true);
            vertex.createIndex("page_lang", OClass.INDEX_TYPE.UNIQUE, "pageid", "lang");
			// Add a cluster for each language
            for(String lang : langs){
            	graph.command(new OCommandSQL("ALTER CLASS Page ADDCLUSTER Pages_" + lang)).execute();
            }
            //graph.getRawGraph().getMetadata().getSchema().reload();
		} catch( Exception e ) {
			LOG.error("Something went wrong during class creation. {}.", e.getMessage());
		} finally {
            graph.shutdown();
        }
	}


    /**
     * Insert all the given pages in the database as vertexes.
     * If there are duplicates all the insertion is rolled back.
     * @param pages List<Page> The pages to be inserted
     * @param lang String 
     * @return boolean True if insertion was committed, false otherwise
     */
    public Boolean insertPages(List<Page> pages, String lang){
    	OrientGraphNoTx graph = connection.getGraphNT();
    	LOG.info("Starting to insert pages...");
		try{
			for(Page p : pages){
				Map<String, Object> props = new HashMap<>();
				props.put("pageid", p.getPageid());
				props.put( "title", p.getTitle());
				props.put("lang", lang);
				props.put("pageRank", p.getPageRank());

				OrientVertex pageNode = graph.addVertex("class:Page,cluster:Pages_"+lang, props);
				LOG.info("Page inserted " + pageNode.toString());
			}
			LOG.info("Pages insertion committed");
			return true;
		} catch (ORecordDuplicatedException or) {
            LOG.error("Page not inserted because it's duplicated. {}", or.getMessage());
		} catch( Exception e ) {
			LOG.error("Something went wrong during page insertion. {}", e.getMessage());
		}finally {
		    graph.shutdown();
        }
        return false;
    }

    /**
     * This methods returns an Iterable over all the pages belonging to a certain cluster,
     * so coming from the same language domain.
     * @param graph OrientGraph An OrientGraph instance
     * @param lang String The language of the cluster
     * @return result Iterable<OrientVertex> with all the pages of the cluster
     */
    public Iterable<OrientVertex> getPagesIteratorFromCluster(OrientGraph graph, String lang){
		Iterable<OrientVertex> result = null;
		try {
		    result = (Iterable<OrientVertex>)  graph.command(new OCommandSQL(
                    "SELECT * FROM cluster:Pages_"+ lang)).execute();
        } catch (Exception e){
		    LOG.error("Something went wrong during quering for pages. {}", e.getMessage());
        }
        return result;
	}
}
