/**
 * 
 */
package org.semanticweb.owlapi.lint.configuration;

import java.util.EventObject;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class LintConfigurationChangeEvent extends EventObject {
	public LintConfigurationChangeEvent(Object source) {
		super(source);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4689985193490464516L;

	public abstract void accept(LintConfigurationChangeEventVisitor visitor);

	public abstract <O> O accept(LintConfigurationChangeEventVisitorEx<O> visitor);
}
