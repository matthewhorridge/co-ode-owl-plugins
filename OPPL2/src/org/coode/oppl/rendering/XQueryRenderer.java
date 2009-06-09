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
package org.coode.oppl.rendering;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.oppl.OPPLQuery;
import org.coode.oppl.OPPLScriptVisitorEx;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.Variable;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLAxiomChange;
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
import org.semanticweb.owl.model.OWLDataRange;
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
 * @author Luigi Iannone
 * 
 */
public class XQueryRenderer implements OPPLScriptVisitorEx<String> {
	private final class OWLAxiomVocabulary implements
			OWLObjectVisitorEx<String> {
		public String visit(OWLSubClassAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":SubClassOf";
		}

		public String visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION
					+ ":NegativeObjectPropertyAssertion";
		}

		public String visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":AsymmetricObjectProperty";
		}

		public String visit(OWLReflexiveObjectPropertyAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ReflexiveObjectProperty";
		}

		public String visit(OWLDisjointClassesAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DisjointClasses";
		}

		public String visit(OWLDataPropertyDomainAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DisjointClasses";
		}

		public String visit(OWLImportsDeclaration axiom) {
			return "";
		}

		public String visit(OWLAxiomAnnotationAxiom axiom) {
			return "";
		}

		public String visit(OWLObjectPropertyDomainAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectPropertyDomain";
		}

		public String visit(OWLEquivalentObjectPropertiesAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION
					+ ":EquivalentObjectProperties";
		}

		public String visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION
					+ ":NegativeDataPropertyAssertion";
		}

		public String visit(OWLDifferentIndividualsAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DifferentIndividuals";
		}

		public String visit(OWLDisjointDataPropertiesAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DisjointDataProperties";
		}

		public String visit(OWLDisjointObjectPropertiesAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DisjointObjectProperties";
		}

		public String visit(OWLObjectPropertyRangeAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectPropertyRange";
		}

		public String visit(OWLObjectPropertyAssertionAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectPropertyAssertion";
		}

		public String visit(OWLFunctionalObjectPropertyAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":FunctionalObjectProperty";
		}

		public String visit(OWLObjectSubPropertyAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":SubObjectPropertyOf";
		}

		public String visit(OWLDisjointUnionAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DisjointUnion";
		}

		public String visit(OWLDeclarationAxiom axiom) {
			return "";
		}

		public String visit(OWLEntityAnnotationAxiom axiom) {
			return "";
		}

		public String visit(OWLOntologyAnnotationAxiom axiom) {
			return "";
		}

		public String visit(OWLSymmetricObjectPropertyAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":SymmetricObjectProperty";
		}

		public String visit(OWLDataPropertyRangeAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DataPropertyRange";
		}

		public String visit(OWLFunctionalDataPropertyAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":FunctionalDataProperty";
		}

		public String visit(OWLEquivalentDataPropertiesAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":EquivalentDataProperties";
		}

		public String visit(OWLClassAssertionAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ClassAssertion";
		}

		public String visit(OWLEquivalentClassesAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":EquivalentClasses";
		}

		public String visit(OWLDataPropertyAssertionAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DataPropertyAssertion";
		}

		public String visit(OWLTransitiveObjectPropertyAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":TransitiveObjectProperty";
		}

		public String visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION
					+ ":EquivalentObjectProperties";
		}

		public String visit(OWLDataSubPropertyAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":IrreflexiveObjectProperty";
		}

		public String visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION
					+ ":InverseFunctionalObjectProperty";
		}

		public String visit(OWLSameIndividualsAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":SameIndividual";
		}

		public String visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":PropertyChain";
		}

		public String visit(OWLInverseObjectPropertiesAxiom axiom) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":InverseObjectProperties";
		}

		public String visit(SWRLRule rule) {
			return "";
		}

		public String visit(OWLClass desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":Class";
		}

		public String visit(OWLObjectIntersectionOf desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectIntersectionOf";
		}

		public String visit(OWLObjectUnionOf desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectUnionOf";
		}

		public String visit(OWLObjectComplementOf desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectComplementOf";
		}

		public String visit(OWLObjectSomeRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectSomeValuesFrom";
		}

		public String visit(OWLObjectAllRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectSomeValuesFrom";
		}

		public String visit(OWLObjectValueRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectAllValuesFrom";
		}

		public String visit(OWLObjectMinCardinalityRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectMinCardinality";
		}

		public String visit(OWLObjectExactCardinalityRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectExactCardinality";
		}

		public String visit(OWLObjectMaxCardinalityRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectMaxCardinality";
		}

		public String visit(OWLObjectSelfRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectSelfRestriction";
		}

		public String visit(OWLObjectOneOf desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectOneOf";
		}

		public String visit(OWLDataSomeRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DataSomeValuesFrom";
		}

		public String visit(OWLDataAllRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectSomeValuesFrom";
		}

		public String visit(OWLDataValueRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DataAllValuesFrom";
		}

		public String visit(OWLDataMinCardinalityRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DataMinCardinality";
		}

		public String visit(OWLDataExactCardinalityRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DataExactCardinality";
		}

		public String visit(OWLDataMaxCardinalityRestriction desc) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DataMaxCardinality";
		}

		public String visit(OWLDataType node) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":Datatype";
		}

		public String visit(OWLDataComplementOf node) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DataComplementOf";
		}

		public String visit(OWLDataOneOf node) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DataOneOf";
		}

		public String visit(OWLDataRangeRestriction node) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DataPropertyRange";
		}

		public String visit(OWLTypedConstant node) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":Literal";
		}

		public String visit(OWLUntypedConstant node) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":Literal";
		}

		public String visit(OWLDataRangeFacetRestriction node) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DatatypeRestriction";
		}

		public String visit(OWLObjectProperty property) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":ObjectProperty";
		}

		public String visit(OWLObjectPropertyInverse property) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":InverseObjectProperty";
		}

		public String visit(OWLDataProperty property) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":DataProperty";
		}

		public String visit(OWLIndividual individual) {
			return OWLXML_NAMESPACE_ABBREVIATION + ":NamedIndividual";
		}

		public String visit(OWLObjectAnnotation annotation) {
			return "";
		}

		public String visit(OWLConstantAnnotation annotation) {
			return "";
		}

		public String visit(SWRLClassAtom node) {
			return "";
		}

		public String visit(SWRLDataRangeAtom node) {
			return "";
		}

		public String visit(SWRLObjectPropertyAtom node) {
			return "";
		}

		public String visit(SWRLDataValuedPropertyAtom node) {
			return "";
		}

		public String visit(SWRLBuiltInAtom node) {
			return "";
		}

		public String visit(SWRLAtomDVariable node) {
			return "";
		}

		public String visit(SWRLAtomIVariable node) {
			return "";
		}

		public String visit(SWRLAtomIndividualObject node) {
			return "";
		}

		public String visit(SWRLAtomConstantObject node) {
			return "";
		}

		public String visit(SWRLSameAsAtom node) {
			return "";
		}

		public String visit(SWRLDifferentFromAtom node) {
			return "";
		}

		public String visit(OWLOntology ontology) {
			return OWLXML_NAMESPACE_ABBREVIATION + "Ontology";
		}
	}

	/**
	 * @author Luigi Iannone
	 * 
	 */
	private final class OWLAxiomTranslator implements
			OWLObjectVisitorEx<String> {
		private final String axiomName;
		private final ConstraintSystem constraintSystem;

		/**
		 * @return the constraintSystem
		 */
		private ConstraintSystem getConstraintSystem() {
			return this.constraintSystem;
		}

		private final OWLAxiomVocabulary vocabulary = new OWLAxiomVocabulary();
		private String path = "";
		private final Map<Variable, Set<String>> variablePaths = new HashMap<Variable, Set<String>>();
		private int index = 1;

		/**
		 * @param axiomName
		 */
		OWLAxiomTranslator(String axiomName, ConstraintSystem constraintSystem) {
			this.axiomName = axiomName;
			this.constraintSystem = constraintSystem;
		}

		public String getAxiomReference() {
			return "$" + this.axiomName;
		}

		private String buildAxiomQuery(OWLAxiom axiom) {
			String toReturn = "/" + OWLXML_NAMESPACE_ABBREVIATION
					+ ":Ontology/" + axiom.accept(this.vocabulary);
			return toReturn;
		}

		public Set<Variable> getVariables() {
			return new HashSet<Variable>(this.variablePaths.keySet());
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLSubClassAxiom)
		 */
		public String visit(OWLSubClassAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLDescription subClass = axiom.getSubClass();
			subClass.accept(this);
			OWLDescription superClass = axiom.getSuperClass();
			superClass.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom)
		 */
		public String visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression property = axiom.getProperty();
			property.accept(this);
			OWLIndividual subject = axiom.getSubject();
			subject.accept(this);
			OWLIndividual object = axiom.getObject();
			object.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom)
		 */
		public String visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression property = axiom.getProperty();
			property.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom)
		 */
		public String visit(OWLReflexiveObjectPropertyAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression property = axiom.getProperty();
			property.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDisjointClassesAxiom)
		 */
		public String visit(OWLDisjointClassesAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			Set<OWLDescription> descriptions = axiom.getDescriptions();
			int i = 0;
			for (OWLDescription description : descriptions) {
				i++;
				description.accept(this);
			}
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDataPropertyDomainAxiom)
		 */
		public String visit(OWLDataPropertyDomainAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLDataPropertyExpression property = axiom.getProperty();
			property.accept(this);
			OWLDescription domain = axiom.getDomain();
			domain.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLImportsDeclaration)
		 */
		public String visit(OWLImportsDeclaration axiom) {
			return "";
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLAxiomAnnotationAxiom)
		 */
		public String visit(OWLAxiomAnnotationAxiom axiom) {
			return "";
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom)
		 */
		public String visit(OWLObjectPropertyDomainAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression property = axiom.getProperty();
			property.accept(this);
			OWLDescription domain = axiom.getDomain();
			domain.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom)
		 */
		public String visit(OWLEquivalentObjectPropertiesAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			Set<OWLObjectPropertyExpression> properties = axiom.getProperties();
			int i = 0;
			for (OWLObjectPropertyExpression objectPropertyExpression : properties) {
				i++;
				objectPropertyExpression.accept(this);
			}
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom)
		 */
		public String visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLDataPropertyExpression property = axiom.getProperty();
			property.accept(this);
			OWLIndividual subject = axiom.getSubject();
			subject.accept(this);
			OWLConstant object = axiom.getObject();
			object.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDifferentIndividualsAxiom)
		 */
		public String visit(OWLDifferentIndividualsAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			Set<OWLIndividual> individuals = axiom.getIndividuals();
			int i = 0;
			for (OWLIndividual individual : individuals) {
				i++;
				individual.accept(this);
			}
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom)
		 */
		public String visit(OWLDisjointDataPropertiesAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			Set<OWLDataPropertyExpression> properties = axiom.getProperties();
			int i = 0;
			for (OWLDataPropertyExpression dataPropertyExpression : properties) {
				i++;
				dataPropertyExpression.accept(this);
			}
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom)
		 */
		public String visit(OWLDisjointObjectPropertiesAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			Set<OWLObjectPropertyExpression> properties = axiom.getProperties();
			int i = 0;
			for (OWLObjectPropertyExpression objectPropertyExpression : properties) {
				i++;
				objectPropertyExpression.accept(this);
			}
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom)
		 */
		public String visit(OWLObjectPropertyRangeAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression property = axiom.getProperty();
			property.accept(this);
			OWLDescription range = axiom.getRange();
			range.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom)
		 */
		public String visit(OWLObjectPropertyAssertionAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression property = axiom.getProperty();
			property.accept(this);
			OWLIndividual subject = axiom.getSubject();
			subject.accept(this);
			OWLIndividual object = axiom.getObject();
			object.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom)
		 */
		public String visit(OWLFunctionalObjectPropertyAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression property = axiom.getProperty();
			property.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLObjectSubPropertyAxiom)
		 */
		public String visit(OWLObjectSubPropertyAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression subProperty = axiom.getSubProperty();
			subProperty.accept(this);
			OWLObjectPropertyExpression superProperty = axiom
					.getSuperProperty();
			superProperty.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDisjointUnionAxiom)
		 */
		public String visit(OWLDisjointUnionAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			Set<OWLDescription> descriptions = axiom.getDescriptions();
			int i = 0;
			for (OWLDescription description : descriptions) {
				i++;
				description.accept(this);
			}
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDeclarationAxiom)
		 */
		public String visit(OWLDeclarationAxiom axiom) {
			return "";
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLEntityAnnotationAxiom)
		 */
		public String visit(OWLEntityAnnotationAxiom axiom) {
			return "";
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLOntologyAnnotationAxiom)
		 */
		public String visit(OWLOntologyAnnotationAxiom axiom) {
			return "";
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom)
		 */
		public String visit(OWLSymmetricObjectPropertyAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression property = axiom.getProperty();
			property.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDataPropertyRangeAxiom)
		 */
		public String visit(OWLDataPropertyRangeAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLDataPropertyExpression property = axiom.getProperty();
			property.accept(this);
			OWLDataRange range = axiom.getRange();
			range.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom)
		 */
		public String visit(OWLFunctionalDataPropertyAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLDataPropertyExpression property = axiom.getProperty();
			property.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom)
		 */
		public String visit(OWLEquivalentDataPropertiesAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			Set<OWLDataPropertyExpression> properties = axiom.getProperties();
			int i = 0;
			for (OWLDataPropertyExpression dataPropertyExpression : properties) {
				i++;
				dataPropertyExpression.accept(this);
			}
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLClassAssertionAxiom)
		 */
		public String visit(OWLClassAssertionAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLDescription description = axiom.getDescription();
			description.accept(this);
			OWLIndividual individual = axiom.getIndividual();
			individual.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLEquivalentClassesAxiom)
		 */
		public String visit(OWLEquivalentClassesAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			Set<OWLDescription> descriptions = axiom.getDescriptions();
			int i = 0;
			for (OWLDescription description : descriptions) {
				i++;
				description.accept(this);
			}
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom)
		 */
		public String visit(OWLDataPropertyAssertionAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLDataPropertyExpression property = axiom.getProperty();
			property.accept(this);
			OWLIndividual subject = axiom.getSubject();
			subject.accept(this);
			OWLConstant object = axiom.getObject();
			object.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom)
		 */
		public String visit(OWLTransitiveObjectPropertyAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression property = axiom.getProperty();
			property.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom)
		 */
		public String visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			axiom.getProperty().accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLDataSubPropertyAxiom)
		 */
		public String visit(OWLDataSubPropertyAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLDataPropertyExpression subProperty = axiom.getSubProperty();
			subProperty.accept(this);
			OWLDataPropertyExpression superProperty = axiom.getSuperProperty();
			superProperty.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom)
		 */
		public String visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression property = axiom.getProperty();
			property.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLSameIndividualsAxiom)
		 */
		public String visit(OWLSameIndividualsAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			Set<OWLIndividual> individuals = axiom.getIndividuals();
			int i = 0;
			for (OWLIndividual individual : individuals) {
				i++;
				individual.accept(this);
			}
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom)
		 */
		public String visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression superProperty = axiom
					.getSuperProperty();
			superProperty.accept(this);
			List<OWLObjectPropertyExpression> propertyChain = axiom
					.getPropertyChain();
			int i = 0;
			for (OWLObjectPropertyExpression objectPropertyExpression : propertyChain) {
				i++;
				objectPropertyExpression.accept(this);
			}
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom)
		 */
		public String visit(OWLInverseObjectPropertiesAxiom axiom) {
			String toReturn = this.buildAxiomQuery(axiom);
			OWLObjectPropertyExpression firstProperty = axiom
					.getFirstProperty();
			firstProperty.accept(this);
			OWLObjectPropertyExpression secondProperty = axiom
					.getSecondProperty();
			secondProperty.accept(this);
			return toReturn;
		}

		/**
		 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.SWRLRule)
		 */
		public String visit(SWRLRule rule) {
			return "";
		}

		public String visit(OWLClass desc) {
			this.path.concat("/" + desc.accept(this.vocabulary) + "["
					+ this.index + "]");
			if (this.getConstraintSystem().isVariable(desc)) {
				Variable variable = this.getConstraintSystem().getVariable(
						desc.getURI());
				Set<String> paths = this.variablePaths.get(variable);
				if (paths == null) {
					paths = new HashSet<String>();
				}
				paths.add(this.path);
				this.variablePaths.put(variable, paths);
			}
			return this.path;
		}

		public String visit(OWLObjectIntersectionOf desc) {
			return null;
		}

		public String visit(OWLObjectUnionOf desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLObjectComplementOf desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLObjectSomeRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLObjectAllRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLObjectValueRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLObjectMinCardinalityRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLObjectExactCardinalityRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLObjectMaxCardinalityRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLObjectSelfRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLObjectOneOf desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLDataSomeRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLDataAllRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLDataValueRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLDataMinCardinalityRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLDataExactCardinalityRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLDataMaxCardinalityRestriction desc) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLDataType node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLDataComplementOf node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLDataOneOf node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLDataRangeRestriction node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLTypedConstant node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLUntypedConstant node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLDataRangeFacetRestriction node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLObjectProperty property) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLObjectPropertyInverse property) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLDataProperty property) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLIndividual individual) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLObjectAnnotation annotation) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLConstantAnnotation annotation) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(SWRLClassAtom node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(SWRLDataRangeAtom node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(SWRLObjectPropertyAtom node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(SWRLDataValuedPropertyAtom node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(SWRLBuiltInAtom node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(SWRLAtomDVariable node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(SWRLAtomIVariable node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(SWRLAtomIndividualObject node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(SWRLAtomConstantObject node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(SWRLSameAsAtom node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(SWRLDifferentFromAtom node) {
			// TODO Auto-generated method stub
			return null;
		}

		public String visit(OWLOntology ontology) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final String OWLXML_NAMESPACE_URI_STRING = "http://www.w3.org/2006/12/owl2-xml#";
	private static final String OWLXML_NAMESPACE_ABBREVIATION = "owl2xml";
	private final String fileName;

	/**
	 * @param fileName
	 */
	private XQueryRenderer(String fileName) {
		this.fileName = fileName;
	}

	public String visit(OPPLQuery q, String p) {
		StringWriter writer = new StringWriter();
		writer.append("declare namespace " + OWLXML_NAMESPACE_ABBREVIATION
				+ " = \"" + OWLXML_NAMESPACE_URI_STRING + "\";\n");
		List<OWLAxiom> assertedAxioms = q.getAssertedAxioms();
		for (int i = 0; i < assertedAxioms.size(); i++) {
			OWLAxiom axiom = assertedAxioms.get(i);
			OWLAxiomTranslator axiomTranslator = new OWLAxiomTranslator(
					"assertedAxiom" + i, q.getConstraintSystem());
			writer.append("for " + axiomTranslator.getAxiomReference());
			writer.append(this.getXQueryContext());
			writer.append(axiom.accept(axiomTranslator));
		}
		List<OWLAxiom> axioms = q.getAxioms();
		for (int i = 0; i < axioms.size(); i++) {
			OWLAxiom axiom = assertedAxioms.get(i);
			OWLAxiomTranslator axiomTranslator = new OWLAxiomTranslator("axiom"
					+ i, q.getConstraintSystem());
			writer.append("for " + axiomTranslator.getAxiomReference());
			writer.append(this.getXQueryContext());
			writer.append(axiom.accept(axiomTranslator));
		}
		return writer.toString();
	}

	public String visit(Variable v, String p) {
		return "";
	}

	public String visitActions(List<OWLAxiomChange> changes, String p) {
		return "";
	}

	protected String getXQueryContext() {
		return " in doc(" + this.getFileName() + ") ";
	}

	private String getFileName() {
		return this.fileName;
	}
}
