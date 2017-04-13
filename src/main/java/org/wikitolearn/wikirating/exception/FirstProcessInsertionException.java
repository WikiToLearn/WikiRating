/**
 * 
 */
package org.wikitolearn.wikirating.exception;

/**
 * @author aletundo
 *
 */
public class FirstProcessInsertionException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 4583926366923483951L;

	public FirstProcessInsertionException(){
		super("Insertion of the first Process failed");
	}

	public FirstProcessInsertionException(String message){
		super(message);
	}
}
