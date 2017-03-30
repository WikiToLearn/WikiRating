package org.wikitolearn.wikirating.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.wikitolearn.wikirating.util.enums.UpdateType;

import java.util.Date;
import java.util.Map;

/**
 * This class represents a RecentChange entry during the fetch Process.
 * It stores the information fetched from the API before the update of the DB.
 * Created by valsdav on 30/03/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateEvent {

    private UpdateType type;
    private int pageid;
    private int revid;
    private int old_revid;
    private int userid;
    private int oldlen;
    private int newlen;
    private UpdateType logtype;
    private Map<String, Object> logparams;
    private Date timestamp;

    public UpdateEvent(){}

    public UpdateType getType() {
        if (this.type == UpdateType.LOG){
            return this.logtype;
        }
        return type;
    }

    public void setType(UpdateType type) {
        this.type = type;
    }

    public int getPageid() {
        return pageid;
    }

    public void setPageid(int pageid) {
        this.pageid = pageid;
    }

    public int getRevid() {
        return revid;
    }

    public void setRevid(int revid) {
        this.revid = revid;
    }

    public int getOld_revid() {
        return old_revid;
    }

    public void setOld_revid(int old_revid) {
        this.old_revid = old_revid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getOldlen() {
        return oldlen;
    }

    public void setOldlen(int oldlen) {
        this.oldlen = oldlen;
    }

    public int getNewlen() {
        return newlen;
    }

    public void setNewlen(int newlen) {
        this.newlen = newlen;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public UpdateType getLogtype() {
        return logtype;
    }

    public void setLogtype(UpdateType logtype) {
        this.logtype = logtype;
    }

    public Map<String, Object> getLogparams() {
        return logparams;
    }

    public void setLogparams(Map<String, Object> logparams) {
        this.logparams = logparams;
    }
}
