/**
 * 
 */
package org.coode.lint.protege.configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owlapi.lint.configuration.LintConfiguration;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationChangeListener;

import uk.ac.manchester.cs.owl.lint.commons.AbstractPropertiesBasedLintConfiguration;

/**
 * @author Luigi Iannone
 * 
 */
public class ProtegePreferenceLintConfiguration extends
		AbstractPropertiesBasedLintConfiguration {
	private final String id;
	private final LintConfiguration initialConfiguration;

	/**
	 * @param id
	 */
	public ProtegePreferenceLintConfiguration(String id,
			LintConfiguration initialConfiguration) {
		if (id == null) {
			throw new NullPointerException("The ID cannot be null");
		}
		if (initialConfiguration == null) {
			throw new NullPointerException(
					"The initial configuration cannot be null");
		}
		this.id = id;
		this.initialConfiguration = initialConfiguration;
		this.initProperties();
	}

	@Override
	protected Properties initProperties() {
		Properties toReturn = new Properties();
		Set<String> propertyKeys = this.initialConfiguration.getPropertyKeys();
		for (String string : propertyKeys) {
			toReturn.put(string, this.initialConfiguration
					.getPropertyValue(string));
		}
		Preferences preferences = PreferencesManager.getInstance()
				.getApplicationPreferences(this.getId());
		if (preferences != null) {
			byte[] byteArray = preferences.getByteArray("configuration", null);
			if (byteArray != null) {
				try {
					toReturn.load(new ByteArrayInputStream(byteArray));
				} catch (IOException e) {
					Formatter formatter = new Formatter();
					formatter.format("Error: %s in loading properties for ", e
							.getMessage(), this.getId());
					Logger.getLogger(this.getClass().getName()).log(
							Level.WARNING, formatter.out().toString());
				}
			}
			Enumeration<?> propertyNames = toReturn.propertyNames();
			while (propertyNames.hasMoreElements()) {
				String name = propertyNames.nextElement().toString();
				this.initialConfiguration.setProperty(name, toReturn
						.getProperty(name));
			}
		}
		return toReturn;
	}

	@Override
	public void setProperty(String key, String value) {
		super.setProperty(key, value);
		this.initialConfiguration.setProperty(key, value);
	}

	public void store() throws IOException {
		Preferences preferences = PreferencesManager.getInstance()
				.getApplicationPreferences(this.getId());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.getProperties().store(out, "");
		preferences.putByteArray("configuration", out.toByteArray());
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	@Override
	public void addLintConfigurationChangeListener(
			LintConfigurationChangeListener l) {
		this.initialConfiguration.addLintConfigurationChangeListener(l);
	}

	@Override
	public void removeAllListeners() {
		this.initialConfiguration.removeAllListeners();
	}

	@Override
	public void removeLintConfigurationChangeListener(
			LintConfigurationChangeListener l) {
		this.initialConfiguration.removeLintConfigurationChangeListener(l);
	}
}
