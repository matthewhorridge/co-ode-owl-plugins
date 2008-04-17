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
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.editor.owl.ui.view.OWLOntologyAnnotationViewComponent;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintManager;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeListener;

import uk.ac.manchester.cs.lintroll.ui.preference.LintRollPreferenceChangeEvent;
import uk.ac.manchester.cs.lintroll.ui.preference.LintRollPreferenceChangeListener;
import uk.ac.manchester.cs.lintroll.ui.preference.LintRollPreferences;
import uk.ac.manchester.cs.owl.lint.LintManagerFactory;

// TODO Manage the possibility of applying only to the active ontology
/**
 * @author Luigi Iannone
 * 
 * 
 * http://www.cs.man.ac.uk/~iannonel
 * 
 * The University Of Manchester Bio Health Informatics Group Date: February 11,
 * 2008
 * 
 */
public class LintRollView extends AbstractOWLViewComponent implements
		OWLOntologyChangeListener, LintRollPreferenceChangeListener,
		OWLModelManagerListener, TreeSelectionListener {
	private static final Logger logger = Logger
			.getLogger(OWLOntologyAnnotationViewComponent.class);
	private OWLOntologyChangeListener listener;
	private static final long serialVersionUID = 2527582629024593024L;
	JTree lintReportTree = null;
	JTextArea lintDescriptionTextArea = new JTextArea();
	DefaultTreeModel lintReportTreeModel;
	private LintManager lintManager;

	@Override
	protected void initialiseOWLView() throws Exception {
		this.listener = this;
		this.getWorkspace();
		LintRollPreferences.addLintRollPreferenceChangeListener(this);
		this.getOWLModelManager().getOWLOntologyManager()
				.addOntologyChangeListener(this.listener);
		this.setLayout(new BorderLayout());
		this.lintReportTree = new JTree(this.lintReportTreeModel);
		this.lintReportTree.addTreeSelectionListener(this);
		this.lintReportTree.setCellRenderer(new LintRenderer(this
				.getOWLEditorKit()));
		this.lintManager = LintManagerFactory.getLintManager();
		Set<LintReport> reports = this.lintManager.run(LintRollPreferences
				.getSelectedLints(), this.getOWLModelManager().getOntologies());
		this.displayReport(reports);
		JScrollPane treePane = new JScrollPane(this.lintReportTree);
		treePane.setBorder(new TitledBorder("Lint Report"));
		this.add(treePane, BorderLayout.NORTH);
		JScrollPane lintDescriptionPane = new JScrollPane(
				this.lintDescriptionTextArea);
		lintDescriptionPane.setBorder(new TitledBorder("Lint Description: "));
		this.lintDescriptionTextArea.setEditable(false);
		this.add(lintDescriptionPane, BorderLayout.SOUTH);
	}

	/**
	 * 
	 */
	private void displayReport(Set<LintReport> lintReports) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Lint Reports");
		this.lintReportTreeModel = new DefaultTreeModel(root);
		this.lintReportTree.removeTreeSelectionListener(this);
		this.lintReportTree.clearSelection();
		this.lintDescriptionTextArea.setText("");
		for (LintReport lintReport : lintReports) {
			DefaultMutableTreeNode lintNode = new DefaultMutableTreeNode(
					lintReport);
			for (OWLOntology ontology : lintReport.getAffectedOntologies()) {
				DefaultMutableTreeNode ontologyNode = new DefaultMutableTreeNode(
						ontology);
				for (OWLObject owlObject : lintReport
						.getAffectedOWLObjects(ontology)) {
					DefaultMutableTreeNode owlObjectNode = new DefaultMutableTreeNode(
							owlObject);
					this.lintReportTreeModel.insertNodeInto(owlObjectNode,
							ontologyNode, 0);
				}
				this.lintReportTreeModel.insertNodeInto(ontologyNode, lintNode,
						0);
			}
			this.lintReportTreeModel.insertNodeInto(lintNode, root, 0);
		}
		this.lintReportTree.setModel(this.lintReportTreeModel);
		this.lintReportTree.addTreeSelectionListener(this);
		this.lintReportTree.revalidate();
		this.revalidate();
	}

	@Override
	protected void disposeOWLView() {
		// TODO Auto-generated method stub
	}

	public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
			throws OWLException {
		this.lintManager = LintManagerFactory.getLintManager();
		Set<LintReport> reports;
		try {
			reports = this.lintManager.run(
					LintRollPreferences.getLoadedLints(), this
							.getOWLModelManager().getOntologies());
			this.displayReport(reports);
		} catch (LintException e) {
			logger.error("Error in running the lint roll ", e);
		}
	}

	public void handleChange(LintRollPreferenceChangeEvent e) {
		if (e
				.getType()
				.equals(
						uk.ac.manchester.cs.lintroll.ui.preference.EventType.SELECTED_LINT_CHANGE)) {
			Set<LintReport> reports;
			try {
				reports = this.lintManager.run(LintRollPreferences
						.getSelectedLints(), this.getOWLModelManager()
						.getOntologies());
				this.displayReport(reports);
			} catch (LintException lintException) {
				logger.error("Error in running the lint roll ", lintException);
			}
		}
	}

	public void handleChange(OWLModelManagerChangeEvent event) {
		if (event.getType().equals(EventType.ONTOLOGY_CLASSIFIED)) {
			try {
				Set<LintReport> reports = this.lintManager.run(
						LintRollPreferences.getSelectedLints(), this
								.getOWLModelManager().getOntologies());
				this.displayReport(reports);
			} catch (LintException lintException) {
				logger.error("Error in running the lint roll ", lintException);
			}
		}
	}

	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) LintRollView.this.lintReportTree
				.getLastSelectedPathComponent();
		if (selected.getUserObject() instanceof LintReport) {
			this.lintDescriptionTextArea.setText(((LintReport) selected
					.getUserObject()).getLint().getDescription());
		}
	}
}
