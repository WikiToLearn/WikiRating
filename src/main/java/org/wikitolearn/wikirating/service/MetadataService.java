package org.wikitolearn.wikirating.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.exception.LatestProcessUpdateException;
import org.wikitolearn.wikirating.model.Metadata;
import org.wikitolearn.wikirating.repository.MetadataRepository;
import org.wikitolearn.wikirating.util.enums.MetadataType;

/**
 * This service manages the addition of information about Metadata
 * @author aletundo
 * @author valsdav
 */
@Service
public class MetadataService {
    private static final Logger LOG = LoggerFactory.getLogger(MetadataService.class);
    @Autowired
    private MetadataRepository metadataRepository;
    
    /**
     * This method insert in the DB the root nodes of the metadata
     */
    public void initMetadata(){
        Metadata metadataProcesses = new Metadata(MetadataType.PROCESSES);
        Metadata metadataStats = new Metadata(MetadataType.STATS);
        metadataRepository.save(metadataProcesses);
        metadataRepository.save(metadataStats);
        LOG.info("Initialized Metadata nodes");
    }
    
    /**
     * Update the LATEST_PROCESS relationship
     * @param process the latest process
     * @return
     */
    public void updateLatestProcess() throws LatestProcessUpdateException{
    	try{
            metadataRepository.updateLatestProcess();
            LOG.info("Updated LATEST_PROCESS relationship");
    	}catch(Exception e){
    		LOG.error("Something went wrong during update LATEST_PROCESS relationship: {}", e.getMessage());
    		throw new LatestProcessUpdateException();
    	}
    }
}
