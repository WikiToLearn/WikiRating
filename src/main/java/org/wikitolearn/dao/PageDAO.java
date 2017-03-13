/**
 * 
 */
package org.wikitolearn.dao;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.wikitolearn.models.Page;
import org.wikitolearn.utils.DbConnection;

/**
 * @author alessandro
 *
 */
public class PageDAO {

    @Autowired
    private DbConnection connection;

    public Boolean insertPages(List<Page> pages){
        for (Page p : pages){
            Vertex pageNode = graph.addVertex("class:Page");
            pageNode.setProperty( "title", dummy.getString("title"));
            pageNode.setProperty("pid",dummy.getInt("pageid"));
            pageNode.setProperty("namespace", namespace.getValue());
            pageNode.setProperty("currentPageVote",-1.0);
            pageNode.setProperty("currentPageReliability", -1.0);
            pageNode.setProperty("PageRating", 0.0);
            pageNode.setProperty("badgeNumber",4);
        }
    }
	
}
