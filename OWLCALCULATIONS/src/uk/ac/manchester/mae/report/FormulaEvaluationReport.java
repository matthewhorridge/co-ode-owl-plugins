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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.model.OWLIndividual;

import uk.ac.manchester.mae.MAEStart;
import uk.ac.manchester.mae.evaluation.EvaluationResult;
import uk.ac.manchester.mae.evaluation.IndividualEvaluationResult;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Mar 17, 2008
 */
public class FormulaEvaluationReport {
	protected MAEStart formula;
	protected Set<IndividualEvaluationResult> results = new HashSet<IndividualEvaluationResult>();
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

	public void addException(Exception exception) {
		this.exceptions.add(exception);
	}

	/**
	 * @return the results
	 */
	public Set<IndividualEvaluationResult> getResults() {
		return this.results;
	}

	/**
	 * @return the set of individuals whose value for the formula has been
	 *         evaluated
	 */
	public Set<OWLIndividual> getIndividuals() {
		Set<OWLIndividual> toReturn = new HashSet<OWLIndividual>();
		for (IndividualEvaluationResult individualEvaluationResult : this.results) {
			toReturn.add(individualEvaluationResult.getIndividual());
		}
		return toReturn;
	}

	/**
	 * @param individual
	 * @return the result for the formula for the input OWLIndividual, it may be
	 *         null if the individual has no value for the formula
	 */
	public EvaluationResult getResult(OWLIndividual individual) {
		// EvaluationResult toReturn = null;
		// boolean found = false;
		Iterator<IndividualEvaluationResult> it = this.results.iterator();
		IndividualEvaluationResult result = null;
		while (it.hasNext()) {
			result = it.next();
			// found = ;
			if (result.getIndividual().equals(individual)) {
				return result.getResults();
			}
		}
		return null;
		// if (found) {
		// toReturn = result.getResults();
		// }
		// return toReturn;
	}
}
