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
 */
public abstract class AbstractPropertiesBasedLintConfiguration
        implements LintConfiguration, PropertyBasedLintConfiguration {

    private Properties properties = null;
    private final Set<LintConfigurationChangeListener> lintConfigurationChangeListeners = new HashSet<>();

    /**
     * 
     */
    protected abstract Properties initProperties();

    @Override
    public Properties getProperties() {
        if (properties == null) {
            properties = initProperties();
        }
        return properties;
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

    private void notifyListeners(LintConfigurationChangeEvent event) {
        for (LintConfigurationChangeListener l : lintConfigurationChangeListeners) {
            l.configurationChanged(event);
        }
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
    public void removeLintConfigurationChangeListener(
            LintConfigurationChangeListener l) {
        lintConfigurationChangeListeners.remove(l);
    }

    @Override
    public void removeAllListeners() {
        lintConfigurationChangeListeners.clear();
    }
}
