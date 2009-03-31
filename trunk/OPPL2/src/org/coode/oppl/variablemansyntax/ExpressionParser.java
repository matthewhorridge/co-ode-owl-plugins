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

import org.coode.oppl.syntax.OPPLParser;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 */
public class ExpressionParser implements VariableTypeVisitorEx<OWLObject> {
	protected VariableManchesterOWLSyntaxParser parser;

	public ExpressionParser(String string, ConstraintSystem cs,
			OWLDataFactory dataFactory, OWLOntologyManager ontologyManager) {
		this.parser = new VariableManchesterOWLSyntaxParser(dataFactory,
				string, cs);
		this.parser.setOWLEntityChecker(OPPLParser.getOPPLFactory()
				.getOWLEntityChecker());
	}

	/**
	 * @see org.coode.oppl.variablemansyntax.VariableTypeVisitorEx#visit(org.coode.oppl.variablemansyntax.VariableType)
	 */
	public OWLObject visit(VariableType type) {
		OWLObject toReturn = null;
		try {
			if (type.equals(VariableType.CLASS)) {
				toReturn = this.parser.parseDescription();
			} else if (type.equals(VariableType.OBJECTPROPERTY)) {
				toReturn = this.parser.parseObjectPropertyExpression();
			} else if (type.equals(VariableType.DATAPROPERTY)) {
				toReturn = this.parser.parseDataProperty();
			} else if (type.equals(VariableType.INDIVIDUAL)) {
				toReturn = this.parser.parseIndividual();
			} else if (type.equals(VariableType.CONSTANT)) {
				toReturn = this.parser.parseConstant();
			}
		} catch (ParserException e) {
		}
		return toReturn;
	}
}
