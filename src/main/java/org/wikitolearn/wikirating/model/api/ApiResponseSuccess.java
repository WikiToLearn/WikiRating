/**
 * 
 */
package org.wikitolearn.wikirating.model.api;

/**
 * @author aletundo
 *
 */
public class ApiResponseSuccess extends ApiResponse {
	/**
	 * 
	 */
	public ApiResponseSuccess(Object data, long timestamp) {
		super("success", data, timestamp);
	}
}
