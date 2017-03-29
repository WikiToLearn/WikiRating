/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.Process;
import org.wikitolearn.wikirating.util.enums.ProcessType;

/**
 * @author aletundo
 *
 */
public interface ProcessRepository extends GraphRepository<Process> {

    /**
     * This query returns the last process created: the one connected to the
     * Metadata node of PROCESSES type.
     * @return the last Process
     */
    @Query("MATCH (p:Process)<-[LAST_PROCESS]-(m:Metadata {type:'PROCESSES'}) RETURN p")
    public Process getLastProcess();

    /**
     * This query returns the latest process registered of the given type.
     * @param type the type of the process to look for
     * @return the found process
     */
    @Query("MATCH (m:Metadata)-[*]->(p:Process {type:{0}}) RETURN p LIMIT 1")
    public Process getLastProcessByType(ProcessType type);

}
