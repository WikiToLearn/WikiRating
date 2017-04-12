/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class GetLogEventsException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5055108174400510799L;

	public GetLogEventsException(){
		super("Get log events from MediaWiki API fails");
	}
	
	public GetLogEventsException(String message){
		super(message);
	}
}
