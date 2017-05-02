/**
 * 
 */
package org.wikitolearn.wikirating.model.api;

/**
 * @author aletundo
 *
 */
public class ApiResponse {
	private String status;
	private Object data;
	private long timestamp;
	
	/**
	 * @param status
	 * @param data
	 */
	public ApiResponse(String status, Object data, long timestamp) {
		this.status = status;
		this.data = data;
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
