package org.coode.oppl.protege.ui;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.coode.oppl.AbstractConstraint;
import org.coode.oppl.OPPLQuery;
import org.coode.oppl.OPPLScript;
import org.coode.oppl.protege.ui.rendering.VariableOWLCellRenderer;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomChange;

public class OPPLBuilder extends JSplitPane implements VerifiedInputEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6106150715610094308L;

	private class OPPLActionList extends ActionList {
		public OPPLActionList() {
			super(OPPLBuilder.this.owlEditorKit,
					OPPLBuilder.this.constraintSystem);
			((DefaultListModel) this.getModel())
					.addElement(new ActionListSectionHeader());
		}

		@Override
		protected void handleAdd() {
			final OWLAxiomChangeEditor actionEditor = new OWLAxiomChangeEditor(
					OPPLBuilder.this.owlEditorKit,
					OPPLBuilder.this.constraintSystem);
			final VerifyingOptionPane optionPane = new VerifyingOptionPane(
					actionEditor) {
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
			actionEditor.addStatusChangedListener(verificationListener);
			final JDialog dlg = optionPane.createDialog(
					OPPLBuilder.this.owlEditorKit.getWorkspace(), null);
			// The editor shouldn't be modal (or should it?)
			dlg.setModal(false);
			dlg.setTitle("Action editor");
			dlg.setResizable(true);
			dlg.pack();
			dlg.setLocationRelativeTo(OPPLBuilder.this.owlEditorKit
					.getWorkspace());
			dlg.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent e) {
					Object retVal = optionPane.getValue();
					if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
						OWLAxiomChange action = actionEditor
								.getOwlAxiomChange();
						DefaultListModel model = (DefaultListModel) OPPLBuilder.this.actionList
								.getModel();
						model.addElement(new OPPLActionListItem(action, true,
								true));
						OPPLBuilder.this.handleChange();
					}
					actionEditor
							.removeStatusChangedListener(verificationListener);
					actionEditor.dispose();
				}
			});
			dlg.setVisible(true);
		}

		@Override
		public void setConstraintSystem(ConstraintSystem constraintSystem) {
			this.setCellRenderer(new VariableOWLCellRenderer(
					OPPLBuilder.this.owlEditorKit,
					OPPLBuilder.this.constraintSystem, new OWLCellRenderer(
							OPPLBuilder.this.owlEditorKit)));
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -3297222035586803090L;
	}

	private class OPPLActionListItem extends ActionListItem {
		public OPPLActionListItem(OWLAxiomChange axiomChange,
				boolean isEditable, boolean isDeleteable) {
			super(axiomChange, isEditable, isDeleteable);
		}

		@Override
		public void handleEdit() {
			final OWLAxiomChangeEditor actionEditor = new OWLAxiomChangeEditor(
					OPPLBuilder.this.owlEditorKit,
					OPPLBuilder.this.constraintSystem);
			actionEditor.setOWLAxiomChange(this.getAxiomChange());
			final VerifyingOptionPane optionPane = new VerifyingOptionPane(
					actionEditor) {
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
			actionEditor.addStatusChangedListener(verificationListener);
			final JDialog dlg = optionPane.createDialog(
					OPPLBuilder.this.owlEditorKit.getWorkspace(), null);
			// The editor shouldn't be modal (or should it?)
			dlg.setModal(false);
			dlg.setTitle("Action editor");
			dlg.setResizable(true);
			dlg.pack();
			dlg.setLocationRelativeTo(OPPLBuilder.this.owlEditorKit
					.getWorkspace());
			dlg.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent e) {
					Object retVal = optionPane.getValue();
					if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
						OWLAxiomChange action = actionEditor
								.getOwlAxiomChange();
						DefaultListModel model = (DefaultListModel) OPPLBuilder.this.actionList
								.getModel();
						model.removeElement(OPPLBuilder.this.actionList
								.getSelectedValue());
						model.addElement(new OPPLActionListItem(action, true,
								true));
						OPPLBuilder.this.handleChange();
					}
					actionEditor
							.removeStatusChangedListener(verificationListener);
					actionEditor.dispose();
				}
			});
			dlg.setVisible(true);
		}
	}

	/**
	 * @author Luigi Iannone
	 * 
	 */
	public class OPPLVariableListItem extends VariableListItem {
		/**
		 * @param variable
		 * @param owlEditorKit
		 */
		public OPPLVariableListItem(Variable variable,
				OWLEditorKit owlEditorKit, boolean isEditable,
				boolean isDeleatable) {
			super(variable, owlEditorKit, isEditable, isDeleatable);
		}

		/**
		 * @see org.protege.editor.core.ui.list.MListItem#getTooltip()
		 */
		@Override
		public String getTooltip() {
			return this.getVariable().toString();
		}

		/**
		 * @see org.protege.editor.core.ui.list.MListItem#handleEdit()
		 */
		@Override
		public void handleEdit() {
			ConstraintSystem cs = OPPLBuilder.this.constraintSystem;
			final AbstractVariableEditor variableEditor = this.getVariable() instanceof GeneratedVariable ? new GeneratedVariableEditor(
					OPPLBuilder.this.owlEditorKit, cs)
					: new VariableEditor(OPPLBuilder.this.owlEditorKit, cs);
			variableEditor.setVariable(this.getVariable());
			final VerifyingOptionPane optionPane = new VerifyingOptionPane(
					variableEditor) {
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
			variableEditor.addStatusChangedListener(verificationListener);
			final JDialog dlg = optionPane.createDialog(
					OPPLBuilder.this.owlEditorKit.getWorkspace(), null);
			// The editor shouldn't be modal (or should it?)
			dlg.setModal(false);
			dlg.setTitle("Action editor");
			dlg.setResizable(true);
			dlg.pack();
			dlg.setLocationRelativeTo(OPPLBuilder.this.owlEditorKit
					.getWorkspace());
			dlg.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent e) {
					Object retVal = optionPane.getValue();
					if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
						Variable variable = variableEditor.getVariable();
						DefaultListModel model = (DefaultListModel) OPPLBuilder.this.variableList
								.getModel();
						model.removeElement(OPPLBuilder.this.variableList
								.getSelectedValue());
						OPPLBuilder.this.variableList
								.placeListItem(new OPPLVariableListItem(
										variable,
										OPPLBuilder.this.owlEditorKit, true,
										true));
						OPPLVariableListItem.this.purgeActions(variable);
						OPPLBuilder.this.handleChange();
					}
					variableEditor
							.removeStatusChangedListener(verificationListener);
					variableEditor.dispose();
					OPPLBuilder.this.handleChange();
				}
			});
			dlg.setVisible(true);
		}

		@Override
		public boolean handleDelete() {
			Variable v = this.getVariable();
			this.purgeActions(v);
			OPPLBuilder.this.handleChange();
			return true;
		}

		/**
		 * @param v
		 */
		private void purgeActions(Variable v) {
			DefaultListModel model = (DefaultListModel) OPPLBuilder.this.actionList
					.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				Object e = model.getElementAt(i);
				if (e instanceof OPPLActionListItem) {
					OWLAxiomChange action = ((OPPLActionListItem) e)
							.getAxiomChange();
					OWLAxiom axiom = action.getAxiom();
					Set<Variable> axiomVariables = OPPLBuilder.this.constraintSystem
							.getAxiomVariables(axiom);
					if (axiomVariables.contains(v)) {
						model.remove(i);
					}
				}
			}
		}
	}

	private class OPPLVariableList extends VariableList implements
			ListDataListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2540053052502672472L;

		@Override
		protected void handleAdd() {
			final AbstractVariableEditor variableEditor = this
					.getSelectedValue() instanceof InputVariableSectionHeader ? new VariableEditor(
					OPPLBuilder.this.owlEditorKit,
					OPPLBuilder.this.constraintSystem)
					: new GeneratedVariableEditor(
							OPPLBuilder.this.owlEditorKit,
							OPPLBuilder.this.constraintSystem);
			final VerifyingOptionPane optionPane = new VerifyingOptionPane(
					variableEditor) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 7217535942418544769L;

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
			variableEditor.addStatusChangedListener(verificationListener);
			final JDialog dlg = optionPane.createDialog(
					OPPLBuilder.this.owlEditorKit.getWorkspace(), null);
			// The editor shouldn't be modal (or should it?)
			dlg.setModal(false);
			dlg.setTitle("Variable editor");
			dlg.setResizable(true);
			dlg.pack();
			dlg.setLocationRelativeTo(OPPLBuilder.this.owlEditorKit
					.getWorkspace());
			dlg.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent e) {
					Object retVal = optionPane.getValue();
					if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
						Variable variable = variableEditor.getVariable();
						OPPLVariableListItem listItem = new OPPLVariableListItem(
								variable, OPPLBuilder.this.owlEditorKit, true,
								true);
						OPPLVariableList.this.placeListItem(listItem);
						OPPLBuilder.this.handleChange();
					}
					variableEditor
							.removeStatusChangedListener(verificationListener);
					variableEditor.dispose();
				}
			});
			dlg.setVisible(true);
		}

		public OPPLVariableList(OWLEditorKit owlEditorKit) {
			super(owlEditorKit);
			((DefaultListModel) this.getModel())
					.addElement(new InputVariableSectionHeader());
			((DefaultListModel) this.getModel())
					.addElement(new GeneratedVariableSectionHeader());
			this.getModel().addListDataListener(this);
		}

		public void clear() {
			((DefaultListModel) this.getModel()).clear();
			((DefaultListModel) this.getModel())
					.addElement(new InputVariableSectionHeader());
			((DefaultListModel) this.getModel())
					.addElement(new GeneratedVariableSectionHeader());
		}

		public void contentsChanged(ListDataEvent e) {
			this.updateOPPLScriptModel();
		}

		/**
		 * 
		 */
		private void updateOPPLScriptModel() {
			ListModel model = this.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				Object element = model.getElementAt(i);
				if (element instanceof OPPLVariableListItem) {
					OPPLVariableListItem item = (OPPLVariableListItem) element;
					if (OPPLBuilder.this.opplScript != null) {
						if (!OPPLBuilder.this.opplScript.getVariables()
								.contains(item.getVariable())) {
							OPPLBuilder.this.opplScript.addVariable(item
									.getVariable());
						}
					}
				}
			}
		}

		public void intervalAdded(ListDataEvent e) {
			this.updateOPPLScriptModel();
		}

		public void intervalRemoved(ListDataEvent e) {
			this.updateOPPLScriptModel();
		}

		/**
		 * @param listItem
		 */
		protected void placeListItem(OPPLVariableListItem listItem) {
			DefaultListModel model = (DefaultListModel) OPPLVariableList.this
					.getModel();
			int i = -1;
			if (listItem.getVariable() instanceof GeneratedVariable) {
				i = model.getSize();
			} else {
				Enumeration<?> elements = model.elements();
				boolean found = false;
				while (!found && elements.hasMoreElements()) {
					i++;
					Object element = elements.nextElement();
					found = element instanceof GeneratedVariableSectionHeader;
				}
				if (!found) {
					throw new RuntimeException("Section lost");
				}
			}
			model.add(i, listItem);
		}
	}

	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
	private OWLEditorKit owlEditorKit;
	private OPPLVariableList variableList;
	private OPPLSelectClauseList selectList;
	private transient ListDataListener selectListListener = new ListDataListener() {
		public void contentsChanged(ListDataEvent e) {
			OPPLBuilder.this.handleChange();
		}

		public void intervalAdded(ListDataEvent e) {
			OPPLBuilder.this.handleChange();
		}

		public void intervalRemoved(ListDataEvent e) {
			OPPLBuilder.this.handleChange();
		}
	};
	private transient ListDataListener constraintListListener = new ListDataListener() {
		public void contentsChanged(ListDataEvent e) {
			OPPLBuilder.this.handleChange();
		}

		public void intervalAdded(ListDataEvent e) {
			OPPLBuilder.this.handleChange();
		}

		public void intervalRemoved(ListDataEvent e) {
			OPPLBuilder.this.handleChange();
		}
	};
	private OPPLConstraintList constraintList;
	private ActionList actionList;
	private transient ListDataListener actionListListener = new ListDataListener() {
		public void contentsChanged(ListDataEvent e) {
			OPPLBuilder.this.handleChange();
		}

		public void intervalAdded(ListDataEvent e) {
			OPPLBuilder.this.handleChange();
		}

		public void intervalRemoved(ListDataEvent e) {
			OPPLBuilder.this.handleChange();
		}
	};
	private ConstraintSystem constraintSystem = OPPLParser.getOPPLFactory()
			.createConstraintSystem();
	private OPPLScript opplScript;

	public OPPLBuilder(OWLEditorKit owlEditorKit) {
		this.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		this.owlEditorKit = owlEditorKit;
		// Setup the variable list on the left
		JPanel variablePanel = new JPanel(new BorderLayout());
		this.variableList = new OPPLVariableList(this.owlEditorKit);
		variablePanel.add(this.variableList);
		this.add(ComponentFactory.createScrollPane(this.variableList),
				JSplitPane.LEFT);
		// Now setup the right hand side panel which will be further split into
		// queries and actions
		final JSplitPane queryActionSplitPane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT);
		// Now setup the query split pane
		final JSplitPane queryConstraintSplitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		// Now the select part
		JPanel queryPanel = new JPanel(new BorderLayout());
		this.selectList = new OPPLSelectClauseList(this.owlEditorKit,
				this.constraintSystem);
		this.selectList.getModel().addListDataListener(this.selectListListener);
		queryPanel.add(ComponentFactory.createScrollPane(this.selectList));
		// Now the constraints
		JPanel constraintPanel = new JPanel(new BorderLayout());
		this.constraintList = new OPPLConstraintList(this.owlEditorKit,
				this.constraintSystem);
		this.constraintList.getModel().addListDataListener(
				this.constraintListListener);
		constraintPanel.add(ComponentFactory
				.createScrollPane(this.constraintList));
		queryConstraintSplitPane.add(queryPanel, JSplitPane.LEFT);
		queryConstraintSplitPane.add(constraintPanel, JSplitPane.RIGHT);
		// Now setup the action panel
		JPanel actionPanel = new JPanel(new BorderLayout());
		this.actionList = new OPPLActionList();
		this.actionList.getModel().addListDataListener(this.actionListListener);
		actionPanel.add(ComponentFactory.createScrollPane(this.actionList));
		queryActionSplitPane.add(queryConstraintSplitPane, JSplitPane.TOP);
		queryActionSplitPane.add(actionPanel, JSplitPane.BOTTOM);
		this.add(queryActionSplitPane, JSplitPane.RIGHT);
		queryConstraintSplitPane.setDividerLocation(.5);
		queryConstraintSplitPane.setResizeWeight(.3);
		queryActionSplitPane.setDividerLocation(.5);
		queryActionSplitPane.setResizeWeight(.3);
		this.setDividerLocation(.5);
		this.setResizeWeight(.3);
	}

	private boolean check() {
		// The numbers include the section headers
		return this.variableList.getModel().getSize() > 2
				&& (this.selectList.getModel().getSize() > 1 || this.actionList
						.getModel().getSize() > 1);
	}

	public void handleChange() {
		boolean isValid = this.check();
		if (isValid) {
			this.opplScript = OPPLParser.getOPPLFactory().buildOPPLScript(
					this.constraintSystem, this.getVariables(),
					this.getOPPLQuery(), this.getActions());
		}
		this.notifyListeners(isValid);
	}

	private void notifyListeners(boolean status) {
		for (InputVerificationStatusChangedListener listener : this.listeners) {
			listener.verifiedStatusChanged(status);
		}
	}

	private OPPLQuery getOPPLQuery() {
		OPPLQuery toReturn = OPPLParser.getOPPLFactory().buildNewQuery(
				this.constraintSystem);
		ListModel model = this.selectList.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Object e = model.getElementAt(i);
			if (e instanceof OPPLSelectClauseListItem) {
				OPPLSelectClauseListItem selectListItem = (OPPLSelectClauseListItem) e;
				OWLAxiom axiom = selectListItem.getAxiom();
				if (selectListItem.isAsserted()) {
					toReturn.addAssertedAxiom(axiom);
				} else {
					toReturn.addAxiom(axiom);
				}
			}
		}
		model = this.constraintList.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Object e = model.getElementAt(i);
			if (e instanceof OPPLConstraintListItem) {
				OPPLConstraintListItem constraintListItem = (OPPLConstraintListItem) e;
				toReturn.addConstraint(constraintListItem.getConstraint());
			}
		}
		return toReturn;
	}

	private List<OWLAxiomChange> getActions() {
		ListModel model = this.actionList.getModel();
		List<OWLAxiomChange> toReturn = new ArrayList<OWLAxiomChange>(model
				.getSize());
		if (model.getSize() > 0) {
			for (int i = 0; i < model.getSize(); i++) {
				Object elementAt = model.getElementAt(i);
				if (elementAt instanceof ActionListItem) {
					ActionListItem actionListItem = (ActionListItem) elementAt;
					toReturn.add(actionListItem.getAxiomChange());
				}
			}
		}
		return toReturn;
	}

	private List<Variable> getVariables() {
		ListModel model = this.variableList.getModel();
		// There is a section header so the initial capacity is the size -1
		List<Variable> toReturn = new ArrayList<Variable>(model.getSize() - 1);
		for (int i = 0; i < model.getSize(); i++) {
			Object elementAt = model.getElementAt(i);
			if (elementAt instanceof VariableListItem) {
				VariableListItem variableListItem = (VariableListItem) elementAt;
				toReturn.add(variableListItem.getVariable());
			}
		}
		return toReturn;
	}

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
		listener.verifiedStatusChanged(this.check());
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * @return the opplScript
	 */
	public OPPLScript getOPPLScript() {
		return this.opplScript;
	}

	/**
	 * @param opplScript
	 *            the opplScript to set
	 */
	public void setOPPLScript(OPPLScript opplScript) {
		this.clear();
		this.constraintSystem = opplScript.getConstraintSystem();
		this.actionList.setConstraintSystem(this.constraintSystem);
		this.selectList.setCellRenderer(new VariableOWLCellRenderer(
				this.owlEditorKit, this.constraintSystem, new OWLCellRenderer(
						this.owlEditorKit)));
		List<Variable> variables = opplScript.getVariables();
		for (Variable variable : variables) {
			OPPLVariableListItem item = new OPPLVariableListItem(variable,
					this.owlEditorKit, true, true);
			this.variableList.placeListItem(item);
		}
		OPPLQuery query = opplScript.getQuery();
		List<OWLAxiom> assertedAxioms = query.getAssertedAxioms();
		for (OWLAxiom axiom : assertedAxioms) {
			((DefaultListModel) this.selectList.getModel())
					.addElement(new OPPLSelectClauseListItem(this.owlEditorKit,
							true, axiom, this.constraintSystem));
		}
		List<OWLAxiom> axioms = query.getAxioms();
		for (OWLAxiom axiom : axioms) {
			((DefaultListModel) this.selectList.getModel())
					.addElement(new OPPLSelectClauseListItem(this.owlEditorKit,
							false, axiom, this.constraintSystem));
		}
		List<AbstractConstraint> constraints = query.getConstraints();
		for (AbstractConstraint abstractConstraint : constraints) {
			((DefaultListModel) this.constraintList.getModel())
					.addElement(new OPPLConstraintListItem(this.owlEditorKit,
							abstractConstraint, this.constraintSystem));
		}
		List<OWLAxiomChange> actions = opplScript.getActions();
		for (OWLAxiomChange axiomChange : actions) {
			((DefaultListModel) this.actionList.getModel())
					.addElement(new OPPLActionListItem(axiomChange, true, true));
		}
		this.handleChange();
	}

	public void clear() {
		this.variableList.clear();
		this.selectList.clear();
		this.selectList.getModel().addListDataListener(this.selectListListener);
		this.constraintList.clear();
		this.constraintList.getModel().addListDataListener(
				this.constraintListListener);
		this.actionList.clear();
		this.actionList.getModel().addListDataListener(this.actionListListener);
		this.constraintSystem = OPPLParser.getOPPLFactory()
				.createConstraintSystem();
	}

	@Override
	public String getName() {
		return "OPPL Builder";
	}
}
