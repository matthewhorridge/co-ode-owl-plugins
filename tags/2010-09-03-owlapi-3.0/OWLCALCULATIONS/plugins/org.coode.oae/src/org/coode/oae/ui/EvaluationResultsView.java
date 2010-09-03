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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owl.model.OWLIndividual;

import uk.ac.manchester.mae.report.EvaluationReport;
import uk.ac.manchester.mae.report.FormulaEvaluationReport;
import uk.ac.manchester.mae.report.PropertyEvaluationReport;
import uk.ac.manchester.mae.report.ReportVisitor;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 28, 2008
 */
@SuppressWarnings("serial")
public class EvaluationResultsView extends AbstractOWLViewComponent implements
		ReportVisitor {
	protected JTree tree;
	protected DefaultTreeModel treeModel;
	private DefaultMutableTreeNode root;

	/**
	 * @see org.protege.editor.owl.ui.view.AbstractOWLViewComponent#disposeOWLView()
	 */
	@Override
	protected void disposeOWLView() {
	}

	/**
	 * @see org.protege.editor.owl.ui.view.AbstractOWLViewComponent#initialiseOWLView()
	 */
	@Override
	protected void initialiseOWLView() throws Exception {
		EvaluationReport report = OWLCalculations.getLastEvaluationReport();
		if (report != null) {
			setLayout(new BorderLayout());
			setBorder(LineBorder.createBlackLineBorder());
			this.root = new DefaultMutableTreeNode("EVALUATION REPORT");
			this.treeModel = new DefaultTreeModel(this.root);
			this.tree = new JTree(this.treeModel);
			this.tree.setCellRenderer(new ReportTreeCellRenderer(
					getOWLEditorKit()));
			report.accept(this, this.root);
			this.add(ComponentFactory.createScrollPane(this.tree));
		}
	}

	public Object visitEvaluationReport(EvaluationReport evaluationReport,
			Object data) {
		Set<PropertyEvaluationReport> propertyEvaluationReports = evaluationReport
				.getPropertyEvaluationReports();
		DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) data;
		DefaultMutableTreeNode propertyNode;
		for (PropertyEvaluationReport propertyEvaluationReport : propertyEvaluationReports) {
			propertyNode = new DefaultMutableTreeNode(propertyEvaluationReport
					.getDataProperty());
			this.treeModel.insertNodeInto(propertyNode, currentNode, 0);
			propertyEvaluationReport.accept(this, propertyNode);
		}
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.report.ReportVisitor#visitFormulaEvaluationReport(uk.ac.manchester.mae.report.FormulaEvaluationReport,
	 *      java.lang.Object)
	 */
	public Object visitFormulaEvaluationReport(
			FormulaEvaluationReport formulaEvaluationReport, Object data) {
		MutableTreeNode formulaNode = new DefaultMutableTreeNode(
				formulaEvaluationReport.getFormula());
		this.treeModel.insertNodeInto(formulaNode, (MutableTreeNode) data, 0);
		Set<OWLIndividual> individuals = formulaEvaluationReport
				.getIndividuals();
		for (OWLIndividual individual : individuals) {
			MutableTreeNode individualNode = new DefaultMutableTreeNode(
					individual);
			formulaNode.insert(individualNode, 0);
			Object value = formulaEvaluationReport.getResult(individual)
					.getValues();
			if (value instanceof Collection<?>) {
				for (Object singleValue : (Collection<?>) value) {
					MutableTreeNode singleValueNode = new DefaultMutableTreeNode(
							singleValue);
					individualNode.insert(singleValueNode, 0);
				}
			} else {
				MutableTreeNode singleValueNode = new DefaultMutableTreeNode(
						value);
				individualNode.insert(singleValueNode, 0);
			}
		}
		List<Exception> exceptions = formulaEvaluationReport.getExceptions();
		for (Exception exception : exceptions) {
			MutableTreeNode exceptionNode = new DefaultMutableTreeNode(
					exception);
			formulaNode.insert(exceptionNode, 0);
		}
		return null;
	}

	// private void showReport(EvaluationReport report) {
	// this.root = new DefaultMutableTreeNode("EVALUATION REPORT");
	// this.treeModel = new DefaultTreeModel(this.root);
	// this.reportTree = new JTree(this.treeModel);
	// this.reportTree.setCellRenderer(new ReportTreeCellRenderer(this
	// .getOWLEditorKit()));
	// report.accept(this, this.root);
	// // JOptionPane pane = new JOptionPane(new JScrollPane(this.reportTree));
	// // JDialog dialog = pane.createDialog(this.getOWLWorkspace(),
	// // "Evaluation Report");
	// // dialog.setResizable(true);
	// // dialog.setVisible(true);
	//
	// this.getOWLWorkspace().showResultsView(resultView, true,
	// Workspace.BOTTOM_RESULTS_VIEW);
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.manchester.mae.report.ReportVisitor#visitEvaluationReport(uk.ac
	 * .manchester.mae.report.EvaluationReport, java.lang.Object)
	 */
	public Object visitPropertyEvaluationReport(
			PropertyEvaluationReport propertyEvaluationReport, Object data) {
		Set<FormulaEvaluationReport> formulaReports = propertyEvaluationReport
				.getFormulaEvaluationReports();
		for (Exception e : propertyEvaluationReport.getExceptions()) {
			MutableTreeNode exceptionNode = new DefaultMutableTreeNode(e);
			this.treeModel.insertNodeInto(exceptionNode,
					(MutableTreeNode) data, 0);
		}
		for (FormulaEvaluationReport formulaEvaluationReport : formulaReports) {
			formulaEvaluationReport.accept(this, data);
		}
		return null;
	}
}
