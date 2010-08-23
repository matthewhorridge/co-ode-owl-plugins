/**
 * 
 */
package uk.ac.manchester.cs.owl.lint.commons;

import org.semanticweb.owlapi.lint.ActingLint;
import org.semanticweb.owlapi.lint.Lint;
import org.semanticweb.owlapi.lint.LintVisitorEx;
import org.semanticweb.owlapi.lint.PatternBasedLint;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class DefaultLintVisitorEx<O> implements LintVisitorEx<O> {
	protected abstract O doDefault(Lint<?> lint);

	/**
	 * @see org.semanticweb.owlapi.lint.LintVisitorEx#visitPatternPasedLint(org.semanticweb.owlapi.lint.PatternBasedLint)
	 */
	public O visitPatternPasedLint(PatternBasedLint<?> lint) {
		return this.doDefault(lint);
	}

	/**
	 * @see org.semanticweb.owlapi.lint.LintVisitorEx#visitActingLint(org.semanticweb.owlapi.lint.ActingLint)
	 */
	public O visitActingLint(ActingLint<?> actingLint) {
		return this.doDefault(actingLint);
	}

	/**
	 * @see org.semanticweb.owlapi.lint.LintVisitorEx#visitGenericLint(org.semanticweb.owlapi.lint.Lint)
	 */
	public O visitGenericLint(Lint<?> lint) {
		return this.doDefault(lint);
	}
}
