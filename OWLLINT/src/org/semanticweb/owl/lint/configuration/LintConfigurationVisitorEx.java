/**
 * 
 */
package org.semanticweb.owl.lint.configuration;

import uk.ac.manchester.cs.owl.lint.commons.AbstractPropertiesBasedLintConfiguration;
import uk.ac.manchester.cs.owl.lint.commons.NonConfigurableLintConfiguration;

/**
 * Visitor that walks the hierarchy of configuration aspects of a Lint
 * 
 * @author Luigi Iannone
 * 
 */
public interface LintConfigurationVisitorEx<O> {
	O visitNonConfigurableLintConfiguration(
			NonConfigurableLintConfiguration nonConfigurableLintConfiguration);

	O visitPropertiesBasedLintConfiguration(
			AbstractPropertiesBasedLintConfiguration abstractPropertiesBasedLintConfiguration);
}
