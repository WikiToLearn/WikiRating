/**
 * 
 */
package org.wikitolearn.wikirating.exception.error;

import java.util.List;

import org.springframework.http.HttpStatus;

/**
 * @author aletundo
 *
 */
public class ApiError {
	private long timestamp;
    private HttpStatus status;
    private String message;
    private List<String> errors;
    private String exception;
    
	/**
	 * 
	 */
	public ApiError() {}

	/**
	 * @param timestamp
	 * @param status
	 * @param message
	 * @param errors
	 * @param exception
	 */
	public ApiError(long timestamp, HttpStatus status, String message, List<String> errors, String exception) {
		this.timestamp = timestamp;
		this.status = status;
		this.message = message;
		this.errors = errors;
		this.exception = exception;
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

	/**
	 * @return the status
	 */
	public HttpStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(HttpStatus status) {
		this.status = status;
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
	 * @return the errors
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	/**
	 * @return the exception
	 */
	public String getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(String exception) {
		this.exception = exception;
	}
}
