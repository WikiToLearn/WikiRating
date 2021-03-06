/**
 * 
 */
package org.wikitolearn.wikirating.model.graph;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.Labels;
import org.neo4j.ogm.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * @author aletundo
 *
 */
@NodeEntity( label = "Page")
public class Page {

	@GraphId
	@JsonIgnore
	private Long graphId;
	@JsonProperty("pageId")
	private int pageId;
	@Index
	private String title;
	@Index
	private String lang;
	@Index(unique = true, primary = true)
	@JsonProperty(access = Access.WRITE_ONLY)
	private String langPageId;
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

	public boolean hasLabel(String label){
		return labels.contains(label);
	}
	
	/**
	 * @param label the label to set
	 */
	public void addLabel(String label) {
		this.labels.add(label);
	}

	public void removeLabel(String label){
		this.labels.remove(label);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Page [graphId=" + graphId + ", pageId=" + pageId + ", title=" + title + ", lang=" + lang
				+ ", langPageId=" + langPageId + ", labels=" + labels + "]";
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