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
package uk.ac.manchester.mae.evaluation;

import java.util.Set;

import uk.ac.manchester.mae.parser.MAEStart;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 30, 2008
 */
public class FormulaEvaluationResult {
	MAEStart formula;
	Set<IndividualEvaluationResult> individualEvaluationResults;

	/**
	 * @param formula
	 * @param individualEvaluationResults
	 */
	public FormulaEvaluationResult(MAEStart formula,
			Set<IndividualEvaluationResult> individualEvaluationResults) {
		this.formula = formula;
		this.individualEvaluationResults = individualEvaluationResults;
	}

	/**
	 * @return the formula
	 */
	public MAEStart getFormula() {
		return this.formula;
	}

	/**
	 * @return the individualEvaluationResults
	 */
	public Set<IndividualEvaluationResult> getIndividualEvaluationResults() {
		return this.individualEvaluationResults;
	}

	@Override
	public String toString() {
		StringBuilder toReturn = new StringBuilder(this.formula.toString());
		for (IndividualEvaluationResult i : this.individualEvaluationResults) {
			toReturn.append("\n");
			toReturn.append(i);
		}
		return toReturn.toString();
	}
}
