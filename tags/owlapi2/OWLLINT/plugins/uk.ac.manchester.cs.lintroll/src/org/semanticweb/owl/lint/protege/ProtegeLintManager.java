/**
 * 
 */
package org.semanticweb.owl.lint.protege;

import java.util.Collection;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintFactory;
import org.semanticweb.owl.lint.LintManager;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.cs.owl.lint.LintManagerImpl;

/**
 * @author Luigi Iannone
 * 
 */
public class ProtegeLintManager implements LintManager {
	private final LintManager delegate;

	/**
	 * @param modelManager
	 */
	public ProtegeLintManager(OWLModelManager modelManager) {
		if (modelManager == null) {
			throw new NullPointerException("The model manager cannot be null");
		}
		this.delegate = new LintManagerImpl(modelManager.getOWLOntologyManager(),
				modelManager.getReasoner());
	}

	/**
	 * @param lints
	 * @param targets
	 * @return
	 * @throws LintException
	 * @see org.semanticweb.owl.lint.LintManager#run(java.util.Set,
	 *      java.util.Set)
	 */
	public Set<LintReport<?>> run(Collection<? extends Lint<?>> lints,
			Collection<? extends OWLOntology> targets) throws LintException {
		return this.delegate.run(lints, targets);
	}

	/**
	 * @return
	 * @see org.semanticweb.owl.lint.LintManager#getLintFactory()
	 */
	public LintFactory getLintFactory() {
		return this.delegate.getLintFactory();
	}
}
