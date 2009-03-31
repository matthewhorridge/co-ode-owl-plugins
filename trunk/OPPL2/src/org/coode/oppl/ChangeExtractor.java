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

import org.coode.oppl.variablemansyntax.ConstraintSystem;
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
	private final OWLOntologyManager ontologyManager;
	private final OWLOntology ontology;
	// private List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();
	private final boolean considerImportClosure;
	private final ConstraintSystem constraintSystem;

	/**
	 * @param ontologyManager
	 */
	public ChangeExtractor(OWLOntology ontology,
			OWLOntologyManager ontologyManager,
			ConstraintSystem constraintSystem, boolean considerImportClosure) {
		this.ontology = ontology;
		this.ontologyManager = ontologyManager;
		this.constraintSystem = constraintSystem;
		this.considerImportClosure = considerImportClosure;
	}

	public List<OWLAxiomChange> visit(OPPLQuery q, List<OWLAxiomChange> p) {
		if (q != null) {
			q.getConstraintSystem().reset();
			List<OWLAxiom> axioms = q.getAssertedAxioms();
			for (OWLAxiom axiom : axioms) {
				q.getConstraintSystem().addAssertedAxiom(axiom);
			}
			axioms = q.getAxioms();
			for (OWLAxiom axiom : axioms) {
				q.getConstraintSystem().addAxiom(axiom);
			}
			for (AbstractConstraint c : q.getConstraints()) {
				q.getConstraintSystem().addConstraint(c);
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
			boolean isAdd = change instanceof AddAxiom;
			String action = isAdd ? "ADD" : "REMOVE";
			List<OWLAxiomChange> actionChanges = this.considerImportClosure
					&& !isAdd ? ActionFactory.createChanges(ActionType
					.valueOf(action), change.getAxiom(), this
					.getConstraintSystem(), this.ontologyManager
					.getOWLDataFactory(), this.ontologyManager
					.getImportsClosure(this.ontology)) : ActionFactory
					.createChanges(ActionType.valueOf(action), change
							.getAxiom(), this.getConstraintSystem(),
							this.ontologyManager.getOWLDataFactory(),
							this.ontology);
			p.addAll(actionChanges);
		}
		return p;
	}

	/**
	 * @return the ontologyManager
	 */
	public OWLOntologyManager getOntologyManager() {
		return this.ontologyManager;
	}

	/**
	 * @return the ontology
	 */
	public OWLOntology getOntology() {
		return this.ontology;
	}

	/**
	 * @return the considerImportClosure
	 */
	public boolean isConsiderImportClosure() {
		return this.considerImportClosure;
	}

	/**
	 * @return the constraintSystem
	 */
	public ConstraintSystem getConstraintSystem() {
		return this.constraintSystem;
	}
}
