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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.coode.oppl.log.Logging;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.OWLObjectInstantiator;
import org.coode.oppl.variablemansyntax.PossibleValueExtractor;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.Assignment;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.coode.oppl.variablemansyntax.bindingtree.LeafBrusher;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 */
public class AssertedAxiomQuery extends AbstractAxiomQuery {
	protected Set<OWLOntology> ontologies;
	private final ConstraintSystem constraintSystem;
	private final Map<BindingNode, Set<OWLAxiom>> instantiatedAxioms = new HashMap<BindingNode, Set<OWLAxiom>>();

	/**
	 * @param ontologies
	 */
	public AssertedAxiomQuery(Set<OWLOntology> ontologies, ConstraintSystem cs) {
		this.ontologies = ontologies;
		this.constraintSystem = cs;
	}

	/**
	 * @param axiom
	 */
	@Override
	protected void match(OWLAxiom axiom) {
		Set<Variable> axiomVariables = this.constraintSystem
				.getAxiomVariables(axiom);
		Logging.getQueryLogger().log(Level.FINE,
				"Matching axiom " + axiom.toString());
		Set<BindingNode> holdingAssignments = new HashSet<BindingNode>();
		for (OWLOntology ontology : this.ontologies) {
			for (OWLAxiom ontologyAxiom : ontology.getAxioms()) {
				if (ontologyAxiom.getClass().isAssignableFrom(axiom.getClass())) {
					Set<BindingNode> leaves = null;
					if (this.constraintSystem.getLeaves() == null) {
						for (Variable variable : axiomVariables) {
							this.extractPossibleValues(variable, ontologyAxiom);
						}
						BindingNode bindingNode = new BindingNode(
								new HashSet<Assignment>(), axiomVariables);
						LeafBrusher leafBrusher = new LeafBrusher();
						bindingNode.accept(leafBrusher);
						leaves = leafBrusher.getLeaves();
					} else {
						leaves = new HashSet<BindingNode>(this.constraintSystem
								.getLeaves());
						// Expand the leaves for possible new variables
						// introduce by
						// this new axiom
						for (BindingNode leaf : new HashSet<BindingNode>(leaves)) {
							Set<Variable> unassignedVariables = new HashSet<Variable>(
									axiomVariables);
							unassignedVariables.removeAll(leaf
									.getAssignedVariables());
							if (!unassignedVariables.isEmpty()) {
								for (Variable variable : unassignedVariables) {
									this.extractPossibleValues(variable,
											ontologyAxiom);
								}
								BindingNode bindingNode = new BindingNode(leaf
										.getAssignments(), unassignedVariables);
								LeafBrusher leafBrusher = new LeafBrusher();
								bindingNode.accept(leafBrusher);
								Set<BindingNode> newLeaves = leafBrusher
										.getLeaves();
								if (!newLeaves.isEmpty()) {
									leaves.remove(leaf);
									leaves.addAll(newLeaves);
									Set<OWLAxiom> bindingInstantiatedAxioms = this.instantiatedAxioms
											.get(leaf);
									this.instantiatedAxioms.remove(leaf);
									for (BindingNode newLeaf : newLeaves) {
										this.instantiatedAxioms.put(newLeaf,
												bindingInstantiatedAxioms);
									}
								}
							}
						}
					}
					for (BindingNode leaf : new HashSet<BindingNode>(leaves)) {
						if (leaf.getAssignedVariables().containsAll(
								axiomVariables)) {
							OWLObjectInstantiator objectInstatiator = new OWLObjectInstantiator(
									leaf, this.constraintSystem);
							OWLAxiom instantiatedAxiom = (OWLAxiom) axiom
									.accept(objectInstatiator);
							if (this.locateAxiom(instantiatedAxiom)) {
								Logging.getQueryLogger().log(Level.FINE,
										instantiatedAxiom + " found ");
								holdingAssignments.add(leaf);
								Set<OWLAxiom> axioms = this.instantiatedAxioms
										.get(leaf);
								if (axioms == null) {
									axioms = new HashSet<OWLAxiom>();
								}
								axioms.add(instantiatedAxiom);
								this.instantiatedAxioms.put(leaf, axioms);
							} else {
								Logging.getQueryLogger().log(Level.FINEST,
										instantiatedAxiom + " not found ");
								this.instantiatedAxioms.remove(leaf);
							}
						}
					}
				}
			}
		}
		this.constraintSystem.setLeaves(holdingAssignments);
	}

	/**
	 * @param variable
	 * @param ontologyAxiom
	 */
	private void extractPossibleValues(Variable variable, OWLAxiom ontologyAxiom) {
		variable.clearBindings();
		PossibleValueExtractor possibleValueExtractor = new PossibleValueExtractor(
				variable.getType());
		Set<OWLObject> possibleValues = ontologyAxiom
				.accept(possibleValueExtractor);
		Logging.getQueryLogger().log(
				Level.FINEST,
				"variable " + variable + " possible values size "
						+ possibleValues.size());
		for (OWLObject object : possibleValues) {
			try {
				variable.addPossibleBinding(object);
				Logging.getQueryLogger().log(Level.FINEST, object.toString());
			} catch (OWLReasonerException e) {
				Logging.getQueryLogger().log(
						Level.WARNING,
						"Problem in adding value " + object + " for variable "
								+ variable);
			}
		}
	}

	/**
	 * @param axiom
	 */
	private boolean locateAxiom(OWLAxiom axiom) {
		Iterator<OWLOntology> it = this.ontologies.iterator();
		boolean found = false;
		OWLOntology ontology;
		while (!found && it.hasNext()) {
			ontology = it.next();
			found = ontology.containsAxiom(axiom);
		}
		return found;
	}

	public Map<BindingNode, Set<OWLAxiom>> getInstantiations() {
		Map<BindingNode, Set<OWLAxiom>> toReturn = new HashMap<BindingNode, Set<OWLAxiom>>();
		if (this.constraintSystem.getLeaves() != null) {
			toReturn.putAll(this.instantiatedAxioms);
		}
		return toReturn;
	}
}
