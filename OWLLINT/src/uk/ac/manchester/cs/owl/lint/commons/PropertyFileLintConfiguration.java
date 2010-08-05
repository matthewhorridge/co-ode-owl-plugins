/**
 * 
 */
package uk.ac.manchester.cs.owl.lint.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owl.lint.configuration.LintConfigurationChangeEvent;
import org.semanticweb.owl.lint.configuration.LintConfigurationChangeListener;
import org.semanticweb.owl.lint.configuration.LintConfigurationVisitor;
import org.semanticweb.owl.lint.configuration.LintConfigurationVisitorEx;
import org.semanticweb.owl.lint.configuration.PropertyBasedLintConfiguration;
import org.semanticweb.owl.lint.configuration.PropertyValueChanged;

/**
 * @author Luigi Iannone
 * 
 */
public class PropertyFileLintConfiguration implements PropertyBasedLintConfiguration {
	private final File file;
	private final Properties properties = new Properties();
	private final Set<LintConfigurationChangeListener> lintConfigurationChangeListeners = new HashSet<LintConfigurationChangeListener>();

	/**
	 * @param file
	 */
	public PropertyFileLintConfiguration(File file) {
		if (file == null) {
			throw new NullPointerException("The file cannot be null");
		}
		this.file = file;
		this.initProperties();
	}

	private void initProperties() {
		try {
			InputStream in = new FileInputStream(this.getFile());
			this.properties.load(in);
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(
					Level.WARNING,
					"The properties could not be loaded from the file " + this.file);
		}
	}

	public void accept(LintConfigurationVisitor visitor) {
		visitor.visitPropertiesBasedLintConfiguration(this);
	}

	public <P> P accept(LintConfigurationVisitorEx<P> visitor) {
		return visitor.visitPropertiesBasedLintConfiguration(this);
	}

	public Properties getProperties() {
		return this.properties;
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

	public void store() throws IOException {
		FileOutputStream out = new FileOutputStream(this.getFile());
		this.getProperties().store(out, "");
		out.close();
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return this.file;
	}

	public void addLintConfigurationChangeListener(LintConfigurationChangeListener l) {
		if (l == null) {
			throw new NullPointerException("The listener cannot be null");
		}
		this.lintConfigurationChangeListeners.add(l);
	}

	public void removeAllListeners() {
		this.lintConfigurationChangeListeners.clear();
	}

	public void removeLintConfigurationChangeListener(LintConfigurationChangeListener l) {
		this.lintConfigurationChangeListeners.remove(l);
	}

	private void notifyListeners(LintConfigurationChangeEvent event) {
		for (LintConfigurationChangeListener l : this.lintConfigurationChangeListeners) {
			l.configurationChanged(event);
		}
	}

	public void setProperty(String key, String value) {
		if (key == null) {
			throw new NullPointerException("The key cannot be null");
		}
		if (this.getPropertyKeys().contains(key)) {
			String oldValue = this.getProperties().getProperty(key);
			this.getProperties().setProperty(key, value);
			this.notifyListeners(new PropertyValueChanged(this, key, oldValue, value));
		}
	}
}
