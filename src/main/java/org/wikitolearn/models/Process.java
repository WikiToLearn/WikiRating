package org.wikitolearn.models;

import java.util.Date;

/**
 * This class represents a Process happened in the engine.
 * It has a definite type and can have different results. It is saved in the DB
 * as a Process vertex.
 * Created by valsdav on 21/03/17.
 */
public class Process {

    private Date timestamp;
    private ProcessType processType;
    private ProcessResult processResult;

    public Process() {}

    public Process(Date timestamp, ProcessType processType, ProcessResult processResult) {
        this.timestamp = timestamp;
        this.processType = processType;
        this.processResult = processResult;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }

    public ProcessResult getProcessResult() {
        return processResult;
    }

    public void setProcessResult(ProcessResult processResult) {
        this.processResult = processResult;
    }
}

enum ProcessType{
    INIT, FETCH, UPDATE_RANKING_PAGES, UPDATE_RANKING_USERS
}

enum ProcessResult{
    DONE, ERROR, EXCEPTION
}