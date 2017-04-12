/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class RevisionNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -5894333501939857908L;
	
	public RevisionNotFoundException(){
		super("Revision not found");
	}
	
	public RevisionNotFoundException(String message){
		super(message);
	}
}
