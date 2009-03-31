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
package org.coode.oppl.variablemansyntax;

import java.util.Iterator;
import java.util.List;

import org.coode.oppl.OPPLScript;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.syntax.ParseException;
import org.coode.oppl.syntax.TokenMgrError;
import org.coode.oppl.utils.ParserFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.model.inference.NoOpReasoner;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owl.inference.OWLReasoner;

/**
 * @author Luigi Iannone
 * 
 */
public class VariableManchesterSyntaxExpressionChecker implements
		OWLExpressionChecker<OPPLScript> {
	private OWLEditorKit owlEditorKit;
	private OPPLScript lastCheckedObject = null;

	public VariableManchesterSyntaxExpressionChecker(OWLEditorKit owlEditorKit) {
		this.owlEditorKit = owlEditorKit;
	}

	/**
	 * @see org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker#check(java.lang.String)
	 */
	public void check(String text) throws OWLExpressionParserException {
		this.lastCheckedObject = null;
		this.initParser(text);
		try {
			OPPLScript statementModel = OPPLParser.Start();
			OWLReasoner reasoner = this.owlEditorKit.getModelManager()
					.getReasoner();
			if (reasoner == null || reasoner instanceof NoOpReasoner) {
				List<InputVariable> variables = statementModel
						.getInputVariables();
				Iterator<InputVariable> iterator = variables.iterator();
				boolean found = false;
				InputVariable v = null;
				while (!found && iterator.hasNext()) {
					v = iterator.next();
					found = v.getVariableScope() != null;
				}
				if (found) {
					throw new OWLExpressionParserException(
							new Exception(
									"Variable "
											+ v.toString()
											+ " has got a scope restriction and there is no Reasoner activated to check"));
				}
			}
			this.lastCheckedObject = statementModel;
		} catch (ParseException e) {
			throw new OWLExpressionParserException(e);
		} catch (TokenMgrError tme) {
			throw new OWLExpressionParserException(tme);
		}
	}

	/**
	 * @see org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker#createObject(java.lang.String)
	 */
	public OPPLScript createObject(String text)
			throws OWLExpressionParserException {
		this.check(text);
		return this.lastCheckedObject;
	}

	/**
	 * @param text
	 * @return
	 */
	protected OPPLParser initParser(String text) {
		return ParserFactory.initParser(text, this.owlEditorKit
				.getModelManager());
	}
}
