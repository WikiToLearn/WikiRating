/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
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
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	Set<Revision> findByUserId(int userId);

	/**
	 * This query returns all the Revisions of a Page.
	 * The direction -> of the link is important to transverse
	 * only the chain of Revisions of the page without reaching other nodes.
	 * @param langPageId
	 * @return
	 */
	@Query("MATCH (p:Page {langPageId:{0}})-[*]->(r:Revision) RETURN r")
	Set<Revision> findAllRevisionOfPage(String langPageId);
}
