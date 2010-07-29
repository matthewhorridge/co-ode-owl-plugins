/**
 * 
 */
package org.semanticweb.owl.lint;

/**
 * @author Luigi Iannone
 * 
 */
public interface LintVisitorEx<O> {
	public O visitPatternPasedLint(PatternBasedLint<?> lint);

	public O visitActingLint(ActingLint<?> actingLint);

	public O visitGenericLint(Lint<?> lint);
}
