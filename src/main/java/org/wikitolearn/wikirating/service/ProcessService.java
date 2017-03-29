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

    public Process createNewProcess(ProcessType type){
        Process proc = new Process(type);
        metadataService.addProcess(proc);
        processRepository.save(proc);
        return proc;
    }

    public Process closeCurrentProcess(ProcessStatus status){
        Process currentProcess = processRepository.getLastProcess();
        currentProcess.setProcessStatus(status);
        currentProcess.setEndOfProcess(new Date());
        processRepository.save(currentProcess);
        return currentProcess;
    }

}
