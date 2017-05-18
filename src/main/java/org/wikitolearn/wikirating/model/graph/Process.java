package org.wikitolearn.wikirating.model.graph;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.wikitolearn.wikirating.util.enums.ProcessStatus;
import org.wikitolearn.wikirating.util.enums.ProcessType;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@JsonIgnore
	@GraphId 
	private Long graphId;
	@Index(unique = true, primary = true)
	private String processId;
    private ProcessType processType;
    private ProcessStatus processStatus;
    @Relationship(type="PREVIOUS_PROCESS", direction = Relationship.OUTGOING)
    private Process previousProcess;
    @DateLong
    private Date startOfProcess;
    @DateLong
    private Date endOfProcess;

    public Process() {}

    public Process(String processId, ProcessType processType,
                   ProcessStatus processStatus, Date startOfProcess, Date endOfProcess) {
        this.processId = processId;
        this.processType = processType;
        this.processStatus = processStatus;
        this.startOfProcess = startOfProcess;
        this.endOfProcess = endOfProcess;
    }

    public Process(ProcessType processType){
        this.processId = UUID.randomUUID().toString();
        this.processType = processType;
        this.processStatus = ProcessStatus.ONGOING;
        this.startOfProcess = new Date();
    }
    
	/**
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}

	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}

	/**
	 * @return the processType
	 */
	public ProcessType getProcessType() {
		return processType;
	}

	/**
	 * @param processType the processType to set
	 */
	public void setProcessType(ProcessType processType) {
		this.processType = processType;
	}

	/**
	 * @return the processStatus
	 */
	public ProcessStatus getProcessStatus() {
		return processStatus;
	}

	/**
	 * @param processStatus the processStatus to set
	 */
	public void setProcessStatus(ProcessStatus processStatus) {
		this.processStatus = processStatus;
	}

	/**
	 * @return the previousProcess
	 */
	public Process getPreviousProcess() {
		return previousProcess;
	}

	/**
	 * @param previousProcess the previousProcess to set
	 */
	public void setPreviousProcess(Process previousProcess) {
		this.previousProcess = previousProcess;
	}

	/**
	 * @return the startOfProcess
	 */
	public Date getStartOfProcess() {
		return startOfProcess;
	}

	/**
	 * @param startOfProcess the startOfProcess to set
	 */
	public void setStartOfProcess(Date startOfProcess) {
		this.startOfProcess = startOfProcess;
	}

	/**
	 * @return the endOfProcess
	 */
	public Date getEndOfProcess() {
		return endOfProcess;
	}

	/**
	 * @param endOfProcess the endOfProcess to set
	 */
	public void setEndOfProcess(Date endOfProcess) {
		this.endOfProcess = endOfProcess;
	}

    @Override
    public String toString() {
        return "Process[" +
                "processId='" + processId + '\'' +
                ", processType=" + processType +
                ", processStatus=" + processStatus +
                ", startOfProcess=" + startOfProcess +
                ", endOfProcess=" + endOfProcess +
                ']';
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endOfProcess == null) ? 0 : endOfProcess.hashCode());
		result = prime * result + ((previousProcess == null) ? 0 : previousProcess.hashCode());
		result = prime * result + ((processId == null) ? 0 : processId.hashCode());
		result = prime * result + ((processStatus == null) ? 0 : processStatus.hashCode());
		result = prime * result + ((processType == null) ? 0 : processType.hashCode());
		result = prime * result + ((startOfProcess == null) ? 0 : startOfProcess.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Process)) {
			return false;
		}
		Process other = (Process) obj;
		if (processId == null) {
			if (other.processId != null) {
				return false;
			}
		} else if (!processId.equals(other.processId)) {
			return false;
		}
		return true;
	}
}
