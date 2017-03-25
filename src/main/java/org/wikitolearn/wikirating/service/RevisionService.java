/**
 * 
 */
package org.wikitolearn.wikirating.service;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.model.Page;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.model.User;
import org.wikitolearn.wikirating.repository.PageRepository;
import org.wikitolearn.wikirating.repository.RevisionRepository;
import org.wikitolearn.wikirating.repository.UserRepository;
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
	@Autowired
	private UserRepository userRepository;

	/**
	 * This method inserts all the revisions for every page, creating the
	 * connections between them and between the users that have written them.
	 * 
	 * @param lang
	 *            String
	 * @param apiUrl
	 *            String The MediaWiki API url
	 * @return CompletableFuture<Boolean>
	 */
	@Async
	public CompletableFuture<Boolean> addAllRevisions(String lang, String apiUrl) {
		Iterable<Page> pages = pageRepository.findAll();
		pages.forEach(page -> {
			List<Revision> revisions = revisionMediaWikiService.getAllRevisionByPageId(apiUrl, page.getPageid());
			// Set the first and the last revisions for the current page
			page.setFistRevision(revisions.get(0));
			page.setLastRevision(revisions.get(revisions.size() - 1));
			revisions.forEach(revision -> {
				revision.setLangRevId(lang + "_" + revision.getRevid());
				// Get the previous revision filtering the collection
				for(Revision r : revisions){
					if(r.getRevid() == revision.getParentid()){
						revision.setPreviousRevision(r);
					}
				}
			});
			revisionRepository.save(revisions);
			pageRepository.save(page);
			LOG.info("Inserted revisions for page {}", page.getLangPageId());
		});
		return CompletableFuture.completedFuture(true);
	}

}
