/**
 * 
 */
package org.semanticweb.owl.lint.configuration;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class DefaultLintConfigurationChangeVisitorAdapter implements
		LintConfigurationChangeEventVisitor {
	protected abstract void doDefault(LintConfigurationChangeEvent e);

	/**
	 * @see org.semanticweb.owl.lint.configuration.LintConfigurationChangeEventVisitor#visitPropertyValueChanged(org.semanticweb.owl.lint.configuration.PropertyValueChanged)
	 */
	public void visitPropertyValueChanged(PropertyValueChanged propertyValueChanged) {
		this.doDefault(propertyValueChanged);
	}
}
