/**
 * 
 */
package org.wikitolearn.wikirating.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
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
import org.wikitolearn.wikirating.model.UpdateInfo;
import org.wikitolearn.wikirating.model.graph.*;
import org.wikitolearn.wikirating.repository.CourseLevelThreeRepository;
import org.wikitolearn.wikirating.repository.CourseLevelTwoRepository;
import org.wikitolearn.wikirating.repository.CourseRootRepository;
import org.wikitolearn.wikirating.repository.PageRepository;
import org.wikitolearn.wikirating.service.mediawiki.PageMediaWikiService;
import org.wikitolearn.wikirating.service.mediawiki.UpdateMediaWikiService;
import org.wikitolearn.wikirating.util.enums.CourseLevel;

import com.google.common.base.CaseFormat;

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
	@Autowired private UpdateMediaWikiService updateMediaWikiService;
	@Autowired private UserService userService;

	@Autowired private PageRepository<Page> pageRepository;
	@Autowired private CourseRootRepository courseRootRepository;
	@Autowired private CourseLevelTwoRepository courseLevelTwoRepository;
	@Autowired private CourseLevelThreeRepository courseLevelThreeRepository;

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
    		//Now we check the level of the page to set the right
            // additional laber for the Course Structure.
            CourseLevel levelLabel = getPageLevelFromTitle(page.getTitle());
            if (levelLabel != CourseLevel.UNCATEGORIZED){
            	String label = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, levelLabel.name());
                page.addLabel(label);
            }
    	});
    	
    	pageRepository.save(pages);
    	LOG.info("Inserted all {} pages", lang);
        return CompletableFuture.completedFuture(true);
    }


    /**
     * 
     * @param lang
     * @param apiUrl
     * @param start
     * @param end
     * @return
     * @throws UpdatePagesAndRevisionsException
     */
    @Async
    public CompletableFuture<Boolean> updatePages(String lang, String apiUrl, Date start, Date end) throws UpdatePagesAndRevisionsException{
    	try{
    		List<UpdateInfo> updates = updateMediaWikiService.getPagesUpdateInfo(apiUrl, namespace, start, end);
    		for(UpdateInfo update : updates){
    			if(!namespace.equals(update.getNs())) continue;
    			switch (update.getType()) {
    			case "new":
                    // Add the new page with the right Course level
    			    Page newPage = addCoursePage(update.getPageLevelFromTitle(),update.getPageid(), update.getTitle(), lang);
    			    if (update.getPageLevelFromTitle() == CourseLevel.COURSE_LEVEL_THREE) {
                        // Create the new revision. The change coefficient for the new revision is set to 0 by default.
                        Revision newRev = revisionService.addRevision(update.getRevid(), lang, update.getUserid(),
                                update.getOld_revid(), update.getNewlen(), update.getTimestamp());
                        // Add the first revision to the page
                        ((CourseLevelThree) newPage).initFirstRevision(newRev);
                        // It's necessary to save the page again
                        courseLevelThreeRepository.save((CourseLevelThree) newPage);
                        userService.setAuthorship(newRev);
                    }
    				break;
    			case "edit":
    			    // Act only on CourseLevelThree pages
                    if (update.getPageLevelFromTitle() == CourseLevel.COURSE_LEVEL_THREE){
                        // Create a new revision
                        Revision updateRev = revisionService.addRevision(update.getRevid(), lang, update.getUserid(),
                                update.getOld_revid(), update.getNewlen(), update.getTimestamp());
                        // Then add it to the page
                        addRevisionToPage(lang + "_" + update.getPageid(), updateRev);
                        // Then calculate the changeCoefficient
                        revisionService.setChangeCoefficient(apiUrl, updateRev);
                        // Finally set the authorship
                        userService.setAuthorship(updateRev);
    			    }
    				break;
    			case "move":
    			    // We have to change the label in case the Course level is changed
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
    	}catch(GetPagesUpdateInfoException | PageNotFoundException e){
			LOG.error("An error occurred while updating pages and revisions: {}", e.getMessage());
			throw new UpdatePagesAndRevisionsException();
		}
		return CompletableFuture.completedFuture(true);
	}


    /**
     * Create a new generic Page entity.
     * @param pageid
     * @param title
     * @param lang
     * @return the added page
     */
    public Page addPage(int pageid, String title, String lang){
        Page page = new Page(pageid, title, lang, lang + "_" + pageid);
        pageRepository.save(page);
        return page;
    }

    /**
     * Add a page entity distinguishing between the different Course levels.
     * @param level
     * @param pageid
     * @param title
     * @param lang
     * @return
     */
    public Page addCoursePage(CourseLevel level, int pageid, String title, String lang){
        switch(level){
            case COURSE_ROOT:
                CourseRoot pageRoot = new CourseRoot(pageid, title, lang, lang + "_" + pageid);
                courseRootRepository.save(pageRoot);
                return pageRoot;
            case COURSE_LEVEL_TWO:
                CourseLevelTwo pageTwo = new CourseLevelTwo(pageid, title, lang, lang + "_" + pageid);
                courseLevelTwoRepository.save(pageTwo);
                return pageTwo;
            case COURSE_LEVEL_THREE:
                CourseLevelThree pageThree = new CourseLevelThree(pageid, title, lang, lang + "_" + pageid);
                courseLevelThreeRepository.save(pageThree);
                return pageThree;
            default:
                return addPage(pageid, title, lang);
        }
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
     * Add a new revision to a CourseLevelThree page. It links the page to the new revision via
     * LAST_REVISION link. Moreover it create the PREVIOUS_REVISION link.
     * @param langPageId
     * @param rev
     */
	public void addRevisionToPage(String langPageId, Revision rev) throws PageNotFoundException{
        CourseLevelThree page = courseLevelThreeRepository.findByLangPageId(langPageId);
        if(page == null){
        	throw new PageNotFoundException();
        }
        //TODO Check if the page has no previous revisions
        // Add PREVIOUS_REVISION relationship
        rev.setPreviousRevision(page.getLastRevision());
        //page.setLastRevision(rev);  //Maybe this is not necessary
        // The changes on the revision will be automatically persisted
        courseLevelThreeRepository.save(page);
        //Update the LAST_REVISION edge with a query
        courseLevelThreeRepository.updateLastRevision(page.getLangPageId());
    }

    /**
     * Change title of a page. The method is prefixed by move to follow MediaWiki naming.
     * The method checks also the new page title to set the right Course level label
     * in case of changes
     * @param oldTitle the old title of the page
     * @param newTitle the new title to set
     * @param lang the language of the page
     * @return the updated page
     * @throws PageNotFoundException
     */
    public Page movePage(String oldTitle, String newTitle, String lang) throws PageNotFoundException{
        Page page = pageRepository.findByTitleAndLang(oldTitle, lang);
        if(page == null){
        	throw new PageNotFoundException();
        }
        page.setTitle(newTitle);
        // Check if the Course level has changed
        if (getPageLevelFromTitle(oldTitle) != getPageLevelFromTitle(newTitle)){
            page.removeLabel(getPageLevelFromTitle(oldTitle).name());
            page.addLabel(getPageLevelFromTitle(newTitle).name());
        }
        pageRepository.save(page);
        return page;
    }

    /**
     * Delete a page from the graph given its title and domain language
     * @param title the title of the page
     * @param lang the language of the page
     * @throws PageNotFoundException
     */
    public void deletePage(String title, String lang) throws PageNotFoundException{
        Page page = pageRepository.findByTitleAndLang(title, lang);
        if(page == null){
        	throw new PageNotFoundException();
        }
        // Delete the revisions of the page if it's CourseLevelThree
        if (page.hasLabel("CourseLevelThree")){
            revisionService.deleteRevisionsOfPage(page.getLangPageId());
        }
        // Delete finally the page itself
        pageRepository.delete(page);
    }

    /**
     * Get the level of a Page in the Course Structure
     * analyzing the number of slashes in the title
     * @param title the title of the page
     * @return the course level
     */
    public static CourseLevel getPageLevelFromTitle(String title){
        int slashesNumber = StringUtils.countMatches(title, "/");
        switch (slashesNumber){
            case 0:
                return CourseLevel.COURSE_ROOT;
            case 1:
                return CourseLevel.COURSE_LEVEL_TWO;
            case 2:
                return CourseLevel.COURSE_LEVEL_THREE;
            default:
                //The page remains generic.
                return CourseLevel.UNCATEGORIZED;
        }
    }
    
    /**
     * Get the pages labeled by :CourseRoot label
     * @return the list of course root pages
     */
    public List<CourseRoot> getCourseRootPages(String lang){
    	return courseRootRepository.findByLang(lang);
    }
    
    /**
     * Get the page labeled only by :Page label
     * @return the list of the uncategorized pages
     */
    public List<Page> getUncategorizedPages(String lang){
    	return pageRepository.findAllUncategorizedPages(lang);
    }
    
	/**
     * @param lang the language of the domain
     * @param apiUrl the MediaWiki API url
	 * @param courseRootPages
	 */
	@Async
	public CompletableFuture<Boolean> applyCourseStructure(String lang, String apiUrl) {
		List<CourseRoot> courseRootPages = getCourseRootPages(lang);
		for (CourseRoot pageRoot : courseRootPages) {
			// Get course tree and prepare relationship set
			CourseTree tree = pageMediaWikiService.getCourseTree(apiUrl, pageRoot.getTitle());
			Set<CourseLevelTwo> levelsTwo = (pageRoot.getLevelsTwo() == null) ? new HashSet<>() :
                    pageRoot.getLevelsTwo();

			int index = 0;
			for (String levelTwo : tree.getLevelsTwo()) {
				String levelTwoTitle = (tree.getRoot() + "/" + levelTwo).trim();
				CourseLevelTwo levelTwoPage = courseLevelTwoRepository.findByTitleAndLang(levelTwoTitle, lang);

				// Skip malformed page
				if (levelTwoPage == null)
					continue;

				// Add levels two to the set to be saved
				if(!levelsTwo.contains(levelTwoPage)){
					levelsTwo.add(levelTwoPage);
				}
				
				Set<CourseLevelThree> levelsThree = (levelTwoPage.getLevelsThree() == null) ? new HashSet<>()
						: levelTwoPage.getLevelsThree();
				// Add levels three to the set to be saved
				for (String levelThree : tree.getLevelsTree().get(index)) {
					String levelThreeTitle = (levelTwoTitle + "/" + levelThree).trim();
					CourseLevelThree levelThreePage = courseLevelThreeRepository.findByTitleAndLang(levelThreeTitle, lang);
					// Skip malformed page
					if (levelThreePage == null)
						continue;
					
					if(!levelsThree.contains(levelThreePage)){
						levelsThree.add(levelThreePage);
					}
				}
				// Set LEVEL_THREE relationships
				levelTwoPage.setLevelsThree(levelsThree);
				courseLevelThreeRepository.save(levelsThree);
				index++;
			}
			// Set LEVEL_TWO relationships
			pageRoot.setLevelsTwo(levelsTwo);
			courseLevelTwoRepository.save(levelsTwo);
			courseRootRepository.save(pageRoot);
		}
		
		return CompletableFuture.completedFuture(true);
	}

}
