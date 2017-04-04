/**
 * 
 */
package org.wikitolearn.wikirating.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * @author aletundo, valsdav
 *
 */
@NodeEntity( label = "User")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @GraphId private Long graphId;
    @JsonProperty("name")
	private String username;
    @Index(unique=true,primary = true)
	private int userid;
	private double votesReliability;
	private double contributesReliability;
	private double totalReliability;
	@Relationship( type="AUTHOR", direction = Relationship.OUTGOING)
	private Set<Author> authors;
	@Relationship( type="VOTE", direction = Relationship.OUTGOING)
	private Set<Vote> votes;
	
	/**
	 * 
	 */
	public User() {
	    this.authors = new HashSet<>();
	    this.votes = new HashSet<>();
    }
	
	/**
	 * @param username
	 * @param userid
	 * @param votesReliability
	 * @param contributesReliability
	 * @param totalReliability
	 */
	public User(String username, int userid, double votesReliability, double contributesReliability,
			double totalReliability) {
		this.username = username;
		this.userid = userid;
		this.votesReliability = votesReliability;
		this.contributesReliability = contributesReliability;
		this.totalReliability = totalReliability;
        this.authors = new HashSet<>();
        this.votes = new HashSet<>();
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
	public void setUserId(int userid) {
		this.userid = userid;
	}

	/**
	 * @return the votesReliability
	 */
	public double getVotesReliability() {
		return votesReliability;
	}

	/**
	 * @param votesReliability the votesReliability to set
	 */
	public void setVotesReliability(double votesReliability) {
		this.votesReliability = votesReliability;
	}

	/**
	 * @return the contributesReliability
	 */
	public double getContributesReliability() {
		return contributesReliability;
	}

	/**
	 * @param contributesReliability the contributesReliability to set
	 */
	public void setContributesReliability(double contributesReliability) {
		this.contributesReliability = contributesReliability;
	}

	/**
	 * @return the totalReliability
	 */
	public double getTotalReliability() {
		return totalReliability;
	}

	/**
	 * @param totalReliability the totalReliability to set
	 */
	public void setTotalReliability(double totalReliability) {
		this.totalReliability = totalReliability;
	}

	/**
	 *
	 * @return
	 */
	public Set<Author> getAuthors() {
		return authors;
	}

	/**
	 *
	 * @param authors
	 */
	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}

	public void addAuthor(Author author){
	    this.authors.add(author);
    }

	/**
	 * 
	 * @return the votes of the user
	 */
	public Set<Vote> getVotes() {
		return votes;
	}
	
	/**
	 * 
	 * @param votes the votes to set
	 */
	public void setVotes(Set<Vote> votes) {
		this.votes = votes;
	}

	public void addVote(Vote vote){
	    this.votes.add(vote);
    }

	/* (non-Javadoc)
             * @see java.lang.Object#toString()
             */
	@Override
	public String toString() {
		return "User [username=" + username + ", userid=" + userid + ", votesReliability=" + votesReliability
				+ ", contributesReliability=" + contributesReliability + ", totalReliability=" + totalReliability + "]";
	}
	
}
