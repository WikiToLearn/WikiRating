/**
 * 
 */
package org.wikitolearn.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wikitolearn.dao.MetadataDAO;
import org.wikitolearn.dao.PageDAO;
import org.wikitolearn.dao.RevisionDAO;
import org.wikitolearn.dao.UserDAO;
import org.wikitolearn.models.Process;
import org.wikitolearn.services.PageService;
import org.wikitolearn.services.RevisionService;
import org.wikitolearn.services.UserService;
import org.wikitolearn.utils.enums.ProcessResult;
import org.wikitolearn.utils.enums.ProcessType;

/**
 * 
 * @author aletundo
 *
 */
@RestController
public class MaintenanceController {

	private static final Logger LOG = LoggerFactory.getLogger(MaintenanceController.class);
	
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
	
	/**
	 * Secured endpoint to enable or disable read only mode. When read only mode is enabled only GET requests are
	 * allowed. To change this behavior @see org.wikitolearn.filters.MaintenanceFilter.
	 * @param active String requested parameter that toggle the re. Binary values are accepted.
	 * @return a JSON object with the response
	 */
	@RequestMapping(value = "${maintenance.readonlymode.uri}", method = RequestMethod.POST, produces = "application/json")
	public String toggleReadOnlyMode(@RequestParam(value = "active") String active) {
		JSONObject response = new JSONObject();
		int mode = Integer.parseInt(active);

		try {
			if (mode == 0) {
				response.put("status", "success");
				response.put("message", "Application is live now.");
				// Delete maintenance lock file if it exists
				File f = new File("maintenance.lock");
				if (f.exists()) {
					f.delete();
					LOG.debug("Deleted maintenance lock file.");
				}
				LOG.info("Application is live now.");
			} else if (mode == 1) {
				try {
					response.put("status", "success");
					response.put("message", "Application is in maintenance mode now.");
					// Create maintenance lock file with a maintenance.active property setted to true
					Properties props = new Properties();
					props.setProperty("maintenance.active", "true");
					File lockFile = new File("maintenance.lock");
					OutputStream out = new FileOutputStream(lockFile);
					DefaultPropertiesPersister p = new DefaultPropertiesPersister();
					p.store(props, out, "Maintenance mode lock file");
					
					LOG.debug("Created maintenance lock file.");
					LOG.info("Application is in maintenance mode now.");
				} catch (Exception e) {
					LOG.error("Something went wrong. {}", e.getMessage());
				}

			} else {
				// The active parameter value is not supported
				response.put("status", "error");
				response.put("message", "Parameter value not supported");
			}
		} catch (JSONException e) {
			LOG.error("Something went wrong using JSON API. {}", e.getMessage());
		}
		return response.toString();
	}
	
	/**
	 * Secured endpoint that handles initialization request for the given language
	 * @param lang
	 * @return true if the initialization is completed without errors, false otherwise
	 */
	@RequestMapping(value = "${maintenance.init.uri}", method = RequestMethod.POST, produces = "application/json")
	public boolean initialize(@RequestParam("lang") String lang, @Value("${mediawiki.protocol}") String protocol, @Value("${mediawiki.api.url}") String apiUrl){
	    String url = protocol + lang + "." + apiUrl;
	    //starting a new Process
        Process initializeProcess = new Process(ProcessType.INIT);

        // Initializing the DB schema, only the first time
        if (! initializedDB){
            initializeDbClasses();
            initializedDB = true;
        }
        
        CompletableFuture<Boolean> parallelInsertions = 
        		CompletableFuture.allOf(pageService.addAllPages(lang, url), userService.addAllUsers(url))
        		.thenCompose(s -> revisionService.addAllRevisions(lang, url));
        
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
	
	/**
	 * 
	 */
	@RequestMapping(value = "${maintenance.wipe.uri}", method = RequestMethod.DELETE, produces = "application/json")
	public void wipeDatabase(){
		// TODO
	}
}
