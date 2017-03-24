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
	@Autowired
	private PageMediaWikiService  pageMediaWikiService;
	@Autowired
	private PageRepository pageRepository;

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
}
