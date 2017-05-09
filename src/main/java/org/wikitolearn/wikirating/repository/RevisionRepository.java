/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.wikitolearn.wikirating.model.graph.Revision;
import org.wikitolearn.wikirating.model.graph.queryresult.RevisionResult;

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

	@Query("MATCH (r:Revision {langRevId:{0}}) WITH r MATCH p=(r)-[s*0..1]-() RETURN r as revision, nodes(p), relationships(p)")
	RevisionResult getRev(String id);
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
	List<Revision> findAllRevisionOfPageOrdered(String langPageId);

	/**
	 * This query returns all the Revisions after the LAST_VALIDATED_REVISION.
	 * The revisions are returned ordered by revId from the oldest to the newest.
	 * @param langPageId
	 * @return
	 */
	@Query("MATCH (page:Page {langPageId:{0}})-[:LAST_VALIDATED_REVISION]->(rev:Revision) " +
			"WITH rev MATCH (rev)<-[:PREVIOUS_REVISION*]-(rev:Revision) " +
			"WITH pr MATCH path=(pr)-[r*0..1]-() " +
			"RETURN pr as revision , nodes(path), relationships(path) ORDER BY pr.revId")
	List<RevisionResult> findAllRevisionNotValidated(String langPageId);

	/**
	 *
	 */
	@Query("MATCH (Page {langPageId:{id}})-[:LAST_VALIDATED_REVISION]->(rev:Revision) " +
            "WITH rev MATCH (rev)-[a:AUTHOR]-(s:User) " +
            "RETURN rev as revision, a, s as author ")
    RevisionResult findLastValidatedRevision(@Param("id") String langPageId);

	/**
	 * This query return the previous revision of a Revision identified
	 * by langRevId.
	 * @param langRevId langRevId of the requested revision
	 * @return the previous revision
	 */
	@Query("MATCH (r:Revision {langRevId:{0}})-[:PREVIOUS_REVISION]->(a:Revision) RETURN a")
	Revision findPreviousRevision(String langRevId);

}
