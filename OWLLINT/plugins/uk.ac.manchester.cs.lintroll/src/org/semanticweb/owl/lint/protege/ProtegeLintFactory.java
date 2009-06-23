/**
 * 
 */
package org.semanticweb.owl.lint.protege;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.lint.InferenceLintPattern;
import org.semanticweb.owl.lint.LintFactory;

import uk.ac.manchester.cs.owl.lint.LintFactoryImpl;

/**
 * @author Luigi Iannone
 * 
 */
public class ProtegeLintFactory extends LintFactoryImpl implements LintFactory {
	private OWLModelManager modelManager;

	/**
	 * @param modelManager
	 */
	public ProtegeLintFactory(OWLModelManager modelManager) {
		super(modelManager.getOWLOntologyManager());
		this.modelManager = modelManager;
		this.reasoner = this.modelManager.getReasoner();
	}

	@Override
	public InferenceLintPattern createInferenceLintPattern() {
		return super.createInferenceLintPattern();
	}
}
