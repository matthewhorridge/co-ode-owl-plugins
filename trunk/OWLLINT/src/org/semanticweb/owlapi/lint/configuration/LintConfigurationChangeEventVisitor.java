/**
 * 
 */
package org.semanticweb.owlapi.lint.configuration;

/**
 * @author Luigi Iannone
 * 
 */
public interface LintConfigurationChangeEventVisitor {
	void visitPropertyValueChanged(PropertyValueChanged propertyValueChanged);
}
