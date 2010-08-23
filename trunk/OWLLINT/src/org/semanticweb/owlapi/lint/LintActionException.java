package org.semanticweb.owlapi.lint;

public class LintActionException extends LintException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -178868283887347796L;

	/**
	 * @param message
	 * @param cause
	 */
	public LintActionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public LintActionException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public LintActionException(Throwable cause) {
		super(cause);
	}
}
