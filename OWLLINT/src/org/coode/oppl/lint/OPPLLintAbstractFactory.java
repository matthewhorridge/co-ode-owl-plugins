package org.coode.oppl.lint;

import org.coode.oppl.OPPLScript;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.Variable;

public interface OPPLLintAbstractFactory {
	/**
	 * Initialises the OPPL Parser with the input String
	 * 
	 * @param string
	 * 
	 */
	OPPLParser initOPPLParser(String string);

	OPPLLintScript buildOPPLLintScript(String name, OPPLScript opplScript,
			Variable v, String description);
}
