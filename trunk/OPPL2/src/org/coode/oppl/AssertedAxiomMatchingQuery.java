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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.coode.oppl.match.AxiomMatcher;
import org.coode.oppl.match.MatcherGenerator;
import org.coode.oppl.utils.VariableExtractor;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.PartialOWLObjectInstantiator;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.Assignment;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 */
public class AssertedAxiomMatchingQuery extends AbstractAxiomQuery {
	private final ConstraintSystem constraintSystem;
	private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	private final Map<BindingNode, Set<OWLAxiom>> instantiations = new HashMap<BindingNode, Set<OWLAxiom>>();

	/**
	 * @param constraintSystem
	 */
	public AssertedAxiomMatchingQuery(ConstraintSystem constraintSystem,
			Collection<? extends OWLOntology> ontologies) {
		if (constraintSystem == null) {
			throw new NullPointerException(
					"The constraint system cannot be null");
		}
		if (ontologies == null) {
			throw new NullPointerException("The ontologies cannot be null");
		}
		this.constraintSystem = constraintSystem;
		this.ontologies.addAll(ontologies);
	}

	/**
	 * @see org.coode.oppl.AbstractAxiomQuery#match(org.semanticweb.owl.model.OWLAxiom)
	 */
	@Override
	protected void match(OWLAxiom axiom) {
		Set<BindingNode> leaves = this.getConstraintSystem().getLeaves();
		Set<BindingNode> newLeaves = new HashSet<BindingNode>();
		VariableExtractor variableExtractor = new VariableExtractor(this
				.getConstraintSystem());
		if (leaves == null) {
			Set<Variable> variables = axiom.accept(variableExtractor);
			BindingNode startBindings = new BindingNode(
					new HashSet<Assignment>(), variables);
			newLeaves.addAll(this.doMatch(axiom, startBindings));
		} else {
			for (BindingNode leaf : leaves) {
				PartialOWLObjectInstantiator partialOWLObjectInstantiator = new PartialOWLObjectInstantiator(
						leaf, this.getConstraintSystem());
				OWLAxiom instantiatedAxiom = (OWLAxiom) axiom
						.accept(partialOWLObjectInstantiator);
				newLeaves.addAll(this.doMatch(instantiatedAxiom, leaf));
			}
		}
		this.getConstraintSystem().setLeaves(newLeaves);
	}

	/**
	 * @param axiom
	 * @param startBindings
	 * @return
	 */
	private Set<BindingNode> doMatch(OWLAxiom axiom, BindingNode startBindings) {
		Set<BindingNode> newLeaves = new HashSet<BindingNode>();
		for (OWLOntology ontology : this.getOntologies()) {
			for (OWLAxiom ontologyAxiom : ontology.getAxioms()) {
				Set<? extends AxiomMatcher> axiomMatchers = MatcherGenerator
						.getAxiomMatchers(axiom, new BindingNode(startBindings
								.getAssignments(), startBindings
								.getUnassignedVariables()), this
								.getConstraintSystem());
				for (AxiomMatcher matcher : axiomMatchers) {
					boolean match = ontologyAxiom.accept(matcher);
					if (match) {
						newLeaves.add(matcher.getBindings());
						Set<OWLAxiom> instantiatedAxioms = this.instantiations
								.get(matcher.getBindings());
						if (instantiatedAxioms == null) {
							instantiatedAxioms = new HashSet<OWLAxiom>();
						}
						instantiatedAxioms.add(ontologyAxiom);
						this.instantiations.put(matcher.getBindings(),
								instantiatedAxioms);
					}
				}
			}
		}
		return newLeaves;
	}

	/**
	 * @see org.coode.oppl.AxiomQuery#getInstantiations()
	 */
	public Map<BindingNode, Set<OWLAxiom>> getInstantiations() {
		return new HashMap<BindingNode, Set<OWLAxiom>>(this.instantiations);
	}

	/**
	 * @return the constraintSystem
	 */
	public ConstraintSystem getConstraintSystem() {
		return this.constraintSystem;
	}

	/**
	 * @return the ontologies
	 */
	public Set<OWLOntology> getOntologies() {
		return new HashSet<OWLOntology>(this.ontologies);
	}
}
