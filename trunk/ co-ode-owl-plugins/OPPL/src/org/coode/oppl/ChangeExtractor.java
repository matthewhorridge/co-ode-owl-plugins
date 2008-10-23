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
package org.coode.oppl;

import java.util.ArrayList;
import java.util.List;

import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.Variable;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * Returns the changes that will occur if the visited OPPL construct is executed
 * 
 * @author Luigi Iannone
 * 
 */
public class ChangeExtractor implements
		OPPLScriptVisitorEx<List<OWLAxiomChange>> {
	protected OWLOntologyManager ontologyManager;
	protected OWLOntology ontology;
	protected List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();

	/**
	 * @param ontologyManager
	 */
	public ChangeExtractor(OWLOntology ontology,
			OWLOntologyManager ontologyManager) {
		this.ontology = ontology;
		this.ontologyManager = ontologyManager;
	}

	public List<OWLAxiomChange> visit(OPPLQuery q, List<OWLAxiomChange> p) {
		if (q != null) {
			List<OWLAxiom> axioms = q.getAssertedAxioms();
			for (OWLAxiom axiom : axioms) {
				OPPLParser.getConstraintSystem().addAssertedAxiom(axiom);
			}
			axioms = q.getAxioms();
			for (OWLAxiom axiom : axioms) {
				OPPLParser.getConstraintSystem().addAxiom(axiom);
			}
			for (Constraint c : q.getConstraints()) {
				OPPLParser.getConstraintSystem().addConstraint(c);
			}
		}
		return null;
	}

	public List<OWLAxiomChange> visit(Variable v, List<OWLAxiomChange> p) {
		return null;
	}

	public List<OWLAxiomChange> visitActions(List<OWLAxiomChange> changes,
			List<OWLAxiomChange> p) {
		p = new ArrayList<OWLAxiomChange>();
		for (OWLAxiomChange change : changes) {
			String action = change instanceof AddAxiom ? "ADD" : "REMOVE";
			p.addAll(ActionFactory.createChange(ActionType.valueOf(action),
					change.getAxiom(), OPPLParser.getConstraintSystem(),
					this.ontologyManager.getOWLDataFactory(), this.ontology));
		}
		return p;
	}
	// /**
	// * @see
	// org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.SimpleNode,
	// * java.lang.Object)
	// */
	// public Object visit(SimpleNode node, Object data) {
	// return null;
	// }
	//
	// /**
	// * @see
	// org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLStart,
	// * java.lang.Object)
	// */
	// public Object visit(OPPLStart node, Object data) {
	// return node.childrenAccept(this, data);
	// }
	//
	// /**
	// * @see
	// org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLActions,
	// * java.lang.Object)
	// */
	// public Object visit(OPPLActions node, Object data) {
	// return node.childrenAccept(this, data);
	// }
	//
	// /**
	// * @see
	// org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLVariableDefinition,
	// * java.lang.Object)
	// */
	// public Object visit(OPPLVariableDefinition node, Object data) {
	// return null;
	// }
	//
	// /**
	// * @see
	// org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLQuery,
	// * java.lang.Object)
	// */
	// public Object visit(OPPLQuery node, Object data) {
	// Set<OWLAxiom> axioms = node.getAxioms();
	// for (OWLAxiom axiom : axioms) {
	// OPPLParser.getConstraintSystem().addAxiom(axiom);
	// }
	// for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	// Node child = node.jjtGetChild(i);
	// if (child instanceof OPPLConstraint) {
	// Constraint c = (Constraint) child.jjtAccept(this, data);
	// OPPLParser.getConstraintSystem().addConstraint(c);
	// }
	// }
	// return null;
	// }
	//
	// /**
	// * @see
	// org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLAction,
	// * java.lang.Object)
	// */
	// public Object visit(OPPLAction node, Object data) {
	// String action = node.getAction();
	// OWLAxiom axiom = node.getAxiom();
	// this.changes.addAll(ActionFactory.createChange(ActionType
	// .valueOf(action), axiom, OPPLParser.getConstraintSystem(),
	// this.ontologyManager.getOWLDataFactory(), this.ontology));
	// return this.changes;
	// }
	//
	// /**
	// * @see
	// org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLClause,
	// * java.lang.Object)
	// */
	// public Object visit(OPPLClause node, Object data) {
	// return null;
	// }
	//
	// /**
	// * @return the changes
	// */
	// public List<OWLAxiomChange> getChanges() {
	// return this.changes;
	// }
	//
	// public Object visit(OPPLConstraint node, Object data) {
	// Constraint c = new Constraint(node.getVariable(), node.getExpression());
	// return c;
	// }
	//
	// public Object visit(OPPLOWLExpression node, Object data) {
	// return null;
	// }
	//
	// public Object visit(OPPLtypeScope node, Object data) {
	// return null;
	// }
	//
	// public Object visit(OPPLparseScope node, Object data) {
	// return null;
	// }
}
