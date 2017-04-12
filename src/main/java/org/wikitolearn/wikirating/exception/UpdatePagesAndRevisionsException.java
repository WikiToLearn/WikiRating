/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class UpdatePagesAndRevisionsException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3288589123744620999L;

	public UpdatePagesAndRevisionsException(){
        super("Update pages and revisions procedure fails");
	}
	
	public UpdatePagesAndRevisionsException(String message){
        super(message);
	}
}
