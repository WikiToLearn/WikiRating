package org.wikitolearn.wikirating.model.graph;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This entity represent the edit of a user on a Page.
 * It stores the the reliability of the user at the moment
 * of the creation.
 */
@RelationshipEntity(type="AUTHOR")
public class Author {
    @GraphId
    @JsonIgnore
    private Long graphId;
    private double reliability;
    @StartNode
    private User user;
    @EndNode
    private Revision revision;
	/**
	 * 
	 */
	public Author() {}
	
	/**
	 * @param reliability
	 */
	public Author(double reliability) {
		this.reliability = reliability;
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
	 * @return the reliability
	 */
	public double getReliability() {
		return reliability;
	}

	/**
	 * @param reliability the reliability to set
	 */
	public void setReliability(double reliability) {
		this.reliability = reliability;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the revision
	 */
	public Revision getRevision() {
		return revision;
	}

	/**
	 * @param revision the revision to set
	 */
	public void setRevision(Revision revision) {
		this.revision = revision;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Author [graphId=" + graphId + ", reliability=" + reliability + ", user=" + user + ", revision="
				+ revision + "]";
	}
}
