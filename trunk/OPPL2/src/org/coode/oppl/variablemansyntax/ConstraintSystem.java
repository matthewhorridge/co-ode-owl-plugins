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
import java.util.Collections;
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
import org.coode.oppl.OPPLException;
import org.coode.oppl.variablemansyntax.bindingtree.Assignment;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.coode.oppl.variablemansyntax.bindingtree.LeafBrusher;
import org.coode.oppl.variablemansyntax.generated.CollectionGeneratedValue;
import org.coode.oppl.variablemansyntax.generated.GeneratedValue;
import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;
import org.coode.oppl.variablemansyntax.generated.OWLObjectCollectionGeneratedVariable;
import org.coode.oppl.variablemansyntax.generated.StringGeneratedVariable;
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
	private Map<String, Variable> variables = new HashMap<String, Variable>();
	private final OWLOntology ontology;
	private Set<OWLOntology> ontologies;
	private Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
	private Set<BindingNode> leaves = null;
	private final OWLDataFactory dataFactory;
	private Set<AbstractConstraint> constraints = new HashSet<AbstractConstraint>();
	private OWLReasoner reasoner = null;
	private final Set<OWLAxiom> instantiatedAxioms = new HashSet<OWLAxiom>();

	public ConstraintSystem(OWLOntology ontology,
			OWLOntologyManager ontologyManager) {
		this.ontology = ontology;
		this.ontologies = ontologyManager.getOntologies();
		this.dataFactory = ontologyManager.getOWLDataFactory();
	}

	public ConstraintSystem(OWLOntology ontology,
			OWLOntologyManager ontologyManager, OWLReasoner reasoner) {
		this.ontology = ontology;
		this.ontologies = ontologyManager.getOntologies();
		this.dataFactory = ontologyManager.getOWLDataFactory();
		this.reasoner = reasoner;
	}

	public Variable getVariable(String name) {
		Variable toReturn = this.variables.get(name);
		return toReturn;
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
		this.updateBindings(axiom);
	}

	private void updateBindings(OWLAxiom axiom) {
		if (this.isVariableAxiom(axiom)) {
			this.updateLeaves(axiom);
			System.out.println("Initial size: "
					+ (this.leaves == null ? "empty" : this.leaves.size()));
			AxiomQuery query = this.reasoner == null
					|| this.reasoner instanceof NoOpReasoner ? new AssertedAxiomQuery(
					this.ontologies, this, this.dataFactory)
					: new InferredAxiomQuery(this.ontologies, this,
							this.dataFactory, this.reasoner);
			axiom.accept(query);
			this.instantiatedAxioms.addAll(query.getInstantiations());
			System.out.println("Currently instantiated axioms count: "
					+ this.instantiatedAxioms.size());
		}
	}

	private void updateLeaves(OWLAxiom axiom) {
		if (this.leaves != null) {
			for (BindingNode bindingNode : this.leaves) {
				Set<Variable> axiomVariables = this.getAxiomVariables(axiom);
				for (Variable variable : axiomVariables) {
					if (!(bindingNode.getAssignedVariables().contains(variable) || bindingNode
							.getAssignedVariables().contains(variable))) {
						bindingNode.addUnassignedVariable(variable);
					}
				}
			}
		}
	}

	private void updateBindingsAssertedAxiom(OWLAxiom axiom) {
		if (this.isVariableAxiom(axiom)) {
			System.out.println("Initial size: "
					+ (this.leaves == null ? "empty" : this.leaves.size()));
			AxiomQuery query = new AssertedAxiomQuery(this.ontologies, this,
					this.dataFactory);
			axiom.accept(query);
			this.instantiatedAxioms.clear();
			this.instantiatedAxioms.addAll(query.getInstantiations());
			System.out.println("Current size: "
					+ this.instantiatedAxioms.size());
		}
	}

	private boolean isVariableAxiom(OWLAxiom axiom) {
		return !this.getAxiomVariables(axiom).isEmpty();
	}

	public Set<Variable> getAxiomVariables(OWLAxiom axiom) {
		AxiomVariableExtractor axiomVariableExtractor = new AxiomVariableExtractor(
				this);
		Set<Variable> axiomVariables = axiom.accept(axiomVariableExtractor);
		return Collections.unmodifiableSet(axiomVariables);
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
		this.updateBindings(axiom);
	}

	public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLDisjointClassesAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLDataPropertyDomainAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLImportsDeclaration axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLAxiomAnnotationAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLDifferentIndividualsAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLObjectSubPropertyAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLDisjointUnionAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLDeclarationAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLEntityAnnotationAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLOntologyAnnotationAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLDataPropertyRangeAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLClassAssertionAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLEquivalentClassesAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLDataSubPropertyAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLSameIndividualsAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		this.updateBindings(axiom);
	}

	public void visit(SWRLRule rule) {
		this.updateBindings(rule);
	}

	public boolean isVariable(OWLObjectProperty property) {
		return this.isVariableURI(property.getURI());
	}

	public boolean isVariable(OWLDataProperty property) {
		return this.isVariableURI(property.getURI());
	}

	public boolean isVariable(OWLIndividual individual) {
		return this.isVariableURI(individual.getURI());
	}

	/**
	 * @return the leaves
	 */
	public Set<BindingNode> getLeaves() {
		// No query nor constraint has been added to this ConstraintSystem
		// variables will assume all the possible values they can
		if (this.leaves == null && this.getAxioms().isEmpty()
				&& this.getConstraints().isEmpty()) {
			this.setupLeaves();
		}
		return this.leaves;
	}

	/**
	 * 
	 */
	protected void setupLeaves() {
		for (Variable variable : this.getVariables()) {
			if (variable instanceof InputVariable) {
				for (OWLOntology ontology : this.getOntologies()) {
					Set<? extends OWLObject> referencedValues = variable
							.getType().getReferencedValues(ontology);
					for (OWLObject object : referencedValues) {
						try {
							variable.addPossibleBinding(object);
						} catch (OWLReasonerException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		BindingNode root = new BindingNode(new HashSet<Assignment>(),
				new HashSet<Variable>(this.getInputVariables()));
		LeafBrusher leafBrusher = new LeafBrusher();
		root.accept(leafBrusher);
		this.leaves = leafBrusher.getLeaves();
	}

	public Set<InputVariable> getInputVariables() {
		Set<InputVariable> toReturn = new HashSet<InputVariable>();
		for (Variable variable : this.getVariables()) {
			if (variable instanceof InputVariable) {
				toReturn.add((InputVariable) variable);
			}
		}
		return toReturn;
	}

	public void removeBinding(BindingNode binding) {
		this.leaves.remove(binding);
	}

	public boolean isVariable(OWLConstant node) {
		return node.toString().trim().startsWith("?");
	}

	public void setLeaves(Set<BindingNode> newLeaves) {
		this.leaves = newLeaves;
	}

	public void addConstraint(AbstractConstraint c) {
		this.constraints.add(c);
		if (this.leaves != null && !this.leaves.isEmpty()) {
			Iterator<BindingNode> it = this.leaves.iterator();
			BindingNode leaf;
			while (it.hasNext()) {
				leaf = it.next();
				boolean holdingLeaf = this.checkConstraints(leaf);
				if (!holdingLeaf) {
					it.remove();
				}
			}
			this.instantiatedAxioms.clear();
			for (OWLAxiom axiom : this.axioms) {
				for (BindingNode aNewLeaf : new HashSet<BindingNode>(
						this.leaves)) {
					OWLObjectInstantiator instantiator = new OWLObjectInstantiator(
							aNewLeaf, this, this.dataFactory);
					OWLAxiom instatiatedAxiom = (OWLAxiom) axiom
							.accept(instantiator);
					this.instantiatedAxioms.add(instatiatedAxiom);
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
	public boolean checkConstraints(BindingNode leaf) {
		boolean hold = true;
		Iterator<AbstractConstraint> it = this.getConstraints().iterator();
		AbstractConstraint c;
		ConstraintChecker constraintChecker = new ConstraintChecker(leaf, this,
				this.dataFactory);
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

	public Variable createStringGeneratedVariable(String name,
			VariableType type, GeneratedValue<String> value)
			throws IncompatibleValueException {
		GeneratedVariable<String> generatedVariable = StringGeneratedVariable
				.buildGeneratedVariable(name, type, value, this.getOntology());
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
		Set<GeneratedVariable<?>> toReturn = new HashSet<GeneratedVariable<?>>(
				this.getVariables().size());
		for (Variable v : this.getVariables()) {
			if (v instanceof GeneratedVariable) {
				toReturn.add((GeneratedVariable<?>) v);
			}
		}
		return toReturn;
	}

	/**
	 * @return the instantiatedAxioms
	 */
	public Set<OWLAxiom> getInstantiatedAxioms() {
		return this.instantiatedAxioms;
	}

	public void addAssertedAxiom(OWLAxiom axiom) {
		this.axioms.add(axiom);
		this.updateBindingsAssertedAxiom(axiom);
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

	public Variable createIntersectionGeneratedVariable(String name,
			VariableType type, CollectionGeneratedValue<OWLClass> collection) {
		Variable toReturn = null;
		switch (type) {
		case CLASS:
			toReturn = OWLObjectCollectionGeneratedVariable.getConjunction(
					name, type, collection, this.dataFactory);
			this.variables.put(name, toReturn);
			break;
		default:
			throw new IllegalArgumentException("Incopatibile type " + type);
		}
		return toReturn;
	}

	public Variable createUnionGeneratedVariable(String name,
			VariableType type, CollectionGeneratedValue<OWLClass> collection) {
		Variable toReturn = null;
		switch (type) {
		case CLASS:
			toReturn = OWLObjectCollectionGeneratedVariable.getDisjunction(
					name, type, collection, this.dataFactory);
			this.variables.put(name, toReturn);
			break;
		default:
			throw new IllegalArgumentException("Incopatibile type " + type);
		}
		return toReturn;
	}

	public String render(Variable variable) {
		return variable.getName();
	}

	public void importVariable(Variable v) {
		this.variables.put(v.getName(), v);
	}
}
