/**
 *
 */
package org.semanticweb.owlapi.lint.configuration;

import uk.ac.manchester.cs.owl.lint.commons.NonConfigurableLintConfiguration;

/**
 * @author Luigi Iannone
 */
public abstract class LintConfigurationVisitorAdapter
        implements LintConfigurationVisitor {

    @Override
    public void visitNonConfigurableLintConfiguration(
            NonConfigurableLintConfiguration t) {}

    @Override
    public void visitPropertiesBasedLintConfiguration(
            PropertyBasedLintConfiguration propertiesBasedLintConfiguration) {}

    @Override
    public void visitGenericLintConfiguration(
            LintConfiguration lintConfiguration) {}
}
