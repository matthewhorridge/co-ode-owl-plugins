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
import java.awt.Color;
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
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.AbstractOWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLClassDescriptionEditor;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;

import uk.ac.manchester.mae.ConflictStrategy;
import uk.ac.manchester.mae.Constants;
import uk.ac.manchester.mae.ExceptionStrategy;
import uk.ac.manchester.mae.OverriddenStrategy;
import uk.ac.manchester.mae.OverridingStrategy;
import uk.ac.manchester.mae.evaluation.BindingModel;
import uk.ac.manchester.mae.evaluation.FormulaModel;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;
import uk.ac.manchester.mae.evaluation.StorageModel;
import uk.ac.manchester.mae.parser.ArithmeticsParser;
import uk.ac.manchester.mae.parser.MAEStart;
import uk.ac.manchester.mae.parser.MAEpropertyChainExpression;
import uk.ac.manchester.mae.parser.ParseException;
import uk.ac.manchester.mae.visitor.ConflictStrategyExtractor;
import uk.ac.manchester.mae.visitor.StorageExtractor;
import uk.ac.manchester.mae.visitor.protege.ProtegeBindingExtractor;
import uk.ac.manchester.mae.visitor.protege.ProtegeClassExtractor;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 4, 2008
 */
public class OWLArithmeticFormulaEditor extends
        AbstractOWLObjectEditor<MAEStart> implements
		VerifiedInputEditor, InputVerificationStatusChangedListener {
	private static final String FORMULA_BODY_LABEL = "Formula body: ";
	private static final String BINDING_TREE_ROOT_NAME = "BINDINGS";
	private static final String APPLIES_TO_LABEL_NAME = "APPLIES TO";
	private static final String MAIN_PANEL_NAME = "Formula Editor";
	private static final String OVERRIDING_BUTTON_TEXT = "OVERRIDING";
	private static final String OVERRIDDEN_BUTTON_TEXT = "OVERRIDDEN";
	private static final String EXCEPTION_BUTTON_TEXT = "EXCEPTION";
	private static final String STORE_TREE_ROOT_NAME = "STORE";
	protected OWLEditorKit owlEditorKit;
	protected JLabel appliesToLabel = new JLabel();
	protected JButton editAppliesToButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"edit.png")));
	protected JTextField formulaURITextField = ComponentFactory
			.createTextField();
	protected JPanel mainPanel = new JPanel();
	private MAEStart formula = null;
	protected FormulaModel formulaModel;
    private OWLClassExpression owlDescription;
	private boolean canEditAppliesTo;
	protected JTree bindingTree, storeTree;
	private Map<ConflictStrategy, JRadioButton> conflictStrategyRadioButtonMap = new HashMap<ConflictStrategy, JRadioButton>();
	protected Map<JRadioButton, ConflictStrategy> radioButtonConflictStrategyMap = new HashMap<JRadioButton, ConflictStrategy>();
	private JLabel arithmeticFormulaLabel = new JLabel(
			OWLArithmeticFormulaEditor.FORMULA_BODY_LABEL);
	protected JTextField arithmeticFormulaTextArea = ComponentFactory
			.createTextField();
	private ButtonGroup conflictButtnGroup;
	private JRadioButton overridingButton;
	private JRadioButton overriddenButton;
	private JRadioButton exceptionButton;
	protected DefaultTreeModel bindingTreeModel = new DefaultTreeModel(
			new DefaultMutableTreeNode(BINDING_TREE_ROOT_NAME));
	protected JButton deleteBindingButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"delete.png")));
	protected JButton addBindingButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"add.png")));
	protected JButton addBindingFacetButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"class.defined.add.png")));
	protected JButton addDataPropertyBindingButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"property.data.add.png")));
	protected JButton addObjectPropertyBindingButton = new JButton(
			new ImageIcon(OWLArithmeticFormulaEditor.class.getClassLoader()
					.getResource("property.object.add.png")));
	protected DefaultTreeModel storeTreeModel = new DefaultTreeModel(
			new DefaultMutableTreeNode(STORE_TREE_ROOT_NAME));
	protected JButton deleteStoreButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"delete.png")));
	protected JButton addStoreFacetButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"class.defined.add.png")));
	protected JButton addObjectPropertyStoreButton = new JButton(new ImageIcon(
			OWLArithmeticFormulaEditor.class.getClassLoader().getResource(
					"property.object.add.png")));
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
    private Map<MAEStart, IRI> formulaURIMap = new HashMap<MAEStart, IRI>();

	/**
	 * @param owlEditorKit
	 * @param formulaAnnotationURIs
	 */
	public OWLArithmeticFormulaEditor(OWLEditorKit owlEditorKit,
            OWLClassExpression appliesToOWLClassExpression,
            boolean canEditAppliesTo,
 Map<MAEStart, IRI> formulaAnnotationURIs) {
		this.owlEditorKit = owlEditorKit;
        owlDescription = appliesToOWLClassExpression;
		formulaModel = new FormulaModel();
		this.canEditAppliesTo = canEditAppliesTo;
		init();
		mainPanel.revalidate();
		formulaURIMap = formulaAnnotationURIs;
	}

	public OWLArithmeticFormulaEditor(OWLEditorKit editorKit,
			OWLClass rootObject, boolean b) {
		this(editorKit, rootObject, b, null);
	}

	private void init() {
		mainPanel.setName(OWLArithmeticFormulaEditor.MAIN_PANEL_NAME);
		BoxLayout boxLayout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
		mainPanel.setLayout(boxLayout);
		drawFormulaURISection();
		drawConflictSection();
		drawBindingSection();
		drawStorageSection();
		drawFormulaBodySection();
		drawAppliesToSection();
		setupSections();
	}

	private void drawFormulaURISection() {
		JPanel formulaURIPanel = new JPanel(new BorderLayout());
		formulaURIPanel.setBorder(ComponentFactory
				.createTitledBorder("Formula URI:"));
		formulaURIPanel.add(formulaURITextField);
		formulaURITextField.getDocument().addDocumentListener(
				new DocumentListener() {
					private void updateFormulaModel() {
						formulaModel
.setFormulaURI(IRI
										.create(formulaURITextField
												.getText()));
						OWLArithmeticFormulaEditor.this
								.handleVerifyEditorContents();
					}

					@Override
                    public void changedUpdate(DocumentEvent arg0) {
						updateFormulaModel();
					}

					@Override
                    public void insertUpdate(DocumentEvent arg0) {
						updateFormulaModel();
					}

					@Override
                    public void removeUpdate(DocumentEvent arg0) {
						updateFormulaModel();
					}
				});
		mainPanel.add(formulaURIPanel);
	}

	/**
	 * 
	 */
	private void setupSections() {
		setupFormulaURISection();
		setupConflictButtons();
		setupAppliesTo();
		setupBindings();
		setupStorage();
		setupFormulaBody();
		addStatusChangedListener(this);
	}

	private void setupFormulaURISection() {
        IRI formulaURI = formulaModel.getFormulaURI();
		if (formulaURI != null) {
			formulaURITextField.setText(formulaURI.toString());
		} else {
			formulaURITextField
					.setText(Constants.FORMULA_NAMESPACE_URI_STRING);
		}
        formulaModel.setFormulaURI(IRI.create(formulaURITextField
				.getText()));
	}

	/**
	 * 
	 */
	private void drawConflictSection() {
		JPanel conflictSection = new JPanel(new GridLayout(1, 3));
		conflictSection.setBorder(ComponentFactory
				.createTitledBorder("conflict Strategy"));
		conflictButtnGroup = new ButtonGroup();
		overridingButton = new JRadioButton(OVERRIDING_BUTTON_TEXT);
		overriddenButton = new JRadioButton(OVERRIDDEN_BUTTON_TEXT);
		exceptionButton = new JRadioButton(EXCEPTION_BUTTON_TEXT);
		conflictButtnGroup.add(overridingButton);
		conflictButtnGroup.add(overriddenButton);
		conflictButtnGroup.add(exceptionButton);
		conflictStrategyRadioButtonMap.put(OverriddenStrategy
				.getInstance(), overriddenButton);
		radioButtonConflictStrategyMap.put(overriddenButton,
				OverriddenStrategy.getInstance());
		conflictStrategyRadioButtonMap.put(OverridingStrategy
				.getInstance(), overridingButton);
		radioButtonConflictStrategyMap.put(overridingButton,
				OverridingStrategy.getInstance());
		conflictStrategyRadioButtonMap.put(
				ExceptionStrategy.getInstance(), exceptionButton);
		radioButtonConflictStrategyMap.put(exceptionButton,
				ExceptionStrategy.getInstance());
		conflictSection.add(overridingButton);
		conflictSection.add(overriddenButton);
		conflictSection.add(exceptionButton);
		overriddenButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent arg0) {
				ConflictStrategy conflictStrategy = radioButtonConflictStrategyMap
						.get(arg0.getSource());
				formulaModel
						.setConflictStrategy(conflictStrategy);
				OWLArithmeticFormulaEditor.this.handleVerifyEditorContents();
			}
		});
		overridingButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent arg0) {
				ConflictStrategy conflictStrategy = radioButtonConflictStrategyMap
						.get(arg0.getSource());
				formulaModel
						.setConflictStrategy(conflictStrategy);
				OWLArithmeticFormulaEditor.this.handleVerifyEditorContents();
			}
		});
		exceptionButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent arg0) {
				ConflictStrategy conflictStrategy = radioButtonConflictStrategyMap
						.get(arg0.getSource());
				formulaModel
						.setConflictStrategy(conflictStrategy);
				OWLArithmeticFormulaEditor.this.handleVerifyEditorContents();
			}
		});
		mainPanel.add(conflictSection);
	}

	/**
	 * 
	 */
	private void setupFormulaBody() {
		if (formula != null || formulaModel != null) {
			String formulaBodyString = formula != null ? formula
					.jjtGetChild(formula.jjtGetNumChildren() - 1)
					.toString()
					+ ";" : formulaModel.getFormulaBody();
			arithmeticFormulaTextArea.setText(formulaBodyString);
		}
	}

	private void setupStorage() {
		MutableTreeNode root = new DefaultMutableTreeNode(
				OWLArithmeticFormulaEditor.STORE_TREE_ROOT_NAME);
		storeTreeModel = new DefaultTreeModel(root);
		storeTree.setModel(storeTreeModel);
		storeTree.setCellRenderer(new StorageTreeCellRenderer(
				owlEditorKit));
		storeTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
            public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) storeTree
						.getLastSelectedPathComponent();
				if (node != null) {
					OWLArithmeticFormulaEditor.this
							.decideStoreButtonEnabling(node);
				}
			}
		});
		if (formula != null) {
			StorageExtractor bindingExtractor = new StorageExtractor();
			formula.jjtAccept(bindingExtractor, null);
			MAEpropertyChainExpression storeToNode = bindingExtractor
					.getExtractedStorage();
			if (storeToNode != null) {
				MutableTreeNode storageNode = MAENodeAdapter.toTreeNode(
						storeToNode, owlEditorKit.getModelManager());
				storeTreeModel.insertNodeInto(storageNode, root, 0);
			}
			storeTree.revalidate();
		} else if (formulaModel != null) {
			StorageModel storageModel = formulaModel.getStorageModel();
			if (storageModel != null) {
				MutableTreeNode storageNode = MAENodeAdapter.toTreeNode(
						storageModel, owlEditorKit.getModelManager());
				storeTreeModel.setRoot(storageNode);
			}
		}
		storeTreeModel.addTreeModelListener(new TreeModelListener() {
			private void updateFormulaModel() {
				DefaultMutableTreeNode currentRoot = (DefaultMutableTreeNode) storeTreeModel
						.getRoot();
				StorageModel storageModel = null;
				if (!currentRoot.isLeaf()) {
					DefaultMutableTreeNode storageSubTreeRoot = (DefaultMutableTreeNode) currentRoot
							.getChildAt(0);
					storageModel = MAENodeAdapter
							.toStorageModel(storageSubTreeRoot);
				}
				formulaModel
						.setStorageModel(storageModel);
				OWLArithmeticFormulaEditor.this.handleVerifyEditorContents();
			}

			@Override
            public void treeNodesChanged(TreeModelEvent e) {
				updateFormulaModel();
			}

			@Override
            public void treeNodesInserted(TreeModelEvent e) {
				updateFormulaModel();
			}

			@Override
            public void treeNodesRemoved(TreeModelEvent e) {
				updateFormulaModel();
			}

			@Override
            public void treeStructureChanged(TreeModelEvent e) {
				updateFormulaModel();
			}
		});
	}

	/**
	 * 
	 */
	@SuppressWarnings( { "unchecked" })
	private void setupBindings() {
		MutableTreeNode root = new DefaultMutableTreeNode(
				OWLArithmeticFormulaEditor.BINDING_TREE_ROOT_NAME);
		bindingTreeModel = new DefaultTreeModel(root);
		bindingTree.setModel(bindingTreeModel);
		bindingTree.setCellRenderer(new BindingTreeCellRenderer(
				owlEditorKit));
		bindingTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
            public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) bindingTree
						.getLastSelectedPathComponent();
				if (node != null) {
					OWLArithmeticFormulaEditor.this
							.decideBindingButtonEnabling(node);
				}
			}
		});
		if (formula != null) {
			ProtegeBindingExtractor bindingExtractor = new ProtegeBindingExtractor(
					owlEditorKit.getModelManager());
			Set<BindingModel> extractedBindings = (Set<BindingModel>) formula
					.jjtAccept(bindingExtractor, null);
			for (BindingModel binding : extractedBindings) {
				MutableTreeNode bindingNode = MAENodeAdapter.toTreeNode(
						binding, owlEditorKit.getModelManager());
				bindingTreeModel.insertNodeInto(bindingNode, root, 0);
				bindingTree.revalidate();
			}
		} else if (formulaModel != null) {
			Set<BindingModel> bindings = formulaModel.getBindings();
			for (BindingModel binding : bindings) {
				MutableTreeNode bindingNode = MAENodeAdapter.toTreeNode(
						binding, owlEditorKit.getModelManager());
				bindingTreeModel.insertNodeInto(bindingNode, root, 0);
				bindingTree.revalidate();
			}
		}
		bindingTreeModel.addTreeModelListener(new TreeModelListener() {
			private void updateFormulaModel() {
				DefaultMutableTreeNode currentRoot = (DefaultMutableTreeNode) bindingTreeModel
						.getRoot();
				int variableCount = bindingTreeModel
						.getChildCount(currentRoot);
				formulaModel.getBindings()
						.clear();
				for (int i = 0; i < variableCount; i++) {
					DefaultMutableTreeNode bindingSubTreeRoot = (DefaultMutableTreeNode) currentRoot
							.getChildAt(i);
					BindingModel bindingModel = MAENodeAdapter
							.toBindingModel(bindingSubTreeRoot);
					formulaModel.getBindings()
							.add(bindingModel);
				}
				OWLArithmeticFormulaEditor.this.handleVerifyEditorContents();
			}

			@Override
            public void treeNodesChanged(TreeModelEvent e) {
				updateFormulaModel();
			}

			@Override
            public void treeNodesInserted(TreeModelEvent e) {
				updateFormulaModel();
			}

			@Override
            public void treeNodesRemoved(TreeModelEvent e) {
				updateFormulaModel();
			}

			@Override
            public void treeStructureChanged(TreeModelEvent e) {
				updateFormulaModel();
			}
		});
	}

	protected void decideBindingButtonEnabling(DefaultMutableTreeNode node) {
		Object userObject = node.getUserObject();
		addObjectPropertyBindingButton
				.setEnabled(!(userObject instanceof PropertyChainModel && ((PropertyChainModel) userObject)
						.getProperty() instanceof OWLDataProperty)
						&& node.isLeaf() && !node.isRoot());
		addBindingFacetButton
				.setEnabled(userObject instanceof PropertyChainModel
						&& ((PropertyChainModel) userObject).getProperty() instanceof OWLObjectProperty);
		addDataPropertyBindingButton
				.setEnabled(!(userObject instanceof PropertyChainModel && ((PropertyChainModel) userObject)
						.getProperty() instanceof OWLDataProperty)
						&& node.isLeaf() && !node.isRoot());
		deleteBindingButton.setEnabled(node.isLeaf() && !node.isRoot());
		addBindingButton.setEnabled(node.isRoot());
	}

	protected void decideStoreButtonEnabling(DefaultMutableTreeNode node) {
		addObjectPropertyStoreButton.setEnabled(true);
		addStoreFacetButton
				.setEnabled(node.getUserObject() instanceof PropertyChainModel
						&& ((PropertyChainModel) node.getUserObject())
								.getProperty() instanceof OWLObjectProperty);
		deleteStoreButton.setEnabled(node.isLeaf() && !node.isRoot());
	}

	/**
	 * 
	 */
	private void drawBindingSection() {
		JPanel bindingPanel = new JPanel(new BorderLayout());
		bindingPanel.setBorder(LineBorder.createBlackLineBorder());
		addBindingButton.setEnabled(false);
		addDataPropertyBindingButton.setEnabled(false);
		addObjectPropertyBindingButton.setEnabled(false);
		addBindingFacetButton.setEnabled(false);
		deleteBindingButton.setEnabled(false);
		JToolBar toolBar = new JToolBar();
		toolBar.add(addBindingButton);
		toolBar.add(addDataPropertyBindingButton);
		toolBar.add(addObjectPropertyBindingButton);
		toolBar.add(addBindingFacetButton);
		toolBar.add(deleteBindingButton);
		toolBar.setBorder(null);
		toolBar.setBorderPainted(false);
		bindingPanel.add(toolBar, BorderLayout.NORTH);
		bindingTree = new JTree(bindingTreeModel);
		JScrollPane bindingTreePane = ComponentFactory
				.createScrollPane(bindingTree);
		bindingPanel.add(bindingTreePane, BorderLayout.CENTER);
		mainPanel.add(bindingPanel);
	}

	/**
	 * 
	 */
	private void drawStorageSection() {
		JPanel storePanel = new JPanel(new BorderLayout());
		storePanel.setBorder(LineBorder.createBlackLineBorder());
		addObjectPropertyStoreButton.setEnabled(false);
		addStoreFacetButton.setEnabled(false);
		deleteStoreButton.setEnabled(false);
		JToolBar storeToolBar = new JToolBar();
		storeToolBar.add(addObjectPropertyStoreButton);
		storeToolBar.add(addStoreFacetButton);
		storeToolBar.add(deleteStoreButton);
		storeToolBar.setBorder(null);
		storeToolBar.setBorderPainted(false);
		storePanel.add(storeToolBar, BorderLayout.NORTH);
		storeTree = new JTree(storeTreeModel);
		JScrollPane storeTreePane = ComponentFactory
				.createScrollPane(storeTree);
		storePanel.add(storeTreePane, BorderLayout.CENTER);
		mainPanel.add(storePanel);
	}

	/**
	 * 
	 */
	private void drawFormulaBodySection() {
		JPanel formulaPanel = new JPanel();
		formulaPanel.setBorder(LineBorder.createBlackLineBorder());
		arithmeticFormulaTextArea.setColumns(20);
		formulaPanel.add(arithmeticFormulaLabel, BorderLayout.WEST);
		formulaPanel.add(arithmeticFormulaTextArea, BorderLayout.CENTER);
		arithmeticFormulaTextArea.getDocument().addDocumentListener(
				new DocumentListener() {
					private void updateFormulaModel() {
						formulaModel
								.setFormulaBody(arithmeticFormulaTextArea
										.getText());
						OWLArithmeticFormulaEditor.this
								.handleVerifyEditorContents();
					}

					@Override
                    public void changedUpdate(DocumentEvent arg0) {
						updateFormulaModel();
					}

					@Override
                    public void insertUpdate(DocumentEvent arg0) {
						updateFormulaModel();
					}

					@Override
                    public void removeUpdate(DocumentEvent arg0) {
						updateFormulaModel();
					}
				});
		addButtonActions();
		mainPanel.add(formulaPanel);
	}

	private void addButtonActions() {
		addBindingButtons();
		addStorageButtons();
	}

	/**
	 * 
	 */
	private void addBindingButtons() {
		deleteBindingButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent ae) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) bindingTree
						.getLastSelectedPathComponent();
				if (node != null) {
					bindingTreeModel
							.removeNodeFromParent(node);
					deleteBindingButton
							.setEnabled(false);
					addBindingButton
							.setEnabled(false);
					addDataPropertyBindingButton
							.setEnabled(false);
					addObjectPropertyBindingButton
							.setEnabled(false);
					bindingTree.revalidate();
				}
			}
		});
		addBindingButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
				String newName = JOptionPane.showInputDialog(
						mainPanel, null,
						"Please Input a New Name for the variable binding",
						JOptionPane.QUESTION_MESSAGE);
				if (newName != null) {
					DefaultMutableTreeNode newVariableNameNode = new DefaultMutableTreeNode(
							newName);
					bindingTreeModel
							.insertNodeInto(
									newVariableNameNode,
									(MutableTreeNode) bindingTreeModel
											.getRoot(), 0);
					bindingTree.revalidate();
				}
			}
		});
		addDataPropertyBindingButton
				.addActionListener(new ActionListener() {
					@Override
                    @SuppressWarnings("unchecked")
					public void actionPerformed(ActionEvent e) {
						OWLModelManagerTree<OWLDataProperty> dataPropertyTree = new OWLModelManagerTree<OWLDataProperty>(
								owlEditorKit,
								owlEditorKit
										.getModelManager()
										.getOWLHierarchyManager()
										.getOWLDataPropertyHierarchyProvider());
						JScrollPane panel = ComponentFactory
								.createScrollPane(dataPropertyTree);
						panel.setBorder(LineBorder.createBlackLineBorder());
						JOptionPane jOptionPane = new JOptionPane(panel,
								JOptionPane.QUESTION_MESSAGE,
								JOptionPane.OK_CANCEL_OPTION, new ImageIcon(
										this.getClass().getClassLoader()
												.getResource(
														"property.data.png")));
						JDialog jDialog = jOptionPane.createDialog(
								mainPanel,
								"data property");
						jDialog.pack();
						jDialog.setVisible(true);
						if ((DefaultMutableTreeNode) dataPropertyTree
								.getLastSelectedPathComponent() != null) {
							Object selectedProperty = ((DefaultMutableTreeNode) dataPropertyTree
									.getLastSelectedPathComponent())
									.getUserObject();
							Object currentNode = bindingTree
									.getLastSelectedPathComponent();
							bindingTreeModel
									.insertNodeInto(
											new DefaultMutableTreeNode(
													new PropertyChainModel(
															(OWLProperty) selectedProperty,
															null)),
											(MutableTreeNode) currentNode, 0);
							bindingTree
									.revalidate();
							bindingTree
									.expandPath(bindingTree
											.getLeadSelectionPath());
						}
					}
				});
		addObjectPropertyBindingButton
				.addActionListener(new ActionListener() {
					@Override
                    @SuppressWarnings("unchecked")
					public void actionPerformed(ActionEvent e) {
						OWLModelManagerTree<OWLObjectProperty> objectPropertyTree = new OWLModelManagerTree<OWLObjectProperty>(
								owlEditorKit,
								owlEditorKit
										.getModelManager()
										.getOWLHierarchyManager()
										.getOWLObjectPropertyHierarchyProvider());
						JScrollPane panel = ComponentFactory
								.createScrollPane(objectPropertyTree);
						panel.setBorder(LineBorder.createBlackLineBorder());
						JOptionPane jOptionPane = new JOptionPane(panel,
								JOptionPane.QUESTION_MESSAGE,
								JOptionPane.OK_CANCEL_OPTION, new ImageIcon(
										this.getClass().getClassLoader()
												.getResource(
														"property.object.png")));
						JDialog jDialog = jOptionPane.createDialog(
								mainPanel,
								"object property");
						jDialog.pack();
						jDialog.setVisible(true);
						if ((DefaultMutableTreeNode) objectPropertyTree
								.getLastSelectedPathComponent() != null) {
							Object selectedProperty = ((DefaultMutableTreeNode) objectPropertyTree
									.getLastSelectedPathComponent())
									.getUserObject();
							Object currentNode = bindingTree
									.getLastSelectedPathComponent();
							bindingTreeModel
									.insertNodeInto(
											new DefaultMutableTreeNode(
													new PropertyChainModel(
															(OWLProperty) selectedProperty,
															null)),
											(MutableTreeNode) currentNode, 0);
							bindingTree
									.expandPath(bindingTree
											.getLeadSelectionPath());
							bindingTree
									.revalidate();
						}
					}
				});
		addBindingFacetButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) bindingTree
						.getLastSelectedPathComponent();
				if (treeNode != null
						&& treeNode.getUserObject() instanceof PropertyChainModel) {
					PropertyChainModel currentPropertyChainModel = (PropertyChainModel) treeNode
							.getUserObject();
                    OWLClassExpression initialFacetDescription = currentPropertyChainModel
							.getFacet();
					OWLClassDescriptionEditor owlDescriptionEditor = new OWLClassDescriptionEditor(
							owlEditorKit,
							initialFacetDescription);
					final JComponent editorComponent = owlDescriptionEditor
							.getEditorComponent();
					final VerifyingOptionPane optionPane = new VerifyingOptionPane(
							editorComponent);
					final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
						@Override
                        public void verifiedStatusChanged(boolean verified) {
							optionPane.setOKEnabled(verified);
						}
					};
					// if the editor is verifying, will need to prevent the OK
					// button from being available
					//			            
					owlDescriptionEditor
							.addStatusChangedListener(verificationListener);
					final JDialog dlg = optionPane.createDialog(
							mainPanel, null);
					dlg.setModal(true);
					dlg.setResizable(true);
					dlg.pack();
					dlg
							.setLocationRelativeTo(mainPanel);
					dlg.setVisible(true);
                    Set<OWLClassExpression> editedObjects = owlDescriptionEditor
							.getEditedObjects();
					if (editedObjects != null && editedObjects.size() > 0) {
						currentPropertyChainModel.setFacet(editedObjects
								.iterator().next());
					}
					OWLArithmeticFormulaEditor.this
							.handleVerifyEditorContents();
				}
			}
		});
	}

	private void addStorageButtons() {
		deleteStoreButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent ae) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) storeTree
						.getLastSelectedPathComponent();
				if (node != null) {
					storeTreeModel
							.removeNodeFromParent(node);
					deleteStoreButton
							.setEnabled(false);
					addObjectPropertyStoreButton
							.setEnabled(false);
					storeTree.revalidate();
				}
			}
		});
		addObjectPropertyStoreButton
				.addActionListener(new ActionListener() {
					@Override
                    @SuppressWarnings("unchecked")
					public void actionPerformed(ActionEvent e) {
						OWLModelManagerTree<OWLObjectProperty> objectPropertyTree = new OWLModelManagerTree<OWLObjectProperty>(
								owlEditorKit,
								owlEditorKit
										.getModelManager()
										.getOWLHierarchyManager()
										.getOWLObjectPropertyHierarchyProvider());
						JScrollPane panel = ComponentFactory
								.createScrollPane(objectPropertyTree);
						panel.setBorder(LineBorder.createBlackLineBorder());
						JOptionPane jOptionPane = new JOptionPane(panel,
								JOptionPane.QUESTION_MESSAGE,
								JOptionPane.OK_CANCEL_OPTION, new ImageIcon(
										this.getClass().getClassLoader()
												.getResource(
														"property.object.png")));
						JDialog jDialog = jOptionPane.createDialog(
								mainPanel,
								"object property");
						jDialog.pack();
						jDialog.setVisible(true);
						if ((DefaultMutableTreeNode) objectPropertyTree
								.getLastSelectedPathComponent() != null) {
							DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) storeTree
									.getLastSelectedPathComponent();
							Object selectedProperty = ((DefaultMutableTreeNode) objectPropertyTree
									.getLastSelectedPathComponent())
									.getUserObject();
							storeTreeModel
									.insertNodeInto(
											new DefaultMutableTreeNode(
													new PropertyChainModel(
															(OWLProperty) selectedProperty,
															null)),
											currentNode, 0);
							storeTree
									.revalidate();
						}
					}
				});
		addStoreFacetButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) storeTree
						.getLastSelectedPathComponent();
				if (treeNode != null
						&& treeNode.getUserObject() instanceof PropertyChainModel) {
					PropertyChainModel currentPropertyChainModel = (PropertyChainModel) treeNode
							.getUserObject();
                    OWLClassExpression initialFacetDescription = currentPropertyChainModel
							.getFacet();
					OWLClassDescriptionEditor owlDescriptionEditor = new OWLClassDescriptionEditor(
							owlEditorKit,
							initialFacetDescription);
					final JComponent editorComponent = owlDescriptionEditor
							.getEditorComponent();
					final VerifyingOptionPane optionPane = new VerifyingOptionPane(
							editorComponent);
					final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
						@Override
                        public void verifiedStatusChanged(boolean verified) {
							optionPane.setOKEnabled(verified);
						}
					};
					owlDescriptionEditor
							.addStatusChangedListener(verificationListener);
					final JDialog dlg = optionPane.createDialog(
							mainPanel, null);
					dlg.setModal(true);
					dlg.setResizable(true);
					dlg.pack();
					dlg
							.setLocationRelativeTo(mainPanel);
					dlg.setVisible(true);
                    Set<OWLClassExpression> editedObjects = owlDescriptionEditor
							.getEditedObjects();
					if (editedObjects != null && editedObjects.size() > 0) {
						currentPropertyChainModel.setFacet(editedObjects
								.iterator().next());
					}
					OWLArithmeticFormulaEditor.this
							.handleVerifyEditorContents();
				}
			}
		});
	}

	/**
	 * 
	 */
	private void setupAppliesTo() {
        OWLClassExpression appliesToOWLClassExpression = owlDescription;
		if (formula != null) {
			ProtegeClassExtractor classExtractor = new ProtegeClassExtractor(
					owlEditorKit.getModelManager());
            appliesToOWLClassExpression = (OWLClassExpression) formula
                    .jjtAccept(
					classExtractor, null);
		} else if (formulaModel != null) {
            appliesToOWLClassExpression = formulaModel.getAppliesTo();
		}
        String string = appliesToOWLClassExpression != null ? owlEditorKit
                .getModelManager().getRendering(appliesToOWLClassExpression)
                : "";
		appliesToLabel.setText(APPLIES_TO_LABEL_NAME + ": " + string);
		editAppliesToButton.setEnabled(canEditAppliesTo);
	}

	/**
	 * 
	 */
	private void drawAppliesToSection() {
		JPanel appliesToSection = new JPanel();
		appliesToSection.setBorder(ComponentFactory
				.createTitledBorder(APPLIES_TO_LABEL_NAME));
		editAppliesToButton.setSize(20, 20);
		appliesToSection.add(appliesToLabel);
		editAppliesToButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
				OWLClassDescriptionEditor owlDescriptionEditor = new OWLClassDescriptionEditor(
						owlEditorKit,
						formulaModel
								.getAppliesTo());
				final JComponent editorComponent = owlDescriptionEditor
						.getEditorComponent();
				final VerifyingOptionPane optionPane = new VerifyingOptionPane(
						editorComponent);
				final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
					@Override
                    public void verifiedStatusChanged(boolean verified) {
						optionPane.setOKEnabled(verified);
					}
				};
				// if the editor is verifying, will need to prevent the OK
				// button from being available
				owlDescriptionEditor
						.addStatusChangedListener(verificationListener);
				final JDialog dlg = optionPane.createDialog(
						mainPanel, null);
				dlg.setModal(true);
				dlg.setResizable(true);
				dlg.pack();
				dlg
						.setLocationRelativeTo(mainPanel);
				dlg.setVisible(true);
                Set<OWLClassExpression> editedObjects = owlDescriptionEditor
						.getEditedObjects();
				if (editedObjects != null && editedObjects.size() > 0) {
                    OWLClassExpression editedObject = editedObjects.iterator()
							.next();
					formulaModel
							.setAppliesTo(editedObject);
					String rendering = editedObject != null ? owlEditorKit
							.getModelManager()
							.getOWLObjectRenderer()
                            .render(editedObject)
							: "";
					appliesToLabel
							.setText(rendering);
					OWLArithmeticFormulaEditor.this
							.handleVerifyEditorContents();
				}
			}
		});
		appliesToSection.add(editAppliesToButton);
		if (canEditAppliesTo) {
			mainPanel.add(appliesToSection);
		}
	}

	/**
	 * 
	 */
	private void setupConflictButtons() {
		if (formula != null
				|| formulaModel.getConflictStrategy() != null) {
			ConflictStrategyExtractor conflictStrategyExtractor = new ConflictStrategyExtractor();
			ConflictStrategy strategy = formula != null ? (ConflictStrategy) formula
					.jjtAccept(conflictStrategyExtractor, null)
					: formulaModel.getConflictStrategy();
			conflictButtnGroup.setSelected(
					conflictStrategyRadioButtonMap.get(strategy)
							.getModel(), true);
		} else {
			conflictButtnGroup.setSelected(overriddenButton
					.getModel(), true);
			formulaModel
					.setConflictStrategy(radioButtonConflictStrategyMap
							.get(overriddenButton));
		}
	}

	@Override
    public void dispose() {
	}

	@Override
    public JComponent getEditorComponent() {
		return mainPanel;
	}

	@Override
    public MAEStart getEditedObject() {
		return formula;
	}

	public void setFormula(MAEStart formula) {
		this.formula = formula;
		formulaModel = MAENodeAdapter.toFormulaModel(formula,
				formulaModel.getFormulaURI(), owlEditorKit);
		setupSections();
	}

	@Override
    public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		listeners.add(listener);
		handleVerifyEditorContents();
	}

	protected final void handleVerifyEditorContents() {
		if (!listeners.isEmpty()) {
			boolean validated = validateFormulaURI() && validateBindings()
					&& validateStorage() && validateFormulaBody();
			highlightInvalidSections();
			for (InputVerificationStatusChangedListener l : listeners) {
				l.verifiedStatusChanged(validated);
			}
			if (validated) {
				try {
					formula = MAENodeAdapter.toFormula(formulaModel,
							owlEditorKit.getModelManager());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void highlightInvalidSections() {
		formulaURITextField
				.setBorder(validateFormulaURI() ? new LineBorder(Color.BLACK)
						: new LineBorder(Color.RED));
		bindingTree.setBorder(validateBindings() ? new LineBorder(
				Color.BLACK) : new LineBorder(Color.RED));
		storeTree
				.setBorder(validateStorage() ? new LineBorder(Color.BLACK)
						: new LineBorder(Color.RED));
		arithmeticFormulaTextArea
				.setBorder(validateFormulaBody() ? new LineBorder(Color.BLACK)
						: new LineBorder(Color.RED));
	}

	private boolean validateFormulaURI() {
        IRI formulaURI = formulaModel.getFormulaURI();
		boolean toReturn = false;
        if (formulaURI != null) {
            toReturn = Constants.FORMULA_NAMESPACE_URI_STRING.equals(formulaURI
                    .getNamespace());
		}
		return toReturn;
	}

	private boolean validateStorage() {
		boolean toReturn = false;
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) storeTreeModel
				.getRoot();
		toReturn = root.isLeaf();
		if (!toReturn) {
			int variableCount = root.getChildCount();
			if (root.getChildCount() == 1) {
				for (int i = 0; i < variableCount; i++) {
					DefaultMutableTreeNode variableNode = (DefaultMutableTreeNode) root
							.getChildAt(i);
					toReturn = validateStoragePropertyChain(variableNode);
				}
			}
		}
		return toReturn;
	}

	private boolean validateFormulaBody() {
		String formulaBody = arithmeticFormulaTextArea.getText();
		if (formulaBody != null && formulaBody.length() > 0) {
			ParserFactory.initParser(formulaBody, owlEditorKit
					.getModelManager());
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
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) bindingTreeModel
				.getRoot();
		toReturn = root.isLeaf();
		if (!toReturn) {
			int variableCount = root.getChildCount();
			for (int i = 0; i < variableCount; i++) {
				DefaultMutableTreeNode variableNode = (DefaultMutableTreeNode) root
						.getChildAt(i);
				toReturn = variableNode.getChildCount() == 1
						&& validatePropertyChain((DefaultMutableTreeNode) variableNode
								.getChildAt(0));
			}
		}
		return toReturn;
	}

	private boolean validatePropertyChain(
			DefaultMutableTreeNode propertychainNode) {
		return propertychainNode.isLeaf()
				&& ((PropertyChainModel) propertychainNode.getUserObject())
						.getProperty() instanceof OWLDataProperty
				|| !propertychainNode.isLeaf()
				&& propertychainNode.getChildCount() == 1
				&& validatePropertyChain((DefaultMutableTreeNode) propertychainNode
						.getChildAt(0));
	}

	private boolean validateStoragePropertyChain(
			DefaultMutableTreeNode propertychainNode) {
		return propertychainNode.isLeaf()
				&& ((PropertyChainModel) propertychainNode.getUserObject())
						.getProperty() instanceof OWLObjectProperty
				|| !propertychainNode.isLeaf()
				&& propertychainNode.getChildCount() == 1
				&& validateStoragePropertyChain((DefaultMutableTreeNode) propertychainNode
						.getChildAt(0));
	}

	@Override
    public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		listeners.remove(listener);
	}

	@Override
    public void verifiedStatusChanged(boolean valid) {
		if (valid) {
			try {
				formula = MAENodeAdapter.toFormula(formulaModel,
						owlEditorKit.getModelManager());
				if (formulaURIMap != null) {
					formulaURIMap.put(formula, formulaModel
							.getFormulaURI());
				}
			} catch (ParseException e) {
				formula = null;
			}
		} else {
			formula = null;
		}
	}

    public void setFormulaURI(IRI annotationURI) {
		formulaModel.setFormulaURI(annotationURI);
		setupFormulaURISection();
	}

    @Override
    public String getEditorTypeName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean canEdit(Object object) {
        return object instanceof MAEStart;
    }

    @Override
    public boolean setEditedObject(MAEStart editedObject) {
        setFormula(editedObject);
        return true;
    }
}
