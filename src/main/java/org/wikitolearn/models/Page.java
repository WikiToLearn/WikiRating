/**
 * 
 */
package org.wikitolearn.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author alessandro
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Page {
	private String title;
	private int pageid;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Page [title=" + title + ", pageid=" + pageid + ", pageRank=" + pageRank + "]";
	}

}
