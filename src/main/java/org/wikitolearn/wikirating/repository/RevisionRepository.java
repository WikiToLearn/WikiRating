/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.graph.Revision;

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
     * @param lang
     * @return
     */
    Set<Revision> findByLang(String lang);

	/**
	 * 
	 * @param userId
	 * @return
	 */
	Set<Revision> findByUserId(int userId);

	/**
	 * This query returns all the Revisions of a Page (CourseLevelThree).
	 * The direction -> of the link is important to traverse
	 * only the chain of Revisions of the page without reaching other nodes.
	 * @param langPageId
	 * @return
	 */
	@Query("MATCH (p:CourseLevelThree {langPageId:{0}})-[:LAST_REVISION|PREVIOUS_REVISION*]->(r:Revision) RETURN r")
	Set<Revision> findAllRevisionOfPage(String langPageId);

	/**
	 * This query returns all the Revisions of a Page ordere from the oldest
     * to the newest.
	 * @param langPageId
	 * @return
	 */
	@Query("MATCH (p:CourseLevelThree {langPageId:{0}})-[:LAST_REVISION|PREVIOUS_REVISION*]->(r:Revision) RETURN r ORDER BY r.revId")
	Set<Revision> findAllRevisionOfPageOrdered(String langPageId);

	/**
	 * This query return the previous revision of a Revision identified
	 * by langRevId.
	 * @param langRevId langRevId of the requested revision
	 * @return the previous revision
	 */
	@Query("MATCH (r:Revision {langRevId:{0}})-[:PREVIOUS_REVISION]->(a:Revision) RETURN a")
	Revision findPreviousRevision(String langRevId);
}
