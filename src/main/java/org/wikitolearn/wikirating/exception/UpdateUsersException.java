/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class UpdateUsersException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8799743663655251377L;

	public UpdateUsersException(){
        super("Update users procedure fails");
	}
	
	public UpdateUsersException(String message){
        super(message);
	}
}
