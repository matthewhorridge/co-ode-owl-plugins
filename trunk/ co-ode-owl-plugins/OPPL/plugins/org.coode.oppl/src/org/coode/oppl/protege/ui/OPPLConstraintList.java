package org.coode.oppl.protege.ui;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;

import org.coode.oppl.AbstractConstraint;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;

public class OPPLConstraintList extends MList {
	static private class OPPLConstraintListItemCellRenderer implements
			ListCellRenderer {
		private DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			return this.defaultListCellRenderer.getListCellRendererComponent(
					list, value.toString(), index, isSelected, cellHasFocus);
		}
	}

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
		this.setCellRenderer(new OPPLConstraintListItemCellRenderer());
		DefaultListModel model = new DefaultListModel();
		this.setModel(model);
		model.addElement(new MListSectionHeader() {
			public boolean canAdd() {
				return true;
			}

			public String getName() {
				return "WHERE";
			}
		});
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
}
