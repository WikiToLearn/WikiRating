/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class GetRecentChangesException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1750185267538745120L;

	public GetRecentChangesException(){
		super("Get recent changes from MediaWiki API fails");
	}
	
	public GetRecentChangesException(String message){
		super(message);
	}
}
