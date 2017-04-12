/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class GetPagesUpdateInfoException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2967374484216582166L;

	public GetPagesUpdateInfoException(){
		super("Get pages update information fails");
	}
	
	public GetPagesUpdateInfoException(String message){
		super(message);
	}
}
