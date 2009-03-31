package org.coode.oppl.protege.ui;

import javax.swing.JPanel;

import org.coode.oppl.variablemansyntax.Variable;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;

public abstract class AbstractVariableEditor extends JPanel implements
		VerifiedInputEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1273971509314422094L;

	public abstract void addStatusChangedListener(
			InputVerificationStatusChangedListener listener);

	public abstract void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener);

	public abstract void setVariable(Variable variable);

	public abstract Variable getVariable();

	public abstract void clear();

	public abstract void dispose();
}