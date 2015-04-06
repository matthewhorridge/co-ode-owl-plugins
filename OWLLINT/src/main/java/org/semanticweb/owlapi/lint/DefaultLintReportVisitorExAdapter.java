/**
 *
 */
package org.semanticweb.owlapi.lint;

/**
 * Default adapter for LintReportVisitorEx. Implementors should override only
 * the desired methods and the doDefault method which provides the default
 * behaviour.
 *
 * @author Luigi Iannone
 */
public abstract class DefaultLintReportVisitorExAdapter<P>
        implements LintReportVisitorEx<P> {

    protected abstract P doDefault(LintReport<?> lintReport);

    @Override
    public P visitErrorLintReport(ErrorLintReport<?> errorLintReport) {
        return this.doDefault(errorLintReport);
    }

    @Override
    public P visitGenericLintReport(LintReport<?> genericLintReport) {
        return this.doDefault(genericLintReport);
    }

    @Override
    public P visitWarningLintReport(WarningLintReport<?> warningLintReport) {
        return this.doDefault(warningLintReport);
    }
}
