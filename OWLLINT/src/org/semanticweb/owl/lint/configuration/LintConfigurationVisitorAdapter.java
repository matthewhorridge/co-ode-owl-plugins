/**
 * 
 */
package org.semanticweb.owl.lint.configuration;

import uk.ac.manchester.cs.owl.lint.commons.AbstractPropertiesBasedLintConfiguration;
import uk.ac.manchester.cs.owl.lint.commons.NonConfigurableLintConfiguration;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class LintConfigurationVisitorAdapter implements LintConfigurationVisitor {
	public void visitNonConfigurableLintConfiguration(NonConfigurableLintConfiguration t) {
	}

	public void visitPropertiesBasedLintConfiguration(
			AbstractPropertiesBasedLintConfiguration propertiesBasedLintConfiguration) {
	}
}
