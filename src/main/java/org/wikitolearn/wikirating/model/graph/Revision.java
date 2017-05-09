package org.wikitolearn.wikirating.model.graph;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
//import org.springframework.data.annotation.Transient;

import java.util.Date;
import java.util.Set;

/**
 * This class handles the data of the Revision of a page.
 * Created by valsdav on 14/03/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity( label = "Revision" )
public class Revision {
	@GraphId 
	@JsonIgnore
	private Long graphId;
	@Index(unique = true, primary=true)
	@JsonProperty(access = Access.WRITE_ONLY)
	private String langRevId;
	@JsonProperty("revid")
    private int revId;
	@JsonProperty(access = Access.WRITE_ONLY)
    private String lang;
    @JsonProperty(value = "userid", access = Access.WRITE_ONLY)
    private int userId;
    @JsonProperty(value = "parentid", access = Access.WRITE_ONLY)
    private int parentId;
    @DateLong
    @JsonProperty(access = Access.WRITE_ONLY)
    private Date timestamp;
    @JsonProperty(value = "size", access = Access.WRITE_ONLY)
    private long length;
    @JsonProperty(access = Access.WRITE_ONLY)
    private double changeCoefficient;
    private double currentMeanVote;
    private double currentVotesReliability;
    private double totalMeanVote;
    private double totalVotesReliability;
    private double normalizedNumberOfVotes;
    private double currentScore;
    private double totalScore;

    @Relationship(type="PREVIOUS_REVISION", direction = Relationship.OUTGOING)
	private Revision previousRevision;
    //@Relationship(type="PREVISOU_REVISION", direction = Relationship.INCOMING)
	//private Revision nextRevision;
    @Relationship(type="VOTE", direction = Relationship.INCOMING)
	private Set<Vote> votes;
    //This relationship represents the connection between the revision
	//and its creator (user). It saves his reliability at the moment of the author.
    @Relationship(type="AUTHOR", direction = Relationship.INCOMING)
	private Author author;
    
	/**
	 * 
	 */
	public Revision() {}

	/**
     * @param revId
     * @param userId
     * @param parentId
     * @param length
     * @param lang
     * @param timestamp
     */
	public Revision(int revId, String lang, int userId, int parentId, long length, Date timestamp) {
		this.revId = revId;
		this.langRevId = lang +"_"+revId;
		this.userId = userId;
		this.parentId = parentId;
		this.length = length;
		this.lang = lang;
        this.timestamp = timestamp;
        this.changeCoefficient = 0.0;
    }

	/**
	 * @return the revId
	 */
	public int getRevId() {
		return revId;
	}

	/**
	 * @param revId the revId to set
	 */
	public void setRevId(int revId) {
		this.revId = revId;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the parentId
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
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

    public double getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(double currentScore) {
        this.currentScore = currentScore;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
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
	 * @return the graphId
	 */
	public Long getGraphId() {
		return graphId;
	}

	/**
	 * @param graphId the graphId to set
	 */
	public void setGraphId(Long graphId) {
		this.graphId = graphId;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the previousRevision
	 */
	public Revision getPreviousRevision() {
		return previousRevision;
	}

	/**
	 * @param previousRevision the previousRevision to set
	 */
	public void setPreviousRevision(Revision previousRevision) {
		this.previousRevision = previousRevision;
	}

	/*
	public Revision getNextRevision() {
		return nextRevision;
	}

	public void setNextRevision(Revision nextRevision) {
		this.nextRevision = nextRevision;
	}
*/

	/**
	 * @return the votes
	 */
	public Set<Vote> getVotes() {
		return votes;
	}

	/**
	 * @param votes the votes to set
	 */
	public void setVotes(Set<Vote> votes) {
		this.votes = votes;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

    public double getNormalizedNumberOfVotes() {
        return normalizedNumberOfVotes;
    }

    public void setNormalizedNumberOfVotes(double normalizedNumberOfVotes) {
        this.normalizedNumberOfVotes = normalizedNumberOfVotes;
    }

	public int getCurrentNumberOfVotes() {
	    if (votes != null) {
            return this.votes.size();
        }else{
	        return 0;
        }
    }

	/**
	 * 
	 * @param vote the vote to set
	 */
	public void addVote(Vote vote){
		this.votes.add(vote);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
        return "Revision{" +
                "langRevId=" + langRevId +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                ", length=" + length +
                ", author=" + author +
                '}';
    }


}