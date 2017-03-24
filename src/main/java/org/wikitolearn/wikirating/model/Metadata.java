package org.wikitolearn.wikirating.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * This entity represents the root of the chain of processes.
 * It is used also to store some global stats.
 * Created by valsdav on 24/03/17.
 */
@NodeEntity(label="Metadata")
public class Metadata {
	@GraphId private Long id;
    @Relationship(type="LAST_PROCESS", direction = Relationship.OUTGOING)
    private Process lastProcess;
    private int currentnumberOfPages;
    private int numberOfRevisions;
    private int numberOfUsers;

    public Metadata() {}

    public Metadata(int currentnumberOfPages, int numberOfRevisions, int numberOfUsers) {
        this.currentnumberOfPages = currentnumberOfPages;
        this.numberOfRevisions = numberOfRevisions;
        this.numberOfUsers = numberOfUsers;
    }

    public Process getLastProcess() {
        return lastProcess;
    }

    public void setLastProcess(Process lastProcess) {
        this.lastProcess = lastProcess;
    }

    public int getCurrentnumberOfPages() {
        return currentnumberOfPages;
    }

    public void setCurrentnumberOfPages(int currentnumberOfPages) {
        this.currentnumberOfPages = currentnumberOfPages;
    }

    public int getNumberOfRevisions() {
        return numberOfRevisions;
    }

    public void setNumberOfRevisions(int numberOfRevisions) {
        this.numberOfRevisions = numberOfRevisions;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }
}
