/**
 * 
 */
package org.coode.lint.protege.configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owl.lint.configuration.LintConfiguration;
import org.semanticweb.owl.lint.configuration.LintConfigurationChangeListener;
import org.semanticweb.owl.lint.configuration.LintConfigurationVisitor;
import org.semanticweb.owl.lint.configuration.LintConfigurationVisitorEx;
import org.semanticweb.owl.lint.configuration.PropertyBasedLintConfiguration;

/**
 * @author Luigi Iannone
 * 
 */
public class ProtegeStoredLintConfiguration implements PropertyBasedLintConfiguration {
	private final Class<?> application;
	private final LintConfiguration delegate;

	/**
	 * @param application
	 */
	public ProtegeStoredLintConfiguration(Class<?> application, LintConfiguration delegate) {
		if (application == null) {
			throw new NullPointerException("The application cannot be null");
		}
		if (delegate == null) {
			throw new NullPointerException("The delegate cannot be null");
		}
		this.application = application;
		this.delegate = delegate;
	}

	/**
	 * @param visitor
	 * @see org.semanticweb.owl.lint.configuration.LintConfiguration#accept(org.semanticweb.owl.lint.configuration.LintConfigurationVisitor)
	 */
	public void accept(LintConfigurationVisitor visitor) {
		this.delegate.accept(visitor);
	}

	/**
	 * @param <P>
	 * @param visitor
	 * @return
	 * @see org.semanticweb.owl.lint.configuration.LintConfiguration#accept(org.semanticweb.owl.lint.configuration.LintConfigurationVisitorEx)
	 */
	public <P> P accept(LintConfigurationVisitorEx<P> visitor) {
		return this.delegate.accept(visitor);
	}

	/**
	 * @return
	 * @see org.semanticweb.owl.lint.configuration.LintConfiguration#getPropertyKeys()
	 */
	public Set<String> getPropertyKeys() {
		return this.delegate.getPropertyKeys();
	}

	/**
	 * @param key
	 * @param value
	 * @see org.semanticweb.owl.lint.configuration.LintConfiguration#setProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public void setProperty(String key, String value) {
		this.delegate.setProperty(key, value);
	}

	/**
	 * @param key
	 * @return
	 * @see org.semanticweb.owl.lint.configuration.LintConfiguration#getPropertyValue(java.lang.String)
	 */
	public String getPropertyValue(String key) {
		return this.delegate.getPropertyValue(key);
	}

	/**
	 * @param l
	 * @see org.semanticweb.owl.lint.configuration.LintConfiguration#addLintConfigurationChangeListener(org.semanticweb.owl.lint.configuration.LintConfigurationChangeListener)
	 */
	public void addLintConfigurationChangeListener(LintConfigurationChangeListener l) {
		this.delegate.addLintConfigurationChangeListener(l);
	}

	/**
	 * @param l
	 * @see org.semanticweb.owl.lint.configuration.LintConfiguration#removeLintConfigurationChangeListener(org.semanticweb.owl.lint.configuration.LintConfigurationChangeListener)
	 */
	public void removeLintConfigurationChangeListener(LintConfigurationChangeListener l) {
		this.delegate.removeLintConfigurationChangeListener(l);
	}

	/**
	 * 
	 * @see org.semanticweb.owl.lint.configuration.LintConfiguration#removeAllListeners()
	 */
	public void removeAllListeners() {
		this.delegate.removeAllListeners();
	}

	public void store() throws IOException {
		Properties properties = new Properties();
		Set<String> propertyKeys = this.getPropertyKeys();
		for (String string : propertyKeys) {
			properties.put(string, this.getPropertyValue(string));
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		properties.store(out, "");
		Preferences applicationPreferences = PreferencesManager.getInstance().getApplicationPreferences(
				this.application);
		applicationPreferences.putByteArray("configuration", out.toByteArray());
	}

	public Properties getProperties() {
		Properties properties = new Properties();
		Set<String> propertyKeys = this.getPropertyKeys();
		for (String string : propertyKeys) {
			properties.put(string, this.getPropertyValue(string));
		}
		return properties;
	}
}
