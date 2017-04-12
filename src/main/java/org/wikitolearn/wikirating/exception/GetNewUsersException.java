/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class GetNewUsersException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8332907384782111321L;

	public GetNewUsersException(){
		super("Get new users fails");
	}
	
	public GetNewUsersException(String message){
		super(message);
	}
}
