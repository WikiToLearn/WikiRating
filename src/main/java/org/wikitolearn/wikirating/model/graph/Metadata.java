package org.wikitolearn.wikirating.model.graph;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.wikitolearn.wikirating.util.enums.MetadataType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This entity represents the root of the chain of processes.
 * It is used also to store some global stats.
 * Created by valsdav on 24/03/17.
 */
@NodeEntity(label="Metadata")
public class Metadata {
    @GraphId
    @JsonIgnore
    private Long id;
    @Relationship(type = "LATEST_PROCESS", direction = Relationship.OUTGOING)
    private Process latestProcess;
    private MetadataType type;

    public Metadata() {
    }

    public Metadata(MetadataType type){
        this.type = type;
    }

    public Process getLatestProcess() {
        return latestProcess;
    }

    public void setLatestProcess(Process latestProcess) {
        this.latestProcess = latestProcess;
    }

    public MetadataType getType() {
        return type;
    }

    public void setType(MetadataType type) {
        this.type = type;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Metadata [id=" + id + ", latestProcess=" + latestProcess + ", type=" + type + "]";
	}
}

