package org.wikitolearn.wikirating.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.Date;

/**
 * This entity represents a Vote that has to be validating,
 * connecting the user and the Revision after a fetch of the mediawiki api.
 * Created by valsdav on 24/03/17.
 */
@NodeEntity (label="TempVote")
public class TemporaryVote {
    @GraphId private Long graphId;
    private double value;
    private double reliability;
    private int userid;
    private int revid;
    private String langRevId;
    @DateLong
    private Date timestamp;

    public TemporaryVote(double value, double reliability, int userid, int revid, String langRevId, Date timestamp) {
        this.value = value;
        this.reliability = reliability;
        this.userid = userid;
        this.revid = revid;
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

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getRevid() {
        return revid;
    }

    public void setRevid(int revid) {
        this.revid = revid;
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
