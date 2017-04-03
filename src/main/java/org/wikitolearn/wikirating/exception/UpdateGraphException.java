/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class UpdateGraphException extends RuntimeException {

	private static final long serialVersionUID = -6040367633687463024L;
	
	public UpdateGraphException(){
		super("Update graph procedure fails");
	}
	
	public UpdateGraphException(String message){
		super(message);
	}
}
