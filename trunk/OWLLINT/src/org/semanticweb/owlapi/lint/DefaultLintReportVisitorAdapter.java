/**
 * 
 */
package org.semanticweb.owlapi.lint;

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
	 * @see org.semanticweb.owlapi.lint.LintReportVisitor#visitGenericLintReport(org.semanticweb.owlapi.lint.LintReport)
	 */
	public void visitGenericLintReport(LintReport<?> genericLintReport) {
		this.doDefault(genericLintReport);
	}

	/**
	 * @see org.semanticweb.owlapi.lint.LintReportVisitor#visitErrorLintReport(org.semanticweb.owlapi.lint.ErrorLintReport)
	 */
	public void visitErrorLintReport(ErrorLintReport<?> errorLintReport) {
		this.doDefault(errorLintReport);
	}

	public void visitWarningLintReport(WarningLintReport<?> warningLintReport) {
		this.doDefault(warningLintReport);
	}
}
