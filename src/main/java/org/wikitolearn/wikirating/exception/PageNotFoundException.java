/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class PageNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -7569942011934387156L;

	public PageNotFoundException(){
		super("Page not found");
	}
	
	public PageNotFoundException(String message){
		super(message);
	}
}
