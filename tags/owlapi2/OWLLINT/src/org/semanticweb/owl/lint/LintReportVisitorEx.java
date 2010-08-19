package org.semanticweb.owl.lint;

/**
 * Visitor for LintReport returning a value
 * 
 * @author Luigi Iannone
 * 
 * @param <P>
 *            the return type for the method of this visitor
 */
public interface LintReportVisitorEx<P> {
	public P visitGenericLintReport(LintReport<?> genericLintReport);

	public P visitErrorLintReport(ErrorLintReport<?> errorLintReport);

	public P visitWarningLintReport(WarningLintReport<?> warningLintReport);
}
