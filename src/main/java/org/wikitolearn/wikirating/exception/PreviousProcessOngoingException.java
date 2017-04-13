/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class PreviousProcessOngoingException extends RuntimeException {

	private static final long serialVersionUID = -4938271048372946378L;

	public PreviousProcessOngoingException(){
		super("Cannot create new Process: the previous one is still ONGOING");
	}

	public PreviousProcessOngoingException(String message){
		super(message);
	}
}
