/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class AddProcessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 704867635816110034L;
	
	public AddProcessException(){
		super("Add a new process to the processes chain fails");
	}
	
	public AddProcessException(String message){
		super(message);
	}
}
