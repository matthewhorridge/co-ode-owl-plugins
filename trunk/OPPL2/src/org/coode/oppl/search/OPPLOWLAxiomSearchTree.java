/**
 * 
 */
package org.coode.oppl.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.coode.oppl.utils.VariableExtractor;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.PartialOWLObjectInstantiator;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.oppl.variablemansyntax.bindingtree.Assignment;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLOWLAxiomSearchTree extends SearchTree<OPPLOWLAxiomSearchNode> {
	private final ConstraintSystem constraintSystem;
	private final OWLOntologyManager manager;

	/**
	 * @param manager
	 * @param constraintSystem
	 */
	public OPPLOWLAxiomSearchTree(OWLOntologyManager manager,
			ConstraintSystem constraintSystem) {
		if (manager == null) {
			throw new NullPointerException("The manager cannot be null");
		}
		if (constraintSystem == null) {
			throw new NullPointerException(
					"The constraint system cannot be null");
		}
		this.manager = manager;
		this.constraintSystem = constraintSystem;
	}

	/**
	 * @see org.coode.oppl.search.SearchTree#getChildren(java.lang.Object)
	 */
	@Override
	protected List<OPPLOWLAxiomSearchNode> getChildren(
			OPPLOWLAxiomSearchNode node) {
		Set<BindingNode> leaves = this.getConstraintSystem().getLeaves();
		List<OPPLOWLAxiomSearchNode> toReturn = new ArrayList<OPPLOWLAxiomSearchNode>();
		VariableExtractor variableExtractor = new VariableExtractor(this
				.getConstraintSystem());
		Set<Variable> variables = node.getAxiom().accept(variableExtractor);
		BindingNode binding = node.getBinding();
		for (Variable variable : variables) {
			Collection<OWLObject> values = new HashSet<OWLObject>();
			if (leaves == null) {
				values.addAll(this.getAssignableValues(variable));
			} else {
				for (BindingNode bindingNode : leaves) {
					if (bindingNode.getAssignedVariables().contains(variable)) {
						values.add(bindingNode.getAssignmentValue(variable));
					} else {
						values.addAll(this.getAssignableValues(variable));
					}
				}
			}
			for (OWLObject value : values) {
				Assignment assignment = new Assignment(variable, value);
				BindingNode childBinding = new BindingNode(binding
						.getAssignments(), binding.getUnassignedVariables());
				childBinding.addAssignment(assignment);
				PartialOWLObjectInstantiator instantiator = new PartialOWLObjectInstantiator(
						childBinding, this.getConstraintSystem());
				OWLAxiom instantiatedAxiom = (OWLAxiom) node.getAxiom().accept(
						instantiator);
				OPPLOWLAxiomSearchNode child = new OPPLOWLAxiomSearchNode(
						instantiatedAxiom, childBinding);
				toReturn.add(child);
			}
		}
		return toReturn;
	}

	/**
	 * @return {@code true} if the input {@link OPPLOWLAxiomSearchNode}
	 *         represents an OWLAxiom that is contained in one of the ontologies
	 *         managed by the ontology manager encapsulated in this
	 *         OPPLOWLAxiomSearchTree.
	 * @see org.coode.oppl.search.SearchTree#goalReached(java.lang.Object)
	 */
	@Override
	protected boolean goalReached(OPPLOWLAxiomSearchNode start) {
		boolean found = false;
		Iterator<OWLOntology> iterator = this.manager.getOntologies()
				.iterator();
		while (!found && iterator.hasNext()) {
			OWLOntology ontology = iterator.next();
			found = ontology.containsAxiom(start.getAxiom());
		}
		return found;
	}

	private Collection<? extends OWLObject> getAllClasses() {
		Set<OWLClass> toReturn = new HashSet<OWLClass>();
		Set<OWLOntology> ontologies = this.manager.getOntologies();
		for (OWLOntology owlOntology : ontologies) {
			toReturn.addAll(owlOntology.getReferencedClasses());
		}
		return toReturn;
	}

	private Collection<? extends OWLObject> getAllConstants() {
		return Collections.emptySet();
	}

	private Collection<? extends OWLObject> getAllDataProperties() {
		Set<OWLDataProperty> toReturn = new HashSet<OWLDataProperty>();
		Set<OWLOntology> ontologies = this.manager.getOntologies();
		for (OWLOntology owlOntology : ontologies) {
			toReturn.addAll(owlOntology.getReferencedDataProperties());
		}
		return toReturn;
	}

	private Collection<? extends OWLObject> getAllIndividuals() {
		Set<OWLIndividual> toReturn = new HashSet<OWLIndividual>();
		Set<OWLOntology> ontologies = this.manager.getOntologies();
		for (OWLOntology owlOntology : ontologies) {
			toReturn.addAll(owlOntology.getReferencedIndividuals());
		}
		return toReturn;
	}

	private Collection<? extends OWLObject> getAssignableValues(
			Variable variable) {
		Set<OWLObject> toReturn = new HashSet<OWLObject>();
		VariableType type = variable.getType();
		switch (type) {
		case CLASS:
			toReturn.addAll(this.getAllClasses());
			break;
		case DATAPROPERTY:
			toReturn.addAll(this.getAllDataProperties());
			break;
		case OBJECTPROPERTY:
			toReturn.addAll(this.getObjectProperties());
			break;
		case INDIVIDUAL:
			toReturn.addAll(this.getAllIndividuals());
			break;
		case CONSTANT:
			toReturn.addAll(this.getAllConstants());
			break;
		default:
			break;
		}
		return toReturn;
	}

	/**
	 * @return the constraintSystem
	 */
	public ConstraintSystem getConstraintSystem() {
		return this.constraintSystem;
	}

	/**
	 * @return the manager
	 */
	public OWLOntologyManager getManager() {
		return this.manager;
	}

	private Collection<? extends OWLObject> getObjectProperties() {
		Set<OWLObjectProperty> toReturn = new HashSet<OWLObjectProperty>();
		Set<OWLOntology> ontologies = this.manager.getOntologies();
		for (OWLOntology owlOntology : ontologies) {
			toReturn.addAll(owlOntology.getReferencedObjectProperties());
		}
		return toReturn;
	}

	/**
	 * @see org.coode.oppl.search.SearchTree#exhaustiveSearchTree(java.lang.Object,
	 *      java.util.List)
	 */
	@Override
	public boolean exhaustiveSearchTree(OPPLOWLAxiomSearchNode start,
			List<List<OPPLOWLAxiomSearchNode>> solutions) {
		boolean found = super.exhaustiveSearchTree(start, solutions);
		Set<BindingNode> newLeaves = new HashSet<BindingNode>();
		for (List<OPPLOWLAxiomSearchNode> path : solutions) {
			OPPLOWLAxiomSearchNode leafSerachNode = path.get(path.size() - 1);
			BindingNode newLeaf = leafSerachNode.getBinding();
			newLeaves.add(newLeaf);
		}
		this.constraintSystem.setLeaves(newLeaves);
		return found;
	}
}
