/**
 * 
 */
package org.wikitolearn.wikirating.model.graph;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * @author aletundo
 *
 */
@NodeEntity( label = "CourseLevelTwo")
public class CourseLevelTwo extends Page {
	
	@Relationship(type = "LEVEL_THREE", direction = Relationship.OUTGOING)
	private Set<CourseLevelThree> levelsThree;
	@Relationship(type = "FIRST_CALCULATION", direction = Relationship.OUTGOING)
	private History firstCalculation;
	@Relationship(type = "LAST_CALCULATION", direction = Relationship.OUTGOING)
	private History lastCalculation;

	public CourseLevelTwo() {}

	public CourseLevelTwo(int pageId, String title, String lang, String langPageId){
		super(pageId, title, lang, langPageId);
	}


	/**
	 * @return the levelsThree
	 */
	public Set<CourseLevelThree> getLevelsThree() {
		return levelsThree;
	}

	/**
	 * @param levelsThree the levelsThree to set
	 */
	public void setLevelsThree(Set<CourseLevelThree> levelsThree) {
		this.levelsThree = levelsThree;
	}
	
	/** 
	 * @param levelThree the levelThree to add
	 */
	public void addLevelThree(CourseLevelThree levelThree){
		this.levelsThree.add(levelThree);
	}

}
