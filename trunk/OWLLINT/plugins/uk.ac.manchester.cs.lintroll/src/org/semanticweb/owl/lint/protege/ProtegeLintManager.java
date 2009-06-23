/**
 * 
 */
package org.semanticweb.owl.lint.protege;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.lint.LintFactory;
import org.semanticweb.owl.lint.LintManager;

import uk.ac.manchester.cs.owl.lint.LintManagerImpl;

/**
 * @author Luigi Iannone
 * 
 */
public class ProtegeLintManager extends LintManagerImpl implements LintManager {
	private OWLModelManager modelManager;

	/**
	 * @param modelManager
	 */
	public ProtegeLintManager(OWLModelManager modelManager) {
		super(modelManager.getOWLOntologyManager());
		this.modelManager = modelManager;
	}

	/**
	 * @see org.semanticweb.owl.lint.LintManager#getLintFactory()
	 */
	@Override
	public LintFactory getLintFactory() {
		return new ProtegeLintFactory(this.modelManager);
	}
}
