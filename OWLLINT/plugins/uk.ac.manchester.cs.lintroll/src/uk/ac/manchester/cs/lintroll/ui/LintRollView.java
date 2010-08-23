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
package uk.ac.manchester.cs.lintroll.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.coode.lint.protege.ProtegeLintManager;
import org.coode.lint.protege.ProtegeLintManager.LintSelectionListener;
import org.jdesktop.swingworker.SwingWorker;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.inference.NoOpReasoner;
import org.protege.editor.owl.ui.tree.OWLLinkedObjectTree;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.lint.ActingLint;
import org.semanticweb.owlapi.lint.Lint;
import org.semanticweb.owlapi.lint.LintActionException;
import org.semanticweb.owlapi.lint.LintManager;
import org.semanticweb.owlapi.lint.LintReport;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationChangeEvent;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationChangeListener;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.owl.lint.LintManagerFactory;
import uk.ac.manchester.cs.owl.lint.commons.DefaultLintVisitorAdapter;
import uk.ac.manchester.cs.owl.lint.commons.LintVisitorAdapter;

/**
 * @author Luigi Iannone
 * 
 * 
 *         http://www.cs.man.ac.uk/~iannonel
 * 
 *         The University Of Manchester Bio Health Informatics Group Date:
 *         February 11, 2008
 * 
 */
