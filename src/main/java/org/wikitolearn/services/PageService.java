/**
 * 
 */
package org.wikitolearn.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wikitolearn.dao.PageDAO;
import org.wikitolearn.models.Page;
import org.wikitolearn.services.mediawiki.PageMediaWikiService;

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
	private PageDAO pageDao;
	
	/**
     * This methods inserts all the pages inside the DB querying the MediaWiki API.
     * @param lang String 
     * @param apiUrl String The MediaWiki API url
     * @return CompletableFuture<Boolean>
     */
    @Async
    public CompletableFuture<Boolean> addAllPages( String lang, String apiUrl ){
        List<Page> pages =  pageMediaWikiService.getAllPages(apiUrl);
        LOG.info("Fetched all the pages");
        boolean insertionResultPages = pageDao.insertPages(pages, lang);
        if(insertionResultPages){
            LOG.info("Inserted pages");
        }else{
            LOG.error("Something went wrong during pages insertion");
        }
        return CompletableFuture.completedFuture(insertionResultPages);
    }
}
