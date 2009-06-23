/**
 * 
 */
package org.semanticweb.owl.lint;

/**
 * Lint that can be detected and can be acted upon
 * 
 * @author Luigi Iannone
 * 
 */
public interface ActingLint extends Lint {
	/**
	 * Execute the actions for this ActingLint on the input LintReport
	 * 
	 * @param report
	 * @throws LintException
	 */
	void executeActions(LintReport report) throws LintException;
}
