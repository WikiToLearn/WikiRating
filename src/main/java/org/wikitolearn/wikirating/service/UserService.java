/**
 * 
 */
package org.wikitolearn.wikirating.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.model.User;
import org.wikitolearn.wikirating.repository.RevisionRepository;
import org.wikitolearn.wikirating.repository.UserRepository;
import org.wikitolearn.wikirating.service.mediawiki.UserMediaWikiService;

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
	private UserRepository userRepository;
	@Autowired
	private RevisionRepository revisionRepository;

	/**
     * This methods inserts all the users inside the DB querying the MediaWiki API.
     *
     * @return CompletableFuture<Boolean>
     */
    @Async
    public CompletableFuture<Boolean> addAllUsers(String apiUrl){
    	List<User> users = userMediaWikiService.getAll(apiUrl);
    	
    	userRepository.save(users);
    	
    	LOG.info("Inserted all users");
        return CompletableFuture.completedFuture(true);
    }
    
	/**
     * This methods connect the users to their revisions
     *
     * @return CompletableFuture<Boolean>
     */
    @Async
    public CompletableFuture<Boolean> setAllUsersAuthorship(){
    	Iterable<User> users = userRepository.findAll();
    	users.forEach(user -> {
    		Set<Revision> revisions = revisionRepository.findByUserid(user.getUserid());
    		user.setRevisionsAuthored(revisions);
    		userRepository.save(user);
    		LOG.info("Set revisions authorship for user {}", user.getUserid());
    	});
    	// Get revisions with userid = 0 (anonymous authors)
    	Set<Revision> anonRevisions = revisionRepository.findByUserid(0);
    	User anonymousUser =  new User("AnonymousUser", 0, 0.0, 0.0, 0.0);
    	anonymousUser.setRevisionsAuthored(anonRevisions);
    	userRepository.save(anonymousUser);
    	LOG.info("Set revisions authorship for anonymous revisions");
        return CompletableFuture.completedFuture(true);
    }

}
