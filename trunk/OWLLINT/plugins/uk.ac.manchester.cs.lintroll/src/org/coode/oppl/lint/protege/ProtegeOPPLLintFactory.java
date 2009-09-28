/**
 * 
 */
package org.coode.oppl.lint.protege;

import org.coode.oppl.OPPLScript;
import org.coode.oppl.lint.OPPLLintAbstractFactory;
import org.coode.oppl.lint.OPPLLintScript;
import org.coode.oppl.lint.OPPLLintScriptImpl;
import org.coode.oppl.utils.ProtegeParserFactory;
import org.coode.oppl.variablemansyntax.Variable;
import org.protege.editor.owl.model.OWLModelManager;

/**
 * @author Luigi Iannone
 * 
 */
public class ProtegeOPPLLintFactory implements OPPLLintAbstractFactory {
	private final OWLModelManager modelManager;

	public ProtegeOPPLLintFactory(OWLModelManager modelManager) {
		this.modelManager = modelManager;
	}

	/**
	 * @see org.coode.oppl.lint.OPPLLintAbstractFactory#buildOPPLLintScript(java.lang.String,
	 *      org.coode.oppl.OPPLScript,
	 *      org.coode.oppl.variablemansyntax.Variable, java.lang.String)
	 */
	public OPPLLintScript buildOPPLLintScript(String name,
			OPPLScript opplScript, Variable v, String description) {
		return new OPPLLintScriptImpl(name, opplScript, v, description, this
				.getModelManager().getOWLOntologyManager());
	}

	/**
	 * @see org.coode.oppl.lint.OPPLLintAbstractFactory#initOPPLParser(java.lang.String)
	 */
	public void initOPPLParser(String string) {
		ProtegeParserFactory.initParser(string, this.modelManager);
	}

	/**
	 * @return the modelManager
	 */
	public OWLModelManager getModelManager() {
		return this.modelManager;
	}
}
