/**
 *
 */
package org.semanticweb.owlapi.lint.configuration;

import uk.ac.manchester.cs.owl.lint.commons.NonConfigurableLintConfiguration;

/**
 * @author Luigi Iannone
 */
public abstract class DefaultLintConfigurationVisitorAdapter
        implements LintConfigurationVisitor {

    protected abstract void doDefault(LintConfiguration lintConfiguration);

    @Override
    public void visitNonConfigurableLintConfiguration(
            NonConfigurableLintConfiguration nonConfigurableLintConfiguration) {
        doDefault(nonConfigurableLintConfiguration);
    }

    @Override
    public void visitPropertiesBasedLintConfiguration(
            PropertyBasedLintConfiguration propertiesBasedLintConfiguration) {
        doDefault(propertiesBasedLintConfiguration);
    }

    @Override
    public void
            visitGenericLintConfiguration(LintConfiguration lintConfiguration) {
        doDefault(lintConfiguration);
    }
}
