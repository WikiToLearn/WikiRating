/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class GetDiffPreviousRevisionExeception extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4210349326134217223L;

	public GetDiffPreviousRevisionExeception(){
		super("Get diff of previous revision failed");
	}

	public GetDiffPreviousRevisionExeception(String message){
		super(message);
	}
}
