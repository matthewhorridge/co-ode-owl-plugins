/**
 * 
 */
package org.semanticweb.owlapi.lint.configuration;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class DefaultLintConfigurationChangeVisitorAdapter implements
		LintConfigurationChangeEventVisitor {
	protected abstract void doDefault(LintConfigurationChangeEvent e);

	/**
	 * @see org.semanticweb.owlapi.lint.configuration.LintConfigurationChangeEventVisitor#visitPropertyValueChanged(org.semanticweb.owlapi.lint.configuration.PropertyValueChanged)
	 */
	public void visitPropertyValueChanged(PropertyValueChanged propertyValueChanged) {
		this.doDefault(propertyValueChanged);
	}
}
