/**
 * 
 */
package org.wikitolearn.wikirating.service;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.exception.RevisionNotFoundException;
import org.wikitolearn.wikirating.model.Page;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.repository.PageRepository;
import org.wikitolearn.wikirating.repository.RevisionRepository;
import org.wikitolearn.wikirating.service.mediawiki.RevisionMediaWikiService;

/**
 * 
 * @author aletundo, valsdav
 *
 */
@Service
public class RevisionService {
	private static final Logger LOG = LoggerFactory.getLogger(RevisionService.class);
	@Autowired
	private RevisionMediaWikiService revisionMediaWikiService;
	@Autowired
	private RevisionRepository revisionRepository;
	@Autowired
	private PageRepository pageRepository;

	/**
	 * Initialize the revisions for the first time querying the MediaWiki API.
	 * This method adds the revisions and sets the FIRST_REVISION,
	 * LAST_REVISION and PREVIOUS_REVISION relationships.
	 * @param lang the domain language
	 * @param apiUrl the MediaWiki API url
	 * @return CompletableFuture
	 */
	@Async
	public CompletableFuture<Boolean> initRevisions(String lang, String apiUrl) {
		List<Page> pages = pageRepository.findAllByLang(lang);
		for(Page page : pages){
			List<Revision> revisions = revisionMediaWikiService.getAllRevisionByPageId(apiUrl, page.getPageid());
			// Set the first and the last revisions for the current page
			page.setFistRevision(revisions.get(0));
			page.setLastRevision(revisions.get(revisions.size() - 1));
            ListIterator<Revision> it = revisions.listIterator();
            while(it.hasNext()){
                Revision rev = it.next();
                rev.setLangRevId(lang + "_" + rev.getRevid());
                rev.setLang(lang);
                if (it.previousIndex() != 0){
                    rev.setPreviousRevision(revisions.get(it.previousIndex()-1));
                }
            }
			// Saving all the revisions node and the page node
			revisionRepository.save(revisions);
			pageRepository.save(page);
			LOG.info("Inserted revisions for page {}", page.getLangPageId());
		}

		return CompletableFuture.completedFuture(true);
	}

    /**
     * Add a new Revision to the graph
     * @param revid
     * @param lang
     * @param userid
     * @param parentid
     * @param length
     * @param timestamp
     * @return
     */
	public Revision addRevision(int revid, String lang, int userid, int parentid, int length, Date timestamp){
		Revision rev = new Revision(revid, lang,userid, parentid, length, timestamp);
		revisionRepository.save(rev);
		return rev;
	}
	
	/**
	 * Delete a revision given its langPageId
	 * @param langPageId the langPageId of the revision
	 */
	public void deleteRevisionsOfPage(String langPageId) throws RevisionNotFoundException{
        Set<Revision> revisions = revisionRepository.findAllRevisionOfPage(langPageId);
        if (revisions.size() == 0){
        	LOG.error("Revisions of page {} not found", langPageId);
        	throw new RevisionNotFoundException("Revisions of page "+langPageId+" not found");
		}
        revisionRepository.delete(revisions);
    }
	
	/**
	 * Get the requested revision
	 * @param langRevId the langRevId of the revision
	 * @return the revision
	 * @throws RevisionNotFoundException
	 */
	public Revision getRevision(String langRevId) throws RevisionNotFoundException{
		Revision revision = revisionRepository.findByLangRevId(langRevId);
		if(revision == null){
			LOG.error("Revision {} not found", langRevId);
			throw new RevisionNotFoundException();
		}
		return revision;
	}
	
	/**
	 * Update the given revision
	 * @param revision
	 * @return the updated revision
	 */
	public Revision updateRevision(Revision revision){
		revisionRepository.save(revision);
		return revision;
	}
}
