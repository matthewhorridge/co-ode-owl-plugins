/**
 * 
 */
package org.semanticweb.owl.lint.configuration;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class LintConfigurationChangeVisitorAdapter implements
		LintConfigurationChangeEventVisitor {
	/**
	 * @see org.semanticweb.owl.lint.configuration.LintConfigurationChangeEventVisitor#visitPropertyValueChanged(org.semanticweb.owl.lint.configuration.PropertyValueChanged)
	 */
	public void visitPropertyValueChanged(PropertyValueChanged propertyValueChanged) {
	}
}
