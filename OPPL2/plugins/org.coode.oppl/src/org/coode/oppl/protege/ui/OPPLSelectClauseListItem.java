package org.coode.oppl.protege.ui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLAxiom;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLSelectClauseListItem implements MListItem {
	private boolean asserted;
	private final OWLEditorKit owlEditorKit;
	private final ConstraintSystem constraintSystem;
	private OWLAxiom axiom;

	/**
	 * @param asserted
	 * @param axiom
	 */
	public OPPLSelectClauseListItem(OWLEditorKit owlEditorKit,
			boolean asserted, OWLAxiom axiom, ConstraintSystem constraintSystem) {
		this.owlEditorKit = owlEditorKit;
		this.asserted = asserted;
		this.axiom = axiom;
		this.constraintSystem = constraintSystem;
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#getTooltip()
	 */
	public String getTooltip() {
		String isAsserted = this.isAsserted() ? "ASSERTED " : "";
		StringBuffer buffer = new StringBuffer(isAsserted);
		return buffer.toString();
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#handleDelete()
	 */
	public boolean handleDelete() {
		return true;
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#handleEdit()
	 */
	public void handleEdit() {
		final OPPLSelectClauseEditor editor = new OPPLSelectClauseEditor(
				this.owlEditorKit, this.constraintSystem);
		editor.setSelectListItem(this);
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
		dlg.setModal(true);
		dlg.setTitle("Clause editor");
		dlg.setResizable(true);
		dlg.pack();
		dlg.setLocationRelativeTo(this.owlEditorKit.getWorkspace());
		dlg.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				Object retVal = optionPane.getValue();
				if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
					OPPLSelectClauseListItem selectListItem = editor
							.getSelectListItem();
					OPPLSelectClauseListItem.this.axiom = selectListItem.axiom;
					OPPLSelectClauseListItem.this.asserted = selectListItem.asserted;
				}
				editor.removeStatusChangedListener(verificationListener);
				editor.dispose();
			}
		});
		dlg.setVisible(true);
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#isDeleteable()
	 */
	public boolean isDeleteable() {
		return true;
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#isEditable()
	 */
	public boolean isEditable() {
		return true;
	}

	/**
	 * @return the asserted
	 */
	public boolean isAsserted() {
		return this.asserted;
	}

	/**
	 * @return the axiom
	 */
	public OWLAxiom getAxiom() {
		return this.axiom;
	}
}