package uk.ac.manchester.mae.evaluation;

import java.util.Set;

import org.semanticweb.owl.model.OWLDataProperty;

public class PropertyEvaluationResult {
	OWLDataProperty dataProperty;
	Set<FormulaEvaluationResult> formulaEvaluationResults;

	/**
	 * @param dataProperty
	 * @param formulaEvaluationResults
	 */
	public PropertyEvaluationResult(OWLDataProperty dataProperty,
			Set<FormulaEvaluationResult> formulaEvaluationResults) {
		this.dataProperty = dataProperty;
		this.formulaEvaluationResults = formulaEvaluationResults;
	}

	/**
	 * @return the dataProperty
	 */
	public OWLDataProperty getDataProperty() {
		return this.dataProperty;
	}

	/**
	 * @return the formulaEvaluationResults
	 */
	public Set<FormulaEvaluationResult> getFormulaEvaluationResults() {
		return this.formulaEvaluationResults;
	}

	@Override
	public String toString() {
		String toReturn = this.dataProperty.toString();
		for (FormulaEvaluationResult formulaEvaluationResult : this.formulaEvaluationResults) {
			toReturn += "\n" + formulaEvaluationResult;
		}
		return toReturn;
	}
}
