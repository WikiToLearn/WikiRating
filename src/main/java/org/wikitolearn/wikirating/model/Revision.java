package org.wikitolearn.wikirating.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * This class handles the data of the Revision of a page.
 * Created by valsdav on 14/03/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity( label = "Revision" )
public class Revision {
	@GraphId private Long graphId;
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
    private boolean validated;
    private String lang;
    @Index(unique = true, primary=true)
    private String langRevId;
    @Relationship(type="PREVIOUS_REVISION", direction = Relationship.OUTGOING)
	private Revision previousRevision;
    @Relationship(type="VOTE", direction = Relationship.INCOMING)
	private Set<Vote> votes;
    
	/**
	 * 
	 */
	public Revision() {}

	/**
	 * @param revid
	 * @param userid
	 * @param parentid
	 * @param length
	 * @param changeCoefficient
	 * @param currentMeanVote
	 * @param currentVotesReliability
	 * @param currentNormalisesVotesReliability
	 * @param totalMeanVote
	 * @param totalVotesReliability
	 * @param totalNormalisesVotesReliability
	 * @param validated
	 * @param lang
	 */
	public Revision(int revid, int userid, int parentid, long length, double changeCoefficient, double currentMeanVote,
			double currentVotesReliability, double currentNormalisesVotesReliability, double totalMeanVote,
			double totalVotesReliability, double totalNormalisesVotesReliability, boolean validated, String lang) {
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
		this.validated = validated;
		this.lang = lang;
	}

	/**
	 * @return the revid
	 */
	public int getRevid() {
		return revid;
	}

	/**
	 * @param revid the revid to set
	 */
	public void setRevid(int revid) {
		this.revid = revid;
	}

	/**
	 * @return the userid
	 */
	public int getUserid() {
		return userid;
	}

	/**
	 * @param userid the userid to set
	 */
	public void setUserid(int userid) {
		this.userid = userid;
	}

	/**
	 * @return the parentid
	 */
	public int getParentid() {
		return parentid;
	}

	/**
	 * @param parentid the parentid to set
	 */
	public void setParentid(int parentid) {
		this.parentid = parentid;
	}

	/**
	 * @return the length
	 */
	public long getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * @return the changeCoefficient
	 */
	public double getChangeCoefficient() {
		return changeCoefficient;
	}

	/**
	 * @param changeCoefficient the changeCoefficient to set
	 */
	public void setChangeCoefficient(double changeCoefficient) {
		this.changeCoefficient = changeCoefficient;
	}

	/**
	 * @return the currentMeanVote
	 */
	public double getCurrentMeanVote() {
		return currentMeanVote;
	}

	/**
	 * @param currentMeanVote the currentMeanVote to set
	 */
	public void setCurrentMeanVote(double currentMeanVote) {
		this.currentMeanVote = currentMeanVote;
	}

	/**
	 * @return the currentVotesReliability
	 */
	public double getCurrentVotesReliability() {
		return currentVotesReliability;
	}

	/**
	 * @param currentVotesReliability the currentVotesReliability to set
	 */
	public void setCurrentVotesReliability(double currentVotesReliability) {
		this.currentVotesReliability = currentVotesReliability;
	}

	/**
	 * @return the currentNormalisesVotesReliability
	 */
	public double getCurrentNormalisesVotesReliability() {
		return currentNormalisesVotesReliability;
	}

	/**
	 * @param currentNormalisesVotesReliability the currentNormalisesVotesReliability to set
	 */
	public void setCurrentNormalisesVotesReliability(double currentNormalisesVotesReliability) {
		this.currentNormalisesVotesReliability = currentNormalisesVotesReliability;
	}

	/**
	 * @return the totalMeanVote
	 */
	public double getTotalMeanVote() {
		return totalMeanVote;
	}

	/**
	 * @param totalMeanVote the totalMeanVote to set
	 */
	public void setTotalMeanVote(double totalMeanVote) {
		this.totalMeanVote = totalMeanVote;
	}

	/**
	 * @return the totalVotesReliability
	 */
	public double getTotalVotesReliability() {
		return totalVotesReliability;
	}

	/**
	 * @param totalVotesReliability the totalVotesReliability to set
	 */
	public void setTotalVotesReliability(double totalVotesReliability) {
		this.totalVotesReliability = totalVotesReliability;
	}

	/**
	 * @return the totalNormalisesVotesReliability
	 */
	public double getTotalNormalisesVotesReliability() {
		return totalNormalisesVotesReliability;
	}

	/**
	 * @param totalNormalisesVotesReliability the totalNormalisesVotesReliability to set
	 */
	public void setTotalNormalisesVotesReliability(double totalNormalisesVotesReliability) {
		this.totalNormalisesVotesReliability = totalNormalisesVotesReliability;
	}

	/**
	 * @return the validated
	 */
	public boolean isValidated() {
		return validated;
	}

	/**
	 * @param validated the validated to set
	 */
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * @return the langRevId
	 */
	public String getLangRevId() {
		return langRevId;
	}

	/**
	 * @param langRevId the langRevId to set
	 */
	public void setLangRevId(String langRevId) {
		this.langRevId = langRevId;
	}

	/**
	 *
	 * @return
	 */
	public Revision getPreviousRevision() {
		return previousRevision;
	}

	/**
	 *
	 * @param previousRevision
	 */
	public void setPreviousRevision(Revision previousRevision) {
		this.previousRevision = previousRevision;
	}

	public Set<Vote> getVotes() {
		return votes;
	}

	public void setVotes(Set<Vote> votes) {
		this.votes = votes;
	}

	/* (non-Javadoc)
             * @see java.lang.Object#toString()
             */
	@Override
	public String toString() {
		return "Revision [graphId=" + graphId + ", revid=" + revid + ", userid=" + userid + ", parentid=" + parentid
				+ ", length=" + length + ", changeCoefficient=" + changeCoefficient + ", currentMeanVote="
				+ currentMeanVote + ", currentVotesReliability=" + currentVotesReliability
				+ ", currentNormalisesVotesReliability=" + currentNormalisesVotesReliability + ", totalMeanVote="
				+ totalMeanVote + ", totalVotesReliability=" + totalVotesReliability
				+ ", totalNormalisesVotesReliability=" + totalNormalisesVotesReliability + ", validated=" + validated
				+ ", lang=" + lang + ", langRevId=" + langRevId + "]";
	}
}