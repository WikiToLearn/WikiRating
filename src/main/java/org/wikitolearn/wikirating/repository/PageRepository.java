/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
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
     * @param lang
	 * @return
	 */
	Page findByTitleAndLang(String title, String lang);

	/**
	 * 
	 * @param langPageId
	 * @return
	 */
	Page findByLangPageId(String langPageId);
	
	/**
	 * 
	 * @param lang
	 * @return
	 */
	@Query("MATCH (p:Page {lang:{0}}) RETURN p")
	List<Page> findAllByLang(String lang);
}
