package org.wikitolearn.wikirating.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.model.Process;
import org.wikitolearn.wikirating.repository.ProcessRepository;
import org.wikitolearn.wikirating.util.enums.ProcessStatus;
import org.wikitolearn.wikirating.util.enums.ProcessType;

import java.util.Date;

/**
 * Created by valsdav on 29/03/17.
 */
@Service
public class ProcessService {
    @Autowired
    private MetadataService metadataService;
    @Autowired
    private ProcessRepository processRepository;

    /**
     * This method creates a new process of the specified
     * type and adds it on the top of the processes chain.
     * @param type Type of process requested
     * @return returns the created process
     */
    public Process createNewProcess(ProcessType type){
        Process proc = new Process(type);
        metadataService.addProcess(proc);
        processRepository.save(proc);
        return proc;
    }

    /**
     * This method modify the status of the last opened process
     * and saves it.
     * @param status Final status of the process
     * @return returns the closed process
     */
    public Process closeCurrentProcess(ProcessStatus status){
        Process currentProcess = processRepository.getLastProcess();
        currentProcess.setProcessStatus(status);
        currentProcess.setEndOfProcess(new Date());
        processRepository.save(currentProcess);
        return currentProcess;
    }

}
