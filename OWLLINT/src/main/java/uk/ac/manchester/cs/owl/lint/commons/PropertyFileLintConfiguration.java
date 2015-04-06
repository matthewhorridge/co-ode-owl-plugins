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

import org.semanticweb.owlapi.lint.configuration.LintConfigurationChangeEvent;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationChangeListener;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationVisitor;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationVisitorEx;
import org.semanticweb.owlapi.lint.configuration.PropertyBasedLintConfiguration;
import org.semanticweb.owlapi.lint.configuration.PropertyValueChanged;

/**
 * @author Luigi Iannone
 */
public class PropertyFileLintConfiguration
        implements PropertyBasedLintConfiguration {

    private final File file;
    private final Properties properties = new Properties();
    private final Set<LintConfigurationChangeListener> lintConfigurationChangeListeners = new HashSet<>();

    /**
     * @param file
     */
    public PropertyFileLintConfiguration(File file) {
        if (file == null) {
            throw new NullPointerException("The file cannot be null");
        }
        this.file = file;
        initProperties();
    }

    private void initProperties() {
        try {
            InputStream in = new FileInputStream(getFile());
            properties.load(in);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(
                    Level.WARNING,
                    "The properties could not be loaded from the file " + file);
        }
    }

    @Override
    public void accept(LintConfigurationVisitor visitor) {
        visitor.visitPropertiesBasedLintConfiguration(this);
    }

    @Override
    public <P> P accept(LintConfigurationVisitorEx<P> visitor) {
        return visitor.visitPropertiesBasedLintConfiguration(this);
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public Set<String> getPropertyKeys() {
        Set<String> toReturn = new HashSet<>();
        Properties properties1 = getProperties();
        Set<Object> keySet = properties1.keySet();
        for (Object object : keySet) {
            // You never know the object could technically be null
            if (object != null) {
                toReturn.add(object.toString());
            }
        }
        return toReturn;
    }

    @Override
    public String getPropertyValue(String key) {
        if (key == null) {
            throw new NullPointerException("The key cannot be null");
        }
        String property = getProperties().getProperty(key);
        return property;
    }

    @Override
    public void store() throws IOException {
        try (FileOutputStream out = new FileOutputStream(getFile())) {
            getProperties().store(out, "");
            out.close();
        }
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    @Override
    public void addLintConfigurationChangeListener(
            LintConfigurationChangeListener l) {
        if (l == null) {
            throw new NullPointerException("The listener cannot be null");
        }
        lintConfigurationChangeListeners.add(l);
    }

    @Override
    public void removeAllListeners() {
        lintConfigurationChangeListeners.clear();
    }

    @Override
    public void removeLintConfigurationChangeListener(
            LintConfigurationChangeListener l) {
        lintConfigurationChangeListeners.remove(l);
    }

    private void notifyListeners(LintConfigurationChangeEvent event) {
        for (LintConfigurationChangeListener l : lintConfigurationChangeListeners) {
            l.configurationChanged(event);
        }
    }

    @Override
    public void setProperty(String key, String value) {
        if (key == null) {
            throw new NullPointerException("The key cannot be null");
        }
        if (getPropertyKeys().contains(key)) {
            String oldValue = getProperties().getProperty(key);
            getProperties().setProperty(key, value);
            notifyListeners(
                    new PropertyValueChanged(this, key, oldValue, value));
        }
    }
}
