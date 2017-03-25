/**
 * 
 */
package org.wikitolearn.wikirating.model;

import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.Date;

/**
 * @author aletundo, valsdav
 *
 */
@RelationshipEntity( type = "VOTE")
public class Vote {
	@GraphId private Long graphId;
	private double value;
	private double reliability;
	@DateLong
	private Date timestamp;
	@StartNode private User user;
	@EndNode private Revision revision;

	/**
	 * 
	 */
	public Vote() {}
	
	/**
	 * @param value
	 * @param reliability
	 * @param timestamp
	 */
	public Vote(double value, double reliability, Date timestamp) {
		this.value = value;
		this.reliability = reliability;
		this.timestamp = timestamp;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
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
	 * 
	 * @return
	 */
	public User getUser() {
		return user;
	}
	/**
	 * 
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * 
	 * @return
	 */
	public Revision getRevision() {
		return revision;
	}
	/**
	 * 
	 * @param revision
	 */
	public void setRevision(Revision revision) {
		this.revision = revision;
	}
	/**
	 * 
	 * @return
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	/**
	 * 
	 * @param timestamp
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/* (non-Javadoc)
             * @see java.lang.Object#toString()
             */
	@Override
	public String toString() {
		return "Vote [value=" + value + ", reliability=" + reliability + "]";
	}
	
}
