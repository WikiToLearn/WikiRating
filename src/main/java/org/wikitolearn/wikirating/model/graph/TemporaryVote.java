package org.wikitolearn.wikirating.model.graph;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * This entity represents a Vote that has to be validating,
 * connecting the user and the Revision after a fetch of the mediawiki api.
 * @author valsdav
 * @author aletundo
 */
@NodeEntity
public class TemporaryVote {
    @GraphId
    @JsonIgnore
    private Long graphId;
    private double value;
    private double reliability;
    @JsonProperty("userid")
    private int userId;
    @JsonProperty("revid")
    private int revId;
    private String langRevId;
    @DateLong
    private Date timestamp;

    public TemporaryVote(double value, double reliability, int userId, int revId, String langRevId, Date timestamp) {
        this.value = value;
        this.reliability = reliability;
        this.userId = userId;
        this.revId = revId;
        this.langRevId = langRevId;
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserid(int userId) {
        this.userId = userId;
    }

    public int getRevId() {
        return revId;
    }

    public void setRevId(int revId) {
        this.revId = revId;
    }

    public String getLangRevId() {
        return langRevId;
    }

    public void setLangRevId(String langRevId) {
        this.langRevId = langRevId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
