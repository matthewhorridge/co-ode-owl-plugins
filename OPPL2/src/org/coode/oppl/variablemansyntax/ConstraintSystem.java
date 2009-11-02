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

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.coode.oppl.AbstractConstraint;
import org.coode.oppl.AssertedAxiomQuery;
import org.coode.oppl.AxiomQuery;
import org.coode.oppl.ConstraintChecker;
import org.coode.oppl.InferredAxiomQuery;
import org.coode.oppl.exceptions.InvalidVariableNameException;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.utils.VariableDetector;
import org.coode.oppl.utils.VariableExtractor;
import org.coode.oppl.variablemansyntax.bindingtree.Assignment;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.coode.oppl.variablemansyntax.bindingtree.LeafBrusher;
import org.coode.oppl.variablemansyntax.generated.AbstractCollectionGeneratedValue;
import org.coode.oppl.variablemansyntax.generated.AbstractGeneratedVariable;
import org.coode.oppl.variablemansyntax.generated.AbstractOWLObjectCollectionGeneratedVariable;
import org.coode.oppl.variablemansyntax.generated.GeneratedValue;
import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;
import org.coode.oppl.variablemansyntax.generated.StringGeneratedVariable;
import org.coode.oppl.visitors.GeneratedVariableCollector;
import org.coode.oppl.visitors.InputVariableCollector;
import org.protege.editor.owl.model.inference.NoOpReasoner;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLAxiomVisitor;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointUnionAxiom;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.SWRLRule;

/**
 * @author Luigi Iannone
 * 
 */
public class ConstraintSystem implements OWLAxiomVisitor {
	private final Map<String, Variable> variables = new HashMap<String, Variable>();
	private final OWLOntology ontology;
	private final Set<OWLOntology> ontologies;
	private final Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
	private Set<BindingNode> leaves = null;
	private final OWLDataFactory dataFactory;
	private final Set<AbstractConstraint> constraints = new HashSet<AbstractConstraint>();
	private OWLReasoner reasoner = null;
	private final Map<BindingNode, Set<OWLAxiom>> instantiatedAxioms = new HashMap<BindingNode, Set<OWLAxiom>>();

	public ConstraintSystem(OWLOntology ontology,
			OWLOntologyManager ontologyManager) {
		this.ontology = ontology;
		this.ontologies = ontologyManager.getOntologies();
		this.dataFactory = ontologyManager.getOWLDataFactory();
	}

	public ConstraintSystem(OWLOntology ontology,
			OWLOntologyManager ontologyManager, OWLReasoner reasoner) {
		this(ontology, ontologyManager);
		this.reasoner = reasoner;
	}

	public Variable getVariable(String name) {
		return this.variables.get(name);
	}

	public Variable createVariable(String name, VariableType type)
			throws OPPLException {
		if (name.matches("\\?([\\p{Alnum}[-_]])+")) {
			VariableImpl newVariable = new VariableImpl(name.trim(), type);
			this.variables.put(name, newVariable);
			return newVariable;
		} else {
			throw new InvalidVariableNameException("Invalid name: " + name);
		}
	}

	public void addAxiom(OWLAxiom axiom) {
		this.axioms.add(axiom);
		axiom.accept(this);
	}

	public void visit(OWLSubClassAxiom axiom) {
		updateBindings(axiom);
	}

	private void updateBindings(OWLAxiom axiom) {
		if (isVariableAxiom(axiom)) {
			updateLeaves(axiom);
			System.out.println("Initial size: "
					+ (this.leaves == null ? "empty" : this.leaves.size()));
			AxiomQuery query = this.reasoner == null
					|| this.reasoner instanceof NoOpReasoner ? new AssertedAxiomQuery(
					this.ontologies, this)
					: new InferredAxiomQuery(this.ontologies, this,
							this.dataFactory, this.reasoner);
			axiom.accept(query);
			Map<BindingNode, Set<OWLAxiom>> queryInstantiations = query
					.getInstantiations();
			// Update the instantiated axioms
			for (BindingNode node : queryInstantiations.keySet()) {
				Set<OWLAxiom> earlierInstantiations = this.instantiatedAxioms
						.get(node);
				// If the binding node already exists I add the instantiated
				// axioms resulting from the last query
				if (earlierInstantiations != null) {
					earlierInstantiations.addAll(queryInstantiations.get(node));
				} else {
					// I create a new one.
					this.instantiatedAxioms.put(node, queryInstantiations
							.get(node));
				}
			}
			System.out.println("Currently instantiated axioms count: "
					+ this.instantiatedAxioms.size());
		}
	}

	private void updateLeaves(OWLAxiom axiom) {
		if (this.leaves != null) {
			for (BindingNode bindingNode : this.leaves) {
				Set<Variable> axiomVariables = getAxiomVariables(axiom);
				for (Variable variable : axiomVariables) {
					if (!(bindingNode.getAssignedVariables().contains(variable) || bindingNode
							.getUnassignedVariables().contains(variable))) {
						Set<OWLAxiom> bindingInstantiatedAxioms = this.instantiatedAxioms
								.get(bindingNode);
						this.instantiatedAxioms.remove(bindingNode);
						bindingNode.addUnassignedVariable(variable);
						this.instantiatedAxioms.put(bindingNode,
								bindingInstantiatedAxioms);
					}
				}
			}
		}
	}

