/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.Process;
import org.wikitolearn.wikirating.util.enums.MetadataType;

import java.util.Date;

/**
 * @author aletundo
 *
 */
public interface ProcessRepository extends GraphRepository<Process> {

    /**
     * This query returns the last process created: the one connected to the
     * Metadata node of PROCESSES type.
     * @return
     */
    @Query("MATCH (p:Process)<-[LAST_PROCESS]-(m:Metadata {type:'PROCESSES'}) RETURN p")
    public Process getLastProcess();

}
