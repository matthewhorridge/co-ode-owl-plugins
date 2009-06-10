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
package org.coode.oppl.rendering.xquery;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.coode.oppl.AbstractConstraint;
import org.coode.oppl.OPPLScript;
import org.coode.oppl.variablemansyntax.Variable;
import org.semanticweb.owl.model.OWLAxiom;

/**
 * @author Luigi Iannone
 * 
 */
public class XQueryRenderer {
	private final static String NAMESPACE_DECLARATION = "declare namespace owl2xml = \"http://www.w3.org/2006/12/owl2-xml#\";";
	private final String context;

	/**
	 * @param context
	 */
	public XQueryRenderer(String context) {
		this.context = context;
	}

	public String render(OPPLScript script) {
		StringWriter writer = new StringWriter();
		writer.append(NAMESPACE_DECLARATION);
		writer.append("\n");
		List<OWLAxiom> axioms = script.getQuery().getAxioms();
		List<AbstractConstraint> constraints = script.getQuery()
				.getConstraints();
		String axiomName;
		int i = 0;
		List<String> whereConditions = new ArrayList<String>();
		for (OWLAxiom axiom : axioms) {
			axiomName = "$axiom_" + ++i;
			VariableXQueryBuilder builder = new VariableXQueryBuilder(
					axiomName, script.getConstraintSystem());
			writer.append("for ");
			writer.append(axiomName);
			writer.append(" in ");
			writer.append(this.getContext());
			String axiomQuery = axiom.accept(builder);
			writer.append(axiomQuery);
			writer.append("\n");
			Map<Variable, List<String>> allPaths = builder.getVariablePaths();
			for (Variable v : allPaths.keySet()) {
				List<String> variablePaths = allPaths.get(v);
				String variableReference = v.getName().replace('?', '$');
				boolean first = true;
				for (String string : variablePaths) {
					if (first) {
						writer.append("let ");
						writer.append(variableReference);
						writer.append(" := ");
						writer.append(string);
						writer.append("\n");
						first = false;
					} else {
						whereConditions.add(variableReference + " = " + string);
					}
				}
			}
			whereConditions.addAll(builder.getWhereConditions());
		}
		i = 0;
		for (AbstractConstraint abstractConstraint : constraints) {
			String constraintName = "$constraint_" + ++i;
			VariableXQueryBuilder builder = new VariableXQueryBuilder(
					constraintName, script.getConstraintSystem());
			abstractConstraint.accept(builder);
			whereConditions.addAll(builder.getWhereConditions());
		}
		writer.append("\n");
		writer.append("where\n");
		boolean first = true;
		for (String string : whereConditions) {
			String andString = first ? "" : " and \n";
			first = first ? false : first;
			writer.append(andString);
			writer.append(string);
		}
		return writer.toString();
	}

	private String getContext() {
		return this.context;
	}
}
