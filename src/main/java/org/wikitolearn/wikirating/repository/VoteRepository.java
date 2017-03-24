/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.Vote;

/**
 * @author aletundo
 *
 */
public interface VoteRepository extends GraphRepository<Vote> {
	
}
