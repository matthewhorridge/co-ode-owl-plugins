/**
 * 
 */
package org.semanticweb.owlapi.lint.configuration;

import uk.ac.manchester.cs.owl.lint.commons.NonConfigurableLintConfiguration;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class LintConfigurationVisitorAdapter implements LintConfigurationVisitor {
	public void visitNonConfigurableLintConfiguration(NonConfigurableLintConfiguration t) {
	}

	public void visitPropertiesBasedLintConfiguration(
			PropertyBasedLintConfiguration propertiesBasedLintConfiguration) {
	}

	public void visitGenericLintConfiguration(LintConfiguration lintConfiguration) {
	}
}
