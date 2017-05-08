/**
 * 
 */
package org.wikitolearn.wikirating.service;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.exception.RevisionNotFoundException;
import org.wikitolearn.wikirating.model.graph.CourseLevelThree;
import org.wikitolearn.wikirating.model.graph.Revision;
import org.wikitolearn.wikirating.repository.CourseLevelThreeRepository;
import org.wikitolearn.wikirating.repository.RevisionRepository;
import org.wikitolearn.wikirating.service.mediawiki.RevisionMediaWikiService;

import static java.lang.Math.exp;

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
	private CourseLevelThreeRepository courseLevelThreeRepository;

	/**
	 * Initialize the revisions for the first time querying the MediaWiki API.
	 * This method adds the revisions and sets the FIRST_REVISION,
	 * LAST_REVISION and PREVIOUS_REVISION relationships.
	 * Only the revisions of CourseLevelThree pages are fetched.
	 * @param lang the domain language
	 * @param apiUrl the MediaWiki API url
	 * @return CompletableFuture
	 */
	@Async
	public CompletableFuture<Boolean> initRevisions(String lang, String apiUrl) {
		List<CourseLevelThree> pages = courseLevelThreeRepository.findByLang(lang);
		for(CourseLevelThree page : pages){
			List<Revision> revisions = revisionMediaWikiService.getAllRevisionByPageId(apiUrl, page.getPageId());
			// Set the first and the last revisions for the current page
			page.setFirstRevision(revisions.get(0));
			page.setLastRevision(revisions.get(revisions.size() - 1));
            // Set the last validated revision to the first one.
            page.setLastValidatedRevision(revisions.get(0));
            // Create the PreviousRevision links
            ListIterator<Revision> it = revisions.listIterator();
            while(it.hasNext()){
                Revision rev = it.next();
                rev.setLangRevId(lang + "_" + rev.getRevId());
                rev.setLang(lang);
                if (it.previousIndex() != 0) {
                    rev.setPreviousRevision(revisions.get(it.previousIndex() - 1));
                }
            }
			// Saving all the revisions node and the page node
			revisionRepository.save(revisions);
			courseLevelThreeRepository.save(page);
			LOG.info("Inserted revisions for page {}", page.getLangPageId());
		}

		return CompletableFuture.completedFuture(true);
	}

	@Async
    public CompletableFuture<Boolean> calculateChangeCoefficientAllRevisions(String lang, String apiUrl){
	    LOG.info("Calculating changeCoefficient for all the revisions...");
        Set<Revision> revisions = revisionRepository.findByLang(lang);
        for (Revision rev : revisions){
            double changeCoefficient = calculateChangeCoefficient(apiUrl, rev);
            rev.setChangeCoefficient(changeCoefficient);
        }
        revisionRepository.save(revisions);
        LOG.info("ChangeCoefficient calculated for all revisions.");
        return CompletableFuture.completedFuture(true);
    }

    /**
     * Add a new Revision to the graph. This method DOESN'T link the
     * revision to a Page.
     * @param revid
     * @param lang
     * @param userid
     * @param parentid
     * @param length
     * @param timestamp
     * @return
     */
	public Revision addRevision(int revid, String lang, int userid, int parentid, int length, Date timestamp){
		Revision rev = new Revision(revid, lang, userid, parentid, length, timestamp);
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
	
	public Set<Revision> getRevisionsOfPage(String langPageId) throws RevisionNotFoundException{
		Set<Revision> revisions = revisionRepository.findAllRevisionOfPage(langPageId);
        if (revisions.size() == 0){
        	LOG.error("Revisions of page {} not found", langPageId);
        	throw new RevisionNotFoundException("Revisions of page "+langPageId+" not found");
		}
        return revisions;
	}

	public Set<Revision> getRevisionsOfPageOrdered(String langPageId) throws RevisionNotFoundException{
		Set<Revision> revisions = revisionRepository.findAllRevisionOfPageOrdered(langPageId);
		if (revisions.size() == 0){
			LOG.error("Revisions of page {} not found", langPageId);
			throw new RevisionNotFoundException("Revisions of page "+langPageId+" not found");
		}
		return revisions;
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

    /**
     * Calculate and set the changeCoefficient of a Revision.
     * This method also persist the Revision changes.
     * @param apiUrl
     * @param revision
     */
	public void setChangeCoefficient(String apiUrl, Revision revision){
		double cc = calculateChangeCoefficient(apiUrl, revision);
		revision.setChangeCoefficient(cc);
		revisionRepository.save(revision);
	}

    /**
	 * Calculate the changeCoefficient for a given Revision querying mediawiki
     * and returns the number. It doesn't store it in the Revision.
	 * @param revision
	 * @return
	 */
	public double calculateChangeCoefficient(String apiUrl, Revision revision){
        double previousLength = 0.0;
        double changeCoefficient = 0.0;
        // Get the previous Revision
        Revision previousRevision = revision.getPreviousRevision();
        if (previousRevision == null){
            previousLength = 1.0;
            changeCoefficient = 0.0;
            LOG.info("Change coefficient of revision {} (first-rev): {}", revision.getLangRevId(), changeCoefficient);
        } else{
            double prevL = (double) previousRevision.getLength();
            // Suppose the mean line length of 120 characters and that the length is in bytes.
            // We want a "lenght" in n° of lines
            if (prevL == 0){
                previousLength = 1.0;
            }else {
                previousLength =  (prevL < 120) ? 1 : prevL / 120;
            }

            // Query mediawiki for diff text
            String diffText = revisionMediaWikiService.getDiffPreviousRevision(apiUrl,
                    previousRevision.getRevId(), revision.getRevId());

            int addedLines = StringUtils.countMatches(diffText, "diff-addedline");
            int deletedLines = StringUtils.countMatches(diffText, "diff-deletedline");
            //int inlineChanges = StringUtils.countMatches(diffText, "diffchange-inline");

            // Finally calculation of change Coefficient
            double t = ((1.2 * deletedLines +  addedLines) ) / previousLength;
            changeCoefficient = 1 / exp(0.5 * t);

            LOG.info("Change coefficient of revision {} (+{}-{}/{}): {}", revision.getLangRevId(), addedLines,
                    deletedLines, previousLength, changeCoefficient);
        }

		return changeCoefficient;
	}


}
