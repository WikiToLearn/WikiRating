/**
 * 
 */
package org.wikitolearn.wikirating.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
     * This method creates a new Page. It requires the firstR evision of the Page in order
     * to create the LAST_REVISION and FIRST_REVISION relationships.
     * @param pageid
     * @param title
     * @param lang
     * @param firstRevision
     * @return
     */
    public Page addPage(int pageid, String title, String lang, Revision firstRevision){
		Page page = new Page(title, pageid, lang);
		//creating the relations with the first revision.
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
        //adding PREVIOUS_REVISION relationship
        rev.setPreviousRevision(page.getLastRevision());
        page.setLastRevision(rev);
        //the changes on the revision will be automatically persisted
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


}
