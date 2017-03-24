/**
 * 
 */
package org.wikitolearn.wikirating.model;

import com.oracle.webservices.internal.api.EnvelopeStyle;
import org.neo4j.ogm.annotation.*;

/**
 * @author aletundo, valsdav
 *
 */
@RelationshipEntity( type = "VOTE")
public class Vote {
	@GraphId private Long graphId;
	private double value;
	private double reliability;
	@StartNode private User user;
	@EndNode private Revision revision;
	/**
	 * 
	 */
	public Vote() {}
	
	/**
	 * @param value
	 * @param reliability
	 */
	public Vote(double value, double reliability) {
		this.value = value;
		this.reliability = reliability;
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

	/* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
	@Override
	public String toString() {
		return "Vote [value=" + value + ", reliability=" + reliability + "]";
	}
	
}
