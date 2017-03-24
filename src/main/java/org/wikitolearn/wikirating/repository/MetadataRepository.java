package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.Metadata;

/**
 * Created by valsdav on 24/03/17.
 */
public interface MetadataRepository extends GraphRepository<Metadata>{
}
