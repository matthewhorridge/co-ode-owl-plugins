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
package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.coode.oae.utils.ParserFactory;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.frame.InputVerificationStatusChangedListener;
import org.protege.editor.owl.ui.frame.OWLClassDescriptionEditor;
import org.protege.editor.owl.ui.frame.VerifiedInputEditor;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;

import uk.ac.manchester.mae.ArithmeticsParser;
import uk.ac.manchester.mae.BindingExtractor;
import uk.ac.manchester.mae.ConflictStrategy;
import uk.ac.manchester.mae.ConflictStrategyExtractor;
import uk.ac.manchester.mae.ExceptionStrategy;
import uk.ac.manchester.mae.MAEBinding;
import uk.ac.manchester.mae.MAEStart;
import uk.ac.manchester.mae.OverriddenStrategy;
import uk.ac.manchester.mae.OverridingStrategy;
import uk.ac.manchester.mae.ParseException;
import uk.ac.manchester.mae.ProtegeClassExtractor;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Apr 4, 2008
 */
public class OWLArithmeticFormulaEditor extends
		AbstractOWLFrameSectionRowObjectEditor<MAEStart> implements
		VerifiedInputEditor, InputVerificationStatusChangedListener {
	private static final String FORMULA_BODY_LABEL = "Formula body: ";
	private static final String BINDING_TREE_ROOT_NAME = "BINDINGS";
	private static final String APPLIES_TO_LABEL_NAME = "APPLIES TO";
	private static final String MAIN_PANEL_NAME = "Formula Editor";
	private static final String OVERRIDING_BUTTON_TEXT = "OVERRIDING";
	private static final String OVERRIDDEN_BUTTON_TEXT = "OVERRIDDEN";
	private static final String EXCEPTION_BUTTON_TEXT = "EXCEPTION";
	private OWLEditorKit owlEditorKit;
	private JLabel appliesToLabel = new JLabel();
	private JButton editAppliesToButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"edit.png")));
	private JPanel mainPanel = new JPanel();
	private MAEStart formula = null;
	private FormulaModel formulaModel;
	private OWLDescription owlDescription;
	private boolean canEditAppliesTo;
	private JTree bindingTree;
	private Map<ConflictStrategy, JRadioButton> conflictStrategyRadioButtonMap = new HashMap<ConflictStrategy, JRadioButton>();
	private Map<JRadioButton, ConflictStrategy> radioButtonConflictStrategyMap = new HashMap<JRadioButton, ConflictStrategy>();
	private JLabel arithmeticFormulaLabel = new JLabel(
			OWLArithmeticFormulaEditor.FORMULA_BODY_LABEL);
	private JTextField arithmeticFormulaTextArea = new JTextField();
	private ButtonGroup conflictButtnGroup;
	private JRadioButton overridingButton;
	private JRadioButton overriddenButton;
	private JRadioButton exceptionButton;
	private DefaultTreeModel bindingTreeModel = new DefaultTreeModel(
			new DefaultMutableTreeNode(BINDING_TREE_ROOT_NAME));
	private JButton deleteButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"delete.png")));
	private JButton addButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"add.png")));
	private JButton addDataPropertyButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"property.data.add.png")));
	private JButton addObjectPropertyButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"property.object.add.png")));
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();

	/**
	 * @param owlEditorKit
	 */
	public OWLArithmeticFormulaEditor(OWLEditorKit owlEditorKit,
			OWLDescription appliesToOWLDescription, boolean canEditAppliesTo) {
		this.owlEditorKit = owlEditorKit;
		this.owlDescription = appliesToOWLDescription;
		this.formulaModel = new FormulaModel(owlEditorKit);
		this.canEditAppliesTo = canEditAppliesTo;
		this.init();
		this.mainPanel.revalidate();
	}

	private void init() {
		this.mainPanel.setName(OWLArithmeticFormulaEditor.MAIN_PANEL_NAME);
		BoxLayout boxLayout = new BoxLayout(this.mainPanel, BoxLayout.Y_AXIS);
		this.mainPanel.setLayout(boxLayout);
		this.drawConflictSection();
		this.drawBindingSection();
		this.drawFormulaBodySection();
		this.drawAppliesToSection();
		this.setupSections();
	}

	/**
	 * 
	 */
	private void setupSections() {
		this.setupConflictButtons();
		this.setupAppliesTo();
		this.setupBindings();
		this.setupFormulaBody();
		this.addStatusChangedListener(this);
	}

	/**
	 * 
	 */
	private void drawConflictSection() {
		JPanel conflictSection = new JPanel(new GridLayout(1, 3));
		conflictSection.setBorder(new TitledBorder("conflict Strategy"));
		this.conflictButtnGroup = new ButtonGroup();
		this.overridingButton = new JRadioButton(OVERRIDING_BUTTON_TEXT);
		this.overriddenButton = new JRadioButton(OVERRIDDEN_BUTTON_TEXT);
		this.exceptionButton = new JRadioButton(EXCEPTION_BUTTON_TEXT);
		this.conflictButtnGroup.add(this.overridingButton);
		this.conflictButtnGroup.add(this.overriddenButton);
		this.conflictButtnGroup.add(this.exceptionButton);
		this.conflictStrategyRadioButtonMap.put(OverriddenStrategy
				.getInstance(), this.overriddenButton);
		this.radioButtonConflictStrategyMap.put(this.overriddenButton,
				OverriddenStrategy.getInstance());
		this.conflictStrategyRadioButtonMap.put(OverridingStrategy
				.getInstance(), this.overridingButton);
		this.radioButtonConflictStrategyMap.put(this.overridingButton,
				OverridingStrategy.getInstance());
		this.conflictStrategyRadioButtonMap.put(
				ExceptionStrategy.getInstance(), this.exceptionButton);
		this.radioButtonConflictStrategyMap.put(this.exceptionButton,
				ExceptionStrategy.getInstance());
		conflictSection.add(this.overridingButton);
		conflictSection.add(this.overriddenButton);
		conflictSection.add(this.exceptionButton);
		this.overriddenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ConflictStrategy conflictStrategy = OWLArithmeticFormulaEditor.this.radioButtonConflictStrategyMap
						.get(arg0.getSource());
				OWLArithmeticFormulaEditor.this.formulaModel
						.setConflictStrategy(conflictStrategy);
				OWLArithmeticFormulaEditor.this.handleVerifyEditorContents();
			}
		});
		this.overridingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ConflictStrategy conflictStrategy = OWLArithmeticFormulaEditor.this.radioButtonConflictStrategyMap
						.get(arg0.getSource());
				OWLArithmeticFormulaEditor.this.formulaModel
						.setConflictStrategy(conflictStrategy);
				OWLArithmeticFormulaEditor.this.handleVerifyEditorContents();
			}
		});
		this.exceptionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ConflictStrategy conflictStrategy = OWLArithmeticFormulaEditor.this.radioButtonConflictStrategyMap
						.get(arg0.getSource());
				OWLArithmeticFormulaEditor.this.formulaModel
						.setConflictStrategy(conflictStrategy);
				OWLArithmeticFormulaEditor.this.handleVerifyEditorContents();
			}
		});
		this.mainPanel.add(conflictSection);
	}

	/**
	 * 
	 */
	private void setupFormulaBody() {
		if (this.formula != null) {
			this.arithmeticFormulaTextArea.setText(this.formula.jjtGetChild(
					this.formula.jjtGetNumChildren() - 1).toString()
					+ ";");
		}
	}

	/**
	 * 
	 */
	/**
	 * 
	 */
	private void setupBindings() {
		MutableTreeNode root = new DefaultMutableTreeNode(
				OWLArithmeticFormulaEditor.BINDING_TREE_ROOT_NAME);
		this.bindingTreeModel = new DefaultTreeModel(root);
		this.bindingTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) OWLArithmeticFormulaEditor.this.bindingTree
						.getLastSelectedPathComponent();
				if (node != null) {
					OWLArithmeticFormulaEditor.this.decideButtonEnabling(node);
				}
			}
		});
		if (this.formula != null) {
			BindingExtractor bindingExtractor = new BindingExtractor();
			Set<MAEBinding> extractedBindings = (Set<MAEBinding>) this.formula
					.jjtAccept(bindingExtractor, null);
			for (MAEBinding binding : extractedBindings) {
				MutableTreeNode bindingNode = MAENodeAdapter.toTreeNode(
						binding, this.owlEditorKit.getModelManager());
				this.bindingTreeModel.insertNodeInto(bindingNode, root, 0);
				this.bindingTree.setModel(this.bindingTreeModel);
				this.bindingTree.setCellRenderer(new BindingTreeCellRenderer(
						this.owlEditorKit));
				this.bindingTree.revalidate();
			}
		}
		this.bindingTreeModel.addTreeModelListener(new TreeModelListener() {
			private void updateFormulaModel() {
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) OWLArithmeticFormulaEditor.this.bindingTreeModel
						.getRoot();
				int variableCount = OWLArithmeticFormulaEditor.this.bindingTreeModel
						.getChildCount(root);
				OWLArithmeticFormulaEditor.this.formulaModel.getBindings()
						.clear();
				for (int i = 0; i < variableCount; i++) {
					DefaultMutableTreeNode bindingSubTreeRoot = (DefaultMutableTreeNode) root
							.getChildAt(i);
					BindingModel bindingModel = MAENodeAdapter
							.toBindingModel(bindingSubTreeRoot);
					OWLArithmeticFormulaEditor.this.formulaModel.getBindings()
							.add(bindingModel);
				}
				OWLArithmeticFormulaEditor.this.handleVerifyEditorContents();
			}

			public void treeNodesChanged(TreeModelEvent e) {
				this.updateFormulaModel();
			}

			public void treeNodesInserted(TreeModelEvent e) {
				this.updateFormulaModel();
			}

			public void treeNodesRemoved(TreeModelEvent e) {
				this.updateFormulaModel();
			}

			public void treeStructureChanged(TreeModelEvent e) {
				this.updateFormulaModel();
			}
		});
	}

	protected void decideButtonEnabling(DefaultMutableTreeNode node) {
		Object userObject = node.getUserObject();
		this.addObjectPropertyButton
				.setEnabled(!(userObject instanceof OWLDataProperty)
						&& node.isLeaf() && !node.isRoot());
		this.addDataPropertyButton
				.setEnabled(!(userObject instanceof OWLDataProperty)
						&& node.isLeaf() && !node.isRoot());
		this.deleteButton.setEnabled(node.isLeaf() && !node.isRoot());
		this.addButton.setEnabled(node.isRoot());
	}

	/**
	 * 
	 */
	private void drawBindingSection() {
		JPanel bindingPanel = new JPanel(new BorderLayout());
		bindingPanel.setBorder(LineBorder.createBlackLineBorder());
		this.addButton.setEnabled(false);
		this.addDataPropertyButton.setEnabled(false);
		this.addObjectPropertyButton.setEnabled(false);
		this.deleteButton.setEnabled(false);
		JToolBar toolBar = new JToolBar();
		toolBar.add(this.addButton);
		toolBar.add(this.addDataPropertyButton);
		toolBar.add(this.addObjectPropertyButton);
		toolBar.add(this.deleteButton);
		toolBar.setBorder(null);
		toolBar.setBorderPainted(false);
		bindingPanel.add(toolBar, BorderLayout.NORTH);
		this.bindingTree = new JTree(this.bindingTreeModel);
		JScrollPane bindingTreePane = new JScrollPane(this.bindingTree);
		bindingPanel.add(bindingTreePane, BorderLayout.CENTER);
		this.mainPanel.add(bindingPanel);
	}

	/**
	 * 
	 */
	private void drawFormulaBodySection() {
		JPanel formulaPanel = new JPanel();
		formulaPanel.setBorder(LineBorder.createBlackLineBorder());
		this.arithmeticFormulaTextArea.setColumns(20);
		formulaPanel.add(this.arithmeticFormulaLabel, BorderLayout.WEST);
		formulaPanel.add(this.arithmeticFormulaTextArea, BorderLayout.CENTER);
		this.arithmeticFormulaTextArea.getDocument().addDocumentListener(
				new DocumentListener() {
					private void updateFormulaModel() {
						OWLArithmeticFormulaEditor.this.formulaModel
								.setFormulaBody(OWLArithmeticFormulaEditor.this.arithmeticFormulaTextArea
										.getText());
						OWLArithmeticFormulaEditor.this
								.handleVerifyEditorContents();
					}

					public void changedUpdate(DocumentEvent arg0) {
						this.updateFormulaModel();
					}

					public void insertUpdate(DocumentEvent arg0) {
						this.updateFormulaModel();
					}

					public void removeUpdate(DocumentEvent arg0) {
						this.updateFormulaModel();
					}
				});
		this.addButtonActions();
		this.mainPanel.add(formulaPanel);
	}

	private void addButtonActions() {
		this.deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) OWLArithmeticFormulaEditor.this.bindingTree
						.getLastSelectedPathComponent();
				if (node != null) {
					OWLArithmeticFormulaEditor.this.bindingTreeModel
							.removeNodeFromParent(node);
					OWLArithmeticFormulaEditor.this.deleteButton
							.setEnabled(false);
					OWLArithmeticFormulaEditor.this.addButton.setEnabled(false);
					OWLArithmeticFormulaEditor.this.addDataPropertyButton
							.setEnabled(false);
					OWLArithmeticFormulaEditor.this.addObjectPropertyButton
							.setEnabled(false);
				}
			}
		});
		this.addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newName = JOptionPane.showInputDialog(
						OWLArithmeticFormulaEditor.this.mainPanel, null,
						"Please Input a New Name for the variable binding",
						JOptionPane.QUESTION_MESSAGE);
				if (newName != null) {
					DefaultMutableTreeNode newVariableNameNode = new DefaultMutableTreeNode(
							newName);
					OWLArithmeticFormulaEditor.this.bindingTreeModel
							.insertNodeInto(
									newVariableNameNode,
									(MutableTreeNode) OWLArithmeticFormulaEditor.this.bindingTreeModel
											.getRoot(), 0);
				}
			}
		});
		this.addDataPropertyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OWLModelManagerTree<OWLDataProperty> dataPropertyTree = new OWLModelManagerTree<OWLDataProperty>(
						OWLArithmeticFormulaEditor.this.owlEditorKit,
						OWLArithmeticFormulaEditor.this.owlEditorKit
								.getOWLModelManager()
								.getOWLDataPropertyHierarchyProvider());
				JOptionPane.showMessageDialog(
						OWLArithmeticFormulaEditor.this.mainPanel,
						dataPropertyTree);
				Object selectedProperty = ((DefaultMutableTreeNode) dataPropertyTree
						.getLastSelectedPathComponent()).getUserObject();
				Object currentNode = OWLArithmeticFormulaEditor.this.bindingTree
						.getLastSelectedPathComponent();
				if (selectedProperty != null && currentNode != null) {
					OWLArithmeticFormulaEditor.this.bindingTreeModel
							.insertNodeInto(new DefaultMutableTreeNode(
									selectedProperty),
									(MutableTreeNode) currentNode, 0);
				}
			}
		});
		this.addObjectPropertyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OWLModelManagerTree<OWLObjectProperty> dataPropertyTree = new OWLModelManagerTree<OWLObjectProperty>(
						OWLArithmeticFormulaEditor.this.owlEditorKit,
						OWLArithmeticFormulaEditor.this.owlEditorKit
								.getOWLModelManager()
								.getOWLObjectPropertyHierarchyProvider());
				JOptionPane.showMessageDialog(
						OWLArithmeticFormulaEditor.this.mainPanel,
						dataPropertyTree);
				Object selectedProperty = ((DefaultMutableTreeNode) dataPropertyTree
						.getLastSelectedPathComponent()).getUserObject();
				Object currentNode = OWLArithmeticFormulaEditor.this.bindingTree
						.getLastSelectedPathComponent();
				if (selectedProperty != null && currentNode != null) {
					OWLArithmeticFormulaEditor.this.bindingTreeModel
							.insertNodeInto(new DefaultMutableTreeNode(
									selectedProperty),
									(MutableTreeNode) currentNode, 0);
				}
			}
		});
	}

	/**
	 * 
	 */
	private void setupAppliesTo() {
		OWLDescription appliesToOWLDescription = this.owlDescription;
		if (this.formula != null) {
			ProtegeClassExtractor classExtractor = new ProtegeClassExtractor(
					this.owlEditorKit.getOWLModelManager());
			appliesToOWLDescription = (OWLDescription) this.formula.jjtAccept(
					classExtractor, null);
		}
		this.appliesToLabel.setText(APPLIES_TO_LABEL_NAME + ": "
				+ this.owlDescription != null ? this.owlEditorKit
				.getOWLModelManager().getRendering(appliesToOWLDescription)
				: "");
		this.editAppliesToButton.setEnabled(this.canEditAppliesTo);
	}

	/**
	 * 
	 */
	private void drawAppliesToSection() {
		JPanel appliesToSection = new JPanel();
		appliesToSection.setBorder(new TitledBorder(APPLIES_TO_LABEL_NAME));
		this.editAppliesToButton.setSize(20, 20);
		appliesToSection.add(this.appliesToLabel);
		this.editAppliesToButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OWLClassDescriptionEditor owlDescriptionEditor = new OWLClassDescriptionEditor(
						OWLArithmeticFormulaEditor.this.owlEditorKit,
						OWLArithmeticFormulaEditor.this.formulaModel
								.getAppliesTo());
				final JComponent editorComponent = owlDescriptionEditor
						.getEditorComponent();
				final VerifyingOptionPane optionPane = new VerifyingOptionPane(
						editorComponent);
				final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
					public void verifiedStatusChanged(boolean verified) {
						optionPane.setOKEnabled(verified);
					}
				};
				// if the editor is verifying, will need to prevent the OK
				// button from being available
				if (owlDescriptionEditor instanceof VerifiedInputEditor) {
					//			            
					((VerifiedInputEditor) owlDescriptionEditor)
							.addStatusChangedListener(verificationListener);
				}
				final JDialog dlg = optionPane.createDialog(
						OWLArithmeticFormulaEditor.this.mainPanel, null);
				dlg.setModal(true);
				dlg.setResizable(true);
				dlg.pack();
				dlg
						.setLocationRelativeTo(OWLArithmeticFormulaEditor.this.mainPanel);
				dlg.setVisible(true);
				OWLDescription editedObject = owlDescriptionEditor
						.getEditedObject();
				OWLArithmeticFormulaEditor.this.formulaModel
						.setAppliesTo(editedObject);
				String rendering = editedObject != null ? OWLArithmeticFormulaEditor.this.owlEditorKit
						.getOWLModelManager().getOWLObjectRenderer().render(
								editedObject,
								OWLArithmeticFormulaEditor.this.owlEditorKit
										.getOWLModelManager()
										.getOWLEntityRenderer())
						: "";
				OWLArithmeticFormulaEditor.this.appliesToLabel
						.setText(rendering);
				OWLArithmeticFormulaEditor.this.handleVerifyEditorContents();
			}
		});
		appliesToSection.add(this.editAppliesToButton);
		if (this.canEditAppliesTo) {
			this.mainPanel.add(appliesToSection);
		}
	}

	/**
	 * 
	 */
	private void setupConflictButtons() {
		if (this.formula != null
				&& this.formulaModel.getConflictStrategy() != null) {
			ConflictStrategyExtractor conflictStrategyExtractor = new ConflictStrategyExtractor();
			ConflictStrategy strategy = (ConflictStrategy) this.formula
					.jjtAccept(conflictStrategyExtractor, null);
			this.conflictButtnGroup.setSelected(
					this.conflictStrategyRadioButtonMap.get(strategy)
							.getModel(), true);
		} else {
			this.conflictButtnGroup.setSelected(this.overriddenButton
					.getModel(), true);
		}
	}

	public void clear() {
		this.formula = null;
	}

	public void dispose() {
		// TODO Auto-generated method stub
	}

	public JComponent getEditorComponent() {
		return this.mainPanel;
	}

	public MAEStart getEditedObject() {
		return this.formula;
	}

	public void setFormula(MAEStart formula) {
		this.formula = formula;
		this.formulaModel = MAENodeAdapter.toFormulaModel(formula,
				this.owlEditorKit);
		this.setupSections();
	}

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
		this.handleVerifyEditorContents();
	}

	private void handleVerifyEditorContents() {
		if (!this.listeners.isEmpty()) {
			boolean validated = this.validateBindings()
					&& this.validateFormulaBody();
			for (InputVerificationStatusChangedListener l : this.listeners) {
				l.verifiedStatusChanged(validated);
			}
			if (validated) {
				try {
					this.formula = MAENodeAdapter.toFormula(this.formulaModel);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean validateFormulaBody() {
		String formulaBody = this.arithmeticFormulaTextArea.getText();
		if (formulaBody != null && formulaBody.length() > 0) {
			ParserFactory.initParser(formulaBody);
			try {
				ArithmeticsParser.Start();
				return true;
			} catch (ParseException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean validateBindings() {
		boolean toReturn = false;
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.bindingTreeModel
				.getRoot();
		toReturn = root.isLeaf();
		if (!toReturn) {
			int variableCount = root.getChildCount();
			for (int i = 0; i < variableCount; i++) {
				DefaultMutableTreeNode variableNode = (DefaultMutableTreeNode) root
						.getChildAt(i);
				toReturn = variableNode.getChildCount() == 1
						&& this
								.validatePropertyChain((DefaultMutableTreeNode) variableNode
										.getChildAt(0));
			}
		}
		return toReturn;
	}

	private boolean validatePropertyChain(
			DefaultMutableTreeNode propertychainNode) {
		return propertychainNode.isLeaf()
				&& propertychainNode.getUserObject() instanceof OWLDataProperty
				|| !propertychainNode.isLeaf()
				&& propertychainNode.getChildCount() == 1
				&& this
						.validatePropertyChain((DefaultMutableTreeNode) propertychainNode
								.getChildAt(0));
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	public void verifiedStatusChanged(boolean valid) {
		if (valid) {
			try {
				this.formula = MAENodeAdapter.toFormula(this.formulaModel);
			} catch (ParseException e) {
				this.formula = null;
			}
		} else {
			this.formula = null;
		}
	}
}
