package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.graph.Metadata;
import org.wikitolearn.wikirating.util.enums.MetadataType;

/**
 * @author aletundo
 * @author valsdav
 */
public interface MetadataRepository extends GraphRepository<Metadata> {
	/**
	 * Get the Metadata node of the specified type
	 * 
	 * @param type
	 * @return the Metadata node of the specified type
	 */
	Metadata getMetadataByType(MetadataType type);

	/**
	 * Update the LATEST_PROCESS relationship deleting the existing edge and
	 * creating a new one.
	 */
	@Query("MATCH (m:Metadata)-[lp:LATEST_PROCESS]->(p1:Process)<-[pp:PREVIOUS_PROCESS]-(p2:Process) DELETE lp CREATE (m)-[:LATEST_PROCESS]->(p2)")
	void updateLatestProcess();
}
