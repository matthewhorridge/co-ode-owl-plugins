/**
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.coode.patterns.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.coode.oppl.protege.ui.ActionList;
import org.coode.oppl.protege.ui.ActionListItem;
import org.coode.oppl.protege.ui.NoDefaultFocusVerifyingOptionPane;
import org.coode.oppl.protege.ui.VariableList;
import org.coode.oppl.protege.ui.VariableListItem;
import org.coode.oppl.variablemansyntax.PartialOWLObjectInstantiator;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.oppl.variablemansyntax.bindingtree.Assignment;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.coode.patterns.AbstractPatternModelFactory;
import org.coode.patterns.InstantiatedPatternModel;
import org.coode.patterns.PatternConstant;
import org.coode.patterns.PatternConstraintSystem;
import org.coode.patterns.PatternModel;
import org.coode.patterns.utils.Utils;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.RemoveAxiom;

/**
 * @author Luigi Iannone
 * 
 *         Jul 2, 2008
 */
public class PatternInstantiationEditor extends
		AbstractOWLFrameSectionRowObjectEditor<InstantiatedPatternModel>
		implements VerifiedInputEditor,
		// InputVerificationStatusChangedListener,
		ListDataListener, ListSelectionListener {
	private final class SpecializedActionListener implements ActionListener {
		final PatternInstantiationEditor pie;

		public SpecializedActionListener(PatternInstantiationEditor _this) {
			this.pie = _this;
		}

		public void actionPerformed(ActionEvent event) {
			this.pie.instantiatedPatternModel = null;
			Object selectedItem = this.pie.patternList.getSelectedItem();
			if (selectedItem instanceof InstantiatedPatternModel) {
				this.pie.instantiatedPatternModel = (InstantiatedPatternModel) selectedItem;
				if (this.pie.owlClass != null) {
					PatternConstant<OWLClass> patternConstant = new PatternConstant<OWLClass>(
							PatternConstraintSystem.THIS_CLASS_VARIABLE_NAME,
							VariableType.CLASS,
							PatternConstant
									.createConstantGeneratedValue(this.pie.owlClass));
					this.pie.instantiatedPatternModel.getConstraintSystem()
							.instantiateThisClass(patternConstant);
				}
				this.pie.refreshInstantiationPanel();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						SpecializedActionListener.this.pie
								.refreshEffectsPanel();
					}
				});
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						SpecializedActionListener.this.pie.handleChange();
					}
				});
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						SpecializedActionListener.this.pie.variableList
								.setSelectedIndex(0);
					}
				});
				this.pie.handleChange();
			}
		}
	}

	static final class SpecializedListCellRenderer implements ListCellRenderer {
		private final OWLCellRenderer owlCellRenderer;

		public SpecializedListCellRenderer(OWLEditorKit kit) {
			this.owlCellRenderer = new OWLCellRenderer(kit);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (value instanceof VariableValueListItem) {
				return this.owlCellRenderer.getListCellRendererComponent(list,
						((VariableValueListItem) value).getValue(), index,
						isSelected, cellHasFocus);
			}
			return this.owlCellRenderer.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
		}
	}

	private class VariableValuesMList extends MList {
		private static final long serialVersionUID = 1182645120185580287L;
		private final Variable variable;

		@Override
		protected void handleAdd() {
			final VariableValueEditor variableValueEditor = VariableValueEditor
					.getVariableValueEditor(
							PatternInstantiationEditor.this.owlEditorKit,
							this.variable);
			final VerifyingOptionPane optionPane = new NoDefaultFocusVerifyingOptionPane(
					variableValueEditor);
			final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
				public void verifiedStatusChanged(boolean verified) {
					optionPane.setOKEnabled(verified);
				}
			};
			variableValueEditor.addStatusChangedListener(verificationListener);
			final JDialog dlg = optionPane
					.createDialog(PatternInstantiationEditor.this.owlEditorKit
							.getWorkspace(), null);
			// The editor shouldn't be modal (or should it?)
			dlg.setModal(false);
			dlg.setTitle(variableValueEditor.getTitle());
			dlg.setResizable(true);
			dlg.pack();
			dlg
					.setLocationRelativeTo(PatternInstantiationEditor.this.owlEditorKit
							.getWorkspace());
			dlg.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent e) {
					Object retVal = optionPane.getValue();
					if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
						Set<OWLObject> variableValues = variableValueEditor
								.getVariableValues();
						for (OWLObject object : variableValues) {
							PatternInstantiationEditor.this.instantiatedPatternModel
									.instantiate(
											VariableValuesMList.this.variable,
											object);
							((DefaultListModel) VariableValuesMList.this
									.getModel())
									.addElement(new VariableValueListItem(
											VariableValuesMList.this.variable,
											object));
						}
					}
					variableValueEditor
							.removeStatusChangedListener(verificationListener);
					variableValueEditor.dispose();
				}
			});
			dlg.setVisible(true);
		}

		VariableValuesMList() {
			this.variable = null;
			DefaultListModel model = new DefaultListModel();
			super.setModel(model);
			model.addElement(new MListSectionHeader() {
				public boolean canAdd() {
					return false;
				}

				public String getName() {
					return "No pattern selected yet";
				}
			});
			this.setCellRenderer(new SpecializedListCellRenderer(
					PatternInstantiationEditor.this.owlEditorKit));
		}

		public VariableValuesMList(Variable variable) {
			this.variable = variable;
			DefaultListModel model = new DefaultListModel();
			super.setModel(model);
			model.addElement(new MListSectionHeader() {
				public boolean canAdd() {
					return true;
				}

				public String getName() {
					return VariableValuesMList.this.variable.getName();
				}
			});
			this.setCellRenderer(new SpecializedListCellRenderer(
					PatternInstantiationEditor.this.owlEditorKit));
		}

		@Override
		protected void handleDelete() {
			if (this.getSelectedValue() instanceof VariableValueListItem) {
				VariableValueListItem item = (VariableValueListItem) this
						.getSelectedValue();
				Variable v = item.getVariable();
				OWLObject value = item.getValue();
				PatternInstantiationEditor.this.instantiatedPatternModel
						.getInstantiations(v).remove(value);
			}
			((DefaultListModel) this.getModel()).removeElement(this
					.getSelectedValue());
			super.handleDelete();
		}
	}

	private class VariableValueListItem implements MListItem {
		private final Variable variable;
		private final OWLObject value;

		/**
		 * @param variable
		 * @param value
		 */
		public VariableValueListItem(Variable variable, OWLObject value) {
			this.variable = variable;
			this.value = value;
		}

		public String getTooltip() {
			return PatternInstantiationEditor.this.owlEditorKit
					.getModelManager().getRendering(this.value);
		}

		public boolean handleDelete() {
			Set<OWLObject> instantiations = PatternInstantiationEditor.this.instantiatedPatternModel
					.getInstantiations(this.variable);
			return instantiations.remove(this.value);
		}

		public void handleEdit() {
		}

		public boolean isDeleteable() {
			return true;
		}

		public boolean isEditable() {
			return false;
		}

		/**
		 * @return the variable
		 */
		public Variable getVariable() {
			return this.variable;
		}

		/**
		 * @return the value
		 */
		public OWLObject getValue() {
			return this.value;
		}
	}

	private final static class InstantiatedPatternCellRenderer implements
			ListCellRenderer {
		public InstantiatedPatternCellRenderer() {
			// TODO Auto-generated constructor stub
		}

		private final static DefaultListCellRenderer DELEGATE = new DefaultListCellRenderer();

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Component toReturn = DELEGATE.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
			if (value instanceof InstantiatedPatternModel) {
				InstantiatedPatternModel instantiatedPatternModel = (InstantiatedPatternModel) value;
				StringWriter writer = new StringWriter();
				writer.append(instantiatedPatternModel.getName());
				writer.append("(");
				List<Variable> inputVariables = instantiatedPatternModel
						.getInputVariables();
				boolean first = true;
				for (Variable inputVariable : inputVariables) {
					String comma = first ? "" : ", ";
					first = first ? false : first;
					writer.append(comma);
					writer.append(inputVariable.getName());
				}
				writer.append(")");
				toReturn = DELEGATE.getListCellRendererComponent(list, writer
						.toString(), index, isSelected, cellHasFocus);
			}
			return toReturn;
		}
	}

	protected OWLEditorKit owlEditorKit;
	private JPanel mainPane;
	// private ExpressionEditor<InstantiatedPatternModel> nameEditor;
	private DefaultComboBoxModel patternListModel = new DefaultComboBoxModel();
	protected JComboBox patternList = new JComboBox(this.patternListModel);
	protected InstantiatedPatternModel instantiatedPatternModel;
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
	private JPanel effectsBorder;
	private ActionList actionList;
	private JSplitPane instantiationPanel;
	protected OWLClass owlClass = null;
	private InstantiatedPatternModel instantantiatedPatternModel2CopyFrom = null;
	protected VariableList variableList;
	private VariableValuesMList valueList;
	private JScrollPane variablePane;
	private JScrollPane valuePane;
	private final JPanel problemPanel = new JPanel(new BorderLayout());
	private final DefaultListModel problemListModel = new DefaultListModel();
	private final JList problemList = new JList(this.problemListModel);
	private final AbstractPatternModelFactory factory;

	/**
	 * Builds a PatternInstantiationEditor for a specific class, i.e.: an editor
	 * for instantiating class patterns
	 * 
	 * @param owlEditorKit
	 * @param owlClass
	 */
	public PatternInstantiationEditor(OWLEditorKit owlEditorKit,
			OWLClass owlClass, AbstractPatternModelFactory f) {
		this(owlEditorKit, f);
		this.owlClass = owlClass;
		// this.owlEditorKit = owlEditorKit;
		//
		// this.mainPane = new JPanel(new BorderLayout());
		// this.mainPane.setFocusable(false);
		// this.factory = new ProtegePatternModelFactory(owlEditorKit
		// .getModelManager());
		// setup();
	}

	/**
	 * Builds a PatternInstantiationEditor for instantiating non-class patterns
	 * 
	 * @param owlEditorKit
	 */
	public PatternInstantiationEditor(OWLEditorKit owlEditorKit,
			AbstractPatternModelFactory f) {
		this.owlEditorKit = owlEditorKit;
		this.factory = f;
		this.mainPane = new JPanel(new BorderLayout());
		this.mainPane.setFocusable(false);
		this.setup();
	}

	private void setup() {
		this.mainPane.setPreferredSize(new Dimension(500, 600));
		this.refillPatternList();
		this.patternList.setPreferredSize(new Dimension(50, 20));
		this.patternList.addActionListener(new SpecializedActionListener(this));
		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.setBorder(ComponentFactory.createTitledBorder("Pattern:"));
		namePanel.add(ComponentFactory.createScrollPane(this.patternList),
				BorderLayout.NORTH);
		this.problemPanel.setBorder(ComponentFactory
				.createTitledBorder("Problems"));
		this.problemPanel.add(ComponentFactory
				.createScrollPane(this.problemList));
		namePanel.add(this.problemPanel, BorderLayout.SOUTH);
		JPanel editorPanel = new JPanel(new BorderLayout());
		editorPanel.add(namePanel);
		this.instantiationPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.instantiationPanel.setBorder(ComponentFactory
				.createTitledBorder("Variables:"));
		this.instantiationPanel.setDividerLocation(.5);
		this.variableList = new VariableList(this.owlEditorKit, this.factory
				.createConstraintSystem());
		this.valueList = new VariableValuesMList();
		this.variablePane = ComponentFactory
				.createScrollPane(this.variableList);
		this.instantiationPanel.add(this.variablePane, JSplitPane.LEFT);
		this.valuePane = ComponentFactory.createScrollPane(this.valueList);
		this.instantiationPanel.add(this.valuePane, JSplitPane.RIGHT);
		this.effectsBorder = new JPanel(new BorderLayout());
		this.effectsBorder.setBorder(ComponentFactory
				.createTitledBorder("Effects: "));
		this.actionList = new ActionList(this.owlEditorKit,
				this.instantiatedPatternModel == null ? this.factory
						.createConstraintSystem()
						: this.instantiatedPatternModel.getConstraintSystem(),
				false);
		this.effectsBorder.add(ComponentFactory
				.createScrollPane(this.actionList), BorderLayout.CENTER);
		this.mainPane.add(editorPanel, BorderLayout.NORTH);
		JSplitPane centerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		centerPane.add(this.instantiationPanel, JSplitPane.TOP);
		centerPane.add(this.effectsBorder, JSplitPane.BOTTOM);
		centerPane.setDividerLocation(.5);
		this.mainPane.add(centerPane, BorderLayout.CENTER);
	}

	private void refillPatternList() {
		this.patternListModel.removeAllElements();
		Set<String> existingPatternNames = Utils
				.getExistingPatternNames(this.owlEditorKit.getModelManager()
						.getOWLOntologyManager());
		this.patternList.setRenderer(new InstantiatedPatternCellRenderer());
		for (String string : existingPatternNames) {
			PatternModel patternModel = Utils.find(string, this.owlEditorKit
					.getModelManager().getOWLOntologyManager(), this.factory);
			InstantiatedPatternModel toAdd = this.factory
					.createInstantiatedPatternModel(patternModel);
			this.patternListModel.addElement(toAdd);
			this.patternList.setSelectedItem(null);
		}
	}

	public void clear() {
		this.mainPane.removeAll();
		this.setup();
	}

	public void dispose() {
		this.valueList.getModel().removeListDataListener(this);
		this.variableList.removeListSelectionListener(this);
	}

	public InstantiatedPatternModel getEditedObject() {
		return this.instantiatedPatternModel;
	}

	public JComponent getEditorComponent() {
		return this.mainPane;
	}

	/**
	 * @param newState
	 * @param listener
	 */
	private void notifyListener(boolean newState,
			InputVerificationStatusChangedListener listener) {
		listener.verifiedStatusChanged(newState);
	}

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
		this.notifyListener(this.check(), listener);
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	protected void refreshEffectsPanel() {
		DefaultListModel model = (DefaultListModel) this.actionList.getModel();
		model.clear();
		if (this.instantiatedPatternModel != null) {
			this.actionList.setConstraintSystem(this.instantiatedPatternModel
					.getConstraintSystem());
			PatternModel instantiatedPattern = this.instantiatedPatternModel
					.getInstantiatedPattern();
			List<OWLAxiomChange> actions = instantiatedPattern.getActions();
			Set<OWLAxiomChange> changes = new HashSet<OWLAxiomChange>(actions
					.size());
			Set<BindingNode> bindingNodes = this.instantiatedPatternModel
					.extractBindingNodes();
			if (bindingNodes.isEmpty()) {
				BindingNode bindingNode = new BindingNode(
						new HashSet<Assignment>(), new HashSet<Variable>(
								instantiatedPattern.getInputVariables()));
				PartialOWLObjectInstantiator instantiator = new PartialOWLObjectInstantiator(
						bindingNode, this.instantiatedPatternModel
								.getConstraintSystem());
				for (OWLAxiomChange change : actions) {
					OWLAxiom axiom = change.getAxiom();
					OWLAxiom instantiatedAxiom = (OWLAxiom) axiom
							.accept(instantiator);
					OWLAxiomChange newAxiomChange = change instanceof AddAxiom ? new AddAxiom(
							change.getOntology(), instantiatedAxiom)
							: new RemoveAxiom(change.getOntology(),
									instantiatedAxiom);
					changes.add(newAxiomChange);
				}
			} else {
				this.instantiatedPatternModel.getConstraintSystem().setLeaves(
						bindingNodes);
				for (BindingNode bindingNode : bindingNodes) {
					PartialOWLObjectInstantiator instantiator = new PartialOWLObjectInstantiator(
							bindingNode, this.instantiatedPatternModel
									.getConstraintSystem());
					for (OWLAxiomChange change : actions) {
						OWLAxiom axiom = change.getAxiom();
						OWLAxiom instantiatedAxiom = (OWLAxiom) axiom
								.accept(instantiator);
						OWLAxiomChange newAxiomChange = change instanceof AddAxiom ? new AddAxiom(
								change.getOntology(), instantiatedAxiom)
								: new RemoveAxiom(change.getOntology(),
										instantiatedAxiom);
						changes.add(newAxiomChange);
					}
				}
			}
			for (OWLAxiomChange axiomChange : changes) {
				model.addElement(new ActionListItem(axiomChange, false, false));
			}
		}
		this.handleChange();
		this.mainPane.revalidate();
	}

	protected void refreshInstantiationPanel() {
		DefaultListModel model = (DefaultListModel) this.variableList
				.getModel();
		model.clear();
		((DefaultListModel) this.valueList.getModel()).clear();
		if (this.instantiatedPatternModel != null) {
			List<Variable> inputVariables = this.instantiatedPatternModel
					.getInputVariables();
			for (Variable inputVariable : inputVariables) {
				model.addElement(new VariableListItem(inputVariable,
						this.owlEditorKit, false, false) {
					@Override
					public String getTooltip() {
						return this.getVariable().toString();
					}
				});
			}
			this.variableList.addListSelectionListener(this);
			if (this.instantantiatedPatternModel2CopyFrom != null) {
				for (Variable inputVariable : inputVariables) {
					Set<OWLObject> instantiations = this.instantantiatedPatternModel2CopyFrom
							.getInstantiations(inputVariable);
					if (instantiations != null) {
						for (OWLObject object : instantiations) {
							this.instantiatedPatternModel.instantiate(
									inputVariable, object);
						}
					}
				}
				this.instantantiatedPatternModel2CopyFrom = null;
			}
		}
	}

	public void handleChange() {
		boolean newState = this.check();
		for (InputVerificationStatusChangedListener listener : this.listeners) {
			this.notifyListener(newState, listener);
		}
		this.problemPanel.setVisible(!this.problemListModel.isEmpty());
	}

	private boolean check() {
		boolean valid = this.instantiatedPatternModel != null ? this.instantiatedPatternModel
				.isValid()
				: false;
		return valid;
	}

	public void setInstantiatedPatternModel(
			InstantiatedPatternModel patternModel) {
		this.instantantiatedPatternModel2CopyFrom = patternModel;
		boolean found = false;
		for (int i = 0; i < this.patternListModel.getSize() && !found; i++) {
			Object element = this.patternListModel.getElementAt(i);
			found = element instanceof InstantiatedPatternModel
					&& patternModel.getName().equals(
							((InstantiatedPatternModel) element).getName());
			if (found) {
				this.patternList.setSelectedItem(element);
			}
		}
	}

	public void contentsChanged(ListDataEvent e) {
		this.refreshEffectsPanel();
	}

	public void intervalAdded(ListDataEvent e) {
		this.refreshEffectsPanel();
	}

	public void intervalRemoved(ListDataEvent e) {
		this.refreshEffectsPanel();
	}

	public void valueChanged(ListSelectionEvent e) {
		Object selectedValue = this.variableList.getSelectedValue();
		this.valueList.getModel().removeListDataListener(this);
		this.instantiationPanel.remove(this.variablePane);
		this.instantiationPanel.remove(this.valuePane);
		if (selectedValue instanceof VariableListItem) {
			VariableListItem item = (VariableListItem) selectedValue;
			Variable variable = item.getVariable();
			this.valueList = new VariableValuesMList(variable);
			this.valueList.getModel().addListDataListener(this);
			Set<OWLObject> instantiations = this.instantiatedPatternModel
					.getInstantiations(variable);
			if (instantiations != null) {
				for (OWLObject object : instantiations) {
					((DefaultListModel) this.valueList.getModel())
							.addElement(new VariableValueListItem(variable,
									object) {
								@Override
								public String getTooltip() {
									return this.getVariable().toString();
								}
							});
				}
			}
			this.valuePane = ComponentFactory.createScrollPane(this.valueList);
		}
		this.instantiationPanel.add(this.variablePane, JSplitPane.LEFT);
		this.instantiationPanel.add(this.valuePane, JSplitPane.RIGHT);
		this.mainPane.revalidate();
	}
}