public class LintRollView extends AbstractOWLViewComponent implements
		TreeSelectionListener, HierarchyListener {
	/**
	 * @author Luigi Iannone
	 * 
	 */
	public class DetectLintAction extends AbstractAction {
		public DetectLintAction() {
			super("Detect Lint", new ImageIcon(DetectLintAction.class
					.getClassLoader().getResource("lintroll.jpg")));
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -570991495654619228L;

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			LintRollView.this.lintSelectionPanel.setEnabled(false);
			LintRollView.this.lintReportTreeModel.setIsRunning(true);
			LintRollView.this.lintReportTree
					.setModel(LintRollView.this.lintReportTreeModel);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					SwingWorker<Set<LintReport<?>>, Collection<? extends Lint<?>>> lintDetectionSwingWorker = new LintDetectionSwingWorker();
					lintDetectionSwingWorker.execute();
					DetectLintAction.this.setEnabled(false);
				}
			});
		}
	}

	private final class LintDetectionSwingWorker extends
			SwingWorker<Set<LintReport<?>>, Collection<? extends Lint<?>>> {
		@Override
		protected Set<LintReport<?>> doInBackground() throws Exception {
			Set<LintReport<?>> toReturn = LintRollView.this.lintManager.run(
					ProtegeLintManager.getInstance(
							LintRollView.this.getOWLEditorKit())
							.getSelectedLints(), LintRollView.this
							.getOWLModelManager().getOntologies());
			LintRollView.this.lintReportTreeModel.setDirty(false);
			return toReturn;
		}

		@Override
		protected void done() {
			try {
				LintRollView.this.displayReport(this.get());
			} catch (InterruptedException e) {
				LintRollView.this.lintReportTreeModel.setIsRunning(false);
				ProtegeApplication.getErrorLog().logError(e);
			} catch (ExecutionException e) {
				LintRollView.this.lintReportTreeModel.setIsRunning(false);
				ProtegeApplication.getErrorLog().logError(e);
			} finally {
				LintRollView.this.lintSelectionPanel.setEnabled(true);
				LintRollView.this.detectLintAction.setEnabled(false);
			}
		}
	}

	private JButton fixButton = new JButton("Fix");
	private static final long serialVersionUID = 2527582629024593024L;
	private OWLLinkedObjectTree lintReportTree = null;
	private JTextArea lintDescriptionTextArea = new JTextArea();
	private JTextArea explanationTextArea = new JTextArea();
	private LintReportTreeModel lintReportTreeModel;
	private LintManager lintManager;
	private final DetectLintAction detectLintAction = new DetectLintAction();
	private LintSelectionPanel lintSelectionPanel;
	private final LintSelectionListener lintSelectionListener = new LintSelectionListener() {
		public void selectionChanged() {
			LintRollView.this.lintReportTreeModel.setSelectionChanged(true);
			LintRollView.this.enableDetectLints();
			if (!ProtegeLintManager.getInstance(
					LintRollView.this.getOWLEditorKit()).getSelectedLints()
					.isEmpty()) {
				for (Lint<?> lint : ProtegeLintManager.getInstance(
						LintRollView.this.getOWLEditorKit()).getSelectedLints()) {
					lint
							.getLintConfiguration()
							.addLintConfigurationChangeListener(
									LintRollView.this.lodedLintConfigurationChangeListener);
				}
			}
		}
	};
	private final OWLOntologyChangeListener ontologyChangeListener = new OWLOntologyChangeListener() {
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
				throws OWLException {
			if (LintRollView.this.isShowing()) {
				LintRollView.this.enableDetectLints();
			} else {
				LintRollView.this.lintReportTreeModel.setDirty(true);
			}
		}
	};
	private OWLModelManagerListener modelManagerListener = new OWLModelManagerListener() {
		public void handleChange(OWLModelManagerChangeEvent event) {
			if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
				LintRollView.this.lintReportTreeModel.setDirty(true);
				LintRollView.this.enableDetectLints();
			}
			if (event.isType(EventType.REASONER_CHANGED)) {
				LintRollView.this.resetLintManager();
			}
		}
	};
	private LintConfigurationChangeListener lodedLintConfigurationChangeListener = new LintConfigurationChangeListener() {
		public void configurationChanged(LintConfigurationChangeEvent event) {
			LintRollView.this.enableDetectLints();
		}
	};

	@Override
	protected void initialiseOWLView() throws Exception {
		this.getOWLEditorKit().getOWLModelManager().addOntologyChangeListener(
				this.ontologyChangeListener);
		this.getOWLEditorKit().getOWLModelManager().addListener(
				this.modelManagerListener);
		this.resetLintManager();
		this.setLayout(new BorderLayout());
		this.lintReportTreeModel = new LintReportTreeModel(this
				.getOWLEditorKit());
		this.lintReportTree = new OWLLinkedObjectTree(this.getOWLEditorKit());
		this.lintReportTree.setModel(this.lintReportTreeModel);
		this.lintReportTree.setCellRenderer(new LintRenderer(this
				.getOWLEditorKit()));
		this.lintReportTree.addTreeSelectionListener(this);
		this.lintSelectionPanel = new LintSelectionPanel(this.getOWLEditorKit()) {
			private static final long serialVersionUID = -944782945498849392L;

			@Override
			protected void lintSelected(Lint<?> lint) {
				ProtegeLintManager.getInstance(
						LintRollView.this.getOWLEditorKit()).addSelectedLint(
						lint);
			}

			@Override
			protected void lintDeSelected(Lint<?> lint) {
				ProtegeLintManager.getInstance(
						LintRollView.this.getOWLEditorKit())
						.removeSelectedLint(lint);
			}
		};
		JToolBar runToolBar = ComponentFactory.createViewToolBar();
		runToolBar.add(this.detectLintAction);
		this.detectLintAction.setEnabled(false);
		JPanel leftPanel = new JPanel(new BorderLayout());
		JScrollPane treePane = ComponentFactory
				.createScrollPane(this.lintReportTree);
		leftPanel.add(runToolBar, BorderLayout.NORTH);
		leftPanel.add(treePane);
		JPanel reportPanel = new JPanel(new BorderLayout());
		JSplitPane centrePanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		centrePanel.setLeftComponent(leftPanel);
		centrePanel.setRightComponent(this.lintSelectionPanel);
		centrePanel.setResizeWeight(.5);
		centrePanel.setDividerLocation(.5);
		reportPanel.add(centrePanel, BorderLayout.CENTER);
		reportPanel.add(this.fixButton, BorderLayout.SOUTH);
		this.fixButton.setEnabled(false);
		this.fixButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode selected = (DefaultMutableTreeNode) LintRollView.this.lintReportTree
						.getLastSelectedPathComponent();
				Object userObject = selected.getUserObject();
				if (selected != null
						&& LintReport.class.isAssignableFrom(userObject
								.getClass())) {
					final LintReport<?> lintReport = (LintReport<?>) userObject;
					lintReport.getLint().accept(new LintVisitorAdapter() {
						@Override
						public void visitActingLint(ActingLint<?> actingLint) {
							try {
								actingLint.executeActions(lintReport
										.getAffectedOntologies());
							} catch (LintActionException e) {
								ProtegeApplication.getErrorLog().logError(e);
								JOptionPane.showMessageDialog(
										LintRollView.this,
										"Could not execute the actions");
							}
						}
					});
				}
			}
		});
		reportPanel.setBorder(ComponentFactory
				.createTitledBorder("Lint Report"));
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainSplitPane.setTopComponent(reportPanel);
		JScrollPane lintDescriptionPane = new JScrollPane(
				this.lintDescriptionTextArea);
		JScrollPane explanationPane = new JScrollPane(this.explanationTextArea);
		JPanel explanationPanel = new JPanel(new BorderLayout());
		explanationPanel.add(explanationPane);
		explanationPanel.setBorder(ComponentFactory
				.createTitledBorder("Explanation"));
		this.explanationTextArea.setEditable(false);
		this.explanationTextArea.setColumns(40);
		this.explanationTextArea.setRows(3);
		JPanel lintDescriptionPanel = new JPanel(new BorderLayout());
		lintDescriptionPanel.add(lintDescriptionPane);
		lintDescriptionPanel.setBorder(ComponentFactory
				.createTitledBorder("Lint Description "));
		this.lintDescriptionTextArea.setEditable(false);
		this.lintDescriptionTextArea.setColumns(40);
		this.lintDescriptionTextArea.setRows(3);
		this.lintDescriptionTextArea.setWrapStyleWord(true);
		JSplitPane bottomSplitPanel = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		bottomSplitPanel.setLeftComponent(lintDescriptionPanel);
		bottomSplitPanel.setRightComponent(explanationPanel);
		mainSplitPane.setBottomComponent(bottomSplitPanel);
		this.add(mainSplitPane, BorderLayout.CENTER);
		this.addHierarchyListener(this);
		ProtegeLintManager.getInstance(this.getOWLEditorKit())
				.addLintSelectionListener(this.lintSelectionListener);
	}

	/**
	 * 
	 */
	private void displayReport(Collection<? extends LintReport<?>> lintReports) {
		this.lintReportTreeModel.setReports(lintReports);
		this.lintReportTree.removeTreeSelectionListener(this);
		this.lintReportTree.clearSelection();
		this.lintDescriptionTextArea.setText("");
		this.lintReportTree.addTreeSelectionListener(this);
		this.lintReportTree.revalidate();
		this.revalidate();
	}

	@Override
	protected void disposeOWLView() {
		this.getOWLEditorKit().getModelManager().removeOntologyChangeListener(
				this.ontologyChangeListener);
		this.getOWLEditorKit().getModelManager().removeListener(
				this.modelManagerListener);
		ProtegeLintManager instance = ProtegeLintManager.getInstance(this
				.getOWLEditorKit());
		instance.removeLintSelectionListener(this.lintSelectionListener);
		this.lintReportTreeModel.dispose();
		try {
			for (Lint<?> lint : instance.getSelectedLints()) {
				lint.getLintConfiguration()
						.removeLintConfigurationChangeListener(
								this.lodedLintConfigurationChangeListener);
			}
			instance.dispose();
		} catch (Exception e) {
			ProtegeApplication.getErrorLog().logError(e);
		}
	}

	/**
	 * 
	 */
	private void enableDetectLints() {
		this.detectLintAction.setEnabled(!ProtegeLintManager.getInstance(
				this.getOWLEditorKit()).getSelectedLints().isEmpty());
	}

	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) LintRollView.this.lintReportTree
				.getLastSelectedPathComponent();
		if (selected != null) {
			if (selected.getUserObject() instanceof LintReport<?>) {
				LintReport<?> lintReport = (LintReport<?>) selected
						.getUserObject();
				this.lintDescriptionTextArea.setText(lintReport.getLint()
						.getDescription());
				this.explanationTextArea.setText("");
				Lint<?> aLint = lintReport.getLint();
				aLint.accept(new DefaultLintVisitorAdapter() {
					@Override
					protected void doDefault(Lint<?> lint) {
						LintRollView.this.fixButton.setEnabled(false);
					}

					@Override
					public void visitActingLint(ActingLint<?> actingLint) {
						LintRollView.this.fixButton.setEnabled(true);
					}
				});
			} else if (selected.getUserObject() instanceof OWLObject
					&& selected.isLeaf()) {
				this.fixButton.setEnabled(false);
				OWLObject object = (OWLObject) selected.getUserObject();
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selected
						.getParent();
				OWLOntology ontology = (OWLOntology) parent.getUserObject();
				DefaultMutableTreeNode reportNode = (DefaultMutableTreeNode) parent
						.getParent();
				LintReport<?> report = (LintReport<?>) reportNode
						.getUserObject();
				String explanation = report.getExplanation(object, ontology);
				if (explanation != null) {
					this.explanationTextArea.setText(explanation);
				}
			} else {
				this.explanationTextArea.setText("");
			}
		} else {
			this.explanationTextArea.setText("");
		}
	}

	public void hierarchyChanged(HierarchyEvent e) {
		if (this.isShowing() && this.lintReportTreeModel.isDirty()) {
			this.enableDetectLints();
		}
	}

	/**
	 * 
	 */
	private void resetLintManager() {
		OWLReasoner reasoner = LintRollView.this.getOWLEditorKit()
				.getOWLModelManager().getReasoner();
		LintRollView.this.lintManager = LintManagerFactory.getInstance(
				LintRollView.this.getOWLEditorKit().getOWLModelManager()
						.getOWLOntologyManager(),
				reasoner instanceof NoOpReasoner ? null : reasoner)
				.getLintManager();
	}
}
