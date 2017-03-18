/**
 * 
 */
package org.wikitolearn.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wikitolearn.dao.PageDAO;
import org.wikitolearn.dao.RevisionDAO;
import org.wikitolearn.dao.UserDAO;
import org.wikitolearn.models.Page;
import org.wikitolearn.models.Revision;
import org.wikitolearn.models.User;
import org.wikitolearn.services.mediawiki.PageMediaWikiController;
import org.wikitolearn.services.mediawiki.RevisionMediaWikiController;
import org.wikitolearn.services.mediawiki.UserMediaWikiController;
import org.wikitolearn.utils.DbConnection;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;

/**
 * @author alessandro
 *
 */
@RestController
public class InitializerController {
	
	private static final Logger LOG = LoggerFactory.getLogger(InitializerController.class);

	@Autowired
	private PageMediaWikiController pageController;
	@Autowired
	private UserMediaWikiController userController;
	@Autowired
    private RevisionMediaWikiController revsController;
	@Autowired
	private PageDAO pageDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private RevisionDAO revisionDAO;
    @Autowired
    private DbConnection dbConnection;

    private boolean initializedDB = false;
	
	@RequestMapping(value = "/init", method = RequestMethod.GET, produces = "application/json")
	public boolean initialize(@RequestParam("lang") String lang){
	    String apiUrl = "https://" + lang + ".wikitolearn.org/api.php";

        // Initializing the DB schema, only the first time
        if (! initializedDB){
            initializeDbClasses();
            initializedDB = true;
        }
        
        CompletableFuture<Boolean> parallelInsertions = 
        		CompletableFuture.allOf(addAllPages(lang, apiUrl), addAllUsers(apiUrl))
        		.thenCompose(s -> addAllRevisions(lang, apiUrl));
        
        try {
			return parallelInsertions.get();
		} catch (InterruptedException | ExecutionException e) {
			LOG.error("Something went wrong during database initialization.", e.getMessage());
			return false;
		}
	}


    /**
     * This methods initializes all the DAO Classes, creating the right types on the DB.
     */
    private void initializeDbClasses(){
        LOG.info("Creating DB lasses...");
        pageDao.createDBClass();
        userDao.createDBClass();
        revisionDAO.createDBClass();
        LOG.info("Completed creation of DB classes.");

    }


	/**
     * This methods inserts all the pages inside the DB querying the MediaWiki API.
     * 
     * @return CompletableFuture<Boolean>
     */
    @Async
    private CompletableFuture<Boolean> addAllPages( String lang, String apiUrl ){
        List<Page> pages =  pageController.getAllPages(apiUrl);
        LOG.info("Fetched all the pages");
        boolean insertionResultPages = pageDao.insertPages(pages, lang);
        if(insertionResultPages){
            LOG.info("Inserted pages");
        }else{
            LOG.error("Something went wrong during pages insertion");
        }
        return CompletableFuture.completedFuture(insertionResultPages);
    }

    /**
     * This methods inserts all the users inside the DB querying the MediaWiki API.
     *
     * @return CompletableFuture<Boolean>
     */
    @Async
    private CompletableFuture<Boolean> addAllUsers(String apiUrl){
        List<User> users =  userController.getAllUsers(apiUrl);
        // Adding the Anonymous user
        users.add(new User("Anonimous", 0, 0, 0, 0));
        LOG.info("Fetched all the users");
        boolean insertionResultUsers = userDao.insertUsers(users);
        if(insertionResultUsers){
            LOG.info("Inserted users");
        }else{
            LOG.error("Something went wrong during users insertion");
        }
        return CompletableFuture.completedFuture(insertionResultUsers);
    }

    /**
     * This method inserts all the revisions for every page, creating the connections between them
     * and between the users that have written them.
     * 
     * @return CompletableFuture<Boolean>
     */
    private CompletableFuture<Boolean> addAllRevisions(String lang, String apiUrl){
        OrientGraph graph = dbConnection.getGraph();
        boolean revInsertionResult = false;
        try{
            for (OrientVertex page : pageDao.getPagesIteratorFromCluster(graph, lang)) {
                int pageid = page.getProperty("pageid");
                LOG.info("Processing page: " + pageid);
                List<Revision> revs = revsController.getAllRevisionForPage(apiUrl, pageid);
                revInsertionResult = revisionDAO.insertRevisions(pageid, revs, lang);
                if(!revInsertionResult){
                    LOG.error("Something was wrong during the insertion of the revisions of page "+ pageid);
                }
            }
        } catch ( ODatabaseException e){
            LOG.error("DB Error during insertion of Revisions.", e.getMessage());
            graph.rollback();
        } catch (Exception e){
            LOG.error("Something went wrong during Revisions insertion. ", e.getMessage());
            graph.rollback();
        } finally {
            graph.shutdown();
        }
        return CompletableFuture.completedFuture(revInsertionResult);
    }


}
