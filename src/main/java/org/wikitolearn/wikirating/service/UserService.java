/**
 * 
 */
package org.wikitolearn.wikirating.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.exception.UserNotFoundException;
import org.wikitolearn.wikirating.model.Author;
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
     * Initialize the graph for the first time querying the MediaWiki API
     * to get the all the users and then insert them.
     * @param apiUrl the MediaWiki API url
     * @return CompletableFuture<Boolean>
     */
    @Async
    public CompletableFuture<Boolean> initUsers(String apiUrl){
    	List<User> users = userMediaWikiService.getAll(apiUrl);
    	addUsers(users);
        return CompletableFuture.completedFuture(true);
    }
    
	/**
     * Initialize relationships between users and their created revisions for the first time
     * @return CompletableFuture<Boolean>
     */
    @Async
    public CompletableFuture<Boolean> initAuthorship(){
    	Iterable<User> users = userRepository.findAll();
    	for(User user : users){
    		Set<Revision> revisions = revisionRepository.findByUserid(user.getUserid());
    		this.setAuthorship(revisions, user);
    		LOG.info("Set revisions authorship for user {}", user.getUserid());
    	}
    	// Get revisions with userid = 0 (anonymous authors)
    	Set<Revision> anonRevisions = revisionRepository.findByUserid(0);
    	User anonymousUser =  new User("AnonymousUser", 0, 0.0, 0.0, 0.0);
        this.setAuthorship(anonRevisions, anonymousUser);
    	LOG.info("Set revisions authorship for anonymous revisions");
    	
        return CompletableFuture.completedFuture(true);
    }
    
    /**
     * Insert users into the graph
     * @param users the list of users to be inserted
     * @return the list of inserted users
     */
    public List<User> addUsers(List<User> users){
    	userRepository.save(users);
    	LOG.info("Inserted users: {}", users);
    	return users;
    	//TODO handle exceptions
    }

    /**
     * Set the authorship for a list a Revisions for a selected User
     * @param revisions
     * @param user
     */
    public void setAuthorship(Collection<Revision> revisions, User user){
        for (Revision rev : revisions){
            Author author = new Author(user.getTotalReliability());
            author.setRevision(rev);
            author.setUser(user);
            user.addAuthor(author);
        }
        userRepository.save(user);
    }
    
    /**
     * Set the authorship for a revision creating a new Author relationship
     * that saves the reliability of the user.
     * @param revision the revision
     */
    public void setAuthorship(Revision revision){
    	User user = userRepository.findByUserId(revision.getUserid());
		Author author = new Author(user.getTotalReliability());
		author.setRevision(revision);
		author.setUser(user);
    	user.addAuthor(author);
    	userRepository.save(user);
    }
    
    /**
     * Set the authorship for a list of revisions
     * @param revisions the list of revisions
     */
    public void setAuthorship(Collection<Revision> revisions){
    	for(Revision revision : revisions){
    		setAuthorship(revision);
    	}
    }
    
    /**
     * Get the requested user.
     * @param userId the user id
     * @return the requested user
     * @throws UserNotFoundException
     */
    public User getUser(int userId) throws UserNotFoundException{
    	User user  = userRepository.findByUserId(userId);
    	if(user == null){
    		LOG.error("User {} not found", userId);
    		throw new UserNotFoundException();
    	}
    	return user;
    }
}
