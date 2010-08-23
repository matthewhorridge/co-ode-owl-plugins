package uk.ac.manchester.cs.owl.lint.commons;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.semanticweb.owlapi.lint.configuration.LintConfiguration;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationChangeEvent;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationChangeListener;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationVisitor;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationVisitorEx;
import org.semanticweb.owlapi.lint.configuration.PropertyBasedLintConfiguration;
import org.semanticweb.owlapi.lint.configuration.PropertyValueChanged;

/**
 * Abstract implementation of a PropertiesBasedLint that provides a default
 * location for properties. i.e.: the file called {@literal
 * <classname>.properties}
 * 
 * @author Luigi Iannone
 * 
 * 
 */
public abstract class AbstractPropertiesBasedLintConfiguration implements
		LintConfiguration, PropertyBasedLintConfiguration {
	private Properties properties = null;
	private final Set<LintConfigurationChangeListener> lintConfigurationChangeListeners = new HashSet<LintConfigurationChangeListener>();

	/**
	 * 
	 */
	protected abstract Properties initProperties();

	public Properties getProperties() {
		if (this.properties == null) {
			this.properties = this.initProperties();
		}
		return this.properties;
	}

	public void accept(LintConfigurationVisitor visitor) {
		visitor.visitPropertiesBasedLintConfiguration(this);
	}

	public <P> P accept(LintConfigurationVisitorEx<P> visitor) {
		return visitor.visitPropertiesBasedLintConfiguration(this);
	}

	public Set<String> getPropertyKeys() {
		Set<String> toReturn = new HashSet<String>();
		Properties properties = this.getProperties();
		Set<Object> keySet = properties.keySet();
		for (Object object : keySet) {
			// You never know the object could technically be null
			if (object != null) {
				toReturn.add(object.toString());
			}
		}
		return toReturn;
	}

	public String getPropertyValue(String key) {
		if (key == null) {
			throw new NullPointerException("The key cannot be null");
		}
		String property = this.getProperties().getProperty(key);
		return property;
	}

	public void setProperty(String key, String value) {
		if (key == null) {
			throw new NullPointerException("The key cannot be null");
		}
		if (this.getPropertyKeys().contains(key)) {
			String oldValue = this.getProperties().getProperty(key);
			this.getProperties().setProperty(key, value);
			this.notifyListeners(new PropertyValueChanged(this, key, oldValue,
					value));
		}
	}

	private void notifyListeners(LintConfigurationChangeEvent event) {
		for (LintConfigurationChangeListener l : this.lintConfigurationChangeListeners) {
			l.configurationChanged(event);
		}
	}

	public void addLintConfigurationChangeListener(
			LintConfigurationChangeListener l) {
		if (l == null) {
			throw new NullPointerException("The listener cannot be null");
		}
		this.lintConfigurationChangeListeners.add(l);
	}

	public void removeLintConfigurationChangeListener(
			LintConfigurationChangeListener l) {
		this.lintConfigurationChangeListeners.remove(l);
	}

	public void removeAllListeners() {
		this.lintConfigurationChangeListeners.clear();
	}
}
