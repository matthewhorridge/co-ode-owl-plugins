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

import java.util.List;

import org.coode.oppl.variablemansyntax.Variable;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 */
public class Executor implements OPPLScriptVisitor {
	private final OWLOntologyManager ontologyManager;
	private final ChangeExtractor changeExtractor;

	/**
	 * @param ontologyManager
	 */
	public Executor(OWLOntology ontology, OWLOntologyManager ontologyManager) {
		this.ontologyManager = ontologyManager;
		this.changeExtractor = new ChangeExtractor(ontology, ontologyManager);
	}

	//
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
	// List<OWLAxiomChange> change = ActionFactory.createChange(ActionType
	// .valueOf(action), axiom, OPPLParser.getConstraintSystem(),
	// this.ontologyManager.getOWLDataFactory(), this.ontology);
	// try {
	// this.ontologyManager.applyChanges(new ArrayList<OWLOntologyChange>(
	// change));
	// } catch (OWLOntologyChangeException e) {
	// e.printStackTrace();
	// }
	// return null;
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
	// public static void main(String[] args) {
	// System.out.println("Reading from standard input...");
	// System.out.print("Enter an expression :");
	// String ontologyPhysicalURI = args[0];
	// OWLOntologyManager ontologyManager = OWLManager
	// .createOWLOntologyManager();
	// try {
	// OWLOntology ontology = ontologyManager.loadOntology(URI
	// .create(ontologyPhysicalURI));
	// ConstraintSystem constraintSystem = new ConstraintSystem(ontology,
	// ontologyManager);
	// new OPPLParser(System.in, ontologyManager, constraintSystem);
	// SimpleNode n = OPPLParser.Start();
	// n.dump("");
	// System.out.println("Parse OK now let us execute it");
	// Executor executor = new Executor(ontology, ontologyManager);
	// n.jjtAccept(executor, null);
	// System.out.println("Execution OK resulting axioms:");
	// Set<OWLAxiom> axioms = ontology.getAxioms();
	// for (OWLAxiom axiom : axioms) {
	// System.out.println(axiom);
	// }
	// } catch (Exception e) {
	// System.out.println("Oops.");
	// System.out.println(e.getMessage());
	// e.printStackTrace();
	// }
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
	// // TODO Auto-generated method stub
	// return null;
	// }
	public void visit(Variable v) {
	}

	public void visit(OPPLQuery q) {
	}

	public void visitActions(List<OWLAxiomChange> changes) {
		List<OWLAxiomChange> actions = this.changeExtractor.visitActions(
				changes, null);
		try {
			this.ontologyManager.applyChanges(actions);
		} catch (OWLOntologyChangeException e) {
			throw new RuntimeException(e);
		}
	}
}
