package org.wikitolearn.wikirating.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
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
    private ProcessType processType;
    private ProcessResult processResult;
    @Relationship(type="PREVIOUS_PROCESS", direction = Relationship.OUTGOING)
    private Process previousProcess;
    @DateLong
    private Date beginOfProcess;
    @DateLong
    private Date endOfProcess;

    public Process() {}

    public Process( ProcessType processType, ProcessResult processResult, Date beginOfProcess, Date endOfProcess) {
        this.processType = processType;
        this.processResult = processResult;
        this.beginOfProcess = beginOfProcess;
        this.endOfProcess = endOfProcess;
    }

    public Process(ProcessType processType){
        this.processType = processType;
        this.processResult = ProcessResult.ONGOING;
        this.beginOfProcess = new Date();
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

    public Process getPreviousProcess() {
        return previousProcess;
    }

    public void setPreviousProcess(Process previousProcess) {
        this.previousProcess = previousProcess;
    }

    public Date getBeginOfProcess() {
        return beginOfProcess;
    }

    public void setBeginOfProcess(Date beginOfProcess) {
        this.beginOfProcess = beginOfProcess;
    }

    public Date getEndOfProcess() {
        return endOfProcess;
    }

    public void setEndOfProcess(Date endOfProcess) {
        this.endOfProcess = endOfProcess;
    }
}
