/**
 * 
 */
package uk.ac.manchester.cs.owl.lint;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.lint.InferenceLintPattern;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 */
public class InferenceLintPatternImpl implements InferenceLintPattern {
	protected OWLReasoner reasoner;
	protected OWLOntologyManager ontologyManager = OWLManager
			.createOWLOntologyManager();

	/**
	 * @param reasoner
	 * @param ontologyManager
	 */
	public InferenceLintPatternImpl(OWLReasoner reasoner,
			OWLOntologyManager ontologyManager) {
		this.reasoner = reasoner;
		this.ontologyManager = ontologyManager;
	}

	public InferenceLintPatternImpl(OWLReasoner reasoner) {
		this.reasoner = reasoner;
	}

	/**
	 * @see org.semanticweb.owl.lint.InferenceLintPattern#getOWLReasoner()
	 */
	public OWLReasoner getOWLReasoner() throws LintException {
		if (this.reasoner != null) {
			return this.reasoner;
		} else {
			throw new LintException("Null reasoner for this pattern");
		}
	}

	/**
	 * @return the ontologyManager
	 */
	public OWLOntologyManager getOWLOntologyManager() {
		return this.ontologyManager;
	}
}
