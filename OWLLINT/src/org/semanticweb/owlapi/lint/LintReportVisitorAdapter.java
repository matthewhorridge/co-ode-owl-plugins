/**
 * 
 */
package org.semanticweb.owlapi.lint;

/**
 * Default adapter for LintReportVisitor. Implementors should override only the
 * desired methods. The default behaviour is do nothing.
 * 
 * @author Luigi Iannone
 * 
 */
public abstract class LintReportVisitorAdapter implements LintReportVisitor {
	/**
	 * @see org.semanticweb.owlapi.lint.LintReportVisitor#visitGenericLintReport(org.semanticweb.owlapi.lint.LintReport)
	 */
	public void visitGenericLintReport(LintReport<?> genericLintReport) {
	}

	/**
	 * @see org.semanticweb.owlapi.lint.LintReportVisitor#visitErrorLintReport(org.semanticweb.owlapi.lint.ErrorLintReport)
	 */
	public void visitErrorLintReport(ErrorLintReport<?> errorLintReport) {
	}

	public void visitWarningLintReport(WarningLintReport<?> warningLintReport) {
	}
}
