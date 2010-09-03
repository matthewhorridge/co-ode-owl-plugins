/**
 * 
 */
package org.semanticweb.owlapi.lint.configuration;

import java.net.URL;

import uk.ac.manchester.cs.owl.lint.commons.NonConfigurableLintConfiguration;
import uk.ac.manchester.cs.owl.lint.commons.SimplePropertyBasedLintConfiguration;

/**
 * @author Luigi Iannone
 * 
 */
public class ConfigurationStrategy {
	public static LintConfiguration getLintConfiguration(Class<?> aClass) {
		LintConfiguration toReturn = NonConfigurableLintConfiguration
				.getInstance();
		URL resource = aClass.getResource(aClass.getName() + ".properties");
		// uk.ac.manchester.cs.bhig.siemens.lintchecks.MissingDisplayNameLintCheckFilter.properties
		// uk.ac.manchester.cs.bhig.siemens.lintchecks.NavigationalHierarchyLintCheckFilter.properties
		if (resource != null) {
			toReturn = new SimplePropertyBasedLintConfiguration(aClass);
		}
		return toReturn;
	}
}
