/**
 * 
 */
package org.semanticweb.owl.lint;

/**
 * Default adapter for LintReportVisitor. Implementors should override only the
 * desired methods. The default behaviour is do nothing.
 * 
 * @author Luigi Iannone
 * 
 */
public abstract class LintReportVisitorAdapter implements LintReportVisitor {
	/**
	 * @see org.semanticweb.owl.lint.LintReportVisitor#visitGenericLintReport(org.semanticweb.owl.lint.LintReport)
	 */
	public void visitGenericLintReport(LintReport<?> genericLintReport) {
	}

	/**
	 * @see org.semanticweb.owl.lint.LintReportVisitor#visitErrorLintReport(org.semanticweb.owl.lint.ErrorLintReport)
	 */
	public void visitErrorLintReport(ErrorLintReport<?> errorLintReport) {
	}

	public void visitWarningLintReport(WarningLintReport<?> warningLintReport) {
	}
}
