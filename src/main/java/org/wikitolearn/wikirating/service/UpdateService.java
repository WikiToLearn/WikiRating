package org.wikitolearn.wikirating.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.model.Process;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.model.UpdateInfo;
import org.wikitolearn.wikirating.service.mediawiki.UpdateMediaWikiService;
import org.wikitolearn.wikirating.util.enums.ProcessType;

import java.util.Date;
import java.util.List;

/**
 * Created by valsdav on 29/03/17.
 */
@Service
public class UpdateService {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateService.class);

    @Autowired private PageService pageService;
    @Autowired private RevisionService revisionService;
    @Autowired private ProcessService processService;
    @Autowired private UpdateMediaWikiService updateMediaWikiService;
    @Value("#{'${mediawiki.langs}'.split(',')}")
    private List<String> langs;
    @Value("${mediawiki.protocol}")
    private String protocol;
    @Value("${mediawiki.api.url}")
    private String apiUrl;
    @Value("${mediawiki.namespace}")
    private String namespace;

    private boolean updateData(){
        //getting the begin timestamp of the latest FETCH Process before opening a new process
        Date beginOfLastFetch = processService.getLastProcessStartDateByType(ProcessType.FETCH);
        //opening a new FETCH process
        Process currentFetchProcess = processService.createNewProcess(ProcessType.FETCH);
        //First of all we get the RecentChangeEvents from Mediawiki API.

        for(String lang : langs) {
            String url = protocol + lang + "." + apiUrl;
            //fetching upda
            List<UpdateInfo> updates = updateMediaWikiService.getPagesUpdateInfo(url, namespace,
                    beginOfLastFetch, currentFetchProcess.getStartOfProcess());

            updates.forEach(update -> {
                switch (update.getType()) {
                    case NEW:
                        //create the new revision
                        Revision newRev = revisionService.addRevision(update.getRevid(), lang,
                                update.getUserid(), update.getOld_revid(), update.getNewlen(), update.getTimestamp());
                        //then create a new Page and link it with the revision
                        pageService.addPage(update.getPageid(), update.getTitle(), lang, newRev);
                        break;
                    case EDIT:
                        //create a new revision
                        Revision updateRev = revisionService.addRevision(update.getRevid(), lang,
                                update.getUserid(), update.getOld_revid(), update.getNewlen(), update.getTimestamp());
                        //then add it to the page
                        pageService.addRevisionToPage(lang + "_" + update.getPageid(), updateRev);
                        break;
                    case MOVE:
                        break;
                    case DELETE:
                        break;
				default:
					break;
                }
            });

        }

        return true;
    }


}
