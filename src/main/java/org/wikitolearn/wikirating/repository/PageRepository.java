/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.wikitolearn.wikirating.model.graph.Page;

/**
 * @author aletundo
 *
 */
public interface PageRepository<T extends Page> extends GraphRepository<T> {
	/**
	 * Get a page given its title and language
	 * @param title the page title
     * @param lang the page language
	 * @return the page
	 */
	T findByTitleAndLang(String title, String lang);

	/**
	 * Get a page given its langPageId
	 * @param langPageId
	 * @return the page
	 */
	T findByLangPageId(String langPageId);
	
	/**
	 * Get all the pages of a given language
	 * @param lang the page language
	 * @return a list of pages
	 */
	List<T> findByLang(String lang);
	
	/**
	 * Get all the uncategorized pages
	 * @return a list of uncategorized pages
	 */
	@Query("MATCH (p:Page) WHERE NOT p:CourseRoot AND NOT p:CourseLevelTwo AND NOT p:CourseLevelThree RETURN p")
	List<T> findAllUncategorizedPages();
	
	/**
	 * Get all the uncategorized pages of a given language
	 * @param lang the page language
	 * @return a list of uncategorized pages
	 */
	@Query("MATCH (p:Page) WHERE p.lang = {lang} AND NOT p:CourseRoot AND NOT p:CourseLevelTwo AND NOT p:CourseLevelThree RETURN p")
	List<T> findAllUncategorizedPages(@Param("lang") String lang);
}
