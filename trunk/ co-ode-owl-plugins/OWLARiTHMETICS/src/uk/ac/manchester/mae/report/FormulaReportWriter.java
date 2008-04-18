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
package uk.ac.manchester.mae.report;

import java.util.Set;

import org.semanticweb.owl.model.OWLDataProperty;

import uk.ac.manchester.mae.MAEStart;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 17, 2008
 */
public abstract class FormulaReportWriter implements ReportVisitor {
	protected OWLDataProperty dataProperty;
	protected MAEStart formula;

	/**
	 * @param dataProperty
	 * @param formula
	 */
	public FormulaReportWriter(OWLDataProperty dataProperty, MAEStart formula) {
		this.dataProperty = dataProperty;
		this.formula = formula;
	}

	public Object visitEvaluationReport(EvaluationReport evaluationReport,
			Object data) {
		Set<PropertyEvaluationReport> formulaEvaluationReports = evaluationReport
				.getPropertyEvaluationReports();
		boolean existing = false;
		for (PropertyEvaluationReport propertyEvaluationReport : formulaEvaluationReports) {
			if (this.dataProperty.equals(propertyEvaluationReport
					.getDataProperty())) {
				propertyEvaluationReport.accept(this, null);
				existing = true;
			}
		}
		if (!existing) {
			PropertyEvaluationReport propertyEvaluationReport = new PropertyEvaluationReport(
					this.dataProperty);
			propertyEvaluationReport.accept(this, data);
			evaluationReport.getPropertyEvaluationReports().add(
					propertyEvaluationReport);
		}
		return null;
	}

	public abstract Object visitFormulaEvaluationReport(
			FormulaEvaluationReport formulaEvaluationReport, Object data);

	public Object visitPropertyEvaluationReport(
			PropertyEvaluationReport propertyEvaluationReport, Object data) {
		Set<FormulaEvaluationReport> formulaEvaluationReports = propertyEvaluationReport
				.getFormulaEvaluationReports();
		boolean existing = false;
		for (FormulaEvaluationReport formulaEvaluationReport : formulaEvaluationReports) {
			if (this.formula.equals(formulaEvaluationReport.getFormula())) {
				formulaEvaluationReport.accept(this, null);
				existing = true;
			}
		}
		if (!existing) {
			FormulaEvaluationReport formulaEvaluationReport = new FormulaEvaluationReport(
					this.formula);
			formulaEvaluationReport.accept(this, data);
			propertyEvaluationReport.getFormulaEvaluationReports().add(
					formulaEvaluationReport);
		}
		return null;
	}
}
