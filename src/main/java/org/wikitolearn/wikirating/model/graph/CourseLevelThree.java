/**
 * 
 */
package org.wikitolearn.wikirating.model.graph;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * @author aletundo
 *
 */
@NodeEntity( label = "CourseLevelThree")
public class CourseLevelThree extends Page {
	
	@Relationship(type = "LAST_REVISION", direction = Relationship.OUTGOING)
	private Revision lastRevision;
	@Relationship(type = "FIRST_REVISION", direction = Relationship.OUTGOING)
	private Revision firstRevision;
	@Relationship(type = "LAST_VALIDATED_REVISION", direction = Relationship.OUTGOING)
	private Revision lastValidatedRevision;
	
	/**
	 * @return the lastRevision
	 */
	public Revision getLastRevision() {
		return lastRevision;
	}

	/**
	 * @param lastRevision the lastRevision to set
	 */
	public void setLastRevision(Revision lastRevision) {
		this.lastRevision = lastRevision;
	}

	/**
	 * @return the firstRevision
	 */
	public Revision getFirstRevision() {
		return firstRevision;
	}

	/**
	 * @param firstRevision the firstRevision to set
	 */
	public void setFirstRevision(Revision firstRevision) {
		this.firstRevision = firstRevision;
	}

	/**
	 * @return the lastValidatedRevision
	 */
	public Revision getLastValidatedRevision() {
		return lastValidatedRevision;
	}

	/**
	 * @param lastValidatedRevision the lastValidatedRevision to set
	 */
	public void setLastValidatedRevision(Revision lastValidatedRevision) {
		this.lastValidatedRevision = lastValidatedRevision;
	}
}
