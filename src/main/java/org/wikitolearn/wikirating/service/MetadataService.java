package org.wikitolearn.wikirating.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.exception.FirstProcessInsertionException;
import org.wikitolearn.wikirating.exception.LatestProcessUpdateException;
import org.wikitolearn.wikirating.model.graph.Metadata;
import org.wikitolearn.wikirating.model.graph.Process;
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

    /**
     * Add the first process of the chain to the Metadata node.
     * @param process First process
     */
    public void addFirstProcess(Process process) throws FirstProcessInsertionException {
        try {
            Metadata metadata = metadataRepository.getMetadataByType(MetadataType.PROCESSES);
            // checking if this isn't the first Process.
            if (metadata.getLatestProcess() != null) {
                LOG.error("A Process already exists. You cannot re-insert the first Process");
                throw new FirstProcessInsertionException("A Process already exists. You cannot re-insert the first Process");
            }
            metadata.setLatestProcess(process);
            metadataRepository.save(metadata);
        } catch (FirstProcessInsertionException f){
            throw f;
        } catch (Exception e){
            LOG.error("Something went wrong during insertion of the first Process: {}", e.getMessage());
            throw new FirstProcessInsertionException();
        }
    }
}
