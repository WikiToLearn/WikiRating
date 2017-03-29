package org.wikitolearn.wikirating.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.wikitolearn.wikirating.util.enums.MetadataType;

/**
 * This entity represents the root of the chain of processes.
 * It is used also to store some global stats.
 * Created by valsdav on 24/03/17.
 */
@NodeEntity(label="Metadata")
public class Metadata {
    @GraphId
    private Long id;
    @Relationship(type = "LAST", direction = Relationship.OUTGOING)
    private Process lastItem;
    private MetadataType type;

    public Metadata() {
    }

    public Metadata(MetadataType type){
        this.type = type;
    }

    public Process getLastItem() {
        return lastItem;
    }

    public void setLastItem(Process lastItem) {
        this.lastItem = lastItem;
    }

    public MetadataType getType() {
        return type;
    }

    public void setType(MetadataType type) {
        this.type = type;
    }
}

