package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.Metadata;
import org.wikitolearn.wikirating.util.enums.MetadataType;

/**
 * Created by valsdav on 24/03/17.
 */
public interface MetadataRepository extends GraphRepository<Metadata>{

    Metadata getMetadataByType(MetadataType type);
    
    @Query("MATCH (m:Metadata)-[r:LATEST_PROCESS]->(p:Process) DELETE r")
    void removeLatestProcess();
}
