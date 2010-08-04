/**
 * 
 */
package org.coode.lint.protege.configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.configuration.LintConfiguration;
import org.semanticweb.owl.lint.configuration.LintConfigurationVisitor;

import uk.ac.manchester.cs.owl.lint.commons.AbstractPropertiesBasedLintConfiguration;

/**
 * This implementation tries to read the properties from the Protege Preference
 * first and then resorts to the default file.
 * 
 * @author Luigi Iannone
 * 
 */
public class ProtegeAbstractPropertiesBasedLintConfiguration extends
		AbstractPropertiesBasedLintConfiguration implements LintConfiguration {
	public ProtegeAbstractPropertiesBasedLintConfiguration(Lint<?> lint) {
		super(lint);
	}

	@Override
	public Properties getProperties() {
		Properties toReturn = new Properties();
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
		} else {
			Set<String> propertyKeys = this.getLint().getLintConfiguration().getPropertyKeys();
			for (String string : propertyKeys) {
				toReturn.put(string, this.getLint().getLintConfiguration().getPropertyValue(string));
			}
		}
		return toReturn;
	}

	@Override
	public void setProperty(String key, String value) {
		this.getLint().getLintConfiguration().setProperty(key, value);
	}

	@Override
	public String getPropertyValue(String key) {
		return this.getLint().getLintConfiguration().getPropertyValue(key);
	}

	@Override
	public void store() throws IOException {
		Preferences lintPreferences = PreferencesManager.getInstance().getApplicationPreferences(
				this.getLint().getClass());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.getProperties().store(out, "");
		lintPreferences.putByteArray("configuration", out.toByteArray());
	}

	@Override
	public void accept(LintConfigurationVisitor visitor) {
		visitor.visitPropertiesBasedLintConfiguration(this);
	}
}
