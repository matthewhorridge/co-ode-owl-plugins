/**
 * 
 */
package org.semanticweb.owlapi.lint.configuration;

import java.util.Set;

/**
 * @author Luigi Iannone
 * 
 */
public interface LintConfiguration {
	public void accept(LintConfigurationVisitor visitor);

	public <P> P accept(LintConfigurationVisitorEx<P> visitor);

	/**
	 * Retrieves the set of valid property keys for this configuration.
	 * 
	 * @return a Set of String
	 */
	public Set<String> getPropertyKeys();

	/**
	 * Sets the input value for the input key (if valid - see
	 * {@link LintConfiguration#getPropertyKeys()}). If not a valid value this
	 * configuration will be unaffected.
	 * 
	 * @param key
	 *            The input key. Cannot be {@code null}. If it does not belong
	 *            to the Set of valid keys (see
	 *            {@link LintConfiguration#getPropertyKeys()}), this
	 *            configuration will be unaffected.
	 * @param value
	 *            The value to assign.
	 * 
	 * @throws NullPointerException
	 *             if the input key is {@code null}.
	 */
	public void setProperty(String key, String value);

	/**
	 * Retrieves the current configuration value for the input key. If the input
	 * key is not valid (see {@link LintConfiguration#getPropertyKeys()}),
	 * {@code null} will be returned.
	 * 
	 * @param key
	 *            The input key. Cannot be @code{null}. If it does not belong to
	 *            the Set of valid keys (see
	 *            {@link LintConfiguration#getPropertyKeys()}) {@code null} will
	 *            be returned.
	 * @return A String or {@code null} if the input key does not belong to the
	 *         Set of valid keys (see
	 *         {@link LintConfiguration#getPropertyKeys()})
	 * @throws NullPointerException
	 *             if the input key is {@code null}.
	 */
	public String getPropertyValue(String key);

	/**
	 * Adds a listener to those that should be notified when this
	 * LintConfiguration changes
	 * 
	 * @param l
	 *            The LintConfigurationChangeListener to add. Cannot be
	 *            {@code null}.
	 * @throws NullPointerException
	 *             if the input is {@code null}.
	 */
	public void addLintConfigurationChangeListener(LintConfigurationChangeListener l);

	/**
	 * Adds a listener from those that should be notified when this
	 * LintConfiguration changes
	 * 
	 * @param l
	 *            The LintConfigurationChangeListener to remove.
	 */
	public void removeLintConfigurationChangeListener(LintConfigurationChangeListener l);

	/**
	 * Removes all the instances listening to change to this LintConfiguration.
	 */
	public void removeAllListeners();
}
