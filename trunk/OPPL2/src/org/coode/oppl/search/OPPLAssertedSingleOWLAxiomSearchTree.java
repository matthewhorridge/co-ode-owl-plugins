/**
 * 
 */
package org.coode.oppl.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDescriptionVisitor;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLAssertedSingleOWLAxiomSearchTree extends
		SearchTree<OPPLOWLAxiomSearchNode> {
	private final ConstraintSystem constraintSystem;
	private final OWLAxiom targetAxiom;
	// private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	private final Set<OWLClass> allClasses = new HashSet<OWLClass>();
	private final Set<OWLObjectProperty> allObjectProperties = new HashSet<OWLObjectProperty>();
	private final Set<OWLDataProperty> allDataProperties = new HashSet<OWLDataProperty>();
	private final Set<OWLIndividual> allIndividuals = new HashSet<OWLIndividual>();
	private final Set<OWLConstant> allConstants = new HashSet<OWLConstant>();

	public OPPLAssertedSingleOWLAxiomSearchTree(OWLAxiom targetAxiom,
			ConstraintSystem constraintSystem) {
		if (constraintSystem == null) {
			throw new NullPointerException(
					"The constraint system cannot be null");
		}
		this.targetAxiom = targetAxiom;
		this.constraintSystem = constraintSystem;
		this.initAssignableValues();
	}

	/**
	 * @see org.coode.oppl.search.SearchTree#getChildren(java.lang.Object)
	 */
	@Override
	protected List<OPPLOWLAxiomSearchNode> getChildren(
			OPPLOWLAxiomSearchNode node) {
		List<OPPLOWLAxiomSearchNode> toReturn = new ArrayList<OPPLOWLAxiomSearchNode>();
		VariableExtractor variableExtractor = new VariableExtractor(this
				.getConstraintSystem());
		Set<Variable> variables = node.getAxiom().accept(variableExtractor);
		BindingNode binding = node.getBinding();
		for (Variable variable : variables) {
			Collection<OWLObject> values = new HashSet<OWLObject>(this
					.getAssignableValues(variable));
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
	 *         represents an OWLAxiom that is equal to the target axiom.
	 * @see org.coode.oppl.search.SearchTree#goalReached(java.lang.Object)
	 */
	@Override
	protected boolean goalReached(OPPLOWLAxiomSearchNode start) {
		return this.targetAxiom.equals(start.getAxiom());
	}

	private Collection<OWLClass> getAllClasses() {
		final Set<OWLClass> toReturn = new HashSet<OWLClass>();
		for (OWLEntity entity : this.getTargetAxiom().getReferencedEntities()) {
			entity.accept(new OWLEntityVisitor() {
				public void visit(OWLDataType dataType) {
				}

				public void visit(OWLIndividual individual) {
				}

				public void visit(OWLDataProperty property) {
				}

				public void visit(OWLObjectProperty property) {
				}

				public void visit(OWLClass cls) {
					toReturn.add(cls);
				}
			});
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
		this.getTargetAxiom().accept(new OWLAxiomVisitorAdapter() {
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
				for (OWLDescription description : axiom.getDescriptions()) {
					description.accept(constantExtractor);
				}
			}

			@Override
			public void visit(OWLEquivalentClassesAxiom axiom) {
				for (OWLDescription description : axiom.getDescriptions()) {
					description.accept(constantExtractor);
				}
			}

			@Override
			public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
				toReturn.add(axiom.getObject());
			}

			@Override
			public void visit(OWLSubClassAxiom axiom) {
				axiom.getSubClass().accept(constantExtractor);
				axiom.getSuperClass().accept(constantExtractor);
			}
		});
		return toReturn;
	}

	private Collection<OWLDataProperty> getAllDataProperties() {
		final Set<OWLDataProperty> toReturn = new HashSet<OWLDataProperty>();
		for (OWLEntity entity : this.getTargetAxiom().getReferencedEntities()) {
			entity.accept(new OWLEntityVisitor() {
				public void visit(OWLDataType dataType) {
				}

				public void visit(OWLIndividual individual) {
				}

				public void visit(OWLDataProperty property) {
					toReturn.add(property);
				}

				public void visit(OWLObjectProperty property) {
				}

				public void visit(OWLClass cls) {
				}
			});
		}
		return toReturn;
	}

	private Collection<OWLIndividual> getAllIndividuals() {
		final Set<OWLIndividual> toReturn = new HashSet<OWLIndividual>();
		for (OWLEntity entity : this.getTargetAxiom().getReferencedEntities()) {
			entity.accept(new OWLEntityVisitor() {
				public void visit(OWLDataType dataType) {
				}

				public void visit(OWLIndividual individual) {
					toReturn.add(individual);
				}

				public void visit(OWLDataProperty property) {
				}

				public void visit(OWLObjectProperty property) {
				}

				public void visit(OWLClass cls) {
				}
			});
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
		this.allDataProperties.addAll(this.getAllDataProperties());
		this.allObjectProperties.addAll(this.getAllObjectProperties());
		this.allIndividuals.addAll(this.getAllIndividuals());
		this.allConstants.addAll(this.getAllConstants());
	}

	/**
	 * @return the constraintSystem
	 */
	public ConstraintSystem getConstraintSystem() {
		return this.constraintSystem;
	}

	private Collection<OWLObjectProperty> getAllObjectProperties() {
		final Set<OWLObjectProperty> toReturn = new HashSet<OWLObjectProperty>();
		for (OWLEntity entity : this.getTargetAxiom().getReferencedEntities()) {
			entity.accept(new OWLEntityVisitor() {
				public void visit(OWLDataType dataType) {
				}

				public void visit(OWLIndividual individual) {
				}

				public void visit(OWLDataProperty property) {
				}

				public void visit(OWLObjectProperty property) {
					toReturn.add(property);
				}

				public void visit(OWLClass cls) {
				}
			});
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
		if (start == null) {
			throw new NullPointerException("The starting node cannot be null");
		}
		if (solutions == null) {
			throw new NullPointerException(
					"The list on which solutions will be stored cannot be null");
		}
		boolean found = false;
		if (this.getTargetAxiom().getAxiomType().equals(
				start.getAxiom().getAxiomType())) {
			found = super.exhaustiveSearchTree(start, solutions);
		}
		return found;
	}

	/**
	 * @return the targetAxiom
	 */
	public OWLAxiom getTargetAxiom() {
		return this.targetAxiom;
	}
}
