/**
 * 
 */
package org.wikitolearn.services;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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

	/**
     * This methods inserts all the users inside the DB querying the MediaWiki API.
     *
     * @return CompletableFuture<Boolean>
     */
    @Async
    public CompletableFuture<Boolean> addAllUsers(String apiUrl){
        return CompletableFuture.completedFuture(true);
    }

}
