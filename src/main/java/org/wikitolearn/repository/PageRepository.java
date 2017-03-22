/**
 * 
 */
package org.wikitolearn.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.models.Page;

/**
 * @author aletundo
 *
 */
public interface PageRepository extends GraphRepository<Page> {
	Page findByTitle(String title);
	
	Page findByLangPageId(String langPageId);
}
