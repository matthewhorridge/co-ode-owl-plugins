/**
 * 
 */
package org.coode.oppl.lint;

import java.util.Formatter;
import java.util.List;
import java.util.Set;

import org.coode.oppl.OPPLScript;
import org.coode.oppl.Variable;
import org.semanticweb.owl.lint.ActingLint;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * An OPPL Script that can be used as a basis for the LInt
 * 
 * @author Luigi Iannone
 * 
 */
public abstract class OPPLLintScript implements OPPLScript, ActingLint<OWLObject> {
	public abstract Set<OWLObject> getDetectedObjects(OWLOntology ontology,
			OWLOntologyManager ontologyManager);

	public abstract List<OWLAxiomChange> getChanges(OWLOntology ontology,
			OWLOntologyManager ontologyManager);

	public abstract Variable getReturnVariable();

	/**
	 * Retrieves the OPPLScript upon which this OPPLLintScript has beeen built.
	 * 
	 * @return an OPPLScript.
	 */
	public abstract OPPLScript getOPPLScript();

	@Override
	public String toString() {
		Formatter formatter = new Formatter();
		formatter.format(
				"%s; %s RETURN %s; %s ",
				this.getName(),
				this.getOPPLScript().render(),
				this.getReturnVariable().getName(),
				this.getDescription());
		return formatter.out().toString();
	}
}
