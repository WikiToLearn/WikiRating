package org.wikitolearn.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class handles the data of the Revision of a page.
 * Created by valsdav on 14/03/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Revision {
    private int revid;
    private int userid;
    private int parentid;
    private long length;
    private double changeCoefficient;
    private double currentMeanVote;
    private double currentVotesReliability;
    private double currentNormalisesVotesReliability;
    private double totalMeanVote;
    private double totalVotesReliability;
    private double totalNormalisesVotesReliability;

    public Revision() {   }

    public Revision(int revid, int userid, int parentid, long length, double changeCoefficient,
                    double currentMeanVote, double currentVotesReliability,
                    double currentNormalisesVotesReliability, double totalMeanVote,
                    double totalVotesReliability, double totalNormalisesVotesReliability) {
        this.revid = revid;
        this.userid = userid;
        this.parentid = parentid;
        this.length = length;
        this.changeCoefficient = changeCoefficient;
        this.currentMeanVote = currentMeanVote;
        this.currentVotesReliability = currentVotesReliability;
        this.currentNormalisesVotesReliability = currentNormalisesVotesReliability;
        this.totalMeanVote = totalMeanVote;
        this.totalVotesReliability = totalVotesReliability;
        this.totalNormalisesVotesReliability = totalNormalisesVotesReliability;
    }

    public int getRevid() {
        return revid;
    }

    public void setRevid(int revid) {
        this.revid = revid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public double getChangeCoefficient() {
        return changeCoefficient;
    }

    public void setChangeCoefficient(double changeCoefficient) {
        this.changeCoefficient = changeCoefficient;
    }

    public double getCurrentMeanVote() {
        return currentMeanVote;
    }

    public void setCurrentMeanVote(double currentMeanVote) {
        this.currentMeanVote = currentMeanVote;
    }

    public double getCurrentVotesReliability() {
        return currentVotesReliability;
    }

    public void setCurrentVotesReliability(double currentVotesReliability) {
        this.currentVotesReliability = currentVotesReliability;
    }

    public double getCurrentNormalisesVotesReliability() {
        return currentNormalisesVotesReliability;
    }

    public void setCurrentNormalisesVotesReliability(double currentNormalisesVotesReliability) {
        this.currentNormalisesVotesReliability = currentNormalisesVotesReliability;
    }

    public double getTotalMeanVote() {
        return totalMeanVote;
    }

    public void setTotalMeanVote(double totalMeanVote) {
        this.totalMeanVote = totalMeanVote;
    }

    public double getTotalVotesReliability() {
        return totalVotesReliability;
    }

    public void setTotalVotesReliability(double totalVotesReliability) {
        this.totalVotesReliability = totalVotesReliability;
    }

    public double getTotalNormalisesVotesReliability() {
        return totalNormalisesVotesReliability;
    }

    public void setTotalNormalisesVotesReliability(double totalNormalisesVotesReliability) {
        this.totalNormalisesVotesReliability = totalNormalisesVotesReliability;
    }
}
