/**
 * 
 */
package org.wikitolearn.models;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author aletundo, valsdav
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity( label = "Page")
public class Page {
	@GraphId private long graphId;
	private int pageid;
	private String title;
	private String lang;
	@Index(unique = true)
	private String langPageId;
	private double pageRank;
	
	/**
	 * 
	 */
	public Page() { }
	
	/**
	 * @param title
	 * @param pageid
	 * @param pageRank
	 */
	public Page(String title, int pageid, double pageRank) {
		this.title = title;
		this.pageid = pageid;
		this.pageRank = pageRank;
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
	 * @return the pageid
	 */
	public int getPageid() {
		return pageid;
	}
	/**
	 * @param pageid the pageid to set
	 */
	public void setPageid(int pageid) {
		this.pageid = pageid;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Page [title=" + title + ", pageid=" + pageid + ", pageRank=" + pageRank + "]";
	}

}
