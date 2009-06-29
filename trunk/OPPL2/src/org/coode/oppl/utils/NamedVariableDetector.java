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
package org.coode.oppl.utils;

import java.util.Iterator;
import java.util.Set;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.Variable;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLConstantAnnotation;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataComplementOf;
import org.semanticweb.owl.model.OWLDataExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataOneOf;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataRangeFacetRestriction;
import org.semanticweb.owl.model.OWLDataRangeRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValueRestriction;
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
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectAnnotation;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectOneOf;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectPropertyInverse;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSelfRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLObjectVisitorEx;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.model.OWLUntypedConstant;
import org.semanticweb.owl.model.SWRLAtomConstantObject;
import org.semanticweb.owl.model.SWRLAtomDVariable;
import org.semanticweb.owl.model.SWRLAtomIVariable;
import org.semanticweb.owl.model.SWRLAtomIndividualObject;
import org.semanticweb.owl.model.SWRLBuiltInAtom;
import org.semanticweb.owl.model.SWRLClassAtom;
import org.semanticweb.owl.model.SWRLDataRangeAtom;
import org.semanticweb.owl.model.SWRLDataValuedPropertyAtom;
import org.semanticweb.owl.model.SWRLDifferentFromAtom;
import org.semanticweb.owl.model.SWRLObjectPropertyAtom;
import org.semanticweb.owl.model.SWRLRule;
import org.semanticweb.owl.model.SWRLSameAsAtom;

/**
 * Detects the presence of a particular variable in an OWLObject instance.
 * 
 * @author Luigi Iannone
 * 
 */
public class NamedVariableDetector implements OWLObjectVisitorEx<Boolean> {
	private ConstraintSystem constraintSystem;
	private final Variable variable;

	/**
	 * @param constraintSystem
	 */
	public NamedVariableDetector(Variable variable,
			ConstraintSystem constraintSystem) {
		this.constraintSystem = constraintSystem;
		this.variable = variable;
	}

	public Boolean visit(OWLClass desc) {
		return this.constraintSystem.isVariableURI(desc.getURI())
				&& this.constraintSystem.getVariable(desc.getURI()).equals(
						this.variable);
	}

	public Boolean visit(OWLObjectIntersectionOf desc) {
		boolean found = false;
		Iterator<OWLDescription> it = desc.getOperands().iterator();
		OWLDescription operand;
		while (!found && it.hasNext()) {
			operand = it.next();
			found = operand.accept(this);
		}
		return found;
	}

	public Boolean visit(OWLObjectUnionOf desc) {
		boolean found = false;
		Iterator<OWLDescription> it = desc.getOperands().iterator();
		OWLDescription operand;
		while (!found && it.hasNext()) {
			operand = it.next();
			found = operand.accept(this);
		}
		return found;
	}

	public Boolean visit(OWLObjectComplementOf desc) {
		return desc.getOperand().accept(this);
	}

