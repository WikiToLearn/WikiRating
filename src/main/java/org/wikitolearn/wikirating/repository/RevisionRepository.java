/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
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
	 * Get a revision given its langRevId
	 * @param langRevId the langRevId of the page
	 * @return the revision
	 */
	Revision findByLangRevId(String langRevId);

    /**
     * Get all the revisions of the requested domain language
     * @param lang the language of the revisions
     * @return a set of revisions
     */
    Set<Revision> findByLang(String lang);

	/**
	 * Get all the revisions authored by a specific user
	 * @param userId the user id
	 * @return a set of revisions
	 */
	Set<Revision> findByUserId(int userId);

	/**
	 * Get all the revisions of a Page (CourseLevelThree).
	 * The direction of the link is important to traverse
	 * only the chain of the page revisions without reaching other nodes.
	 * @param langPageId the page langPageId
	 * @return a set of revisions
	 */
	@Query("MATCH (p:CourseLevelThree {langPageId:{langPageId}})-[:LAST_REVISION|PREVIOUS_REVISION*]->(r:Revision) RETURN r")
	Set<Revision> findAllRevisionOfPage(@Param("langPageId") String langPageId);

	/**
	 * Get all the revisions of a Page ordered by revision id
	 * @param langPageId the page langPageId
	 * @return a list of revisions
	 */
	@Query("MATCH (p:CourseLevelThree {langPageId:{langPageId}})-[:LAST_REVISION|PREVIOUS_REVISION*]->(r:Revision) RETURN r ORDER BY r.revId")
	List<Revision> findAllRevisionOfPageOrdered(@Param("langPageId") String langPageId);

	/**
	 * Get all the revisions after the LAST_VALIDATED_REVISION.
	 * The revisions are returned ordered by revision id
	 * @param langPageId the page langPageId
	 * @return a revision result with its related nodes and relationships partially hydrated
	 */
	@Query("MATCH (page:Page {langPageId:{langPageId}})-[:LAST_VALIDATED_REVISION]->(rev:Revision) " +
            "WITH rev MATCH (prev:Revision)<-[pr:PREVIOUS_REVISION]-(rev)<-[:PREVIOUS_REVISION*]-(nextr:Revision) " +
            "WITH rev, prev,pr, nextr " +
            "MATCH p=(nextr)-[r*0..1]-() " +
            "RETURN rev as revision,prev,pr, nodes(p) as nodes, relationships(p) as rels ")
	RevisionResult getNotValidatedRevisionsChain(@Param("langPageId") String langPageId);
	
	/**
	 * Get the last validated revision and map its related nodes and relationships too
	 * @param langPageId the page langPageId
	 * @return a revision result with its related nodes and relationships partially hydrated
	 */
	@Query("MATCH (Page {langPageId:{langPageId}})-[:LAST_VALIDATED_REVISION]->(rev:Revision) " +
            "WITH rev MATCH p=(rev)-[a*0..1]-() " +
            "RETURN rev as revision, nodes(p), relationships(p) ")
    RevisionResult findLastValidatedRevision(@Param("langPageId") String langPageId);

	/**
	 * Get the previous revision of a specific revision given its langRevId
	 * @param langRevId the revision langRevId
	 * @return the previous revision
	 */
	@Query("MATCH (r:Revision {langRevId:{langRevId})-[:PREVIOUS_REVISION]->(a:Revision) RETURN a")
	Revision findPreviousRevision(@Param("langRevId") String langRevId);

}
