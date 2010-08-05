/**
 * 
 */
package org.semanticweb.owl.lint.configuration;

/**
 * @author Luigi Iannone
 * 
 */
public interface LintConfigurationChangeEventVisitorEx<O> {
	O visitPropertyValueChanged(PropertyValueChanged propertyValueChanged);
}
