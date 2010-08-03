/**
 * 
 */
package org.semanticweb.owl.lint.configuration;

/**
 * @author Luigi Iannone
 * 
 */
public interface LintConfiguration {
	public void accept(LintConfigurationVisitor visitor);

	public <P> P accept(LintConfigurationVisitorEx<P> visitor);
}
