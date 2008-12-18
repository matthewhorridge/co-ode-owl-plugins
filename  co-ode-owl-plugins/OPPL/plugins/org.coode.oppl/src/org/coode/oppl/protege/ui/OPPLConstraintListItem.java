package org.coode.oppl.protege.ui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.coode.oppl.AbstractConstraint;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLConstraintListItem implements MListItem {
	private AbstractConstraint constraint;
	private final OWLEditorKit owlEditorKit;
	private ConstraintSystem constraintSystem;

	/**
	 * @param owlEditorKit
	 * @param constraint
	 * @param constraintSystem
	 */
	public OPPLConstraintListItem(OWLEditorKit owlEditorKit,
			AbstractConstraint constraint, ConstraintSystem constraintSystem) {
		this.owlEditorKit = owlEditorKit;
		this.constraint = constraint;
		this.constraintSystem = constraintSystem;
	}

	public String getTooltip() {
		return "";
	}

	public boolean handleDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	public void handleEdit() {
		final OPPLConstraintEditor editor = new OPPLConstraintEditor(
				this.owlEditorKit, this.constraintSystem);
		editor.setConstraint(this.getConstraint());
		final VerifyingOptionPane optionPane = new VerifyingOptionPane(editor) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7816306100172449202L;

			/**
			 * 
			 */
			@Override
			public void selectInitialValue() {
				// This is overridden so that the option pane dialog default
				// button
				// doesn't get the focus.
			}
		};
		final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
			public void verifiedStatusChanged(boolean verified) {
				optionPane.setOKEnabled(verified);
			}
		};
		editor.addStatusChangedListener(verificationListener);
		final JDialog dlg = optionPane.createDialog(this.owlEditorKit
				.getWorkspace(), null);
		// The editor shouldn't be modal (or should it?)
		dlg.setModal(false);
		dlg.setTitle("Constraint editor");
		dlg.setResizable(true);
		dlg.pack();
		dlg.setLocationRelativeTo(this.owlEditorKit.getWorkspace());
		dlg.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				Object retVal = optionPane.getValue();
				if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
					OPPLConstraintListItem.this.constraint = editor
							.getConstraint();
				}
				editor.removeStatusChangedListener(verificationListener);
				editor.dispose();
			}
		});
		dlg.setVisible(true);
	}

	public boolean isDeleteable() {
		return true;
	}

	public boolean isEditable() {
		return true;
	}

	/**
	 * @return the constraint
	 */
	public AbstractConstraint getConstraint() {
		return this.constraint;
	}

	@Override
	public String toString() {
		return this.constraint.toString();
	}
}