	private void updateBindingsAssertedAxiom(OWLAxiom axiom) {
		if (isVariableAxiom(axiom)) {
			System.out.println("Initial size: "
					+ (this.leaves == null ? "empty" : this.leaves.size()));
			AxiomQuery query = new AssertedAxiomQuery(this.ontologies, this);
			axiom.accept(query);
			this.instantiatedAxioms.putAll(query.getInstantiations());
			System.out.println("Current size: "
					+ this.instantiatedAxioms.size());
		}
	}

	private boolean isVariableAxiom(OWLAxiom axiom) {
		return !getAxiomVariables(axiom).isEmpty();
	}

	public Set<Variable> getAxiomVariables(OWLAxiom axiom) {
		VariableExtractor axiomVariableExtractor = new VariableExtractor(this);
		Set<Variable> axiomVariables = axiom.accept(axiomVariableExtractor);
		return new HashSet<Variable>(axiomVariables);
	}

	public boolean isVariableURI(URI uri) {
		boolean found = false;
		Iterator<Variable> it = this.variables.values().iterator();
		while (!found && it.hasNext()) {
			Variable variable = it.next();
			found = uri.equals(variable.getURI());
		}
		return found;
	}

	public Variable getVariable(URI uri) {
		boolean found = false;
		Iterator<Variable> it = this.variables.values().iterator();
		Variable variable = null;
		while (!found && it.hasNext()) {
			variable = it.next();
			found = uri.equals(variable.getURI());
		}
		return found ? variable : null;
	}

