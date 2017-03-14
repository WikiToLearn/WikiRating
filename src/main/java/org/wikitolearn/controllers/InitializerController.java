/**
 * 
 */
package org.wikitolearn.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.wikitolearn.controllers.mediawikiClient.PageMediaWikiController;
import org.wikitolearn.controllers.mediawikiClient.UserMediaWikiController;
import org.wikitolearn.dao.PageDAO;
import org.wikitolearn.dao.UserDAO;
import org.wikitolearn.models.Page;
import org.wikitolearn.models.User;

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
	private PageDAO pageDao;
    @Autowired
    private UserDAO userDao;
	
	@RequestMapping(value = "/init", method = RequestMethod.GET, produces = "application/json")
	public List<Page> initialize(){

	    //Inizitializing the DB schema, only the first time
        initializeDbClasses();

		List<Page> pages =  pageController.getAllPages("https://en.wikitolearn.org/api.php");
		LOG.info("Fetched all the pages");
		boolean insertionResultPages = pageDao.insertPages(pages);
		if(insertionResultPages){
			LOG.info("Inserted pages");
		}else{
			LOG.error("Something went wrong during pages insertion");
		}

		List<User> users =  userController.getAllUsers("https://en.wikitolearn.org/api.php");
		LOG.info("Fetched all the users");
		boolean insertionResultUsers = userDao.insertUsers(users);
		if(insertionResultUsers){
			LOG.info("Inserted users");
		}else{
			LOG.error("Something went wrong during users insertion");
		}
		
        return pages;
	}

    private void initializeDbClasses(){
        LOG.info("Create DB Classes...");
        pageDao.createDBClass();
        userDao.createDBClass();
        LOG.info("Create DB Classes...DONE");

    }

}
