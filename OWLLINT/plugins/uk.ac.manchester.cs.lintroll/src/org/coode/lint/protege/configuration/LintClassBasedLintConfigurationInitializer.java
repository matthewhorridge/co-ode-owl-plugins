package org.coode.lint.protege.configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.configuration.LintConfiguration;

public final class LintClassBasedLintConfigurationInitializer implements
		LintConfigurationInitializer {
	private final Lint<?> lint;

	/**
	 * @param lint
	 */
	public LintClassBasedLintConfigurationInitializer(Lint<?> lint) {
		assert lint != null;
		this.lint = lint;
	}

	public void initialise(LintConfiguration lintConfiguration) {
		Preferences lintPreferences = PreferencesManager.getInstance().getApplicationPreferences(
				this.getLint().getClass());
		byte[] byteArray = lintPreferences.getByteArray("configuration", null);
		if (byteArray != null) {
			try {
				Properties properties = new Properties();
				properties.load(new ByteArrayInputStream(byteArray));
				Enumeration<?> keys = properties.propertyNames();
				while (keys.hasMoreElements()) {
					Object propertyName = keys.nextElement();
					String value = properties.getProperty(propertyName.toString());
					lintConfiguration.setProperty(propertyName.toString(), value);
				}
			} catch (IOException e) {
				ProtegeApplication.getErrorLog().logError(e);
			}
		}
	}

	/**
	 * @return the lint
	 */
	public Lint<?> getLint() {
		return this.lint;
	}
}