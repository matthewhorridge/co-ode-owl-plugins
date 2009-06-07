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
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.OWLObjectInstantiator;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.Assignment;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.coode.oppl.variablemansyntax.bindingtree.LeafBrusher;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
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
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.SWRLRule;

import com.clarkparsia.explanation.SatisfiabilityConverter;

/**
 * @author Luigi Iannone
 * 
 */
public class InferredAxiomQuery implements AxiomQuery {
	protected Set<OWLOntology> ontologies;
	private ConstraintSystem constraintSystem;
	private OWLDataFactory dataFactory;
	private Map<BindingNode, Set<OWLAxiom>> instantiatedAxioms = new HashMap<BindingNode, Set<OWLAxiom>>();
	protected OWLReasoner reasoner;

	/**
	 * @param ontologies
	 */
	public InferredAxiomQuery(Set<OWLOntology> ontologies, ConstraintSystem cs,
			OWLDataFactory dataFactory, OWLReasoner reasoner) {
		this.ontologies = ontologies;
		this.constraintSystem = cs;
		this.dataFactory = dataFactory;
		this.reasoner = reasoner;
	}

	public void visit(OWLSubClassAxiom axiom) {
		this.match(axiom);
	}

	/**
	 * @param axiom
	 */
	private void match(OWLAxiom axiom) {
		Set<Variable> axiomVariables = this.constraintSystem
				.getAxiomVariables(axiom);
		for (Variable variable : axiomVariables) {
			this.extractPossibleValues(variable);
		}
		Set<BindingNode> preExistingLeaves = this.constraintSystem.getLeaves();
		Set<BindingNode> leaves;
		if (preExistingLeaves == null) {
			BindingNode bindingNode = new BindingNode(
					new HashSet<Assignment>(), axiomVariables);
			LeafBrusher leafBrusher = new LeafBrusher();
			bindingNode.accept(leafBrusher);
			leaves = leafBrusher.getLeaves();
		} else {
			leaves = new HashSet<BindingNode>(preExistingLeaves);
			for (BindingNode preExistingLeaf : preExistingLeaves) {
				LeafBrusher leafBrusher = new LeafBrusher();
				preExistingLeaf.accept(leafBrusher);
				leaves.remove(preExistingLeaf);
				leaves.addAll(leafBrusher.getLeaves());
			}
		}
		for (BindingNode leaf : new HashSet<BindingNode>(leaves)) {
			OWLObjectInstantiator instantiator = new OWLObjectInstantiator(
					leaf, this.constraintSystem);
			OWLAxiom instatiatedAxiom = (OWLAxiom) axiom.accept(instantiator);
			if (this.locateAxiom(instatiatedAxiom)) {
				Set<OWLAxiom> axioms = this.instantiatedAxioms.get(leaf);
				if (axioms == null) {
					axioms = new HashSet<OWLAxiom>();
				}
				axioms.add(instatiatedAxiom);
				this.instantiatedAxioms.put(leaf, axioms);
			} else {
				leaves.remove(leaf);
			}
		}
		this.constraintSystem.setLeaves(leaves);
	}

	/**
	 * @param axiom
	 */
	private boolean locateAxiom(OWLAxiom axiom) {
		SatisfiabilityConverter converter = new SatisfiabilityConverter(
				this.dataFactory);
		OWLDescription conversion = converter.convert(axiom);
		try {
			if (!this.reasoner.isClassified()) {
				this.reasoner.classify();
			}
			return !this.reasoner.isSatisfiable(conversion);
		} catch (OWLReasonerException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLDisjointClassesAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLDataPropertyDomainAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLImportsDeclaration axiom) {
		this.match(axiom);
	}

	public void visit(OWLAxiomAnnotationAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLDifferentIndividualsAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLObjectSubPropertyAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLDisjointUnionAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLDeclarationAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLEntityAnnotationAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLOntologyAnnotationAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLDataPropertyRangeAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLClassAssertionAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLEquivalentClassesAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLDataSubPropertyAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLSameIndividualsAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
		this.match(axiom);
	}

	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		this.match(axiom);
	}

	public void visit(SWRLRule rule) {
		this.match(rule);
	}

	public Map<BindingNode, Set<OWLAxiom>> getInstantiations() {
		Map<BindingNode, Set<OWLAxiom>> toReturn = new HashMap<BindingNode, Set<OWLAxiom>>();
		if (this.constraintSystem.getLeaves() != null) {
			toReturn.putAll(this.instantiatedAxioms);
		}
		return toReturn;
	}

	/**
	 * @param variable
	 * @param ontologyAxiom
	 */
	private void extractPossibleValues(Variable variable) {
		variable.clearBindings();
		ValueExtractor valueExtractor = new ValueExtractor(this.ontologies);
		Set<OWLObject> values;
		values = variable.getType().accept(valueExtractor);
		for (OWLObject object : values) {
			try {
				variable.addPossibleBinding(object);
			} catch (OWLReasonerException e) {
				Logger.getLogger(this.getClass().getName()).warning(
						"Problem in adding value " + object + " for variable "
								+ variable);
			}
		}
	}
}
