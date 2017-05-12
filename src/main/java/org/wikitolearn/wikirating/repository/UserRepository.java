/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.graph.User;

/**
 * @author aletundo
 *
 */
public interface UserRepository extends GraphRepository<User> {
	/**
	 * Get a user by his username
	 * @param username the user username
	 * @return the user
	 */
	User findByUsername(String username);
	
	/**
	 * Get a user by his id
	 * @param userId the user id
	 * @return the user
	 */
	User findByUserId(int userId);
}
