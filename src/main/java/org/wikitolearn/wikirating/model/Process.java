package org.wikitolearn.wikirating.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.wikitolearn.wikirating.util.enums.ProcessStatus;
import org.wikitolearn.wikirating.util.enums.ProcessType;

import java.util.Date;
import java.util.UUID;

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
	@Index(unique = true, primary = true)
	private String processId;
    private ProcessType processType;
    private ProcessStatus processStatus;
    @Relationship(type="PREVIOUS_PROCESS", direction = Relationship.OUTGOING)
    private Process previousProcess;
    @DateLong
    private Date beginOfProcess;
    @DateLong
    private Date endOfProcess;

    public Process() {}

    public Process(String processId, ProcessType processType,
                   ProcessStatus processStatus, Date beginOfProcess, Date endOfProcess) {
        this.processId = processId;
        this.processType = processType;
        this.processStatus = processStatus;
        this.beginOfProcess = beginOfProcess;
        this.endOfProcess = endOfProcess;
    }

    public Process(ProcessType processType){
        this.processId = UUID.randomUUID().toString();
        this.processType = processType;
        this.processStatus = ProcessStatus.ONGOING;
        this.beginOfProcess = new Date();
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
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

    @Override
    public String toString() {
        return "Process[" +
                "processId='" + processId + '\'' +
                ", processType=" + processType +
                ", processStatus=" + processStatus +
                ", beginOfProcess=" + beginOfProcess +
                ", endOfProcess=" + endOfProcess +
                ']';
    }
}
