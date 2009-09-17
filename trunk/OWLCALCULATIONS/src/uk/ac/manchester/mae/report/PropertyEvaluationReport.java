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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.model.OWLDataProperty;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 27, 2008
 */
public class PropertyEvaluationReport {
	protected OWLDataProperty dataProperty;
	protected Set<FormulaEvaluationReport> formulaEvaluationReports = new HashSet<FormulaEvaluationReport>();
	protected List<Exception> exceptions = new ArrayList<Exception>();

	/**
	 * @param dataProperty
	 */
	public PropertyEvaluationReport(OWLDataProperty dataProperty) {
		this.dataProperty = dataProperty;
	}

	public Object accept(ReportVisitor visitor, Object data) {
		return visitor.visitPropertyEvaluationReport(this, data);
	}

	/**
	 * @return the dataProperty
	 */
	public OWLDataProperty getDataProperty() {
		return this.dataProperty;
	}

	/**
	 * @return the formulaEvaluationReports
	 */
	public Set<FormulaEvaluationReport> getFormulaEvaluationReports() {
		return this.formulaEvaluationReports;
	}

	/**
	 * @return the exceptions
	 */
	public List<Exception> getExceptions() {
		return this.exceptions;
	}
}
