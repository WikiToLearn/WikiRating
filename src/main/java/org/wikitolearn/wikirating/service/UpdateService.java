package org.wikitolearn.wikirating.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.model.Process;
import org.wikitolearn.wikirating.model.UpdateEvent;
import org.wikitolearn.wikirating.service.mediawiki.RecentChangesMediaWikiService;
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
    @Autowired private UserService userService;
    @Autowired private RevisionService revisionService;
    @Autowired private ProcessService processService;
    @Autowired private MetadataService metadataService;
    @Autowired private RecentChangesMediaWikiService recentChangesMediaWikiService;
    @Value("#{'${mediawiki.langs}'.split(',')}")
    private List<String> langs;
    @Value("${mediawiki.protocol}")
    private String protocol;
    @Value("${mediawiki.api.url}")
    private String apiUrl;

    private boolean updateData(){
        //getting the begin timestamp of the latest FETCH Process before opening a new process
        Date beginOfLastFetch = processService.getLastProcessStartDateByType(ProcessType.FETCH);
        //opening a new FETCH process
        Process currentFetchProcess = processService.createNewProcess(ProcessType.FETCH);
        //First of all we get the RecentChangeEvents from Mediawiki API.
        String url = protocol + langs.get(0) + "." + apiUrl;
        List<UpdateEvent> updates = recentChangesMediaWikiService.getRecentChangesBetweenDates(apiUrl,"2800",
                beginOfLastFetch, currentFetchProcess.getStartOfProcess());


        return true;
    }



}
