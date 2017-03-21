/**
 * 
 */
package org.wikitolearn.controllers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wikitolearn.dao.MetadataDAO;
import org.wikitolearn.dao.PageDAO;
import org.wikitolearn.dao.RevisionDAO;
import org.wikitolearn.dao.UserDAO;
import org.wikitolearn.models.Process;
import org.wikitolearn.utils.enums.ProcessResult;
import org.wikitolearn.utils.enums.ProcessType;
import org.wikitolearn.services.PageService;
import org.wikitolearn.services.RevisionService;
import org.wikitolearn.services.UserService;

/**
 * 
 * @author aletundo, valsdav
 *
 */
@RestController
public class InitializerController {
	
	private static final Logger LOG = LoggerFactory.getLogger(InitializerController.class);

	@Autowired
	private PageService pageService;
	@Autowired
	private UserService userService;
	@Autowired
    private RevisionService revisionService;
    @Autowired
    private MetadataDAO metadataDAO;
	@Autowired
	private PageDAO pageDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private RevisionDAO revisionDAO;

    private boolean initializedDB = false;
	
	@RequestMapping(value = "/init", method = RequestMethod.GET, produces = "application/json")
	public boolean initialize(@RequestParam("lang") String lang){
	    String apiUrl = "https://" + lang + ".wikitolearn.org/api.php";
	    //starting a new Process
        Process initializeProcess = new Process(ProcessType.INIT);

        // Initializing the DB schema, only the first time
        if (! initializedDB){
            initializeDbClasses();
            initializedDB = true;
        }
        
        CompletableFuture<Boolean> parallelInsertions = 
        		CompletableFuture.allOf(pageService.addAllPages(lang, apiUrl), userService.addAllUsers(apiUrl))
        		.thenCompose(s -> revisionService.addAllRevisions(lang, apiUrl));
        
        try {
			boolean result =  parallelInsertions.get();
			//saving the result of the process
			if (result){
			    initializeProcess.setProcessResult(ProcessResult.DONE);
            }else{
                initializeProcess.setProcessResult(ProcessResult.ERROR);
            }
			metadataDAO.addProcess(initializeProcess);
            return result;
		} catch (InterruptedException | ExecutionException e) {
			LOG.error("Something went wrong during database initialization. {}", e.getMessage());
			return false;
		}
	}


    /**
     * This methods initializes all the DAO Classes, creating the right types on the DB.
     * @return void
     */
    private void initializeDbClasses(){
        LOG.info("Creating Database classes...");
        metadataDAO.createDatabaseClass();
        pageDao.createDatabaseClass();
        userDao.createDatabaseClass();
        revisionDAO.createDatabaseClass();
        LOG.info("Completed creation of database classes.");
    }

}
