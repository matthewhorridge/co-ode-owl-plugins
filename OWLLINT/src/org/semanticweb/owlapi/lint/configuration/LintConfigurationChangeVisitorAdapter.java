/**
 * 
 */
package org.semanticweb.owlapi.lint.configuration;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class LintConfigurationChangeVisitorAdapter implements
		LintConfigurationChangeEventVisitor {
	/**
	 * @see org.semanticweb.owlapi.lint.configuration.LintConfigurationChangeEventVisitor#visitPropertyValueChanged(org.semanticweb.owlapi.lint.configuration.PropertyValueChanged)
	 */
	public void visitPropertyValueChanged(PropertyValueChanged propertyValueChanged) {
	}
}
