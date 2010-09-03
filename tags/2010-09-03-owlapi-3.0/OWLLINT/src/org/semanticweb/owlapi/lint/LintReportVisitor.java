/**
 * 
 */
package org.semanticweb.owlapi.lint;

/**
 * @author Luigi Iannone
 * 
 */
public interface LintReportVisitor {
	public void visitGenericLintReport(LintReport<?> genericLintReport);

	public void visitErrorLintReport(ErrorLintReport<?> errorLintReport);

	public void visitWarningLintReport(WarningLintReport<?> warningLintReport);
}
