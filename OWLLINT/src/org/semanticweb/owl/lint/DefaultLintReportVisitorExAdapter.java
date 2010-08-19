/**
 * 
 */
package org.semanticweb.owl.lint;

/**
 * Default adapter for LintReportVisitorEx. Implementors should override only
 * the desired methods and the doDefault method which provides the default
 * behaviour.
 * 
 * @author Luigi Iannone
 * 
 */
public abstract class DefaultLintReportVisitorExAdapter<P> implements LintReportVisitorEx<P> {
	protected abstract P doDefault(LintReport<?> lintReport);

	public P visitErrorLintReport(ErrorLintReport<?> errorLintReport) {
		return this.doDefault(errorLintReport);
	}

	public P visitGenericLintReport(LintReport<?> genericLintReport) {
		return this.doDefault(genericLintReport);
	}

	public P visitWarningLintReport(WarningLintReport<?> warningLintReport) {
		return this.doDefault(warningLintReport);
	}
}
