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

import org.semanticweb.owl.model.OWLDataProperty;

import uk.ac.manchester.mae.MAEStart;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 17, 2008
 */
public class ExceptionReportWriter extends FormulaReportWriter {
	protected Exception exception;

	public ExceptionReportWriter(OWLDataProperty dataProperty,
			MAEStart formula, Exception e) {
		super(dataProperty, formula);
		this.exception = e;
	}

	@Override
	public Object visitPropertyEvaluationReport(
			PropertyEvaluationReport propertyEvaluationReport, Object data) {
		Object toReturn = null;
		if (this.formula == null) {
			toReturn = super.visitPropertyEvaluationReport(
					propertyEvaluationReport, data);
		} else {
			propertyEvaluationReport.getExceptions().add(this.exception);
		}
		return toReturn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.manchester.mae.report.ReportWriter#visitFormulaEvaluationReport(uk.ac.manchester.mae.report.FormulaEvaluationReport,
	 *      java.lang.Object)
	 */
	@Override
	public Object visitFormulaEvaluationReport(
			FormulaEvaluationReport formulaEvaluationReport, Object data) {
		formulaEvaluationReport.addException(this.exception);
		return null;
	}
}
