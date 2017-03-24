package org.wikitolearn.wikirating.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.wikitolearn.wikirating.util.enums.ProcessResult;
import org.wikitolearn.wikirating.util.enums.ProcessType;

import java.util.Date;

/**
 * This class represents a Process happened in the engine.
 * It has a definite type and can have different results. It is saved in the DB
 * as a Process vertex.
 * @author aletundo
 * @author valsdav
 */
@NodeEntity( label = "Process")
public class Process {
	@GraphId private Long graphId;
    private Date timestamp;
    private ProcessType processType;
    private ProcessResult processResult;

    public Process() {}

    public Process(Date timestamp, ProcessType processType, ProcessResult processResult) {
        this.timestamp = timestamp;
        this.processType = processType;
        this.processResult = processResult;
    }

    public Process(ProcessType processType){
        this.timestamp = new Date();
        this.processType = processType;
        this.processResult = ProcessResult.ONGOING;
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