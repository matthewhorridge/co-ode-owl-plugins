package uk.ac.manchester.mae.evaluation;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataProperty;

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
        return dataProperty;
    }

    /**
     * @return the formulaEvaluationResults
     */
    public Set<FormulaEvaluationResult> getFormulaEvaluationResults() {
        return formulaEvaluationResults;
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder(dataProperty.toString());
        for (FormulaEvaluationResult formula : formulaEvaluationResults) {
            toReturn.append("\n");
            toReturn.append(formula);
        }
        return toReturn.toString();
    }
}
