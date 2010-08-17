/**
 * 
 */
package org.coode.lint.protege.configuration.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.semanticweb.owl.lint.configuration.LintConfiguration;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class AddPropertyNameAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3308215633163094123L;
	private LintConfiguration lintConfiguration = null;

	public AddPropertyNameAction() {
		super("Add");
		this.setEnabled(this.getLintConfiguration() != null);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String name = JOptionPane
				.showInputDialog("Please input a property name");
		if (name != null) {
			this.add(name);
		}
	}

	/**
	 * @return the lintConfiguration
	 */
	public LintConfiguration getLintConfiguration() {
		return this.lintConfiguration;
	}

	/**
	 * @param lintConfiguration
	 *            the lintConfiguration to set
	 */
	public void setLintConfiguration(LintConfiguration lintConfiguration) {
		this.lintConfiguration = lintConfiguration;
		this.setEnabled(this.getLintConfiguration() != null);
	}

	/**
	 * Performs the addition
	 * 
	 * @param propertyName
	 *            The property name to add. Cannot be {@code null}.
	 */
	protected abstract void add(String propertyName);
}
