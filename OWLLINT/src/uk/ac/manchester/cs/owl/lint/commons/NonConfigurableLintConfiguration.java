/**
 * 
 */
package uk.ac.manchester.cs.owl.lint.commons;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.lint.configuration.LintConfiguration;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationChangeListener;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationVisitor;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationVisitorEx;

/**
 * Simple Configuration for those Lint that will not be configurable from
 * outside.
 * 
 * @author Luigi Iannone
 * 
 */
public class NonConfigurableLintConfiguration implements LintConfiguration {
	private final static NonConfigurableLintConfiguration instance = new NonConfigurableLintConfiguration();

	private NonConfigurableLintConfiguration() {
	}

	/**
	 * @see org.semanticweb.owlapi.lint.configuration.LintConfiguration#accept(org.semanticweb.owlapi.lint.configuration.LintConfigurationVisitor)
	 */
	public void accept(LintConfigurationVisitor visitor) {
		visitor.visitNonConfigurableLintConfiguration(this);
	}

	/**
	 * @see org.semanticweb.owlapi.lint.configuration.LintConfiguration#accept(org.semanticweb.owlapi.lint.configuration.LintConfigurationVisitorEx)
	 */
	public <P> P accept(LintConfigurationVisitorEx<P> visitor) {
		return visitor.visitNonConfigurableLintConfiguration(this);
	}

	/**
	 * @return the instance
	 */
	public static NonConfigurableLintConfiguration getInstance() {
		return instance;
	}

	public Set<String> getPropertyKeys() {
		return Collections.emptySet();
	}

	public String getPropertyValue(String key) {
		// Just to obey to the interface specification.
		if (key == null) {
			throw new NullPointerException("The key cannot be null");
		}
		return null;
	}

	public void setProperty(String key, String value) {
		if (key == null) {
			throw new NullPointerException("The key cannot be null");
		}
	}

	public void addLintConfigurationChangeListener(LintConfigurationChangeListener l) {
		// Do Nothing as it cannot change hence no need to keep track of
		// listeners.
	}

	public void removeLintConfigurationChangeListener(LintConfigurationChangeListener l) {
		// Do Nothing as it cannot change hence no need to keep track of
		// listeners.
	}

	public void removeAllListeners() {
		// Do Nothing as it cannot change hence no need to keep track of
		// listeners.
	}
}
