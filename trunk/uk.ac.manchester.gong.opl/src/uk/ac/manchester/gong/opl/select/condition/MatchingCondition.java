package uk.ac.manchester.gong.opl.select.condition;

import org.semanticweb.owl.model.OWLOntology;
import uk.ac.manchester.gong.opl.select.SelectStatementResultSet;

public interface MatchingCondition {
	public SelectStatementResultSet match (String SelectExpression, OWLOntology ontology);
	public String getConditionName ();
}
