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
@NodeEntity( label = "CourseRoot")
public class CourseRoot extends Page {
	
	@Relationship(type = "LEVEL_TWO", direction = Relationship.OUTGOING)
	private Set<CourseLevelTwo> levelsTwo;
	@Relationship(type = "FIRST_CALCULATION", direction = Relationship.OUTGOING)
	private History firstCalculation;
	@Relationship(type = "LAST_CALCULATION", direction = Relationship.OUTGOING)
	private History lastCalculation;

	public CourseRoot(){}

	public CourseRoot(int pageId, String title, String lang, String langPageId){
		super(pageId, title, lang, langPageId);
	}
	
	/**
	 * @return the levelsTwo
	 */
	public Set<CourseLevelTwo> getLevelsTwo() {
		return levelsTwo;
	}

	/**
	 * @param levelsTwo the levelsTwo to set
	 */
	public void setLevelsTwo(Set<CourseLevelTwo> levelsTwo) {
		this.levelsTwo = levelsTwo;
	}
	
	/** 
	 * @param levelTwo the levelTwo to add
	 */
	public void addLevelTwo(CourseLevelTwo levelTwo){
		this.levelsTwo.add(levelTwo);
	}

}
