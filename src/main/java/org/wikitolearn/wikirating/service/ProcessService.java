package org.wikitolearn.wikirating.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.exception.AddProcessException;
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
     * @throws AddProcessException
     */
    public Process addProcess(ProcessType type) throws AddProcessException{
    	try{
    		Process process = new Process(type);
    		Process previousProcess = processRepository.getLatestProcess();
    		if (previousProcess != null) {
                process.setPreviousProcess(previousProcess);
            }
            processRepository.save(process);
            LOG.info("Created new process: {}", process.toString());
            return process;
    	}catch(Exception e){
    		LOG.error("An error occurred during process creation: {}", e.getMessage());
    		throw new AddProcessException();
    	}
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
        LOG.info("Update the status of the latest process: {}", currentProcess.toString());
        return currentProcess;
    }
    
    /**
     * Get the start date of the latest process
     * @return the start date of the latest process
     */
    public Date getLastProcessStartDate(){
    	Process latestProcess = processRepository.getLatestProcess();
        if(latestProcess != null){
        	return latestProcess.getStartOfProcess();
        }
        return null;
    }
    
    /**
     * Get the start date of the latest process of the specified type
     * @param type the process type requested
     * @return the start date of the latest process of the specified type
     */
    public Date getLastProcessStartDateByType(ProcessType type){
        Process latestProcess = processRepository.getLatestProcessByType(type);
        if(latestProcess != null){
        	return latestProcess.getStartOfProcess();
        }
        return null;
    }

}
