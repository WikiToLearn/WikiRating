/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 5153683580236807114L;
	
	public UserNotFoundException(){
        super("User not found.");
	}
	
	public UserNotFoundException(String message){
        super(message);
	}
}
