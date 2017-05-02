/**
 * 
 */
package org.wikitolearn.wikirating.exception.handler;

import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.wikitolearn.wikirating.exception.GenericException;
import org.wikitolearn.wikirating.exception.PageNotFoundException;
import org.wikitolearn.wikirating.exception.RevisionNotFoundException;
import org.wikitolearn.wikirating.model.api.ApiResponseError;

/**
 * @author aletundo
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler{
	
	@ExceptionHandler(value = { GenericException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponseError handleGenericException(GenericException exception) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ApiResponseError body = buildResponseBody(exception);
		return body;
	}
	
	@ExceptionHandler(value = { PageNotFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponseError handlePageNotFoundException(PageNotFoundException exception) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ApiResponseError body = buildResponseBody(exception);
		return body;
	}
	
	@ExceptionHandler(value = { RevisionNotFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponseError handleRevisionNotFoundException(RevisionNotFoundException exception) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ApiResponseError body = buildResponseBody(exception);
		return body;
	}
	
	/**
	 * 
	 * @param exception
	 * @return
	 */
	private ApiResponseError buildResponseBody(Exception exception){
		Map<String, Object> data = new HashMap<>();
		data.put("error", HttpStatus.NOT_FOUND.name());
		data.put("exception", exception.getClass().getCanonicalName());
		data.put("stacktrace", exception.getStackTrace());
		
		return new ApiResponseError(data, exception.getMessage(), HttpStatus.NOT_FOUND.value(), Instant.now().toEpochMilli());
	}
}
