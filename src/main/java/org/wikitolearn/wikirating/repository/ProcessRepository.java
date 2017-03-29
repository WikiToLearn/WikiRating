/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.Process;
import org.wikitolearn.wikirating.util.enums.MetadataType;

/**
 * @author aletundo
 *
 */
public interface ProcessRepository extends GraphRepository<Process> {

    @Query("MATCH (p:Process)<-[LAST_PROCESS]-(m:Metadata {type:'PROCESSES'}) RETURN p")
    public Process getLastProcess();
}
