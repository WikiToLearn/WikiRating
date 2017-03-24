/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.User;

/**
 * @author aletundo
 *
 */
public interface UserRepository extends GraphRepository<User> {
	/**
	 * 
	 * @param username
	 * @return
	 */
	User findByUsername(String username);
	
	/**
	 * 
	 * @param userid
	 * @return
	 */
	User findByUserId(int userid);
}
