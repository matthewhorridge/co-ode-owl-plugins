/**
 * 
 */
package org.semanticweb.owl.lint;

import java.util.Collection;

import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

/**
 * Lint that can be detected and can be acted upon
 * 
 * @author Luigi Iannone
 * 
 */
public interface ActingLint<O extends OWLObject> extends Lint<O> {
	/**
	 * Execute the actions for this ActingLint on the input ontologies
	 * 
	 * @param report
	 * @throws LintException
	 */
	void executeActions(Collection<? extends OWLOntology> ontologies) throws LintActionException;
}
