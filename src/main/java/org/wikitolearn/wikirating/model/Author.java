package org.wikitolearn.wikirating.model;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.Date;

/**
 * This entity represent the edit of a user on a Page.
 * It stores the the reliability of the user at the moment
 * of the creation.
 */
@RelationshipEntity(type="Author")
public class Author {
    @GraphId
    private Long graphId;
    private double reliability;
    @StartNode
    private User user;
    @EndNode
    private Revision revision;

    public Author() {
    }

    public Author(double reliability){
        this.reliability = reliability;
    }

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Revision getRevision() {
        return revision;
    }

    public void setRevision(Revision revision) {
        this.revision = revision;
    }

    @Override
    public String toString() {
        return "Author{" +
                "graphId=" + graphId +
                ", reliability=" + reliability +
                ", user=" + user +
                ", revision=" + revision +
                '}';
    }
}