	public boolean isVariable(OWLDescription desc) {
		VariableDetector variableDetector = new VariableDetector(this);
		return desc.accept(variableDetector);
	}

	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLDisjointClassesAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLDataPropertyDomainAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLImportsDeclaration axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLAxiomAnnotationAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLDifferentIndividualsAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLObjectSubPropertyAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLDisjointUnionAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLDeclarationAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLEntityAnnotationAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLOntologyAnnotationAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLDataPropertyRangeAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLClassAssertionAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLEquivalentClassesAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLDataSubPropertyAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLSameIndividualsAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		updateBindings(axiom);
	}

	public void visit(SWRLRule rule) {
		updateBindings(rule);
	}

	public boolean isVariable(OWLObjectProperty property) {
		return isVariableURI(property.getURI());
	}

	public boolean isVariable(OWLDataProperty property) {
		return isVariableURI(property.getURI());
	}

	public boolean isVariable(OWLIndividual individual) {
		return isVariableURI(individual.getURI());
	}

	/**
	 * @return the leaves
	 */
	public Set<BindingNode> getLeaves() {
		// No query nor constraint has been added to this ConstraintSystem
		// variables will assume all the possible values they can
		if (this.leaves == null && getAxioms().isEmpty()
				&& getConstraints().isEmpty()) {
			setupLeaves();
		}
		return this.leaves;
	}

	protected void setupLeaves() {
		Set<Variable> inputVariables = getInputVariables();
		for (Variable variable : inputVariables) {
			for (OWLOntology ontology1 : getOntologies()) {
				Set<? extends OWLObject> referencedValues = variable.getType()
						.getReferencedValues(ontology1);
				for (OWLObject object : referencedValues) {
					try {
						variable.addPossibleBinding(object);
					} catch (OWLReasonerException e) {
						e.printStackTrace();
					}
				}
			}
		}
		BindingNode root = new BindingNode(new HashSet<Assignment>(),
				inputVariables);
		LeafBrusher leafBrusher = new LeafBrusher();
		root.accept(leafBrusher);
		this.leaves = leafBrusher.getLeaves();
	}

	public Set<Variable> getInputVariables() {
		InputVariableCollector visitor = new InputVariableCollector();
		for (Variable variable : this.variables.values()) {
			variable.accept(visitor);
		}
		return new HashSet<Variable>(visitor.getCollectedVariables());
	}

	public void removeBinding(BindingNode binding) {
		this.leaves.remove(binding);
	}

	public boolean isVariable(OWLConstant node) {
		return node.getLiteral().startsWith("?");
	}

	public void setLeaves(Set<BindingNode> newLeaves) {
		this.leaves = newLeaves;
		for (BindingNode bindingNode : new HashSet<BindingNode>(
				this.instantiatedAxioms.keySet())) {
			if (!this.leaves.contains(bindingNode)) {
				this.instantiatedAxioms.remove(bindingNode);
			}
		}
	}

	public void addConstraint(AbstractConstraint c) {
		this.constraints.add(c);
		if (this.leaves != null && !this.leaves.isEmpty()) {
			Iterator<BindingNode> it = this.leaves.iterator();
			BindingNode leaf;
			while (it.hasNext()) {
				leaf = it.next();
				boolean holdingLeaf = checkConstraints(leaf);
				if (!holdingLeaf) {
					it.remove();
				}
			}
			this.instantiatedAxioms.keySet().retainAll(this.leaves);
			for (OWLAxiom axiom : this.axioms) {
				for (BindingNode aNewLeaf : new HashSet<BindingNode>(
						this.leaves)) {
					OWLObjectInstantiator instantiator = new OWLObjectInstantiator(
							aNewLeaf, this);
					OWLAxiom instatiatedAxiom = (OWLAxiom) axiom
							.accept(instantiator);
					Set<OWLAxiom> values = this.instantiatedAxioms
							.get(aNewLeaf);
					if (values == null) {
						values = new HashSet<OWLAxiom>();
					}
					values.add(instatiatedAxiom);
					this.instantiatedAxioms.put(aNewLeaf, values);
				}
			}
		}
	}

	public Set<AbstractConstraint> getConstraints() {
		return new HashSet<AbstractConstraint>(this.constraints);
	}

	/**
	 * @param leaf
	 * @return if the BindingNode satisfies the constraints
	 */
	private boolean checkConstraints(BindingNode leaf) {
		boolean hold = true;
		Iterator<AbstractConstraint> it = getConstraints().iterator();
		AbstractConstraint c;
		ConstraintChecker constraintChecker = new ConstraintChecker(leaf, this);
		while (hold && it.hasNext()) {
			c = it.next();
			hold = c.accept(constraintChecker);
		}
		return hold;
	}

	public void setReasoner(OWLReasoner reasoner) {
		this.reasoner = reasoner;
	}

	/**
	 * @return the variables
	 */
	public Map<String, Variable> getVariableMap() {
		return this.variables;
	}

	public Set<Variable> getVariables() {
		return new HashSet<Variable>(this.variables.values());
	}

	/**
	 * @return the ontologies
	 */
	public Set<OWLOntology> getOntologies() {
		return this.ontologies;
	}

	/**
	 * @return the axioms
	 */
	public Set<OWLAxiom> getAxioms() {
		return this.axioms;
	}

	/**
	 * @return the reasoner
	 */
	public OWLReasoner getReasoner() {
		return this.reasoner;
	}

	public GeneratedVariable<String> createStringGeneratedVariable(String name,
			VariableType type, GeneratedValue<String> value) {
		AbstractGeneratedVariable<String> generatedVariable = StringGeneratedVariable
				.buildGeneratedVariable(name, type, value, getOntology());
		this.variables.put(name, generatedVariable);
		return generatedVariable;
	}

	/**
	 * @return the ontology
	 */
	public OWLOntology getOntology() {
		return this.ontology;
	}

	public Set<GeneratedVariable<?>> getGeneratedVariables() {
		GeneratedVariableCollector visitor = new GeneratedVariableCollector();
		for (Variable v : this.variables.values()) {
			v.accept(visitor);
		}
		return new HashSet<GeneratedVariable<?>>(visitor
				.getCollectedVariables());
	}

	/**
	 * @return the instantiatedAxioms
	 */
	public Map<BindingNode, Set<OWLAxiom>> getInstantiatedAxioms() {
		return new HashMap<BindingNode, Set<OWLAxiom>>(this.instantiatedAxioms);
	}

	public void addAssertedAxiom(OWLAxiom axiom) {
		this.axioms.add(axiom);
		updateBindingsAssertedAxiom(axiom);
	}

	/**
	 * @return the dataFactory
	 */
	public OWLDataFactory getDataFactory() {
		return this.dataFactory;
	}

	public void reset() {
		this.leaves = null;
		this.instantiatedAxioms.clear();
	}

	public void removeVariable(Variable variable) {
		this.variables.remove(variable.getName());
	}

	public GeneratedVariable<Collection<OWLClass>> createIntersectionGeneratedVariable(
			String name, VariableType type,
			AbstractCollectionGeneratedValue<OWLClass> collection) {
		GeneratedVariable<Collection<OWLClass>> toReturn = null;
		if (type.equals(VariableType.CLASS)) {
			toReturn = AbstractOWLObjectCollectionGeneratedVariable
					.getConjunction(name, type, collection, this.dataFactory);
			this.variables.put(name, toReturn);
		} else {
			throw new IllegalArgumentException("Incompatibile type "
					+ type.name());
		}
		return toReturn;
	}

	public GeneratedVariable<Collection<OWLClass>> createUnionGeneratedVariable(
			String name, VariableType type,
			AbstractCollectionGeneratedValue<OWLClass> collection) {
		GeneratedVariable<Collection<OWLClass>> toReturn = null;
		if (type.equals(VariableType.CLASS)) {
			toReturn = AbstractOWLObjectCollectionGeneratedVariable
					.getDisjunction(name, type, collection, this.dataFactory);
			this.variables.put(name, toReturn);
		}
		if (toReturn == null) {
			throw new IllegalArgumentException("Incompatibile type "
					+ type.name());
		}
		return toReturn;
	}

	public String render(Variable variable) {
		return variable.getName();
	}

	public void importVariable(Variable v) {
		this.variables.put(v.getName(), v);
	}

	public void clearVariables() {
		this.variables.clear();
	}
}
