/**
 * 
 */
package org.wikitolearn.wikirating.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.exception.GetPagesUpdateInfoException;
import org.wikitolearn.wikirating.exception.PageNotFoundException;
import org.wikitolearn.wikirating.exception.UpdatePagesAndRevisionsException;
import org.wikitolearn.wikirating.model.CourseTree;
import org.wikitolearn.wikirating.model.Page;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.model.UpdateInfo;
import org.wikitolearn.wikirating.repository.PageRepository;
import org.wikitolearn.wikirating.service.mediawiki.PageMediaWikiService;
import org.wikitolearn.wikirating.service.mediawiki.UpdateMediaWikiService;

/**
 * 
 * @author aletundo, valsdav
 *
 */
@Service
public class PageService {
	private static final Logger LOG = LoggerFactory.getLogger(PageService.class);

	@Autowired private PageMediaWikiService  pageMediaWikiService;
	@Autowired private RevisionService revisionService;
	@Autowired private PageRepository pageRepository;
	@Autowired private UpdateMediaWikiService updateMediaWikiService;
	@Autowired private UserService userService;
	@Value("${mediawiki.namespace}")
	private String namespace;

	/**
     * This methods inserts all the pages inside the DB querying the MediaWiki API.
     * @param lang String 
     * @param apiUrl String The MediaWiki API url
     * @return CompletableFuture<Boolean>
     */
    @Async
    public CompletableFuture<Boolean> initPages( String lang, String apiUrl ){
    	List<Page> pages = pageMediaWikiService.getAll(apiUrl);
    	
    	pages.forEach(page -> {
    		page.setLang(lang);
    		page.setLangPageId(lang + "_" +page.getPageId());
    	});
    	
    	pageRepository.save(pages);
    	LOG.info("Inserted all {} pages", lang);
        return CompletableFuture.completedFuture(true);
    }
    
