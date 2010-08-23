/**
 * 
 */
package org.semanticweb.owlapi.lint.configuration;

/**
 * @author Luigi Iannone
 * 
 */
public interface LintConfigurationChangeEventVisitorEx<O> {
	O visitPropertyValueChanged(PropertyValueChanged propertyValueChanged);
}
