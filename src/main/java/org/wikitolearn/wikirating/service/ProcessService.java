package org.wikitolearn.wikirating.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(ProcessService.class);
    
    @Autowired
    private ProcessRepository processRepository;

    /**
     * This method creates a new process of the specified
     * type and adds it on the top of the processes chain.
     * @param type Type of process requested
     * @return returns the created process
     */
    public Process createNewProcess(ProcessType type){
    	Process previousProcess = processRepository.getLatestProcess();
        Process proc = new Process(type);
        proc.setPreviousProcess(previousProcess);
        processRepository.save(proc);
        LOG.info("Created new process: {}", proc.toString());
        return proc;
    }

    /**
     * This method modify the status of the last opened process
     * and saves it.
     * @param status Final status of the process
     * @return returns the closed process
     */
    public Process closeCurrentProcess(ProcessStatus status){
        Process currentProcess = processRepository.getOnGoingProcess();
        currentProcess.setProcessStatus(status);
        currentProcess.setEndOfProcess(new Date());
        processRepository.save(currentProcess);
        LOG.info("Updated last process: {}", currentProcess.toString());
        return currentProcess;
    }

    public Date getLastProcessBeginDate(){
        return processRepository.getLatestProcess().getStartOfProcess();
    }

    public Date getLastProcessStartDateByType(ProcessType type){
        Process latestProcess = processRepository.getLastProcessByType(type);
        if(latestProcess != null){
        	return latestProcess.getStartOfProcess();
        }
        return null;
    }

}
