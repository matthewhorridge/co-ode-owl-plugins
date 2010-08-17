/**
 * 
 */
package uk.ac.manchester.cs.owl.lint.commons;

import org.semanticweb.owl.lint.ActingLint;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintVisitorEx;
import org.semanticweb.owl.lint.PatternBasedLint;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class DefaultLintVisitorEx<O> implements LintVisitorEx<O> {
	protected abstract O doDefault(Lint<?> lint);

	/**
	 * @see org.semanticweb.owl.lint.LintVisitorEx#visitPatternPasedLint(org.semanticweb.owl.lint.PatternBasedLint)
	 */
	public O visitPatternPasedLint(PatternBasedLint<?> lint) {
		return this.doDefault(lint);
	}

	/**
	 * @see org.semanticweb.owl.lint.LintVisitorEx#visitActingLint(org.semanticweb.owl.lint.ActingLint)
	 */
	public O visitActingLint(ActingLint<?> actingLint) {
		return this.doDefault(actingLint);
	}

	/**
	 * @see org.semanticweb.owl.lint.LintVisitorEx#visitGenericLint(org.semanticweb.owl.lint.Lint)
	 */
	public O visitGenericLint(Lint<?> lint) {
		return this.doDefault(lint);
	}
}
