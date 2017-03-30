package org.wikitolearn.wikirating.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.model.Metadata;
import org.wikitolearn.wikirating.model.Process;
import org.wikitolearn.wikirating.repository.MetadataRepository;
import org.wikitolearn.wikirating.util.enums.MetadataType;

/**
 * This service manages the addition of information about Metadata.
 * Created by valsdav on 29/03/17.
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
    }

    /**
     * This method inserts in the DB a new Process connecting it
     * to the Metadata root node and to the previous Process.
     * @param process
     * @return
     */
    public boolean addProcess(Process process){
        Metadata metadata = metadataRepository.getMetadataByType(MetadataType.PROCESSES);
        // Creating the new process node of the chain
        process.setPreviousProcess(metadata.getLastItem());
        metadata.setLastItem(process);
        metadataRepository.save(metadata);
        LOG.info("Process {} inserted ", process);
        return true;
    }
}