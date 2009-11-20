/**
 * 
 */
package org.coode.oppl.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.coode.oppl.log.Logging;
import org.coode.oppl.utils.VariableExtractor;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.PartialOWLObjectInstantiator;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.oppl.variablemansyntax.bindingtree.Assignment;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDescriptionVisitor;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;

import com.clarkparsia.explanation.SatisfiabilityConverter;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLInferredOWLAxiomSearchTree extends
		SearchTree<OPPLOWLAxiomSearchNode> {
	private final ConstraintSystem constraintSystem;
	private final Set<OWLClass> allClasses = new HashSet<OWLClass>();
	private final Set<OWLObjectProperty> allObjectProperties = new HashSet<OWLObjectProperty>();
	private final Set<OWLDataProperty> allDataProperties = new HashSet<OWLDataProperty>();
	private final Set<OWLIndividual> allIndividuals = new HashSet<OWLIndividual>();
	private final Set<OWLConstant> allConstants = new HashSet<OWLConstant>();

	public OPPLInferredOWLAxiomSearchTree(ConstraintSystem constraintSystem) {
		if (constraintSystem == null) {
			throw new NullPointerException(
					"The constraint system cannot be null");
		}
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
	 *         represents an OWLAxiom that can be inferred using the reasoner
	 *         exposed by the ConstraintSystem.
	 * @see org.coode.oppl.search.SearchTree#goalReached(java.lang.Object)
	 */
	@Override
	protected boolean goalReached(OPPLOWLAxiomSearchNode start) {
		SatisfiabilityConverter converter = new SatisfiabilityConverter(this
				.getConstraintSystem().getOntologyManager().getOWLDataFactory());
		OWLDescription conversion = converter.convert(start.getAxiom());
		try {
			return !this.getConstraintSystem().getReasoner().isSatisfiable(
					conversion);
		} catch (OWLReasonerException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Collection<OWLClass> getAllClasses() {
		Set<OWLClass> toReturn = new HashSet<OWLClass>();
		for (OWLOntology owlOntology : this.getConstraintSystem()
				.getOntologyManager().getOntologies()) {
			toReturn.addAll(owlOntology.getReferencedClasses());
		}
		return toReturn;
	}

	private Collection<? extends OWLConstant> getAllConstants() {
		final Set<OWLConstant> toReturn = new HashSet<OWLConstant>();
		final OWLDescriptionVisitor constantExtractor = new OWLDescriptionVisitorAdapter() {
			@Override
			public void visit(OWLDataValueRestriction desc) {
				toReturn.add(desc.getValue());
			}
		};
		for (OWLOntology owlOntology : this.getConstraintSystem()
				.getOntologyManager().getOntologies()) {
			for (OWLAxiom axiomToVisit : owlOntology.getAxioms()) {
				axiomToVisit.accept(new OWLAxiomVisitorAdapter() {
					@Override
					public void visit(OWLClassAssertionAxiom axiom) {
						axiom.getDescription().accept(constantExtractor);
					}

					@Override
					public void visit(OWLDataPropertyAssertionAxiom axiom) {
						toReturn.add(axiom.getObject());
					}

					@Override
					public void visit(OWLDisjointClassesAxiom axiom) {
						for (OWLDescription description : axiom
								.getDescriptions()) {
							description.accept(constantExtractor);
						}
					}

					@Override
					public void visit(OWLEquivalentClassesAxiom axiom) {
						for (OWLDescription description : axiom
								.getDescriptions()) {
							description.accept(constantExtractor);
						}
					}

					@Override
					public void visit(
							OWLNegativeDataPropertyAssertionAxiom axiom) {
						toReturn.add(axiom.getObject());
					}

					@Override
					public void visit(OWLSubClassAxiom axiom) {
						axiom.getSubClass().accept(constantExtractor);
						axiom.getSuperClass().accept(constantExtractor);
					}
				});
			}
		}
		return toReturn;
	}

	private Collection<OWLDataProperty> getAllDataProperties() {
		Set<OWLDataProperty> toReturn = new HashSet<OWLDataProperty>();
		for (OWLOntology owlOntology : this.getConstraintSystem()
				.getOntologyManager().getOntologies()) {
			toReturn.addAll(owlOntology.getReferencedDataProperties());
		}
		return toReturn;
	}

	private Collection<OWLIndividual> getAllIndividuals() {
		Set<OWLIndividual> toReturn = new HashSet<OWLIndividual>();
		for (OWLOntology owlOntology : this.getConstraintSystem()
				.getOntologyManager().getOntologies()) {
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
			toReturn.addAll(this.allClasses);
			break;
		case DATAPROPERTY:
			toReturn.addAll(this.allDataProperties);
			break;
		case OBJECTPROPERTY:
			toReturn.addAll(this.allObjectProperties);
			break;
		case INDIVIDUAL:
			toReturn.addAll(this.allIndividuals);
			break;
		case CONSTANT:
			toReturn.addAll(this.allConstants);
			break;
		default:
			break;
		}
		return toReturn;
	}

	private void initAssignableValues() {
		this.allClasses.addAll(this.getAllClasses());
		Logging.getQueryLogger().log(Level.FINE,
				"Possible class values " + this.allClasses.size());
		this.allDataProperties.addAll(this.getAllDataProperties());
		Logging.getQueryLogger().log(
				Level.FINE,
				"Possible data property values "
						+ this.allDataProperties.size());
		this.allObjectProperties.addAll(this.getObjectProperties());
		Logging.getQueryLogger().log(
				Level.FINE,
				"Possible object property values "
						+ this.allObjectProperties.size());
		this.allIndividuals.addAll(this.getAllIndividuals());
		Logging.getQueryLogger().log(Level.FINE,
				"Possible individual  values " + this.allIndividuals.size());
		this.allConstants.addAll(this.getAllConstants());
		Logging.getQueryLogger().log(Level.FINE,
				"Possible constant  values " + this.allConstants.size());
	}

	/**
	 * @return the constraintSystem
	 */
	public ConstraintSystem getConstraintSystem() {
		return this.constraintSystem;
	}

	private Collection<OWLObjectProperty> getObjectProperties() {
		Set<OWLObjectProperty> toReturn = new HashSet<OWLObjectProperty>();
		for (OWLOntology owlOntology : this.getConstraintSystem()
				.getOntologyManager().getOntologies()) {
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
		this.initAssignableValues();
		Set<BindingNode> existingLeaves = this.getConstraintSystem()
				.getLeaves();
		boolean found = false;
		if (existingLeaves != null) {
			Logging.getQueryTestLogging().log(Level.INFO,
					"Existing leaves count: " + existingLeaves.size());
			int leafIndex = 1;
			for (BindingNode bindingNode : existingLeaves) {
				Logging.getQueryTestLogging().log(
						Level.FINER,
						"Exhaustive search on leaf: " + leafIndex++
								+ " out of " + existingLeaves.size());
				PartialOWLObjectInstantiator partialObjectInstantiator = new PartialOWLObjectInstantiator(
						bindingNode, this.getConstraintSystem());
				OWLAxiom newStartAxiom = (OWLAxiom) start.getAxiom().accept(
						partialObjectInstantiator);
				OPPLOWLAxiomSearchNode newStart = new OPPLOWLAxiomSearchNode(
						newStartAxiom, bindingNode);
				List<List<OPPLOWLAxiomSearchNode>> bindingNodeSolutions = new ArrayList<List<OPPLOWLAxiomSearchNode>>();
				boolean bindingNodeSearch = super.exhaustiveSearchTree(
						newStart, bindingNodeSolutions);
				found = found || bindingNodeSearch;
				if (bindingNodeSearch) {
					solutions.addAll(bindingNodeSolutions);
				}
			}
		} else {
			found = super.exhaustiveSearchTree(start, solutions);
		}
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