    @Async
    public CompletableFuture<Boolean> updatePages(String lang, String apiUrl, Date start, Date end){
    	try{
    		List<UpdateInfo> updates = updateMediaWikiService.getPagesUpdateInfo(apiUrl, namespace, start, end);
    		for(UpdateInfo update : updates){
    			switch (update.getType()) {
    			case "new":
    				// Create the new revision
    				Revision newRev = revisionService.addRevision(update.getRevid(), lang, update.getUserid(),
    						update.getOld_revid(), update.getNewlen(), update.getTimestamp());
    				// Then create a new Page and link it with the revision
    				addPage(update.getPageid(), update.getTitle(), lang, newRev);
    				userService.setAuthorship(newRev);
    				break;
    			case "edit":
    				// Create a new revision
    				Revision updateRev = revisionService.addRevision(update.getRevid(), lang, update.getUserid(),
    						update.getOld_revid(), update.getNewlen(), update.getTimestamp());
    				// Then add it to the page
    				addRevisionToPage(lang + "_" + update.getPageid(), updateRev);
    				userService.setAuthorship(updateRev);
    				break;
    			case "move":
    				// Move the page to the new title
    				movePage(update.getTitle(), update.getNewTitle(), lang);
    				break;
    			case "delete":
    				// Delete the page and all its revisions
    				deletePage(update.getTitle(), lang);
    				break;
    			default:
    				break;
    			}
    		}
    	}catch(GetPagesUpdateInfoException e){
			LOG.error("An error occurred while updating pages and revisions: {}", e.getMessage());
			throw new UpdatePagesAndRevisionsException();
		}
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
     * Get the page with the given pageId and language
     * @param pageId the id of the page
     * @param lang the language of the page
     * @return the requested page
     * @throws PageNotFoundException
     */
    public Page getPage(int pageId, String lang) throws PageNotFoundException{
    	Page page = pageRepository.findByLangPageId(lang + "_" + pageId);
    	
    	if(page == null){
    		LOG.error("Page with pageId {} and lang {} not found.", pageId, lang);
    		throw new PageNotFoundException();
    	}
    	return page;
    }
    
    /**
     * Get the page with the given langePageId
     * @param langPageId the langPageId of the page
     * @return the requested page
     * @throws PageNotFoundException
     */
    public Page getPage(String langPageId) throws PageNotFoundException{
    	Page page = pageRepository.findByLangPageId(langPageId);
    	if(page == null){
    		LOG.error("Page with langPageId: {} not found.", langPageId);
    		throw new PageNotFoundException();
    	}
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
    
    /**
     * Delete a page from the graph given its title and domain language.
     * @param title
     * @param lang
     */
    public void deletePage(String title, String lang){
        Page page = pageRepository.findByTitleAndLang(title, lang);
        // Delete the revisions of the page
        revisionService.deleteRevisionsOfPage(page.getLangPageId());
        // Delete finally the page itself
        pageRepository.delete(page);
        //TODO throw specific exceptions
    }
    
    /**
     * 
     * @return
     */
    public List<Page> getCourseRootPages(String lang){
    	return pageRepository.findAllCourseRootPages(lang);
    }
    
    /**
     * 
     * @return
     */
    public List<Page> getUncategorizedPages(String lang){
    	return pageRepository.findAllUncategorizedPages(lang);
    }
    
    /**
     * Apply the course structure using labels and relationships to the graph
     * @param lang
     * @param apiUrl
     */
    @Async
    public CompletableFuture<Boolean> initCourseStructure(String lang, String apiUrl){
    	List<Page> pages = pageRepository.findAllByLang(lang);
    	
    	// Remove all pages that are not course root pages
        Predicate<Page> pagePredicate = page -> page.getTitle().contains("/");
    	pages.removeIf(pagePredicate);
    	
    	/*for(Page p : pages){
    		// Get course tree and prepare relationship sets
    		CourseTree tree = pageMediaWikiService.getCourseTree(apiUrl, p.getTitle());
    		Set<Page> levelsTwo = new HashSet<>();
    		
    		int index = 0;
     		for(String levelTwo : tree.getLevelsTwo()){
     			Set<Page> levelsThree = new HashSet<>();
     			// Add CourseLEvelTwo label and add levels two to the set to be saved
    			String levelTwoTitle = (tree.getRoot() + "/" + levelTwo).trim();
    			Page levelTwoPage = pageRepository.findByTitleAndLang(levelTwoTitle, lang);
    			// Skip malformed page
    			if(levelTwoPage == null) continue;
    			
    			levelTwoPage.addLabel("CourseLevelTwo");
    			levelsTwo.add(levelTwoPage);
    			
    			// Add CourseLevelThree labels and add levels three to the set to be saved
    			for(String levelThree : tree.getLevelsTree().get(index)){
    				String levelThreeTitle = (levelTwoTitle + "/" + levelThree).trim();
    				Page levelThreePage = pageRepository.findByTitleAndLang(levelThreeTitle, lang);
    				// Skip malformed page
        			if(levelThreePage == null) continue;
        			
        			levelThreePage.addLabel("CourseLevelThree");
        			levelsThree.add(levelThreePage);
    			}
    			// Set LEVEL_THREE relationships
         		levelTwoPage.setLevelsThree(levelsThree);
         		pageRepository.save(levelsThree);
    			index++;
    		}
     		// Set LEVEL_TWO relationships and CourseRoot label
     		p.addLabel("CourseRoot");
     		p.setLevelsTwo(levelsTwo);
     		pageRepository.save(levelsTwo);
     		pageRepository.save(p);
    	}*/
    	applyCourseStructure(lang, apiUrl, pages);
    	
    	return CompletableFuture.completedFuture(true);
    }
    
	public CompletableFuture<Boolean> updateCourseStructure(String lang, String apiUrl) {
		List<Page> courseRootPages = getCourseRootPages(lang);
		List<Page> uncategorizedPages = getUncategorizedPages(lang);

		// Remove all uncategorized pages that are not course root pages
		Predicate<Page> pagePredicate = page -> page.getTitle().contains("/");
		uncategorizedPages.removeIf(pagePredicate);

		courseRootPages.addAll(uncategorizedPages);

		applyCourseStructure(lang, apiUrl, courseRootPages);

		return CompletableFuture.completedFuture(true);
	}

	/**
	 * @param lang
	 * @param apiUrl
	 * @param courseRootPages
	 */
	private void applyCourseStructure(String lang, String apiUrl, List<Page> courseRootPages) {
		for (Page p : courseRootPages) {
			// Get course tree and prepare relationship set
			CourseTree tree = pageMediaWikiService.getCourseTree(apiUrl, p.getTitle());
			Set<Page> levelsTwo = (p.getLevelsTwo() == null) ? new HashSet<>() : p.getLevelsTwo();

			int index = 0;
			for (String levelTwo : tree.getLevelsTwo()) {
				String levelTwoTitle = (tree.getRoot() + "/" + levelTwo).trim();
				Page levelTwoPage = pageRepository.findByTitleAndLang(levelTwoTitle, lang);

				// Skip malformed page
				if (levelTwoPage == null)
					continue;

				// Add CourseLevelTwo label and add levels two to the set to be saved
				if(!levelsTwo.contains(levelTwoPage)){
					levelTwoPage.addLabel("CourseLevelTwo");
					levelsTwo.add(levelTwoPage);
				}
				
				Set<Page> levelsThree = (levelTwoPage.getLevelsThree() == null) ? new HashSet<>()
						: levelTwoPage.getLevelsThree();
				// Add CourseLevelThree labels and add levels three to the set to be saved
				for (String levelThree : tree.getLevelsTree().get(index)) {
					String levelThreeTitle = (levelTwoTitle + "/" + levelThree).trim();
					Page levelThreePage = pageRepository.findByTitleAndLang(levelThreeTitle, lang);
					// Skip malformed page
					if (levelThreePage == null)
						continue;
					
					if(!levelsThree.contains(levelThreePage)){
						levelThreePage.addLabel("CourseLevelThree");
						levelsThree.add(levelThreePage);
					}
				}
				// Set LEVEL_THREE relationships
				levelTwoPage.setLevelsThree(levelsThree);
				pageRepository.save(levelsThree);
				index++;
			}
			// Set LEVEL_TWO relationships and CourseRoot label
			p.addLabel("CourseRoot");
			p.setLevelsTwo(levelsTwo);
			pageRepository.save(levelsTwo);
			pageRepository.save(p);
		}
	}

}
