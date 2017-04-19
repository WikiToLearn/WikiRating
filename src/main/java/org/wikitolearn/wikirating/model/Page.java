/**
 * 
 */
package org.wikitolearn.wikirating.model;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.Labels;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author aletundo
 *
 */
@NodeEntity( label = "Page")
public class Page {

	@GraphId
	private Long graphId;
	@JsonProperty("pageId")
	private int pageId;
	@Index
	private String title;
	@Index
	private String lang;
	@Index(unique = true, primary = true)
	private String langPageId;
	private double pageRank;
	@Relationship(type = "LAST_REVISION", direction = Relationship.OUTGOING)
	private Revision lastRevision;
	@Relationship(type = "FIRST_REVISION", direction = Relationship.OUTGOING)
	private Revision fistRevision;
	@Relationship(type = "LEVEL_TWO", direction = Relationship.OUTGOING)
	private Set<Page> levelsTwo;
	@Relationship(type = "LEVEL_THREE", direction = Relationship.OUTGOING)
	private Set<Page> levelsThree;
	@Labels
	private Set<String> labels = new HashSet<>();
	
	/**
	 * 
	 */
	public Page() {}

	/**
	 * @param pageId
	 * @param title
	 * @param lang
	 */
	public Page(int pageId, String title, String lang, String langPageId) {
		this.pageId = pageId;
		this.title = title;
		this.lang = lang;	
		this.langPageId = langPageId;
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
	 * @return the pageId
	 */
	public int getPageId() {
		return pageId;
	}

	/**
	 * @param pageId the pageId to set
	 */
	public void setPageid(int pageId) {
		this.pageId = pageId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
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
	 * @return the langPageId
	 */
	public String getLangPageId() {
		return langPageId;
	}

	/**
	 * @param langPageId the langPageId to set
	 */
	public void setLangPageId(String langPageId) {
		this.langPageId = langPageId;
	}

	/**
	 * @return the pageRank
	 */
	public double getPageRank() {
		return pageRank;
	}

	/**
	 * @param pageRank the pageRank to set
	 */
	public void setPageRank(double pageRank) {
		this.pageRank = pageRank;
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
	 * @return the fistRevision
	 */
	public Revision getFistRevision() {
		return fistRevision;
	}

	/**
	 * @param fistRevision the fistRevision to set
	 */
	public void setFistRevision(Revision fistRevision) {
		this.fistRevision = fistRevision;
	}

	/**
	 * @return the levelsTwo
	 */
	public Set<Page> getLevelsTwo() {
		return levelsTwo;
	}

	/**
	 * @param levelsTwo the levelsTwo to set
	 */
	public void setLevelsTwo(Set<Page> levelsTwo) {
		this.levelsTwo = levelsTwo;
	}
	
	/** 
	 * @param levelTwo the levelTwo to add
	 */
	public void addLevelTwo(Page levelTwo){
		this.levelsTwo.add(levelTwo);
	}

	/**
	 * @return the levelsThree
	 */
	public Set<Page> getLevelsThree() {
		return levelsThree;
	}

	/**
	 * @param levelsThree the levelsThree to set
	 */
	public void setLevelsThree(Set<Page> levelsThree) {
		this.levelsThree = levelsThree;
	}
	
	/** 
	 * @param levelThree the levelThree to add
	 */
	public void addLevelThree(Page levelThree){
		this.levelsThree.add(levelThree);
	}

	/**
	 * @return the labels
	 */
	public Set<String> getLabels() {
		return labels;
	}

	/**
	 * @param labels the labels to set
	 */
	public void setLabels(Set<String> labels) {
		this.labels = labels;
	}
	
	/**
	 * @param label the label to set
	 */
	public void addLabel(String label) {
		this.labels.add(label);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Page [graphId=" + graphId + ", pageId=" + pageId + ", title=" + title + ", lang=" + lang
				+ ", langPageId=" + langPageId + ", pageRank=" + pageRank + ", lastRevision=" + lastRevision
				+ ", fistRevision=" + fistRevision + ", levelsTwo=" + levelsTwo + ", levelsThree=" + levelsThree
				+ ", labels=" + labels + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Page)) {
			return false;
		}
		
		Page other = (Page) obj;
		
		if (langPageId == null) {
			if (other.langPageId != null) {
				return false;
			}
		} else if (!langPageId.equals(other.langPageId)) {
			return false;
		}
		return true;
	}
}