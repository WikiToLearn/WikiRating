/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class LatestProcessUpdateException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -470495634179091203L;

	public LatestProcessUpdateException(){
		super("Update LATEST_PROCESS relationship fails");
	}
	
	public LatestProcessUpdateException(String message){
		super(message);
	}
}