	public Boolean visit(OWLObjectSomeRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty())
				|| desc.getFiller().accept(this);
	}

	public Boolean visit(OWLObjectAllRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty())
				|| desc.getFiller().accept(this);
	}

	public Boolean visit(OWLObjectValueRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty())
				|| this.constraintSystem.isVariable(desc.getValue());
	}

	public Boolean visit(OWLObjectMinCardinalityRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty())
				|| (desc.getFiller() == null ? false : desc.getFiller().accept(
						this));
	}

	public Boolean visit(OWLObjectExactCardinalityRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty())
				|| (desc.getFiller() == null ? false : desc.getFiller().accept(
						this));
	}

	public Boolean visit(OWLObjectMaxCardinalityRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty())
				|| (desc.getFiller() == null ? false : desc.getFiller().accept(
						this));
	}

	public Boolean visit(OWLObjectSelfRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty());
	}

	public Boolean visit(OWLObjectOneOf desc) {
		boolean found = false;
		Iterator<OWLIndividual> it = desc.getIndividuals().iterator();
		OWLIndividual individual;
		while (!found && it.hasNext()) {
			individual = it.next();
			found = this.constraintSystem.isVariable(individual);
		}
		return found;
	}

	public Boolean visit(OWLDataSomeRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLDataProperty());
	}

	public Boolean visit(OWLDataAllRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLDataProperty());
	}

	public Boolean visit(OWLDataValueRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLDataProperty());
	}

	public Boolean visit(OWLDataMinCardinalityRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLDataProperty());
	}

	public Boolean visit(OWLDataExactCardinalityRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLDataProperty());
	}

	public Boolean visit(OWLDataMaxCardinalityRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLDataProperty());
	}

	public Boolean visit(OWLSubClassAxiom axiom) {
		return axiom.getSubClass().accept(this)
				|| axiom.getSuperClass().accept(this);
	}

	public Boolean visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		return axiom.getProperty().accept(this)
				|| axiom.getSubject().accept(this)
				|| axiom.getObject().accept(this);
	}

	public Boolean visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
		return axiom.getProperty().accept(this);
	}

	public Boolean visit(OWLReflexiveObjectPropertyAxiom axiom) {
		return axiom.getProperty().accept(this);
	}

	public Boolean visit(OWLDisjointClassesAxiom axiom) {
		Set<OWLDescription> descriptions = axiom.getDescriptions();
		Iterator<OWLDescription> iterator = descriptions.iterator();
		boolean found = false;
		while (!found && iterator.hasNext()) {
			found = iterator.next().accept(this);
		}
		return found;
	}

	public Boolean visit(OWLDataPropertyDomainAxiom axiom) {
		return axiom.getProperty().accept(this)
				|| axiom.getDomain().accept(this);
	}

	public Boolean visit(OWLImportsDeclaration axiom) {
		return false;
	}

	public Boolean visit(OWLAxiomAnnotationAxiom axiom) {
		return false;
	}

	public Boolean visit(OWLObjectPropertyDomainAxiom axiom) {
		return axiom.getProperty().accept(this)
				|| axiom.getDomain().accept(this);
	}

	public Boolean visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		Set<OWLObjectPropertyExpression> descriptions = axiom.getProperties();
		Iterator<OWLObjectPropertyExpression> iterator = descriptions
				.iterator();
		boolean found = false;
		while (!found && iterator.hasNext()) {
			found = iterator.next().accept(this);
		}
		return found;
	}

	public Boolean visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		return axiom.getProperty().accept(this)
				|| axiom.getSubject().accept(this)
				|| axiom.getObject().accept(this);
	}

	public Boolean visit(OWLDifferentIndividualsAxiom axiom) {
		Set<OWLIndividual> descriptions = axiom.getIndividuals();
		Iterator<OWLIndividual> iterator = descriptions.iterator();
		boolean found = false;
		while (!found && iterator.hasNext()) {
			found = iterator.next().accept(this);
		}
		return found;
	}

	public Boolean visit(OWLDisjointDataPropertiesAxiom axiom) {
		Set<OWLDataPropertyExpression> descriptions = axiom.getProperties();
		Iterator<OWLDataPropertyExpression> iterator = descriptions.iterator();
		boolean found = false;
		while (!found && iterator.hasNext()) {
			found = iterator.next().accept(this);
		}
		return found;
	}

	public Boolean visit(OWLDisjointObjectPropertiesAxiom axiom) {
		Set<OWLObjectPropertyExpression> descriptions = axiom.getProperties();
		Iterator<OWLObjectPropertyExpression> iterator = descriptions
				.iterator();
		boolean found = false;
		while (!found && iterator.hasNext()) {
			found = iterator.next().accept(this);
		}
		return found;
	}

	public Boolean visit(OWLObjectPropertyRangeAxiom axiom) {
		return axiom.getProperty().accept(this)
				|| axiom.getRange().accept(this);
	}

	public Boolean visit(OWLObjectPropertyAssertionAxiom axiom) {
		return axiom.getProperty().accept(this)
				|| axiom.getSubject().accept(this)
				|| axiom.getObject().accept(this);
	}

	public Boolean visit(OWLFunctionalObjectPropertyAxiom axiom) {
		return axiom.getProperty().accept(this);
	}

	public Boolean visit(OWLObjectSubPropertyAxiom axiom) {
		return axiom.getSubProperty().accept(this)
				|| axiom.getSuperProperty().accept(this);
	}

	public Boolean visit(OWLDisjointUnionAxiom axiom) {
		Set<OWLDescription> descriptions = axiom.getDescriptions();
		Iterator<OWLDescription> iterator = descriptions.iterator();
		boolean found = false;
		while (!found && iterator.hasNext()) {
			found = iterator.next().accept(this);
		}
		return found;
	}

	public Boolean visit(OWLDeclarationAxiom axiom) {
		return false;
	}

	public Boolean visit(OWLEntityAnnotationAxiom axiom) {
		return false;
	}

	public Boolean visit(OWLOntologyAnnotationAxiom axiom) {
		return false;
	}

	public Boolean visit(OWLSymmetricObjectPropertyAxiom axiom) {
		return axiom.getProperty().accept(this);
	}

	public Boolean visit(OWLDataPropertyRangeAxiom axiom) {
		return axiom.getProperty().accept(this)
				|| axiom.getRange().accept(this);
	}

	public Boolean visit(OWLFunctionalDataPropertyAxiom axiom) {
		return axiom.getProperty().accept(this);
	}

	public Boolean visit(OWLEquivalentDataPropertiesAxiom axiom) {
		Set<OWLDataPropertyExpression> descriptions = axiom.getProperties();
		Iterator<OWLDataPropertyExpression> iterator = descriptions.iterator();
		boolean found = false;
		while (!found && iterator.hasNext()) {
			found = iterator.next().accept(this);
		}
		return found;
	}

	public Boolean visit(OWLClassAssertionAxiom axiom) {
		return axiom.getDescription().accept(this)
				|| axiom.getIndividual().accept(this);
	}

	public Boolean visit(OWLEquivalentClassesAxiom axiom) {
		Set<OWLDescription> descriptions = axiom.getDescriptions();
		Iterator<OWLDescription> iterator = descriptions.iterator();
		boolean found = false;
		while (!found && iterator.hasNext()) {
			found = iterator.next().accept(this);
		}
		return found;
	}

	public Boolean visit(OWLDataPropertyAssertionAxiom axiom) {
		return axiom.getProperty().accept(this)
				|| axiom.getSubject().accept(this)
				|| axiom.getObject().accept(this);
	}

	public Boolean visit(OWLTransitiveObjectPropertyAxiom axiom) {
		return axiom.getProperty().accept(this);
	}

	public Boolean visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		return axiom.getProperty().accept(this);
	}

	public Boolean visit(OWLDataSubPropertyAxiom axiom) {
		return axiom.getSubProperty().accept(this)
				|| axiom.getSuperProperty().accept(this);
	}

	public Boolean visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		return axiom.getProperty().accept(this);
	}

	public Boolean visit(OWLSameIndividualsAxiom axiom) {
		Iterator<OWLIndividual> iterator = axiom.getIndividuals().iterator();
		boolean found = false;
		while (!found && iterator.hasNext()) {
			found = iterator.next().accept(this);
		}
		return found;
	}

	public Boolean visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
		Iterator<OWLObjectPropertyExpression> iterator = axiom
				.getPropertyChain().iterator();
		boolean found = false;
		while (found && iterator.hasNext()) {
			found = iterator.next().accept(this);
		}
		return found || axiom.getSuperProperty().accept(this);
	}

	public Boolean visit(OWLInverseObjectPropertiesAxiom axiom) {
		Iterator<OWLObjectPropertyExpression> iterator = axiom.getProperties()
				.iterator();
		boolean found = false;
		while (found && iterator.hasNext()) {
			found = iterator.next().accept(this);
		}
		return found;
	}

	public Boolean visit(SWRLRule rule) {
		return false;
	}

	public Boolean visit(OWLDataType node) {
		return false;
	}

	public Boolean visit(OWLDataComplementOf node) {
		return false;
	}

	public Boolean visit(OWLDataOneOf node) {
		Iterator<OWLConstant> iterator = node.getValues().iterator();
		boolean found = false;
		while (!found && iterator.hasNext()) {
			found = iterator.next().accept(this);
		}
		return false;
	}

	public Boolean visit(OWLDataRangeRestriction node) {
		return false;
	}

	public Boolean visit(OWLTypedConstant node) {
		return this.constraintSystem.isVariable(node)
				&& this.constraintSystem.getVariable(node.getLiteral()).equals(
						this.variable);
	}

	public Boolean visit(OWLUntypedConstant node) {
		return this.constraintSystem.isVariable(node)
				&& this.constraintSystem.getVariable(node.getLiteral()).equals(
						this.variable);
	}

	public Boolean visit(OWLDataRangeFacetRestriction node) {
		return false;
	}

	public Boolean visit(OWLObjectProperty property) {
		return this.constraintSystem.isVariable(property)
				&& this.constraintSystem.getVariable(property.getURI()).equals(
						this.variable);
	}

	public Boolean visit(OWLObjectPropertyInverse property) {
		return property.getInverseProperty().accept(this);
	}

	public Boolean visit(OWLDataProperty property) {
		return this.constraintSystem.isVariable(property)
				&& this.constraintSystem.getVariable(property.getURI()).equals(
						this.variable);
	}

	public Boolean visit(OWLIndividual individual) {
		return this.constraintSystem.isVariable(individual)
				&& this.constraintSystem.getVariable(individual.getURI())
						.equals(this.variable);
	}

	public Boolean visit(OWLObjectAnnotation annotation) {
		return false;
	}

	public Boolean visit(OWLConstantAnnotation annotation) {
		return false;
	}

	public Boolean visit(SWRLClassAtom node) {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean visit(SWRLDataRangeAtom node) {
		return false;
	}

	public Boolean visit(SWRLObjectPropertyAtom node) {
		return false;
	}

	public Boolean visit(SWRLDataValuedPropertyAtom node) {
		return false;
	}

	public Boolean visit(SWRLBuiltInAtom node) {
		return false;
	}

	public Boolean visit(SWRLAtomDVariable node) {
		return false;
	}

	public Boolean visit(SWRLAtomIVariable node) {
		return false;
	}

	public Boolean visit(SWRLAtomIndividualObject node) {
		return false;
	}

	public Boolean visit(SWRLAtomConstantObject node) {
		return false;
	}

	public Boolean visit(SWRLSameAsAtom node) {
		return false;
	}

	public Boolean visit(SWRLDifferentFromAtom node) {
		return false;
	}

	public Boolean visit(OWLOntology ontology) {
		return false;
	}
}
