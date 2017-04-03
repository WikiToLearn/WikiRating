/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class TemporaryVoteValidationException extends RuntimeException {

	private static final long serialVersionUID = 3942868822128902263L;
	
	public TemporaryVoteValidationException(){
		super();
	}
	
	public TemporaryVoteValidationException(String message){
		super("An error occurs during temporary votes validation. " +  message);
	}

}
