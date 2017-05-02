/**
 * 
 */
package org.wikitolearn.wikirating.model.api;

/**
 * @author aletundo
 *
 */
public class ApiResponseError extends ApiResponse{
	private String message;
	private int code;

	/**
	 * 
	 */
	public ApiResponseError(Object data, String message, int code, long timestamp) {
		super("error", data, timestamp);
		this.code = code;
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

}
