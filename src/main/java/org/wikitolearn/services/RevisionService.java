/**
 * 
 */
package org.wikitolearn.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wikitolearn.dao.PageDAO;
import org.wikitolearn.dao.RevisionDAO;
import org.wikitolearn.models.Revision;
import org.wikitolearn.services.mediawiki.RevisionMediaWikiService;
import org.wikitolearn.utils.DbConnection;

import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * 
 * @author aletundo, valsdav
 *
 */
@Service
public class RevisionService {
	private static final Logger LOG = LoggerFactory.getLogger(RevisionService.class);
	@Autowired
	private RevisionMediaWikiService  revisionMediaWikiService;
	@Autowired
	private RevisionDAO revisionDao;
	@Autowired
	private PageDAO pageDao;
    @Autowired
    private DbConnection dbConnection;
	
	/**
     * This method inserts all the revisions for every page, creating the connections between them
     * and between the users that have written them.
     * @param lang String 
     * @param apiUrl String The MediaWiki API url
     * @return CompletableFuture<Boolean>
     */
    @Async
    public CompletableFuture<Boolean> addAllRevisions(String lang, String apiUrl){
        OrientGraph graph = dbConnection.getGraph();
        boolean revInsertionResult = false;
        try{
            for (OrientVertex page : pageDao.getPagesIteratorFromCluster(graph, lang)) {
                int pageId = page.getProperty("pageid");
                LOG.info("Processing page: {}", pageId);
                List<Revision> revs = revisionMediaWikiService.getAllRevisionByPageId(apiUrl, pageId);
                revInsertionResult = revisionDao.insertRevisions(pageId, revs, lang);
                if(!revInsertionResult){
                    LOG.error("Something was wrong during the insertion of the revisions of page {}", pageId);
                }
            }
        } catch ( ODatabaseException e){
            LOG.error("DB Error during insertion of Revisions. {}", e.getMessage());
            graph.rollback();
        } catch (Exception e){
            LOG.error("Something went wrong during Revisions insertion. {}", e.getMessage());
            graph.rollback();
        } finally {
            graph.shutdown();
        }
        return CompletableFuture.completedFuture(revInsertionResult);
    }

}
