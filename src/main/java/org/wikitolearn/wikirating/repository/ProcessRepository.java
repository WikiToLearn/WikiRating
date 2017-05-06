/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.wikitolearn.wikirating.model.graph.Process;
import org.wikitolearn.wikirating.util.enums.ProcessType;

/**
 * @author aletundo
 * @author valsdav
 */
public interface ProcessRepository extends GraphRepository<Process> {

    /**
     * This query returns the last process created: the one connected to the
     * Metadata node of PROCESSES type.
     * @return the last Process
     */
    @Query("MATCH (p:Process)<-[LATEST_PROCESS]-(m:Metadata {type:'PROCESSES'}) RETURN p")
    public Process getLatestProcess();
    
    @Query("MATCH (p:Process {processStatus:'ONGOING'}) RETURN p")
    public Process getOnGoingProcess();

    /**
     * This query returns the latest process registered of the given type.
     * @param type the type of the process to look for
     * @return the found process
     */
    @Query("MATCH path = (m:Metadata)-[*]->(p:Process {processType:{type}}) RETURN p ORDER BY length(path) LIMIT 1")
    public Process getLatestProcessByType(@Param("type") ProcessType type);

}
