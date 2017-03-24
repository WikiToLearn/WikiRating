/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.Page;

/**
 * @author aletundo
 *
 */
public interface PageRepository extends GraphRepository<Page> {
	/**
	 * 
	 * @param title
	 * @return
	 */
	Page findByTitle(String title);
	
	/**
	 * 
	 * @param langPageId
	 * @return
	 */
	Page findByLangPageId(String langPageId);
}
