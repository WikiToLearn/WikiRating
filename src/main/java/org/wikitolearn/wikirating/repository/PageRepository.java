/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.transaction.annotation.Transactional;
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
