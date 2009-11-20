/**
 *
 */
package org.coode.oppl.lint;

import org.coode.oppl.OPPLScript;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.utils.ParserFactory;
import org.coode.oppl.variablemansyntax.Variable;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLLintFactory implements OPPLLintAbstractFactory {
	private final OWLOntologyManager ontologyManager;
	private final OWLOntology ontology;

	/**
	 * @param ontologyManager
	 */
	public OPPLLintFactory(OWLOntology ontology,
			OWLOntologyManager ontologyManager) {
		this.ontologyManager = ontologyManager;
		this.ontology = ontology;
	}

	/**
	 * @see org.coode.oppl.lint.OPPLLintAbstractFactory#initOPPLParser(java.lang.String)
	 */
	public OPPLParser initOPPLParser(String string) {
		return ParserFactory.initParser(string, this.ontology, this
				.getOntologyManager(), null);
	}

	public OPPLLintScript buildOPPLLintScript(String name,
			OPPLScript opplScript, Variable v, String description) {
		return new OPPLLintScriptImpl(name, opplScript, v, description, this
				.getOntologyManager());
	}

	/**
	 * @return the ontologyManager
	 */
	public OWLOntologyManager getOntologyManager() {
		return this.ontologyManager;
	}
}
