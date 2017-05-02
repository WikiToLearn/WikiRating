/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.wikitolearn.wikirating.model.graph.Vote;

/**
 * @author aletundo
 *
 */
public interface VoteRepository extends GraphRepository<Vote> {
	
	/**
	 * Get all votes of the requested revision
	 * @param langRevId the langRevId of the revision
	 * @return the list of the votes
	 */
	@Query("MATCH (:User)-[v:Vote]->(:Revision {langRevId: {langRevId}}) RETURN v")
	List<Vote> getAllVotesOfRevision(@Param("langRevId") String langRevId);
}
