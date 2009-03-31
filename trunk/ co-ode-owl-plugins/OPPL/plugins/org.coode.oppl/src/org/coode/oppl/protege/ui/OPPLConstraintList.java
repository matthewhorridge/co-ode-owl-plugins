package org.coode.oppl.protege.ui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.coode.oppl.AbstractConstraint;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;

public class OPPLConstraintList extends MList {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4366866288573896156L;
	private final OWLEditorKit owlEditorKit;
	private ConstraintSystem constraintSystem;

	/**
	 * @param owlEditorKit
	 * @param constraintSystem
	 */
	public OPPLConstraintList(OWLEditorKit owlEditorKit,
			ConstraintSystem constraintSystem) {
		this.owlEditorKit = owlEditorKit;
		this.constraintSystem = constraintSystem;
		DefaultListModel model = this.clearModel();
		this.setModel(model);
	}

	/**
	 * @return
	 */
	private DefaultListModel clearModel() {
		DefaultListModel model = new DefaultListModel();
		model.addElement(new MListSectionHeader() {
			public boolean canAdd() {
				return true;
			}

			public String getName() {
				return "WHERE";
			}
		});
		return model;
	}

	@Override
	protected void handleEdit() {
		if (this.getSelectedValue() instanceof OPPLConstraintListItem) {
			OPPLConstraintListItem item = (OPPLConstraintListItem) this
					.getSelectedValue();
			((DefaultListModel) this.getModel()).removeElement(item);
			item.handleEdit();
			((DefaultListModel) this.getModel()).addElement(item);
		}
	}

	@Override
	protected void handleDelete() {
		if (this.getSelectedValue() instanceof OPPLConstraintListItem) {
			OPPLConstraintListItem item = (OPPLConstraintListItem) this
					.getSelectedValue();
			((DefaultListModel) this.getModel()).removeElement(item);
		}
	}

	@Override
	protected void handleAdd() {
		final OPPLConstraintEditor editor = new OPPLConstraintEditor(
				this.owlEditorKit, this.constraintSystem);
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
					AbstractConstraint constraint = editor.getConstraint();
					((DefaultListModel) OPPLConstraintList.this.getModel())
							.addElement(new OPPLConstraintListItem(
									OPPLConstraintList.this.owlEditorKit,
									constraint,
									OPPLConstraintList.this.constraintSystem));
				}
				editor.removeStatusChangedListener(verificationListener);
				editor.dispose();
			}
		});
		dlg.setVisible(true);
	}

	public void clear() {
		this.setModel(this.clearModel());
	}
}
