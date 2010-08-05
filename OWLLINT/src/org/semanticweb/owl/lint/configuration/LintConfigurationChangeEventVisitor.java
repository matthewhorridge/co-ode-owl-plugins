/**
 * 
 */
package org.semanticweb.owl.lint.configuration;

/**
 * @author Luigi Iannone
 * 
 */
public interface LintConfigurationChangeEventVisitor {
	void visitPropertyValueChanged(PropertyValueChanged propertyValueChanged);
}
