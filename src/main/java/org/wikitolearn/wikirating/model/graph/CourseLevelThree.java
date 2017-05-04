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

	public CourseLevelThree(){}

    public CourseLevelThree(int pageId, String title, String lang, String langPageId){
        super(pageId, title, lang, langPageId);
    }

	public CourseLevelThree(int pageId, String title, String lang, String langPageId, Revision firstRevision){
		super(pageId, title, lang, langPageId);
		setFirstRevision(firstRevision);
		setLastRevision(firstRevision);
		setLastValidatedRevision(firstRevision);
	}

    /**
     * Add the first revision the the Page setting the right links.
     * @param firstRevision
     */
	public void initFirstRevision(Revision firstRevision){
        setFirstRevision(firstRevision);
        setLastRevision(firstRevision);
        setLastValidatedRevision(firstRevision);
    }

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
