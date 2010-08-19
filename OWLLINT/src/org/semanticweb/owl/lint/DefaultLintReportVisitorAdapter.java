/**
 * 
 */
package org.semanticweb.owl.lint;

/**
 * Default adapter for LintReportVisitor. Implementors should override only the
 * desired methods and the doDefault method which provides the default
 * behaviour.
 * 
 * @author Luigi Iannone
 * 
 */
public abstract class DefaultLintReportVisitorAdapter implements LintReportVisitor {
	protected abstract void doDefault(LintReport<?> lintReport);

	/**
	 * @see org.semanticweb.owl.lint.LintReportVisitor#visitGenericLintReport(org
	 *      .semanticweb.owl.lint.LintReport)
	 */
	public void visitGenericLintReport(LintReport<?> genericLintReport) {
		this.doDefault(genericLintReport);
	}

	/**
	 * @see org.semanticweb.owl.lint.LintReportVisitor#visitErrorLintReport(org.semanticweb.owl.lint.ErrorLintReport)
	 */
	public void visitErrorLintReport(ErrorLintReport<?> errorLintReport) {
		this.doDefault(errorLintReport);
	}

	public void visitWarningLintReport(WarningLintReport<?> warningLintReport) {
		this.doDefault(warningLintReport);
	}
}
