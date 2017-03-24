/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.Process;

/**
 * @author aletundo
 *
 */
public interface ProcessRepository extends GraphRepository<Process> {
	
}
