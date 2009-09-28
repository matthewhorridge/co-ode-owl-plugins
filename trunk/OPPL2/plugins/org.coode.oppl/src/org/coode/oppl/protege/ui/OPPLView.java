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
package org.coode.oppl.protege.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.coode.oppl.ChangeExtractor;
import org.coode.oppl.OPPLQuery;
import org.coode.oppl.OPPLScript;
import org.coode.oppl.OPPLScriptVisitorEx;
import org.coode.oppl.protege.ui.rendering.BindingTreeRenderer;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.utils.ProtegeParserFactory;
import org.coode.oppl.validation.OPPLScriptValidator;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.jdesktop.swingworker.SwingWorker;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.inference.NoOpReasoner;
import org.protege.editor.owl.ui.list.OWLLinkedObjectList;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeListener;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLView extends AbstractOWLViewComponent implements
		InputVerificationStatusChangedListener, OWLOntologyChangeListener,
		OWLModelManagerListener {
	private final class ReasonerOPPLScriptValiator implements
			OPPLScriptValidator {
		public boolean accept(OPPLScript script) {
			OWLReasoner reasoner = OPPLView.this.getOWLEditorKit()
					.getModelManager().getReasoner();
			return !(reasoner instanceof NoOpReasoner)
					|| script.accept(new OPPLScriptVisitorEx<Boolean>() {
						public Boolean visitActions(
								List<OWLAxiomChange> changes, Boolean p) {
							return p == null ? true : p;
						}

						public Boolean visit(OPPLQuery q, Boolean p) {
							return p == null ? true : p;
						}

						public Boolean visit(Variable v, Boolean p) {
							return p == null ? v.getVariableScope() == null : p
									|| v.getVariableScope() == null;
						}
					});
		}

		public String getValidationRuleDescription() {
			return "If the script contains scoped variables the selected reasoner cannot be NoOpReasoner";
		}
	}

	class OPPLExecutorSwingWorker extends
			SwingWorker<List<OWLAxiomChange>, OPPLScript> {
		private final List<OWLAxiomChange> changes;

		/**
		 * @param changes
		 */
		public OPPLExecutorSwingWorker(List<OWLAxiomChange> changes) {
			this.changes = changes;
		}

		@Override
		protected List<OWLAxiomChange> doInBackground() throws Exception {
			OPPLView.this.getOWLEditorKit().getModelManager().applyChanges(
					this.changes);
			return this.changes;
		}

		@Override
		protected void done() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					OPPLView.this.window.setVisible(false);
					OPPLView.this.window.dispose();
				}
			});
		}
	}

	class OPPLChangeDetectorSwingWorker extends
			SwingWorker<List<OWLAxiomChange>, OPPLScript> {
		@Override
		protected void done() {
			List<OWLAxiomChange> changes;
			try {
				changes = this.get();
				ActionListModel model = (ActionListModel) OPPLView.this.affectedAxioms
						.getModel();
				model.clear();
				for (OWLAxiomChange axiomChange : changes) {
					model.addAction(axiomChange, false, true);
				}
				OPPLView.this.revalidate();
				OPPLView.this
						.updateInstantiatedAxioms(OPPLView.this.statementModel);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						OPPLView.this.instantiatedScrollPane
								.setBorder(ComponentFactory
										.createTitledBorder("Instantiated axioms: "
												+ OPPLView.this.instantiatedAxiomsList
														.getModel().getSize()));
						OPPLView.this.affectedScrollPane
								.setBorder(ComponentFactory
										.createTitledBorder("Affected axioms: "
												+ OPPLView.this.affectedAxioms
														.getModel().getSize()));
						OPPLView.this.window.setVisible(false);
						OPPLView.this.window.dispose();
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected List<OWLAxiomChange> doInBackground() throws Exception {
			ChangeExtractor changeExtractor = new ChangeExtractor(OPPLView.this
					.getOWLEditorKit().getModelManager().getActiveOntology(),
					OPPLView.this.getOWLEditorKit().getModelManager()
							.getOWLOntologyManager(),
					OPPLView.this.statementModel.getConstraintSystem(),
					OPPLView.this.considerImportClosureCheckBox.isSelected());
			System.out.println(OPPLView.this.statementModel.toString());
			List<OWLAxiomChange> result = OPPLView.this.statementModel
					.accept(changeExtractor);
			return result;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1897093057453176659L;
	private static final String OPPL_COMPUTATION_IN_PROGRESS_PLEASE_WAIT = "OPPL Computation in progress...please wait";
	private OPPLEditor editor;
	private final ReasonerOPPLScriptValiator validator = new ReasonerOPPLScriptValiator();
	private JButton evaluate = new JButton("Evaluate");
	private JButton execute = new JButton("Execute");
	private ActionList affectedAxioms;
	private OWLLinkedObjectList instantiatedAxiomsList;
	private OPPLScript statementModel;
	private JDialog window;
	private JScrollPane affectedScrollPane;
	private JScrollPane instantiatedScrollPane;
	private JCheckBox considerImportClosureCheckBox = new JCheckBox(
			"When removing consider Active Ontology Imported Closure", false);
	private DefaultTreeModel bindingTreeNodeModel = new DefaultTreeModel(
			new DefaultMutableTreeNode("Bindings"));
	private JTree bindingNodeTree = new JTree(this.bindingTreeNodeModel);

	@Override
	protected void disposeOWLView() {
		this.getOWLEditorKit().getModelManager().removeListener(this);
		this.editor.removeStatusChangedListener(this);
	}

	@Override
	protected void initialiseOWLView() throws Exception {
		this.setLayout(new BorderLayout());
		JPanel statementPanel = new JPanel(new BorderLayout());
		ProtegeParserFactory.initParser("", this.getOWLModelManager());
		this.affectedAxioms = new ActionList(this.getOWLEditorKit(),
				new ConstraintSystem(this.getOWLEditorKit().getModelManager()
						.getActiveOntology(), this.getOWLEditorKit()
						.getModelManager().getOWLOntologyManager()), false);
		this.instantiatedAxiomsList = new OWLLinkedObjectList(this
				.getOWLEditorKit());
		this.instantiatedAxiomsList.setModel(new DefaultListModel());
		OWLCellRenderer cellRenderer = new OWLCellRenderer(this
				.getOWLEditorKit());
		cellRenderer.setWrap(true);
		cellRenderer.setHighlightKeywords(true);
		this.considerImportClosureCheckBox
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						((DefaultListModel) OPPLView.this.instantiatedAxiomsList
								.getModel()).clear();
						((DefaultListModel) OPPLView.this.affectedAxioms
								.getModel()).clear();
						OPPLView.this.instantiatedScrollPane
								.setBorder(ComponentFactory
										.createTitledBorder("Instantiated axioms: "));
						OPPLView.this.affectedScrollPane
								.setBorder(ComponentFactory
										.createTitledBorder("Affected axioms: "));
						OPPLView.this.evaluate
								.setEnabled(OPPLView.this.statementModel != null);
						OPPLView.this.execute.setEnabled(false);
					}
				});
		statementPanel.add(this.considerImportClosureCheckBox,
				BorderLayout.EAST);
		this.instantiatedAxiomsList.setCellRenderer(cellRenderer);
		this.getOWLEditorKit().getModelManager().addListener(this);
		this.getOWLModelManager().getOWLOntologyManager()
				.addOntologyChangeListener(this);
		this.editor = new OPPLEditor(this.getOWLEditorKit(), this.validator);
		this.editor.setPreferredSize(new Dimension(200, 300));
		statementPanel.add(ComponentFactory.createScrollPane(this.editor),
				BorderLayout.NORTH);
		statementPanel.add(this.evaluate, BorderLayout.SOUTH);
		this.add(statementPanel, BorderLayout.NORTH);
		// Effects GUI portion
		JSplitPane effects = new JSplitPane();
		this.affectedScrollPane = ComponentFactory
				.createScrollPane(this.affectedAxioms);
		this.instantiatedScrollPane = ComponentFactory
				.createScrollPane(this.instantiatedAxiomsList);
		this.instantiatedScrollPane.setBorder(ComponentFactory
				.createTitledBorder("Instantiated axioms: "));
		this.affectedScrollPane.setBorder(ComponentFactory
				.createTitledBorder("Affected axioms:"));
		effects.add(this.affectedScrollPane, JSplitPane.LEFT);
		JSplitPane instantiatonPanel = new JSplitPane();
		JScrollPane bindingTreeScrollPane = ComponentFactory
				.createScrollPane(this.bindingNodeTree);
		bindingTreeScrollPane.setBorder(ComponentFactory
				.createTitledBorder("Binding Tree:"));
		instantiatonPanel.add(this.instantiatedScrollPane, JSplitPane.LEFT);
		instantiatonPanel.add(bindingTreeScrollPane, JSplitPane.RIGHT);
		effects.add(instantiatonPanel, JSplitPane.RIGHT);
		this.add(effects, BorderLayout.CENTER);
		this.add(this.execute, BorderLayout.SOUTH);
		this.evaluate.setEnabled(false);
		this.execute.setEnabled(false);
		this.evaluate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OPPLView.this.evaluate.setEnabled(false);
				JPanel panel = new JPanel(new BorderLayout(7, 7));
				JProgressBar progressBar = new JProgressBar();
				panel.add(progressBar, BorderLayout.SOUTH);
				panel
						.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
								10));
				progressBar.setIndeterminate(true);
				JLabel label = new JLabel(
						OPPL_COMPUTATION_IN_PROGRESS_PLEASE_WAIT);
				panel.add(label, BorderLayout.NORTH);
				OPPLView.this.window = new JDialog((Frame) SwingUtilities
						.getAncestorOfClass(Frame.class, OPPLView.this
								.getOWLEditorKit().getWorkspace()),
						"OPPL Engine progress", true);
				OPPLView.this.window.setLocation(400, 400);
				JPanel holderPanel = new JPanel(new BorderLayout(7, 7));
				holderPanel.add(panel, BorderLayout.NORTH);
				holderPanel.setBorder(BorderFactory.createEmptyBorder(5, 10,
						10, 10));
				OPPLView.this.window.getContentPane().setLayout(
						new BorderLayout());
				OPPLView.this.window.getContentPane().add(holderPanel,
						BorderLayout.NORTH);
				OPPLView.this.window.pack();
				Dimension windowSize = OPPLView.this.window.getSize();
				OPPLView.this.window.setSize(400, windowSize.height);
				OPPLView.this.window.setResizable(false);
				OPPLChangeDetectorSwingWorker opplSwingWorker = new OPPLChangeDetectorSwingWorker();
				OPPLView.this.window.pack();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						OPPLView.this.window.setVisible(true);
					}
				});
				opplSwingWorker.execute();
			}
		});
		this.affectedAxioms.getModel().addListDataListener(
				new ListDataListener() {
					public void contentsChanged(ListDataEvent e) {
						OPPLView.this.execute
								.setEnabled(OPPLView.this.affectedAxioms
										.getModel().getSize() > 0);
					}

					public void intervalAdded(ListDataEvent e) {
						OPPLView.this.execute
								.setEnabled(OPPLView.this.affectedAxioms
										.getModel().getSize() > 0);
					}

					public void intervalRemoved(ListDataEvent e) {
						OPPLView.this.execute
								.setEnabled(OPPLView.this.affectedAxioms
										.getModel().getSize() > 0);
					}
				});
		this.execute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ActionListModel model = (ActionListModel) OPPLView.this.affectedAxioms
						.getModel();
				JPanel panel = new JPanel(new BorderLayout(7, 7));
				JProgressBar progressBar = new JProgressBar();
				panel.add(progressBar, BorderLayout.SOUTH);
				panel
						.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
								10));
				progressBar.setIndeterminate(true);
				JLabel label = new JLabel(
						OPPL_COMPUTATION_IN_PROGRESS_PLEASE_WAIT);
				panel.add(label, BorderLayout.NORTH);
				OPPLView.this.window = new JDialog((Frame) SwingUtilities
						.getAncestorOfClass(Frame.class, OPPLView.this
								.getOWLEditorKit().getWorkspace()),
						"OPPL Engine progress", true);
				OPPLView.this.window.setLocation(400, 400);
				JPanel holderPanel = new JPanel(new BorderLayout(7, 7));
				holderPanel.add(panel, BorderLayout.NORTH);
				holderPanel.setBorder(BorderFactory.createEmptyBorder(5, 10,
						10, 10));
				OPPLView.this.window.getContentPane().setLayout(
						new BorderLayout());
				OPPLView.this.window.getContentPane().add(holderPanel,
						BorderLayout.NORTH);
				OPPLView.this.window.pack();
				Dimension windowSize = OPPLView.this.window.getSize();
				OPPLView.this.window.setSize(400, windowSize.height);
				OPPLView.this.window.setResizable(false);
				OPPLView.this.window.pack();
				List<OWLAxiomChange> changes = model.getOWLAxiomChanges();
				OPPLExecutorSwingWorker executorSwingWorker = new OPPLExecutorSwingWorker(
						changes);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						OPPLView.this.instantiatedScrollPane
								.setBorder(ComponentFactory
										.createTitledBorder("Instantiated axioms: "));
						OPPLView.this.affectedScrollPane
								.setBorder(ComponentFactory
										.createTitledBorder("Affected axioms: "));
						OPPLView.this.editor.clear();
						OPPLView.this.window.setVisible(true);
					}
				});
				executorSwingWorker.execute();
				model.clear();
			}
		});
		this.editor.addStatusChangedListener(this);
	}

	public void verifiedStatusChanged(boolean newState) {
		this.bindingTreeNodeModel = new DefaultTreeModel(
				new DefaultMutableTreeNode("Bindings"));
		this.bindingNodeTree.setModel(this.bindingTreeNodeModel);
		this.evaluate.setEnabled(newState);
		ListModel model = this.affectedAxioms.getModel();
		((DefaultListModel) model).clear();
		OPPLView.this.instantiatedScrollPane.setBorder(ComponentFactory
				.createTitledBorder("Instantiated axioms: "));
		OPPLView.this.affectedScrollPane.setBorder(ComponentFactory
				.createTitledBorder("Affected axioms: "));
		this.instantiatedAxiomsList.setModel(new DefaultListModel());
		if (newState) {
			this.statementModel = this.editor.getOPPLScript();
			this.bindingNodeTree.setCellRenderer(new BindingTreeRenderer(this
					.getOWLEditorKit(), this.statementModel
					.getConstraintSystem()));
		}
	}

	public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
			throws OWLException {
		ActionListModel model = (ActionListModel) OPPLView.this.affectedAxioms
				.getModel();
		model.clear();
		((DefaultListModel) this.instantiatedAxiomsList.getModel()).clear();
		OPPLView.this.instantiatedScrollPane.setBorder(ComponentFactory
				.createTitledBorder("Instantiated axioms: "));
		OPPLView.this.affectedScrollPane.setBorder(ComponentFactory
				.createTitledBorder("Affected axioms: "));
	}

	/**
	 * 
	 */
	private void updateInstantiatedAxioms(OPPLScript statementModel) {
		Map<BindingNode, Set<OWLAxiom>> instantiationMap = statementModel
				.getConstraintSystem().getInstantiatedAxioms();
		DefaultListModel model = new DefaultListModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.bindingNodeTree
				.getModel().getRoot();
		root.removeAllChildren();
		for (BindingNode leaf : instantiationMap.keySet()) {
			DefaultMutableTreeNode bindingTreeNode = new DefaultMutableTreeNode(
					leaf);
			root.add(bindingTreeNode);
			for (OWLAxiom axiom : instantiationMap.get(leaf)) {
				bindingTreeNode.add(new DefaultMutableTreeNode(axiom));
				if (!model.contains(axiom)) {
					model.addElement(axiom);
				}
			}
		}
		this.instantiatedAxiomsList.setModel(model);
		this.revalidate();
	}

	public void handleChange(OWLModelManagerChangeEvent event) {
		if (event.getType().equals(EventType.REASONER_CHANGED)) {
			OPPLParser.setReasoner(this.getOWLEditorKit().getModelManager()
					.getReasoner());
			// try {
			// this.statementModel = this.opplStatementExpressionEditor
			// .createObject();
			this.statementModel = this.editor.getOPPLScript();
			ActionListModel model = (ActionListModel) OPPLView.this.affectedAxioms
					.getModel();
			model.clear();
			DefaultListModel instantiatedModel = (DefaultListModel) this.instantiatedAxiomsList
					.getModel();
			instantiatedModel.clear();
			OPPLView.this.instantiatedScrollPane.setBorder(ComponentFactory
					.createTitledBorder("Instantiated axioms: "));
			OPPLView.this.affectedScrollPane.setBorder(ComponentFactory
					.createTitledBorder("Affected axioms: "));
			this.evaluate.setEnabled(true);
			// } catch (OWLExpressionParserException e) {
			// this.evaluate.setEnabled(false);
			// } catch (OWLException e) {
			// this.evaluate.setEnabled(false);
			// }
		}
	}
	// /**
	// *
	// */
	// private void removeKeyListeners() {
	// KeyListener[] keyListeners = this.opplStatementExpressionEditor
	// .getKeyListeners();
	// for (KeyListener keyListener : keyListeners) {
	// this.opplStatementExpressionEditor.removeKeyListener(keyListener);
	// }
	// }
}
