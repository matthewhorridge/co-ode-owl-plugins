/**
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.coode.oppl.utils;

import java.io.StringReader;

import org.coode.oppl.protege.ProtegeOPPLFactory;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.clsdescriptioneditor.AutoCompleterMatcherImpl;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 */
public class ParserFactory {
	static OPPLParser parser = null;

	public static OPPLParser initParser(String formulaBody,
			OWLOntology ontology, OWLOntologyManager ontologyManager) {
		if (parser == null) {
			parser = new OPPLParser(new StringReader(formulaBody),
					ontologyManager, new ConstraintSystem(ontology,
							ontologyManager));
		} else {
			OPPLParser.ReInit(new StringReader(formulaBody), ontologyManager,
					new ConstraintSystem(ontology, ontologyManager));
		}
		return parser;
	}

	public static OPPLParser initParser(String formulaBody,
			OWLOntologyManager ontologyManager, ConstraintSystem cs) {
		if (parser == null) {
			parser = new OPPLParser(new StringReader(formulaBody),
					ontologyManager, cs);
		} else {
			OPPLParser.ReInit(new StringReader(formulaBody), ontologyManager,
					cs);
		}
		return parser;
	}

	public static OPPLParser initParser(String formulaBody,
			OWLModelManager manager) {
		if (parser == null) {
			parser = new OPPLParser(new StringReader(formulaBody), manager
					.getOWLOntologyManager(), new ConstraintSystem(manager
					.getActiveOntology(), manager.getOWLOntologyManager(),
					manager.getReasoner()));
		} else {
			OPPLParser.ReInit(new StringReader(formulaBody), manager
					.getOWLOntologyManager(), new ConstraintSystem(manager
					.getActiveOntology(), manager.getOWLOntologyManager(),
					manager.getReasoner()));
		}
		OPPLParser.setOPPLFactory(new ProtegeOPPLFactory(manager, OPPLParser
				.getConstraintSystem(), manager.getOWLDataFactory()));
		OPPLParser
				.setAutoCompleterMatcher(new AutoCompleterMatcherImpl(manager));
		return parser;
	}

	public static OPPLParser initParser(String formulaBody,
			OWLModelManager manager, ConstraintSystem cs) {
		if (parser == null) {
			parser = new OPPLParser(new StringReader(formulaBody), manager
					.getOWLOntologyManager(), cs);
		} else {
			OPPLParser.ReInit(new StringReader(formulaBody), manager
					.getOWLOntologyManager(), cs);
		}
		OPPLParser.setOPPLFactory(new ProtegeOPPLFactory(manager, OPPLParser
				.getConstraintSystem(), manager.getOWLDataFactory()));
		OPPLParser
				.setAutoCompleterMatcher(new AutoCompleterMatcherImpl(manager));
		return parser;
	}
}
