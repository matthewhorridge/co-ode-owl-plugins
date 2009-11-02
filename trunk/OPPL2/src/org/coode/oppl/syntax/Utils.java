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
package org.coode.oppl.syntax;

import java.util.Collections;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.VariableManchesterOWLSyntaxParser;
import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;
import org.coode.oppl.variablemansyntax.generated.RegExpGeneratedVariable;
import org.coode.oppl.variablemansyntax.generated.VariableExpressionGeneratedVariable;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public class Utils {
	public static ParserException buildException(int beginningSubSection,
			int beginningSubSectionLine, ParserException e) {
		ParserException test = new ParserException(e.getCurrentToken(), e
				.getStartPos()
				+ beginningSubSection, e.getLineNumber()
				+ beginningSubSectionLine - 1, e.getColumnNumber()
				+ beginningSubSection, e.isClassNameExpected(), e
				.isObjectPropertyNameExpected(),
				e.isDataPropertyNameExpected(), e.isIndividualNameExpected(), e
						.isDatatypeNameExpected(), e.getExpectedKeywords());
		return test;
	}

	public static ParserException buildException(String expression,
			int beginningSubSection, int beginningSubSectionLine, Exception e) {
		ParserException test = new ParserException(expression + "\n"
				+ e.getMessage(), beginningSubSection, beginningSubSectionLine,
				beginningSubSection, false, false, false, false, false,
				Collections.EMPTY_SET);
		return test;
	}

	protected static String readString(boolean spaceTokens,
			int... delimiterTokenKinds) {
		StringBuilder toReturn = new StringBuilder();
		boolean found = false;
		while (!found) {
			Token token = OPPLParser.getToken(1);
			for (int i = 0; !found && i < delimiterTokenKinds.length; i++) {
				found = token.kind == OPPLParserConstants.EOF
						|| delimiterTokenKinds[i] == token.kind;
			}
			if (!found) {
				toReturn.append(token.image);
				if (spaceTokens) {
					toReturn.append(" ");
				}
				OPPLParser.getNextToken();
			}
		}
		System.out.println();
		return toReturn.toString();
	}

	protected static GeneratedVariable<OWLObject> parseVariableExpressionGeneratedVariable(
			String name, VariableType type, String string,
			ConstraintSystem constraintSystem) throws ParserException {
		VariableManchesterOWLSyntaxParser parser = new VariableManchesterOWLSyntaxParser(
				string, constraintSystem);
		OWLObject owlObject = null;
		switch (type) {
			case CLASS:
				owlObject = parser.parseDescription();
				break;
			case OBJECTPROPERTY:
				owlObject = parser.parseObjectPropertyExpression();
				break;
			case DATAPROPERTY:
				owlObject = parser.parseDataProperty();
				break;
			case INDIVIDUAL:
				owlObject = parser.parseIndividual();
				break;
			case CONSTANT:
				owlObject = parser.parseConstant();
				break;
			default:
				throw new IllegalArgumentException("Unsupported type: "
						+ type.name());
		}
		VariableExpressionGeneratedVariable variableExpressionGeneratedVariable = new VariableExpressionGeneratedVariable(
				name, owlObject, constraintSystem);
		constraintSystem.importVariable(variableExpressionGeneratedVariable);
		return variableExpressionGeneratedVariable;
	}

	protected static GeneratedVariable<OWLEntity> parseRegexpGeneratedVariable(
			String name, VariableType type, String string,
			ConstraintSystem constraintSystem) throws ParserException {
		RegExpGeneratedVariable v = new RegExpGeneratedVariable(name, type,
				constraintSystem, string);
		constraintSystem.importVariable(v);
		return v;
	}
}
