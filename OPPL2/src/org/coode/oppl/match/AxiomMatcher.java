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
package org.coode.oppl.match;

import java.util.Collection;
import java.util.Iterator;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLAxiomVisitorEx;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyCharacteristicAxiom;
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
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyCharacteristicAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLPropertyExpression;
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
public class AxiomMatcher implements OWLAxiomVisitorEx<Boolean> {
	private final BindingNode bindings;
	private final ConstraintSystem constraintSystem;

	AxiomMatcher(BindingNode bindingNode, ConstraintSystem constraintSystem) {
		if (bindingNode == null) {
			throw new NullPointerException("The binding node cannot be null");
		}
		if (constraintSystem == null) {
			throw new NullPointerException("The constraint system");
		}
		this.bindings = bindingNode;
		this.constraintSystem = constraintSystem;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLSubClassAxiom)
	 */
	public Boolean visit(OWLSubClassAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom)
	 */
	public Boolean visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom)
	 */
	public Boolean visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom)
	 */
	public Boolean visit(OWLReflexiveObjectPropertyAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDisjointClassesAxiom)
	 */
	public Boolean visit(OWLDisjointClassesAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDataPropertyDomainAxiom)
	 */
	public Boolean visit(OWLDataPropertyDomainAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLImportsDeclaration)
	 */
	public Boolean visit(OWLImportsDeclaration axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLAxiomAnnotationAxiom)
	 */
	public Boolean visit(OWLAxiomAnnotationAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom)
	 */
	public Boolean visit(OWLObjectPropertyDomainAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom)
	 */
	public Boolean visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom)
	 */
	public Boolean visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDifferentIndividualsAxiom)
	 */
	public Boolean visit(OWLDifferentIndividualsAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom)
	 */
	public Boolean visit(OWLDisjointDataPropertiesAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom)
	 */
	public Boolean visit(OWLDisjointObjectPropertiesAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom)
	 */
	public Boolean visit(OWLObjectPropertyRangeAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom)
	 */
	public Boolean visit(OWLObjectPropertyAssertionAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom)
	 */
	public Boolean visit(OWLFunctionalObjectPropertyAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLObjectSubPropertyAxiom)
	 */
	public Boolean visit(OWLObjectSubPropertyAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDisjointUnionAxiom)
	 */
	public Boolean visit(OWLDisjointUnionAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDeclarationAxiom)
	 */
	public Boolean visit(OWLDeclarationAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLEntityAnnotationAxiom)
	 */
	public Boolean visit(OWLEntityAnnotationAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLOntologyAnnotationAxiom)
	 */
	public Boolean visit(OWLOntologyAnnotationAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom)
	 */
	public Boolean visit(OWLSymmetricObjectPropertyAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDataPropertyRangeAxiom)
	 */
	public Boolean visit(OWLDataPropertyRangeAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom)
	 */
	public Boolean visit(OWLFunctionalDataPropertyAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom)
	 */
	public Boolean visit(OWLEquivalentDataPropertiesAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLClassAssertionAxiom)
	 */
	public Boolean visit(OWLClassAssertionAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLEquivalentClassesAxiom)
	 */
	public Boolean visit(OWLEquivalentClassesAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom)
	 */
	public Boolean visit(OWLDataPropertyAssertionAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom)
	 */
	public Boolean visit(OWLTransitiveObjectPropertyAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom)
	 */
	public Boolean visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDataSubPropertyAxiom)
	 */
	public Boolean visit(OWLDataSubPropertyAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom)
	 */
	public Boolean visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLSameIndividualsAxiom)
	 */
	public Boolean visit(OWLSameIndividualsAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom)
	 */
	public Boolean visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom)
	 */
	public Boolean visit(OWLInverseObjectPropertiesAxiom axiom) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.SWRLRule)
	 */
	public Boolean visit(SWRLRule rule) {
		return false;
	}

	public static AxiomMatcher getNoMatchAxiomMatcher(BindingNode bindings,
			ConstraintSystem constraintSystem) {
		return new AxiomMatcher(bindings, constraintSystem);
	}

	/**
	 * @return the bindings
	 */
	public BindingNode getBindings() {
		return this.bindings;
	}

	/**
	 * @return the constraintSystem
	 */
	public ConstraintSystem getConstraintSystem() {
		return this.constraintSystem;
	}

	protected <O extends OWLDescription> boolean matchDescriptionCollections(
			Collection<O> aCollection, Collection<O> anotherCollection) {
		boolean matches = aCollection.size() == anotherCollection.size();
		if (matches) {
			Iterator<? extends OWLDescription> collectionIterator = aCollection
					.iterator();
			Iterator<? extends OWLDescription> anotherCollectionIterator = anotherCollection
					.iterator();
			while (matches && collectionIterator.hasNext()) {
				OWLDescription aDescription = collectionIterator.next();
				OWLDescription anotherDescription = anotherCollectionIterator
						.next();
				OWLDescriptionMatcher matcher = OWLDescriptionMatcher
						.getMatcher(aDescription, this.getBindings(), this
								.getConstraintSystem());
				matches = anotherDescription.accept(matcher);
			}
		}
		return matches;
	}

	protected <O extends OWLIndividual> boolean matchIndividualCollections(
			Collection<O> aCollection, Collection<O> anotherCollection) {
		boolean matches = aCollection.size() == anotherCollection.size();
		if (matches) {
			Iterator<O> collectionIterator = aCollection.iterator();
			Iterator<O> anotherCollectionIterator = anotherCollection
					.iterator();
			while (matches && collectionIterator.hasNext()) {
				O anIndividual = collectionIterator.next();
				O anotherIndividual = anotherCollectionIterator.next();
				OWLIndividualMatcher matcher = OWLIndividualMatcher.getMatcher(
						anIndividual, this.getBindings(), this
								.getConstraintSystem());
				matches = anotherIndividual.accept(matcher);
			}
		}
		return matches;
	}

	protected <O extends OWLPropertyExpression<?, ?>> boolean matchPropertyCollections(
			Collection<O> aCollection, Collection<O> anotherCollection) {
		boolean matches = aCollection.size() == anotherCollection.size();
		if (matches) {
			Iterator<O> collectionIterator = aCollection.iterator();
			Iterator<O> anotherCollectionIterator = anotherCollection
					.iterator();
			while (matches && collectionIterator.hasNext()) {
				O aProperty = collectionIterator.next();
				O anotherProperty = anotherCollectionIterator.next();
				OWLPropertyExpressionMatcher matcher = OWLPropertyExpressionMatcher
						.getMatcher(aProperty, this.getBindings(), this
								.getConstraintSystem());
				matches = anotherProperty.accept(matcher);
			}
		}
		return matches;
	}

	protected <O extends OWLObjectPropertyCharacteristicAxiom> boolean matchUnaryPropertyAxioms(
			O aPropertyAxiom, O anotherPropertyAxiom) {
		OWLPropertyExpressionMatcher matcher = OWLPropertyExpressionMatcher
				.getMatcher(aPropertyAxiom.getProperty(), this.getBindings(),
						this.getConstraintSystem());
		return anotherPropertyAxiom.getProperty().accept(matcher);
	}

	protected <O extends OWLDataPropertyCharacteristicAxiom> boolean matchUnaryPropertyAxioms(
			O aPropertyAxiom, O anotherPropertyAxiom) {
		OWLPropertyExpressionMatcher matcher = OWLPropertyExpressionMatcher
				.getMatcher(aPropertyAxiom.getProperty(), this.getBindings(),
						this.getConstraintSystem());
		return anotherPropertyAxiom.getProperty().accept(matcher);
	}
}
