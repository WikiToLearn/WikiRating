/**
 * 
 */
package org.wikitolearn.exceptions.handlers;

import java.util.ArrayList;
import java.time.Instant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.wikitolearn.exceptions.GenericException;
import org.wikitolearn.exceptions.errors.ApiError;

/**
 * @author aletundo
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = { GenericException.class })
	public ResponseEntity<Object> handleGenericException(GenericException exception, WebRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		ApiError apiError = new ApiError(Instant.now().toEpochMilli(), HttpStatus.BAD_REQUEST, exception.getMessage(),
				new ArrayList<String>(), exception.getClass().getCanonicalName());
		
		return handleExceptionInternal(exception, apiError, headers, apiError.getStatus(), request);
	}
}
