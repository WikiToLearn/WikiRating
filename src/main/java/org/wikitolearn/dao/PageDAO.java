/**
 * 
 */
package org.wikitolearn.dao;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.util.List;

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

    @Autowired
    private DbConnection connection;
    
    /**
     * Insert all the given pages in the database as vertexes. 
     * @param pages List<Page> The pages to be inserted
     * @return boolean True if insertion was committed, false otherwise
     */
    public Boolean insertPages(List<Page> pages){
    	System.out.println("Getting the connection");
    	OrientGraph graph = connection.getGraph();
    	System.out.println("Starting to insert pages");
		try{
			// Implicitly begins the transaction and create the class too
			Vertex pageNode = graph.addVertex("class:Page");
	    	System.out.println("Created vertex class");

			for(Page p : pages){
				pageNode.setProperty( "title", p.getTitle());
				pageNode.setProperty("pid", p.getPageid());
				pageNode.setProperty("pageRank", 0.0);
				System.out.println("Page inserted");
			}
			graph.commit();
			return true;
		} catch( Exception e ) {
			graph.rollback();
		}
		return false;
    }
	
}
