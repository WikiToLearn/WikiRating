/**
 * 
 */
package org.wikitolearn.wikirating.model.api;

/**
 * @author aletundo
 *
 */
public class ApiResponseFail extends ApiResponse{

	/**
	 * @param status
	 * @param data
	 * @param timestamp
	 */
	public ApiResponseFail(Object data, long timestamp) {
		super("fail", data, timestamp);
	}

}
