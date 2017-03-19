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
import org.wikitolearn.dao.UserDAO;
import org.wikitolearn.models.User;
import org.wikitolearn.services.mediawiki.UserMediaWikiService;

/**
 * 
 * @author aletundo, valsdav
 *
 */
@Service
public class UserService {
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private UserMediaWikiService  userMediaWikiService;
	@Autowired
	private UserDAO userDao;
	
	/**
     * This methods inserts all the users inside the DB querying the MediaWiki API.
     *
     * @return CompletableFuture<Boolean>
     */
    @Async
    public CompletableFuture<Boolean> addAllUsers(String apiUrl){
        List<User> users =  userMediaWikiService.getAllUsers(apiUrl);
        // Adding the Anonymous user
        users.add(new User("Anonymous", 0, 0, 0, 0));
        LOG.info("Fetched all the users");
        boolean insertionResultUsers = userDao.insertUsers(users);
        if(insertionResultUsers){
            LOG.info("Inserted users");
        }else{
            LOG.error("Something went wrong during users insertion");
        }
        return CompletableFuture.completedFuture(insertionResultUsers);
    }

}
