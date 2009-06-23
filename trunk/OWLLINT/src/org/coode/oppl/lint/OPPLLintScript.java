/**
 * 
 */
package org.coode.oppl.lint;

import java.util.List;
import java.util.Set;

import org.coode.oppl.OPPLScript;
import org.coode.oppl.variablemansyntax.Variable;
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
public abstract class OPPLLintScript implements OPPLScript, ActingLint {
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
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getName());
		buffer.append("; ");
		buffer.append(this.getOPPLScript().render());
		buffer.append("RETURN ");
		buffer.append(this.getReturnVariable().getName());
		buffer.append("; ");
		buffer.append(this.getDescription());
		return buffer.toString();
	}
}
