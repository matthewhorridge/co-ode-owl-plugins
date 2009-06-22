package org.coode.oppl.utils;

import java.io.StringReader;

import org.coode.oppl.protege.ProtegeOPPLFactory;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.validation.OPPLScriptValidator;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.clsdescriptioneditor.AutoCompleterMatcherImpl;

public class ProtegeParserFactory {
	static OPPLParser parser = null;

	public static OPPLParser initParser(String formulaBody,
			OWLModelManager manager) {
		return initParser(formulaBody, manager, null);
	}

	public static OPPLParser initParser(String formulaBody,
			OWLModelManager manager, OPPLScriptValidator validator) {
		if (parser == null) {
			parser = new OPPLParser(new StringReader(formulaBody), manager
					.getOWLOntologyManager(), manager.getActiveOntology(),
					manager.getReasoner());
		} else {
			OPPLParser.ReInit(new StringReader(formulaBody), manager
					.getOWLOntologyManager(), manager.getActiveOntology(),
					manager.getReasoner());
		}
		OPPLParser.setOPPLFactory(new ProtegeOPPLFactory(manager));
		OPPLParser
				.setAutoCompleterMatcher(new AutoCompleterMatcherImpl(manager));
		OPPLParser.setOPPLScriptValidator(validator);
		return parser;
	}
}
