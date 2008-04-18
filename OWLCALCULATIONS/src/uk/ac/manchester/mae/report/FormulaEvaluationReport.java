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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.owl.model.OWLIndividual;

import uk.ac.manchester.mae.MAEStart;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 17, 2008
 */
public class FormulaEvaluationReport {
	protected MAEStart formula;
	protected Map<OWLIndividual, Object> individualReports = new HashMap<OWLIndividual, Object>();
	protected List<Exception> exceptions = new ArrayList<Exception>();

	/**
	 * @param formula
	 */
	public FormulaEvaluationReport(MAEStart formula) {
		this.formula = formula;
	}

	/**
	 * @return the exceptions
	 */
	public List<Exception> getExceptions() {
		return this.exceptions;
	}

	public Object accept(ReportVisitor visitor, Object data) {
		return visitor.visitFormulaEvaluationReport(this, data);
	}

	/**
	 * @return the formula
	 */
	public MAEStart getFormula() {
		return this.formula;
	}

	/**
	 * @return the individualReports
	 */
	public Map<OWLIndividual, Object> getIndividualReports() {
		return this.individualReports;
	}

	public void addException(Exception exception) {
		this.exceptions.add(exception);
	}
}
