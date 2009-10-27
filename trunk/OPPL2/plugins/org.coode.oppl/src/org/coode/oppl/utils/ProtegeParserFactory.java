package org.coode.oppl.utils;

import org.coode.oppl.protege.ProtegeOPPLFactory;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.validation.OPPLScriptValidator;
import org.protege.editor.owl.model.OWLModelManager;

public class ProtegeParserFactory {
	static OPPLParser parser = null;

	public static OPPLParser initParser(String formulaBody,
			OWLModelManager manager) {
		parser = ParserFactory.initParser(formulaBody, manager
				.getActiveOntology(), manager.getOWLOntologyManager(), manager
				.getReasoner(), null);
		OPPLParser.setOPPLFactory(new ProtegeOPPLFactory(manager));
		return parser;
	}

	public static OPPLParser initParser(String formulaBody,
			OWLModelManager manager, OPPLScriptValidator validator) {
		parser = ParserFactory.initParser(formulaBody, manager
				.getActiveOntology(), manager.getOWLOntologyManager(), manager
				.getReasoner(), validator);
		OPPLParser.setOPPLFactory(new ProtegeOPPLFactory(manager));
		return parser;
	}
}
