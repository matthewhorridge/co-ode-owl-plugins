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
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.coode.oppl.protege.ui.AbstractVariableEditor;
import org.coode.oppl.protege.ui.ActionList;
import org.coode.oppl.protege.ui.ActionListItem;
import org.coode.oppl.protege.ui.GeneratedVariableEditor;
import org.coode.oppl.protege.ui.GeneratedVariableSectionHeader;
import org.coode.oppl.protege.ui.InputVariableSectionHeader;
import org.coode.oppl.protege.ui.OWLAxiomChangeEditor;
import org.coode.oppl.protege.ui.VariableEditor;
import org.coode.oppl.protege.ui.VariableList;
import org.coode.oppl.protege.ui.VariableListItem;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.InvalidVariableNameException;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;
import org.coode.patterns.EmptyActionListException;
import org.coode.patterns.EmptyVariableListException;
import org.coode.patterns.PatternConstraintSystem;
import org.coode.patterns.PatternModel;
import org.coode.patterns.PatternModelChangeListener;
import org.coode.patterns.UnsuitableOPPLScriptException;
import org.coode.patterns.syntax.PatternParser;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLException;

/**
 * @author Luigi Iannone
 * 
 *         Jun 10, 2008
 */
public class PatternBuilder extends
		AbstractOWLFrameSectionRowObjectEditor<PatternModel> implements
		OWLFrameSectionRowObjectEditor<PatternModel>, VerifiedInputEditor,
		PatternModelChangeListener {
	private class PatternBuilderActionList extends ActionList {
		public PatternBuilderActionList() {
			super(PatternBuilder.this.owlEditorKit,
					PatternBuilder.this.constraintSystem, true);
		}

		@Override
		protected void handleAdd() {
			final OWLAxiomChangeEditor actionEditor = new OWLAxiomChangeEditor(
					PatternBuilder.this.owlEditorKit,
					PatternBuilder.this.constraintSystem);
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
					PatternBuilder.this.owlEditorKit.getWorkspace(), null);
			// The editor shouldn't be modal (or should it?)
			dlg.setModal(false);
			dlg.setTitle("Action editor");
			dlg.setResizable(true);
			dlg.pack();
			dlg.setLocationRelativeTo(PatternBuilder.this.owlEditorKit
					.getWorkspace());
			dlg.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent e) {
					Object retVal = optionPane.getValue();
					if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
						OWLAxiomChange action = actionEditor
								.getOwlAxiomChange();
						DefaultListModel model = (DefaultListModel) PatternBuilder.this.actionList
								.getModel();
						model.addElement(new PatternBuilderActionListItem(
								action, true, true));
						PatternBuilder.this.handleChange();
					}
					actionEditor
							.removeStatusChangedListener(verificationListener);
					actionEditor.dispose();
				}
			});
			dlg.setVisible(true);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -3297222035586803090L;
	}

	private class PatternBuilderActionListItem extends ActionListItem {
		public PatternBuilderActionListItem(OWLAxiomChange axiomChange,
				boolean isEditable, boolean isDeleteable) {
			super(axiomChange, isEditable, isDeleteable);
		}

		@Override
		public void handleEdit() {
			ConstraintSystem cs = PatternBuilder.this.constraintSystem;
			final OWLAxiomChangeEditor actionEditor = new OWLAxiomChangeEditor(
					PatternBuilder.this.owlEditorKit, cs);
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
					PatternBuilder.this.owlEditorKit.getWorkspace(), null);
			// The editor shouldn't be modal (or should it?)
			dlg.setModal(false);
			dlg.setTitle("Action editor");
			dlg.setResizable(true);
			dlg.pack();
			dlg.setLocationRelativeTo(PatternBuilder.this.owlEditorKit
					.getWorkspace());
			dlg.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent e) {
					Object retVal = optionPane.getValue();
					if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
						OWLAxiomChange action = actionEditor
								.getOwlAxiomChange();
						DefaultListModel model = (DefaultListModel) PatternBuilder.this.actionList
								.getModel();
						model.removeElement(PatternBuilder.this.actionList
								.getSelectedValue());
						model.addElement(new PatternBuilderActionListItem(
								action, true, true));
						PatternBuilder.this.handleChange();
					}
					actionEditor
							.removeStatusChangedListener(verificationListener);
					actionEditor.dispose();
				}
			});
			dlg.setVisible(true);
		}
	}

	private class PatternVariableList extends VariableList implements
			ListDataListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2540053052502672472L;

		@Override
		protected void handleAdd() {
			final AbstractVariableEditor variableEditor = this
					.getSelectedValue() instanceof InputVariableSectionHeader ? new VariableEditor(
					PatternBuilder.this.owlEditorKit,
					PatternBuilder.this.constraintSystem)
					: new GeneratedVariableEditor(
							PatternBuilder.this.owlEditorKit,
							PatternBuilder.this.constraintSystem);
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
					PatternBuilder.this.owlEditorKit.getWorkspace(), null);
			// The editor shouldn't be modal (or should it?)
			dlg.setModal(false);
			dlg.setTitle("Variable editor");
			dlg.setResizable(true);
			dlg.pack();
			dlg.setLocationRelativeTo(PatternBuilder.this.owlEditorKit
					.getWorkspace());
			dlg.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent e) {
					Object retVal = optionPane.getValue();
					if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
						Variable variable = variableEditor.getVariable();
						PatternBuilderVariableListItem listItem = new PatternBuilderVariableListItem(
								variable, PatternBuilder.this.owlEditorKit,
								true, true);
						PatternVariableList.this.placeListItem(listItem);
						PatternBuilder.this.handleChange();
					}
					variableEditor
							.removeStatusChangedListener(verificationListener);
					variableEditor.dispose();
				}
			});
			dlg.setVisible(true);
		}

		public PatternVariableList(OWLEditorKit owlEditorKit) {
			super(owlEditorKit, PatternBuilder.this.constraintSystem);
			((DefaultListModel) this.getModel())
					.addElement(new InputVariableSectionHeader());
			((DefaultListModel) this.getModel())
					.addElement(new GeneratedVariableSectionHeader());
			this.getModel().addListDataListener(this);
		}

		public void contentsChanged(ListDataEvent e) {
			this.updatePatternModel();
		}

		/**
		 * 
		 */
		private void updatePatternModel() {
			ListModel model = this.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				Object element = model.getElementAt(i);
				if (element instanceof PatternBuilderVariableListItem) {
					PatternBuilderVariableListItem item = (PatternBuilderVariableListItem) element;
					if (PatternBuilder.this.patternModel != null) {
						if (!PatternBuilder.this.patternModel.getVariables()
								.contains(item.getVariable())) {
							PatternBuilder.this.patternModel.addVariable(item
									.getVariable());
						}
					}
				}
			}
		}

		public void clear() {
			((DefaultListModel) this.getModel()).clear();
			((DefaultListModel) this.getModel())
					.addElement(new InputVariableSectionHeader());
			((DefaultListModel) this.getModel())
					.addElement(new GeneratedVariableSectionHeader());
		}

		public void intervalAdded(ListDataEvent e) {
			this.updatePatternModel();
		}

		public void intervalRemoved(ListDataEvent e) {
			this.updatePatternModel();
		}

		/**
		 * @param listItem
		 */
		protected void placeListItem(PatternBuilderVariableListItem listItem) {
			DefaultListModel model = (DefaultListModel) PatternVariableList.this
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

	/**
	 * @author Luigi Iannone
	 * 
	 */
	public class PatternBuilderVariableListItem extends VariableListItem {
		/**
		 * @param variable
		 * @param owlEditorKit
		 */
		public PatternBuilderVariableListItem(Variable variable,
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
			ConstraintSystem cs = PatternBuilder.this.constraintSystem;
			final AbstractVariableEditor variableEditor = this.getVariable() instanceof GeneratedVariable ? new GeneratedVariableEditor(
					PatternBuilder.this.owlEditorKit, cs)
					: new VariableEditor(PatternBuilder.this.owlEditorKit, cs);
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
					PatternBuilder.this.owlEditorKit.getWorkspace(), null);
			// The editor shouldn't be modal (or should it?)
			dlg.setModal(false);
			dlg.setTitle("Action editor");
			dlg.setResizable(true);
			dlg.pack();
			dlg.setLocationRelativeTo(PatternBuilder.this.owlEditorKit
					.getWorkspace());
			dlg.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent e) {
					Object retVal = optionPane.getValue();
					if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
						Variable variable = variableEditor.getVariable();
						DefaultListModel model = (DefaultListModel) PatternBuilder.this.variableList
								.getModel();
						model.removeElement(PatternBuilder.this.variableList
								.getSelectedValue());
						PatternBuilder.this.variableList
								.placeListItem(new PatternBuilderVariableListItem(
										variable,
										PatternBuilder.this.owlEditorKit, true,
										true));
						PatternBuilderVariableListItem.this
								.purgeActions(variable);
						PatternBuilder.this.handleChange();
					}
					variableEditor
							.removeStatusChangedListener(verificationListener);
					variableEditor.dispose();
					PatternBuilder.this.handleChange();
				}
			});
			dlg.setVisible(true);
		}

		@Override
		public boolean handleDelete() {
			Variable v = this.getVariable();
			this.purgeActions(v);
			PatternBuilder.this.handleChange();
			return true;
		}

		/**
		 * @param v
		 */
		private void purgeActions(Variable v) {
			DefaultListModel model = (DefaultListModel) PatternBuilder.this.actionList
					.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				Object e = model.getElementAt(i);
				if (e instanceof PatternBuilderActionListItem) {
					OWLAxiomChange action = ((PatternBuilderActionListItem) e)
							.getAxiomChange();
					OWLAxiom axiom = action.getAxiom();
					Set<Variable> axiomVariables = PatternBuilder.this.constraintSystem
							.getAxiomVariables(axiom);
					if (axiomVariables.contains(v)) {
						model.remove(i);
					}
				}
			}
		}
	}

	private class ErrorListCellRenderer implements ListCellRenderer {
		private final DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Component toReturn = this.defaultListCellRenderer
					.getListCellRendererComponent(list, value, index,
							isSelected, cellHasFocus);
			if (toReturn instanceof JLabel) {
				((JLabel) toReturn).setIcon(new ImageIcon(this.getClass()
						.getClassLoader().getResource("error.png")));
			}
			return toReturn;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4071865934355642992L;
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
	private OWLEditorKit owlEditorKit;
	private PatternModel patternModel = null;
	private JPanel mainPanel = new JPanel();
	private PatternVariableList variableList;
	private ActionList actionList;
	private ExpressionEditor<String> nameEditor;
	private ExpressionEditor<String> rendering;
	private JCheckBox allowReturnValueCheckBox;
	private JComboBox returnValuesComboBox;
	private PatternConstraintSystem constraintSystem = PatternParser
			.getPatternModelFactory().createConstraintSystem();
	private final transient ListDataListener actionListListener = new ListDataListener() {
		public void contentsChanged(ListDataEvent e) {
			PatternBuilder.this.handleChange();
		}

		public void intervalAdded(ListDataEvent e) {
			PatternBuilder.this.handleChange();
		}

		public void intervalRemoved(ListDataEvent e) {
			PatternBuilder.this.handleChange();
		}
	};
	private final DefaultListModel errorListModel = new DefaultListModel();
	private final JList errorList = new JList(this.errorListModel);

	public PatternBuilder(OWLEditorKit owlEditorKit) {
		this.mainPanel.setLayout(new BorderLayout());
		this.mainPanel.setName("Pattern Builder");
		JPanel errorPanel = new JPanel(new BorderLayout());
		errorPanel.setBorder(ComponentFactory.createTitledBorder("Errors:"));
		errorPanel.add(ComponentFactory.createScrollPane(this.errorList));
		errorPanel.setPreferredSize(new Dimension(200, 75));
		this.errorList.setCellRenderer(new ErrorListCellRenderer());
		JPanel builderPanel = new JPanel(new BorderLayout());
		this.owlEditorKit = owlEditorKit;
		this.nameEditor = new ExpressionEditor<String>(owlEditorKit,
				new OWLExpressionChecker<String>() {
					private String lastName;

					public void check(String text)
							throws OWLExpressionParserException {
						this.lastName = null;
						if (text.matches("\\w+")) {
							this.lastName = text;
						} else {
							throw new OWLExpressionParserException(
									new Exception("Invalid pattern name "
											+ text));
						}
					}

					public String createObject(String text)
							throws OWLExpressionParserException {
						this.check(text);
						return this.lastName;
					}
				});
		JPanel patternNamePanel = new JPanel(new BorderLayout());
		patternNamePanel.add(this.nameEditor);
		this.nameEditor
				.addStatusChangedListener(new InputVerificationStatusChangedListener() {
					public void verifiedStatusChanged(boolean newState) {
						PatternBuilder.this.handleChange();
					}
				});
		patternNamePanel.setBorder(ComponentFactory
				.createTitledBorder("Pattern name"));
		builderPanel.add(patternNamePanel, BorderLayout.NORTH);
		this.removeKeyListeners();
		JPanel patternBodyPanel = new JPanel(new BorderLayout());
		this.variableList = new PatternVariableList(this.owlEditorKit);
		this.variableList.getModel().addListDataListener(
				new ListDataListener() {
					public void contentsChanged(ListDataEvent e) {
						this.updateReturnValues();
					}

					private void updateReturnValues() {
						DefaultComboBoxModel model = (DefaultComboBoxModel) PatternBuilder.this.returnValuesComboBox
								.getModel();
						model.removeAllElements();
						ListModel variableListModel = PatternBuilder.this.variableList
								.getModel();
						Variable thisClassVariable = PatternBuilder.this.constraintSystem
								.getVariable(PatternConstraintSystem.THIS_CLASS_VARIABLE_CONSTANT_SYMBOL);
						model
								.addElement(new VariableListItem(
										thisClassVariable,
										PatternBuilder.this.owlEditorKit,
										false, false));
						for (int i = 0; i < variableListModel.getSize(); i++) {
							Object e = variableListModel.getElementAt(i);
							if (e instanceof VariableListItem) {
								model.addElement(e);
							}
						}
					}

					public void intervalAdded(ListDataEvent e) {
						this.updateReturnValues();
					}

					public void intervalRemoved(ListDataEvent e) {
						this.updateReturnValues();
					}
				});
		this.actionList = new PatternBuilderActionList();
		this.actionList.getModel().addListDataListener(this.actionListListener);
		JScrollPane variablePane = ComponentFactory
				.createScrollPane(this.variableList);
		patternBodyPanel.add(variablePane, BorderLayout.NORTH);
		patternBodyPanel.add(
				ComponentFactory.createScrollPane(this.actionList),
				BorderLayout.CENTER);
		builderPanel.add(patternBodyPanel, BorderLayout.CENTER);
		this.rendering = new ExpressionEditor<String>(this.owlEditorKit,
				new OWLExpressionChecker<String>() {
					private String renderingString;

					public void check(String text)
							throws OWLExpressionParserException {
						StringTokenizer tokenizer = new StringTokenizer(text);
						while (tokenizer.hasMoreTokens()) {
							String token = tokenizer.nextToken();
							if (token.startsWith("?")) {
								String variableName = token.trim();
								if (PatternBuilder.this.patternModel == null
										&& PatternBuilder.this.constraintSystem
												.getVariable(variableName) == null
										|| PatternBuilder.this.patternModel != null
										&& PatternBuilder.this.patternModel
												.getConstraintSystem()
												.getVariable(variableName) == null) {
									throw new OWLExpressionParserException(
											new InvalidVariableNameException(
													variableName));
								}
							}
						}
						this.renderingString = text;
					}

					public String createObject(String text)
							throws OWLExpressionParserException {
						this.check(text);
						return this.renderingString;
					}
				});
		this.rendering
				.addStatusChangedListener(new InputVerificationStatusChangedListener() {
					public void verifiedStatusChanged(boolean newState) {
						PatternBuilder.this.handleChange();
					}
				});
		JPanel renderingPanelBorder = new JPanel(new BorderLayout());
		renderingPanelBorder.setBorder(ComponentFactory
				.createTitledBorder("Rendering "));
		renderingPanelBorder.add(ComponentFactory
				.createScrollPane(this.rendering));
		JPanel returnPanelBorder = new JPanel(new BorderLayout());
		returnPanelBorder.setBorder(ComponentFactory
				.createTitledBorder("Return"));
		this.allowReturnValueCheckBox = new JCheckBox("Allow Return Value");
		this.returnValuesComboBox = new JComboBox(new DefaultComboBoxModel());
		this.allowReturnValueCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PatternBuilder.this.returnValuesComboBox
						.setEnabled(PatternBuilder.this.allowReturnValueCheckBox
								.isSelected());
			}
		});
		this.returnValuesComboBox.setEnabled(false);
		this.returnValuesComboBox.setRenderer(this.variableList
				.getVariableListCellRenderer());
		this.returnValuesComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object selectedItem = PatternBuilder.this.returnValuesComboBox
						.getSelectedItem();
				if (selectedItem instanceof VariableListItem) {
					PatternBuilder.this.handleChange();
				}
			}
		});
		returnPanelBorder
				.add(this.allowReturnValueCheckBox, BorderLayout.NORTH);
		returnPanelBorder.add(this.returnValuesComboBox, BorderLayout.CENTER);
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(renderingPanelBorder, BorderLayout.NORTH);
		southPanel.add(returnPanelBorder, BorderLayout.CENTER);
		builderPanel.add(southPanel, BorderLayout.SOUTH);
		builderPanel.revalidate();
		this.mainPanel.add(errorPanel, BorderLayout.NORTH);
		this.mainPanel.add(builderPanel, BorderLayout.CENTER);
	}

	/**
	 * @see org.protege.editor.owl.ui.frame.VerifiedInputEditor#addStatusChangedListener(org.protege.editor.owl.ui.frame.InputVerificationStatusChangedListener)
	 */
	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
		this.notifyListener(listener, this.check());
	}

	/**
	 * @see org.protege.editor.owl.ui.frame.VerifiedInputEditor#removeStatusChangedListener(org.protege.editor.owl.ui.frame.InputVerificationStatusChangedListener)
	 */
	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	public PatternModel getPatternModel() {
		return this.patternModel;
	}

	public void handleChange() {
		this.patternModel = null;
		this.errorListModel.clear();
		boolean newState = this.check();
		if (newState) {
			ListModel model = this.variableList.getModel();
			List<Variable> variables = new ArrayList<Variable>();
			for (int i = 0; i < model.getSize(); i++) {
				Object e = model.getElementAt(i);
				if (e instanceof PatternBuilderVariableListItem) {
					variables.add(((PatternBuilderVariableListItem) e)
							.getVariable());
				}
			}
			model = this.actionList.getModel();
			List<OWLAxiomChange> actions = new ArrayList<OWLAxiomChange>();
			for (int i = 0; i < model.getSize(); i++) {
				Object e = model.getElementAt(i);
				if (e instanceof PatternBuilderActionListItem) {
					actions.add(((PatternBuilderActionListItem) e)
							.getAxiomChange());
				}
			}
			try {
				this.patternModel = PatternParser
						.getPatternModelFactory()
						.createPatternModel(this.nameEditor.getText(),
								variables, actions, null,
								this.rendering.getText(), this.constraintSystem);
				if (this.allowReturnValueCheckBox.isSelected()) {
					this.patternModel
							.setReturnVariable(((VariableListItem) this.returnValuesComboBox
									.getSelectedItem()).getVariable());
				}
			} catch (EmptyVariableListException e) {
				this.patternModel = null;
				this.errorListModel.addElement("No variables");
			} catch (EmptyActionListException e) {
				this.patternModel = null;
				this.errorListModel.addElement("No actions");
			} catch (UnsuitableOPPLScriptException e) {
				this.patternModel = null;
				this.errorListModel.addElement("Failed " + e.getMessage());
			}
		} else {
			this.patternModel = null;
		}
		this.notifyListeners(newState);
	}

	/**
	 * @param newState
	 */
	private void notifyListeners(boolean newState) {
		for (InputVerificationStatusChangedListener listener : this.listeners) {
			this.notifyListener(listener, newState);
		}
	}

	/**
	 * @param listener
	 */
	private void notifyListener(
			InputVerificationStatusChangedListener listener, boolean newState) {
		listener.verifiedStatusChanged(newState);
	}

	public void clear() {
		this.nameEditor.setText("");
		this.variableList.clear();
		this.actionList.clear();
		this.actionList.getModel().addListDataListener(this.actionListListener);
		this.constraintSystem = PatternParser.getPatternModelFactory()
				.createConstraintSystem();
		this.rendering.setText("");
	}

	public void dispose() {
	}

	public PatternModel getEditedObject() {
		return this.patternModel;
	}

	public JComponent getEditorComponent() {
		return this.mainPanel;
	}

	/**
	 * @param patternModel
	 *            the patternModel to set
	 */
	public void setPatternModel(PatternModel patternModel) {
		this.clear();
		this.constraintSystem = patternModel.getConstraintSystem();
		this.actionList.setConstraintSystem(this.constraintSystem);
		this.nameEditor.setText(patternModel.getPatternLocalName());
		List<Variable> variables = patternModel.getVariables();
		for (Variable variable : variables) {
			PatternBuilderVariableListItem variableListItem = new PatternBuilderVariableListItem(
					variable, this.owlEditorKit, true, true);
			this.variableList.placeListItem(variableListItem);
		}
		List<OWLAxiomChange> actions = patternModel.getActions();
		DefaultListModel model = (DefaultListModel) this.actionList.getModel();
		for (OWLAxiomChange axiomChange : actions) {
			model.addElement(new PatternBuilderActionListItem(axiomChange,
					true, true));
		}
		this.rendering.setText(patternModel.getRendering());
		Variable returnVariable = patternModel.getReturnVariable();
		if (returnVariable != null) {
			boolean found = false;
			this.allowReturnValueCheckBox.setSelected(true);
			this.returnValuesComboBox.setEnabled(true);
			for (int i = 0; i < this.returnValuesComboBox.getModel().getSize()
					&& !found; i++) {
				Object e = this.returnValuesComboBox.getModel().getElementAt(i);
				found = e instanceof VariableListItem
						&& ((VariableListItem) e).getVariable().equals(
								returnVariable);
				if (found) {
					this.returnValuesComboBox.setSelectedIndex(i);
				}
			}
		} else {
			this.allowReturnValueCheckBox.setSelected(false);
			this.returnValuesComboBox.setSelectedItem(null);
			this.returnValuesComboBox.setEnabled(false);
		}
	}

	/**
	 * 
	 */
	private void removeKeyListeners() {
		KeyListener[] keyListeners = this.nameEditor.getKeyListeners();
		for (KeyListener keyListener : keyListeners) {
			this.nameEditor.removeKeyListener(keyListener);
		}
	}

	private boolean check() {
		try {
			this.nameEditor.createObject();
			// size must be > 2 in variable list and > 1 in the action as
			// section
			// headers must be taken
			// into account
			boolean enoughVariables = this.variableList.getModel().getSize() > 2;
			boolean enoughActions = this.actionList.getModel().getSize() > 1;
			if (!enoughVariables) {
				this.errorListModel.addElement("No Variables");
			}
			if (!enoughActions) {
				this.errorListModel.addElement("No actions");
			}
			return enoughVariables
					&& enoughActions
					&& (!this.allowReturnValueCheckBox.isSelected() || this.returnValuesComboBox
							.getSelectedItem() instanceof VariableListItem);
		} catch (OWLExpressionParserException e) {
			this.constraintSystem = PatternParser.getPatternModelFactory()
					.createConstraintSystem();
			this.errorListModel.addElement("Invalid name");
			return false;
		} catch (OWLException e) {
			this.constraintSystem = PatternParser.getPatternModelFactory()
					.createConstraintSystem();
			this.errorListModel.addElement("Invalid name");
			return false;
		}
	}
}
