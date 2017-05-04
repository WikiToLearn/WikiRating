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
	
	/**
	 * 
	 * @return
	 */
	@Query("MATCH (p:CourseRoot) RETURN p")
	List<Page> findAllCourseRootPages();
	
	/**
	 * 
	 * @param lang
	 * @return
	 */
	@Query("MATCH (p:CourseRoot) WHERE p.lang = {lang} RETURN p")
	List<Page> findAllCourseRootPages(@Param("lang") String lang);
	
	/**
	 * 
	 * @return
	 */
	@Query("MATCH (p:Page) WHERE NOT p:CourseRoot AND NOT p:CourseLevelTwo AND NOT p:CourseLevelThree RETURN p")
	List<Page> findAllUncategorizedPages();
	
	/**
	 * 
	 * @param lang
	 * @return
	 */
	@Query("MATCH (p:Page) WHERE p.lang = {lang} AND NOT p:CourseRoot AND NOT p:CourseLevelTwo AND NOT p:CourseLevelThree RETURN p")
	List<Page> findAllUncategorizedPages(@Param("lang") String lang);
}
