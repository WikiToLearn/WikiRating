/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;
import org.wikitolearn.wikirating.model.graph.CourseLevelThree;

/**
 * @author aletundo
 *
 */
public interface CourseLevelThreeRepository extends PageRepository<CourseLevelThree> {

    /**
     * Update the LAST_REVISION relationship deleting the existing edge and
     * creating a new one.
     * @param langPageId the page langPageId
     */
    @Query("MATCH (p:CourseLevelThree {langPageId: {langPageId}})-[lr:LAST_REVISION]->(r1:Revision)<-[pp:PREVIOUS_REVISION]-(r2:Revision) " +
            "DELETE lr CREATE (p)-[:LAST_REVISION]->(r2)")
    void updateLastRevision(@Param("langPageId") String langPageId);

}
