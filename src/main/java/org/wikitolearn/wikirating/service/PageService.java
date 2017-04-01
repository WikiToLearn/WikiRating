/**
 * 
 */
package org.wikitolearn.wikirating.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.model.CourseTree;
import org.wikitolearn.wikirating.model.Page;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.repository.PageRepository;
import org.wikitolearn.wikirating.service.mediawiki.PageMediaWikiService;

/**
 * 
 * @author aletundo, valsdav
 *
 */
@Service
public class PageService {
	private static final Logger LOG = LoggerFactory.getLogger(PageService.class);

	@Autowired private PageMediaWikiService  pageMediaWikiService;
	@Autowired RevisionService revisionService;
	@Autowired private PageRepository pageRepository;

	/**
     * This methods inserts all the pages inside the DB querying the MediaWiki API.
     * @param lang String 
     * @param apiUrl String The MediaWiki API url
     * @return CompletableFuture<Boolean>
     */
    @Async
    public CompletableFuture<Boolean> addAllPages( String lang, String apiUrl ){
    	List<Page> pages = pageMediaWikiService.getAll(apiUrl);
    	
    	pages.forEach(page -> {
    		page.setLang(lang);
    		page.setLangPageId(lang + "_" +page.getPageid());
    	});
    	
    	pageRepository.save(pages);
    	LOG.info("Inserted all {} pages", lang);
        return CompletableFuture.completedFuture(true);
    }

    /**
     * This method creates a new Page. It requires the firstRevision of the Page in order
     * to create the LAST_REVISION and FIRST_REVISION relationships.
     * @param pageid
     * @param title
     * @param lang
     * @param firstRevision
     * @return
     */
    public Page addPage(int pageid, String title, String lang, Revision firstRevision){
		Page page = new Page(pageid, title, lang, lang + "_" + pageid);
		// Add the links from the page to the first and last revision
		page.setFistRevision(firstRevision);
		page.setLastRevision(firstRevision);
		pageRepository.save(page);
		
		return page;
	}

    /**
     * This method adds a new Revision to a page. It links the Page to the new revision via
     * LAST_REVISION link. Moreover it create the PREVIOUS_REVISION link.
     * @param langPageId
     * @param rev
     * @return
     */
	public Page addRevisionToPage(String langPageId, Revision rev){
        Page page = pageRepository.findByLangPageId(langPageId);
        // Add PREVIOUS_REVISION relationship
        rev.setPreviousRevision(page.getLastRevision());
        page.setLastRevision(rev);
        // The changes on the revision will be automatically persisted
        pageRepository.save(page);
        return page;
    }

    /**
     * This method changes only the title of a given Page.
     * @param oldTitle
     * @param newTitle
     * @param lang
     * @return
     */
    public Page movePage(String oldTitle, String newTitle, String lang){
        Page page = pageRepository.findByTitleAndLang(oldTitle, lang);
        page.setTitle(newTitle);
        pageRepository.save(page);
        return page;
    }

    public void deletePage(String title, String lang ){
        Page page = pageRepository.findByTitleAndLang(title, lang);
        // Delete the revisions of the page
        revisionService.deleteRevisionsOfPage(page.getLangPageId());
        // Delete finally the page itself
        pageRepository.delete(page);
        //TODO throw specific exceptions
    }
    
    /**
     * Apply the course structure using labels and relationships to the graph
     * @param apiUrl
     * @param lang
     */
    public void applyCourseStructure(String apiUrl, String lang){
    	List<Page> pages = pageRepository.findAllByLang(lang);
    	
    	// Remove all pages that are not course root pages
        Predicate<Page> pagePredicate = page -> page.getTitle().contains("/");
    	pages.removeIf(pagePredicate);
    	
    	for(Page p : pages){
    		// Get course tree and prepare relationship sets
    		CourseTree tree = pageMediaWikiService.getCourseTree(apiUrl, p.getTitle());
    		Set<Page> levelsTwo = new HashSet<>();
    		Set<Page> levelsThree = new HashSet<>();
    		
    		int index = 0;
     		for(String levelTwo : tree.getLevelsTwo()){
     			// Add CourseLEvelTwo label and add levels two to the set to be saved
    			String levelTwoTitle = (tree.getRoot() + "/" + levelTwo).trim();
    			Page levelTwoPage = pageRepository.findByTitleAndLang(levelTwoTitle, lang);
    			levelTwoPage.addLabel("CourseLevelTwo");
    			levelsTwo.add(levelTwoPage);
    			
    			// Add CourseLevelThree labels and add levels three to the set to be saved
    			for(String levelThree : tree.getLevelsTree().get(index)){
    				String levelThreeTitle = (levelTwoTitle + "/" + levelThree).trim();
    				Page levelTwoThree = pageRepository.findByTitleAndLang(levelThreeTitle, lang);
        			levelTwoThree.addLabel("CourseLevelThree");
        			levelsThree.add(levelTwoPage);
    			}
    			// Set LEVEL_THREE relationships
         		levelTwoPage.setLevelsThree(levelsThree);
    			index++;
    		}
     		// Set LEVEL_TWO relationships and CourseRoot label
     		p.addLabel("CourseRoot");
     		p.setLevelsTwo(levelsTwo);
     		
     		// Save all the course structure
     		pageRepository.save(levelsThree);
     		pageRepository.save(levelsTwo);
     		pageRepository.save(p);
    	}
    }

}
