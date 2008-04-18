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
package org.coode.oae.ui.action;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.coode.oae.ui.ReportTreeCellRenderer;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntologyChangeException;

import uk.ac.manchester.mae.EvaluationException;
import uk.ac.manchester.mae.ProtegeEvaluator;
import uk.ac.manchester.mae.report.EvaluationReport;
import uk.ac.manchester.mae.report.FormulaEvaluationReport;
import uk.ac.manchester.mae.report.PropertyEvaluationReport;
import uk.ac.manchester.mae.report.ReportVisitor;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 17, 2008
 */
public class EvaluateAction extends ProtegeOWLAction implements ReportVisitor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTree reportTree;
	DefaultTreeModel treeModel;
	private MutableTreeNode root = null;

	/**
	 * @see org.protege.editor.core.plugin.ProtegePluginInstance#dispose()
	 */
	public void dispose() throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * @see org.protege.editor.core.plugin.ProtegePluginInstance#initialise()
	 */
	public void initialise() throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		ProtegeEvaluator protegeEvaluator = null;
		try {
			protegeEvaluator = new ProtegeEvaluator(this.getOWLModelManager());
			Object evaluationResult = protegeEvaluator.evaluate(true);
			this.showReport(protegeEvaluator.getReport());
		} catch (OWLReasonerException e) {
			JOptionPane
					.showMessageDialog(this.getWorkspace(),
							"Exception in creating the reasoner, impossible to evaluate the formulas");
			e.printStackTrace();
		} catch (OWLOntologyChangeException e) {
			JOptionPane.showMessageDialog(this.getWorkspace(),
					"Exception in saving the computed values in the ontology");
			e.printStackTrace();
		} catch (EvaluationException e) {
			JOptionPane
					.showMessageDialog(this.getWorkspace(),
							"Exception in computing the values please see the report for more details");
			e.printStackTrace();
		} finally {
			if (protegeEvaluator != null) {
				protegeEvaluator.getReport();
			}
		}
	}

	private void showReport(EvaluationReport report) {
		this.root = new DefaultMutableTreeNode("EVALUATION REPORT");
		this.treeModel = new DefaultTreeModel(this.root);
		this.reportTree = new JTree(this.treeModel);
		this.reportTree.setCellRenderer(new ReportTreeCellRenderer(this
				.getOWLEditorKit()));
		report.accept(this, this.root);
		JOptionPane pane = new JOptionPane(new JScrollPane(this.reportTree));
		JDialog dialog = pane.createDialog(this.getOWLWorkspace(),
				"Evaluation Report");
		dialog.setResizable(true);
		dialog.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.manchester.mae.report.ReportVisitor#visitEvaluationReport(uk.ac.manchester.mae.report.EvaluationReport,
	 *      java.lang.Object)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.manchester.mae.report.ReportVisitor#visitFormulaEvaluationReport(uk.ac.manchester.mae.report.FormulaEvaluationReport,
	 *      java.lang.Object)
	 */
	public Object visitFormulaEvaluationReport(
			FormulaEvaluationReport formulaEvaluationReport, Object data) {
		MutableTreeNode formulaNode = new DefaultMutableTreeNode(
				formulaEvaluationReport.getFormula());
		this.treeModel.insertNodeInto(formulaNode, (MutableTreeNode) data, 0);
		Set<OWLIndividual> individuals = formulaEvaluationReport
				.getIndividualReports().keySet();
		for (OWLIndividual individual : individuals) {
			MutableTreeNode individualNode = new DefaultMutableTreeNode(
					individual);
			formulaNode.insert(individualNode, 0);
			Object value = formulaEvaluationReport.getIndividualReports().get(
					individual);
			if (value instanceof Collection) {
				for (Object singleValue : (Collection<Object>) value) {
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
}
