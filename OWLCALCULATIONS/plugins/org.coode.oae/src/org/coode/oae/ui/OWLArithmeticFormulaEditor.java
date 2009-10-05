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
import java.net.URI;
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
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.frame.OWLClassDescriptionEditor;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.util.NamespaceUtil;

import uk.ac.manchester.mae.ArithmeticsParser;
import uk.ac.manchester.mae.ConflictStrategy;
import uk.ac.manchester.mae.Constants;
import uk.ac.manchester.mae.ExceptionStrategy;
import uk.ac.manchester.mae.MAEPropertyChain;
import uk.ac.manchester.mae.MAEStart;
import uk.ac.manchester.mae.OverriddenStrategy;
import uk.ac.manchester.mae.OverridingStrategy;
import uk.ac.manchester.mae.ParseException;
import uk.ac.manchester.mae.evaluation.BindingModel;
import uk.ac.manchester.mae.evaluation.FormulaModel;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;
import uk.ac.manchester.mae.evaluation.StorageModel;
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
		AbstractOWLFrameSectionRowObjectEditor<MAEStart> implements
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
	protected JTextField formulaURITextField = new JTextField();
	protected JPanel mainPanel = new JPanel();
	private MAEStart formula = null;
	protected FormulaModel formulaModel;
	private OWLDescription owlDescription;
	private boolean canEditAppliesTo;
	protected JTree bindingTree, storeTree;
	private Map<ConflictStrategy, JRadioButton> conflictStrategyRadioButtonMap = new HashMap<ConflictStrategy, JRadioButton>();
	protected Map<JRadioButton, ConflictStrategy> radioButtonConflictStrategyMap = new HashMap<JRadioButton, ConflictStrategy>();
	private JLabel arithmeticFormulaLabel = new JLabel(
			OWLArithmeticFormulaEditor.FORMULA_BODY_LABEL);
	protected JTextField arithmeticFormulaTextArea = new JTextField();
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
	private Map<MAEStart, URI> formulaURIMap = new HashMap<MAEStart, URI>();

	/**
	 * @param owlEditorKit
	 * @param formulaAnnotationURIs
	 */
	public OWLArithmeticFormulaEditor(OWLEditorKit owlEditorKit,
			OWLDescription appliesToOWLDescription, boolean canEditAppliesTo,
			Map<MAEStart, URI> formulaAnnotationURIs) {
		this.owlEditorKit = owlEditorKit;
		this.owlDescription = appliesToOWLDescription;
		this.formulaModel = new FormulaModel();
		this.canEditAppliesTo = canEditAppliesTo;
		init();
		this.mainPanel.revalidate();
		this.formulaURIMap = formulaAnnotationURIs;
	}

	public OWLArithmeticFormulaEditor(OWLEditorKit editorKit,
			OWLClass rootObject, boolean b) {
		this(editorKit, rootObject, b, null);
	}

	private void init() {
		this.mainPanel.setName(OWLArithmeticFormulaEditor.MAIN_PANEL_NAME);
		BoxLayout boxLayout = new BoxLayout(this.mainPanel, BoxLayout.Y_AXIS);
		this.mainPanel.setLayout(boxLayout);
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
		formulaURIPanel.setBorder(new TitledBorder("Formula URI:"));
		formulaURIPanel.add(this.formulaURITextField);
		this.formulaURITextField.getDocument().addDocumentListener(
				new DocumentListener() {
					private void updateFormulaModel() {
						OWLArithmeticFormulaEditor.this.formulaModel
								.setFormulaURI(URI
										.create(OWLArithmeticFormulaEditor.this.formulaURITextField
												.getText()));
						OWLArithmeticFormulaEditor.this
								.handleVerifyEditorContents();
					}

					public void changedUpdate(DocumentEvent arg0) {
						updateFormulaModel();
					}

					public void insertUpdate(DocumentEvent arg0) {
						updateFormulaModel();
					}

					public void removeUpdate(DocumentEvent arg0) {
						updateFormulaModel();
					}
				});
		this.mainPanel.add(formulaURIPanel);
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
		URI formulaURI = this.formulaModel.getFormulaURI();
		if (formulaURI != null) {
			this.formulaURITextField.setText(formulaURI.toString());
		} else {
			this.formulaURITextField
					.setText(Constants.FORMULA_NAMESPACE_URI_STRING);
		}
		this.formulaModel.setFormulaURI(URI.create(this.formulaURITextField
				.getText()));
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
		if (this.formula != null || this.formulaModel != null) {
			String formulaBodyString = this.formula != null ? this.formula
					.jjtGetChild(this.formula.jjtGetNumChildren() - 1)
					.toString()
					+ ";" : this.formulaModel.getFormulaBody();
			this.arithmeticFormulaTextArea.setText(formulaBodyString);
		}
	}

	private void setupStorage() {
		MutableTreeNode root = new DefaultMutableTreeNode(
				OWLArithmeticFormulaEditor.STORE_TREE_ROOT_NAME);
		this.storeTreeModel = new DefaultTreeModel(root);
		this.storeTree.setModel(this.storeTreeModel);
		this.storeTree.setCellRenderer(new StorageTreeCellRenderer(
				this.owlEditorKit));
		this.storeTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) OWLArithmeticFormulaEditor.this.storeTree
						.getLastSelectedPathComponent();
				if (node != null) {
					OWLArithmeticFormulaEditor.this
							.decideStoreButtonEnabling(node);
				}
			}
		});
		if (this.formula != null) {
			StorageExtractor bindingExtractor = new StorageExtractor();
			this.formula.jjtAccept(bindingExtractor, null);
			MAEPropertyChain storeToNode = bindingExtractor
					.getExtractedStorage();
			if (storeToNode != null) {
				MutableTreeNode storageNode = MAENodeAdapter.toTreeNode(
						storeToNode, this.owlEditorKit.getModelManager());
				this.storeTreeModel.insertNodeInto(storageNode, root, 0);
			}
			this.storeTree.revalidate();
		} else if (this.formulaModel != null) {
			StorageModel storageModel = this.formulaModel.getStorageModel();
			if (storageModel != null) {
				MutableTreeNode storageNode = MAENodeAdapter.toTreeNode(
						storageModel, this.owlEditorKit.getModelManager());
				this.storeTreeModel.setRoot(storageNode);
			}
		}
		this.storeTreeModel.addTreeModelListener(new TreeModelListener() {
			private void updateFormulaModel() {
				DefaultMutableTreeNode currentRoot = (DefaultMutableTreeNode) OWLArithmeticFormulaEditor.this.storeTreeModel
						.getRoot();
				StorageModel storageModel = null;
				if (!currentRoot.isLeaf()) {
					DefaultMutableTreeNode storageSubTreeRoot = (DefaultMutableTreeNode) currentRoot
							.getChildAt(0);
					storageModel = MAENodeAdapter
							.toStorageModel(storageSubTreeRoot);
				}
				OWLArithmeticFormulaEditor.this.formulaModel
						.setStorageModel(storageModel);
				OWLArithmeticFormulaEditor.this.handleVerifyEditorContents();
			}

			public void treeNodesChanged(TreeModelEvent e) {
				updateFormulaModel();
			}

			public void treeNodesInserted(TreeModelEvent e) {
				updateFormulaModel();
			}

			public void treeNodesRemoved(TreeModelEvent e) {
				updateFormulaModel();
			}

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
		this.bindingTreeModel = new DefaultTreeModel(root);
		this.bindingTree.setModel(this.bindingTreeModel);
		this.bindingTree.setCellRenderer(new BindingTreeCellRenderer(
				this.owlEditorKit));
		this.bindingTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) OWLArithmeticFormulaEditor.this.bindingTree
						.getLastSelectedPathComponent();
				if (node != null) {
					OWLArithmeticFormulaEditor.this
							.decideBindingButtonEnabling(node);
				}
			}
		});
		if (this.formula != null) {
			ProtegeBindingExtractor bindingExtractor = new ProtegeBindingExtractor(
					this.owlEditorKit.getModelManager());
			Set<BindingModel> extractedBindings = (Set<BindingModel>) this.formula
					.jjtAccept(bindingExtractor, null);
			for (BindingModel binding : extractedBindings) {
				MutableTreeNode bindingNode = MAENodeAdapter.toTreeNode(
						binding, this.owlEditorKit.getModelManager());
				this.bindingTreeModel.insertNodeInto(bindingNode, root, 0);
				this.bindingTree.revalidate();
			}
		} else if (this.formulaModel != null) {
			Set<BindingModel> bindings = this.formulaModel.getBindings();
			for (BindingModel binding : bindings) {
				MutableTreeNode bindingNode = MAENodeAdapter.toTreeNode(
						binding, this.owlEditorKit.getModelManager());
				this.bindingTreeModel.insertNodeInto(bindingNode, root, 0);
				this.bindingTree.revalidate();
			}
		}
		this.bindingTreeModel.addTreeModelListener(new TreeModelListener() {
			private void updateFormulaModel() {
				DefaultMutableTreeNode currentRoot = (DefaultMutableTreeNode) OWLArithmeticFormulaEditor.this.bindingTreeModel
						.getRoot();
				int variableCount = OWLArithmeticFormulaEditor.this.bindingTreeModel
						.getChildCount(currentRoot);
				OWLArithmeticFormulaEditor.this.formulaModel.getBindings()
						.clear();
				for (int i = 0; i < variableCount; i++) {
					DefaultMutableTreeNode bindingSubTreeRoot = (DefaultMutableTreeNode) currentRoot
							.getChildAt(i);
					BindingModel bindingModel = MAENodeAdapter
							.toBindingModel(bindingSubTreeRoot);
					OWLArithmeticFormulaEditor.this.formulaModel.getBindings()
							.add(bindingModel);
				}
				OWLArithmeticFormulaEditor.this.handleVerifyEditorContents();
			}

			public void treeNodesChanged(TreeModelEvent e) {
				updateFormulaModel();
			}

			public void treeNodesInserted(TreeModelEvent e) {
				updateFormulaModel();
			}

			public void treeNodesRemoved(TreeModelEvent e) {
				updateFormulaModel();
			}

			public void treeStructureChanged(TreeModelEvent e) {
				updateFormulaModel();
			}
		});
	}

	protected void decideBindingButtonEnabling(DefaultMutableTreeNode node) {
		Object userObject = node.getUserObject();
		this.addObjectPropertyBindingButton
				.setEnabled(!(userObject instanceof PropertyChainModel && ((PropertyChainModel) userObject)
						.getProperty() instanceof OWLDataProperty)
						&& node.isLeaf() && !node.isRoot());
		this.addBindingFacetButton
				.setEnabled(userObject instanceof PropertyChainModel
						&& ((PropertyChainModel) userObject).getProperty() instanceof OWLObjectProperty);
		this.addDataPropertyBindingButton
				.setEnabled(!(userObject instanceof PropertyChainModel && ((PropertyChainModel) userObject)
						.getProperty() instanceof OWLDataProperty)
						&& node.isLeaf() && !node.isRoot());
		this.deleteBindingButton.setEnabled(node.isLeaf() && !node.isRoot());
		this.addBindingButton.setEnabled(node.isRoot());
	}

	protected void decideStoreButtonEnabling(DefaultMutableTreeNode node) {
		this.addObjectPropertyStoreButton.setEnabled(true);
		this.addStoreFacetButton
				.setEnabled(node.getUserObject() instanceof PropertyChainModel
						&& ((PropertyChainModel) node.getUserObject())
								.getProperty() instanceof OWLObjectProperty);
		this.deleteStoreButton.setEnabled(node.isLeaf() && !node.isRoot());
	}

	/**
	 * 
	 */
	private void drawBindingSection() {
		JPanel bindingPanel = new JPanel(new BorderLayout());
		bindingPanel.setBorder(LineBorder.createBlackLineBorder());
		this.addBindingButton.setEnabled(false);
		this.addDataPropertyBindingButton.setEnabled(false);
		this.addObjectPropertyBindingButton.setEnabled(false);
		this.addBindingFacetButton.setEnabled(false);
		this.deleteBindingButton.setEnabled(false);
		JToolBar toolBar = new JToolBar();
		toolBar.add(this.addBindingButton);
		toolBar.add(this.addDataPropertyBindingButton);
		toolBar.add(this.addObjectPropertyBindingButton);
		toolBar.add(this.addBindingFacetButton);
		toolBar.add(this.deleteBindingButton);
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
	private void drawStorageSection() {
		JPanel storePanel = new JPanel(new BorderLayout());
		storePanel.setBorder(LineBorder.createBlackLineBorder());
		this.addObjectPropertyStoreButton.setEnabled(false);
		this.addStoreFacetButton.setEnabled(false);
		this.deleteStoreButton.setEnabled(false);
		JToolBar storeToolBar = new JToolBar();
		storeToolBar.add(this.addObjectPropertyStoreButton);
		storeToolBar.add(this.addStoreFacetButton);
		storeToolBar.add(this.deleteStoreButton);
		storeToolBar.setBorder(null);
		storeToolBar.setBorderPainted(false);
		storePanel.add(storeToolBar, BorderLayout.NORTH);
		this.storeTree = new JTree(this.storeTreeModel);
		JScrollPane storeTreePane = new JScrollPane(this.storeTree);
		storePanel.add(storeTreePane, BorderLayout.CENTER);
		this.mainPanel.add(storePanel);
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
						updateFormulaModel();
					}

					public void insertUpdate(DocumentEvent arg0) {
						updateFormulaModel();
					}

					public void removeUpdate(DocumentEvent arg0) {
						updateFormulaModel();
					}
				});
		addButtonActions();
		this.mainPanel.add(formulaPanel);
	}

	private void addButtonActions() {
		addBindingButtons();
		addStorageButtons();
	}

	/**
	 * 
	 */
	private void addBindingButtons() {
		this.deleteBindingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) OWLArithmeticFormulaEditor.this.bindingTree
						.getLastSelectedPathComponent();
				if (node != null) {
					OWLArithmeticFormulaEditor.this.bindingTreeModel
							.removeNodeFromParent(node);
					OWLArithmeticFormulaEditor.this.deleteBindingButton
							.setEnabled(false);
					OWLArithmeticFormulaEditor.this.addBindingButton
							.setEnabled(false);
					OWLArithmeticFormulaEditor.this.addDataPropertyBindingButton
							.setEnabled(false);
					OWLArithmeticFormulaEditor.this.addObjectPropertyBindingButton
							.setEnabled(false);
					OWLArithmeticFormulaEditor.this.bindingTree.revalidate();
				}
			}
		});
		this.addBindingButton.addActionListener(new ActionListener() {
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
					OWLArithmeticFormulaEditor.this.bindingTree.revalidate();
				}
			}
		});
		this.addDataPropertyBindingButton
				.addActionListener(new ActionListener() {
					@SuppressWarnings("unchecked")
					public void actionPerformed(ActionEvent e) {
						OWLModelManagerTree<OWLDataProperty> dataPropertyTree = new OWLModelManagerTree<OWLDataProperty>(
								OWLArithmeticFormulaEditor.this.owlEditorKit,
								OWLArithmeticFormulaEditor.this.owlEditorKit
										.getModelManager()
										.getOWLHierarchyManager()
										.getOWLDataPropertyHierarchyProvider());
						JScrollPane panel = new JScrollPane(dataPropertyTree);
						panel.setBorder(LineBorder.createBlackLineBorder());
						JOptionPane jOptionPane = new JOptionPane(panel,
								JOptionPane.QUESTION_MESSAGE,
								JOptionPane.OK_CANCEL_OPTION, new ImageIcon(
										this.getClass().getClassLoader()
												.getResource(
														"property.data.png")));
						JDialog jDialog = jOptionPane.createDialog(
								OWLArithmeticFormulaEditor.this.mainPanel,
								"data property");
						jDialog.pack();
						jDialog.setVisible(true);
						if ((DefaultMutableTreeNode) dataPropertyTree
								.getLastSelectedPathComponent() != null) {
							Object selectedProperty = ((DefaultMutableTreeNode) dataPropertyTree
									.getLastSelectedPathComponent())
									.getUserObject();
							Object currentNode = OWLArithmeticFormulaEditor.this.bindingTree
									.getLastSelectedPathComponent();
							OWLArithmeticFormulaEditor.this.bindingTreeModel
									.insertNodeInto(
											new DefaultMutableTreeNode(
													new PropertyChainModel(
															(OWLProperty) selectedProperty,
															null)),
											(MutableTreeNode) currentNode, 0);
							OWLArithmeticFormulaEditor.this.bindingTree
									.revalidate();
							OWLArithmeticFormulaEditor.this.bindingTree
									.expandPath(OWLArithmeticFormulaEditor.this.bindingTree
											.getLeadSelectionPath());
						}
					}
				});
		this.addObjectPropertyBindingButton
				.addActionListener(new ActionListener() {
					@SuppressWarnings("unchecked")
					public void actionPerformed(ActionEvent e) {
						OWLModelManagerTree<OWLObjectProperty> objectPropertyTree = new OWLModelManagerTree<OWLObjectProperty>(
								OWLArithmeticFormulaEditor.this.owlEditorKit,
								OWLArithmeticFormulaEditor.this.owlEditorKit
										.getModelManager()
										.getOWLHierarchyManager()
										.getOWLObjectPropertyHierarchyProvider());
						JScrollPane panel = new JScrollPane(objectPropertyTree);
						panel.setBorder(LineBorder.createBlackLineBorder());
						JOptionPane jOptionPane = new JOptionPane(panel,
								JOptionPane.QUESTION_MESSAGE,
								JOptionPane.OK_CANCEL_OPTION, new ImageIcon(
										this.getClass().getClassLoader()
												.getResource(
														"property.object.png")));
						JDialog jDialog = jOptionPane.createDialog(
								OWLArithmeticFormulaEditor.this.mainPanel,
								"object property");
						jDialog.pack();
						jDialog.setVisible(true);
						if ((DefaultMutableTreeNode) objectPropertyTree
								.getLastSelectedPathComponent() != null) {
							Object selectedProperty = ((DefaultMutableTreeNode) objectPropertyTree
									.getLastSelectedPathComponent())
									.getUserObject();
							Object currentNode = OWLArithmeticFormulaEditor.this.bindingTree
									.getLastSelectedPathComponent();
							OWLArithmeticFormulaEditor.this.bindingTreeModel
									.insertNodeInto(
											new DefaultMutableTreeNode(
													new PropertyChainModel(
															(OWLProperty) selectedProperty,
															null)),
											(MutableTreeNode) currentNode, 0);
							OWLArithmeticFormulaEditor.this.bindingTree
									.expandPath(OWLArithmeticFormulaEditor.this.bindingTree
											.getLeadSelectionPath());
							OWLArithmeticFormulaEditor.this.bindingTree
									.revalidate();
						}
					}
				});
		this.addBindingFacetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) OWLArithmeticFormulaEditor.this.bindingTree
						.getLastSelectedPathComponent();
				if (treeNode != null
						&& treeNode.getUserObject() instanceof PropertyChainModel) {
					PropertyChainModel currentPropertyChainModel = (PropertyChainModel) treeNode
							.getUserObject();
					OWLDescription initialFacetDescription = currentPropertyChainModel
							.getFacet();
					OWLClassDescriptionEditor owlDescriptionEditor = new OWLClassDescriptionEditor(
							OWLArithmeticFormulaEditor.this.owlEditorKit,
							initialFacetDescription);
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
					//			            
					owlDescriptionEditor
							.addStatusChangedListener(verificationListener);
					final JDialog dlg = optionPane.createDialog(
							OWLArithmeticFormulaEditor.this.mainPanel, null);
					dlg.setModal(true);
					dlg.setResizable(true);
					dlg.pack();
					dlg
							.setLocationRelativeTo(OWLArithmeticFormulaEditor.this.mainPanel);
					dlg.setVisible(true);
					Set<OWLDescription> editedObjects = owlDescriptionEditor
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
		this.deleteStoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) OWLArithmeticFormulaEditor.this.storeTree
						.getLastSelectedPathComponent();
				if (node != null) {
					OWLArithmeticFormulaEditor.this.storeTreeModel
							.removeNodeFromParent(node);
					OWLArithmeticFormulaEditor.this.deleteStoreButton
							.setEnabled(false);
					OWLArithmeticFormulaEditor.this.addObjectPropertyStoreButton
							.setEnabled(false);
					OWLArithmeticFormulaEditor.this.storeTree.revalidate();
				}
			}
		});
		this.addObjectPropertyStoreButton
				.addActionListener(new ActionListener() {
					@SuppressWarnings("unchecked")
					public void actionPerformed(ActionEvent e) {
						OWLModelManagerTree<OWLObjectProperty> objectPropertyTree = new OWLModelManagerTree<OWLObjectProperty>(
								OWLArithmeticFormulaEditor.this.owlEditorKit,
								OWLArithmeticFormulaEditor.this.owlEditorKit
										.getModelManager()
										.getOWLHierarchyManager()
										.getOWLObjectPropertyHierarchyProvider());
						JScrollPane panel = new JScrollPane(objectPropertyTree);
						panel.setBorder(LineBorder.createBlackLineBorder());
						JOptionPane jOptionPane = new JOptionPane(panel,
								JOptionPane.QUESTION_MESSAGE,
								JOptionPane.OK_CANCEL_OPTION, new ImageIcon(
										this.getClass().getClassLoader()
												.getResource(
														"property.object.png")));
						JDialog jDialog = jOptionPane.createDialog(
								OWLArithmeticFormulaEditor.this.mainPanel,
								"object property");
						jDialog.pack();
						jDialog.setVisible(true);
						if ((DefaultMutableTreeNode) objectPropertyTree
								.getLastSelectedPathComponent() != null) {
							DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) OWLArithmeticFormulaEditor.this.storeTree
									.getLastSelectedPathComponent();
							Object selectedProperty = ((DefaultMutableTreeNode) objectPropertyTree
									.getLastSelectedPathComponent())
									.getUserObject();
							OWLArithmeticFormulaEditor.this.storeTreeModel
									.insertNodeInto(
											new DefaultMutableTreeNode(
													new PropertyChainModel(
															(OWLProperty) selectedProperty,
															null)),
											currentNode, 0);
							OWLArithmeticFormulaEditor.this.storeTree
									.revalidate();
						}
					}
				});
		this.addStoreFacetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) OWLArithmeticFormulaEditor.this.storeTree
						.getLastSelectedPathComponent();
				if (treeNode != null
						&& treeNode.getUserObject() instanceof PropertyChainModel) {
					PropertyChainModel currentPropertyChainModel = (PropertyChainModel) treeNode
							.getUserObject();
					OWLDescription initialFacetDescription = currentPropertyChainModel
							.getFacet();
					OWLClassDescriptionEditor owlDescriptionEditor = new OWLClassDescriptionEditor(
							OWLArithmeticFormulaEditor.this.owlEditorKit,
							initialFacetDescription);
					final JComponent editorComponent = owlDescriptionEditor
							.getEditorComponent();
					final VerifyingOptionPane optionPane = new VerifyingOptionPane(
							editorComponent);
					final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
						public void verifiedStatusChanged(boolean verified) {
							optionPane.setOKEnabled(verified);
						}
					};
					owlDescriptionEditor
							.addStatusChangedListener(verificationListener);
					final JDialog dlg = optionPane.createDialog(
							OWLArithmeticFormulaEditor.this.mainPanel, null);
					dlg.setModal(true);
					dlg.setResizable(true);
					dlg.pack();
					dlg
							.setLocationRelativeTo(OWLArithmeticFormulaEditor.this.mainPanel);
					dlg.setVisible(true);
					Set<OWLDescription> editedObjects = owlDescriptionEditor
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
		OWLDescription appliesToOWLDescription = this.owlDescription;
		if (this.formula != null) {
			ProtegeClassExtractor classExtractor = new ProtegeClassExtractor(
					this.owlEditorKit.getModelManager());
			appliesToOWLDescription = (OWLDescription) this.formula.jjtAccept(
					classExtractor, null);
		} else if (this.formulaModel != null) {
			appliesToOWLDescription = this.formulaModel.getAppliesTo();
		}
		String string = appliesToOWLDescription != null ? this.owlEditorKit
				.getModelManager().getRendering(appliesToOWLDescription) : "";
		this.appliesToLabel.setText(APPLIES_TO_LABEL_NAME + ": " + string);
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
				owlDescriptionEditor
						.addStatusChangedListener(verificationListener);
				final JDialog dlg = optionPane.createDialog(
						OWLArithmeticFormulaEditor.this.mainPanel, null);
				dlg.setModal(true);
				dlg.setResizable(true);
				dlg.pack();
				dlg
						.setLocationRelativeTo(OWLArithmeticFormulaEditor.this.mainPanel);
				dlg.setVisible(true);
				Set<OWLDescription> editedObjects = owlDescriptionEditor
						.getEditedObjects();
				if (editedObjects != null && editedObjects.size() > 0) {
					OWLDescription editedObject = editedObjects.iterator()
							.next();
					OWLArithmeticFormulaEditor.this.formulaModel
							.setAppliesTo(editedObject);
					String rendering = editedObject != null ? OWLArithmeticFormulaEditor.this.owlEditorKit
							.getModelManager()
							.getOWLObjectRenderer()
							.render(
									editedObject,
									OWLArithmeticFormulaEditor.this.owlEditorKit
											.getModelManager()
											.getOWLEntityRenderer())
							: "";
					OWLArithmeticFormulaEditor.this.appliesToLabel
							.setText(rendering);
					OWLArithmeticFormulaEditor.this
							.handleVerifyEditorContents();
				}
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
				|| this.formulaModel.getConflictStrategy() != null) {
			ConflictStrategyExtractor conflictStrategyExtractor = new ConflictStrategyExtractor();
			ConflictStrategy strategy = this.formula != null ? (ConflictStrategy) this.formula
					.jjtAccept(conflictStrategyExtractor, null)
					: this.formulaModel.getConflictStrategy();
			this.conflictButtnGroup.setSelected(
					this.conflictStrategyRadioButtonMap.get(strategy)
							.getModel(), true);
		} else {
			this.conflictButtnGroup.setSelected(this.overriddenButton
					.getModel(), true);
			this.formulaModel
					.setConflictStrategy(this.radioButtonConflictStrategyMap
							.get(this.overriddenButton));
		}
	}

	public void clear() {
		this.formula = null;
	}

	public void dispose() {
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
				this.formulaModel.getFormulaURI(), this.owlEditorKit);
		setupSections();
	}

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
		handleVerifyEditorContents();
	}

	protected final void handleVerifyEditorContents() {
		if (!this.listeners.isEmpty()) {
			boolean validated = validateFormulaURI() && validateBindings()
					&& validateStorage() && validateFormulaBody();
			highlightInvalidSections();
			for (InputVerificationStatusChangedListener l : this.listeners) {
				l.verifiedStatusChanged(validated);
			}
			if (validated) {
				try {
					this.formula = MAENodeAdapter.toFormula(this.formulaModel,
							this.owlEditorKit.getModelManager());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void highlightInvalidSections() {
		this.formulaURITextField
				.setBorder(validateFormulaURI() ? new LineBorder(Color.BLACK)
						: new LineBorder(Color.RED));
		this.bindingTree.setBorder(validateBindings() ? new LineBorder(
				Color.BLACK) : new LineBorder(Color.RED));
		this.storeTree
				.setBorder(validateStorage() ? new LineBorder(Color.BLACK)
						: new LineBorder(Color.RED));
		this.arithmeticFormulaTextArea
				.setBorder(validateFormulaBody() ? new LineBorder(Color.BLACK)
						: new LineBorder(Color.RED));
	}

	private boolean validateFormulaURI() {
		URI formulaURI = this.formulaModel.getFormulaURI();
		boolean toReturn = false;
		if (formulaURI != null && formulaURI.toString().length() > 0) {
			NamespaceUtil nsUtil = new NamespaceUtil();
			String[] split = nsUtil.split(formulaURI.toString(), null);
			toReturn = split.length > 1
					&& split[0]
							.compareTo(Constants.FORMULA_NAMESPACE_URI_STRING) == 0;
		}
		return toReturn;
	}

	private boolean validateStorage() {
		boolean toReturn = false;
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.storeTreeModel
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
		String formulaBody = this.arithmeticFormulaTextArea.getText();
		if (formulaBody != null && formulaBody.length() > 0) {
			ParserFactory.initParser(formulaBody, this.owlEditorKit
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
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.bindingTreeModel
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

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	public void verifiedStatusChanged(boolean valid) {
		if (valid) {
			try {
				this.formula = MAENodeAdapter.toFormula(this.formulaModel,
						this.owlEditorKit.getModelManager());
				if (this.formulaURIMap != null) {
					this.formulaURIMap.put(this.formula, this.formulaModel
							.getFormulaURI());
				}
			} catch (ParseException e) {
				this.formula = null;
			}
		} else {
			this.formula = null;
		}
	}

	public void setFormulaURI(URI annotationURI) {
		this.formulaModel.setFormulaURI(annotationURI);
		setupFormulaURISection();
	}
}
