/**
 * 
 */
package org.coode.lint.protege.configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import org.coode.lint.protege.LintProtegePluginInstance;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owl.lint.configuration.LintConfiguration;
import org.semanticweb.owl.lint.configuration.LintConfigurationVisitorEx;

import uk.ac.manchester.cs.owl.lint.commons.AbstractPropertiesBasedLintConfiguration;

/**
 * This implementation tries to read the properties from the Protege Preference
 * first and then resorts to the default file {@literal <classname>.pr}
 * 
 * @author Luigi Iannone
 * 
 */
public class ProtegeAbstractPropertiesBasedLintConfiguration extends
		AbstractPropertiesBasedLintConfiguration implements LintConfiguration {
	public ProtegeAbstractPropertiesBasedLintConfiguration(LintProtegePluginInstance<?> l) {
		super(l);
	}

	@Override
	public Properties getProperties() {
		Properties toReturn = super.getProperties();
		Preferences lintPreferences = PreferencesManager.getInstance().getApplicationPreferences(
				this.getLint().getClass());
		byte[] byteArray = lintPreferences.getByteArray("configuration", null);
		if (byteArray != null) {
			Properties properties = new Properties();
			try {
				properties.load(new ByteArrayInputStream(byteArray));
				toReturn = properties;
			} catch (IOException e) {
				ProtegeApplication.getErrorLog().logError(e);
			}
		}
		return toReturn;
	}

	/**
	 * @see org.semanticweb.owl.lint.configuration.LintConfiguration#accept(org.semanticweb.owl.lint.configuration.LintConfigurationVisitorEx)
	 */
	public <P> P accept(LintConfigurationVisitorEx<P> visitor) {
		// TODO Auto-generated method stub
		return null;
	}
}
