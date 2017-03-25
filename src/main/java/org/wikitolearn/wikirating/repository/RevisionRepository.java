/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import java.util.Set;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.Revision;

/**
 * @author aletundo
 *
 */
public interface RevisionRepository extends GraphRepository<Revision> {
	/**
	 * 
	 * @param langRevId
	 * @return
	 */
	Revision findByLangRevId(String langRevId);
	
	Set<Revision> findByUserid(int userId);
}
