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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLAxiomVisitorEx;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
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
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataRangeFacetRestriction;
import org.semanticweb.owl.model.OWLDataRangeRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
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

final class Path {
	private final class PathNode {
		private final List<PathNode> children = new ArrayList<PathNode>();
		private final String name;
		private final PathNode parent;

		private PathNode(String name, PathNode parent) {
			if (name == null) {
				throw new NullPointerException("The name cannot be null");
			}
			this.name = name;
			this.parent = parent;
		}

		/**
		 * @return the children
		 */
		private List<PathNode> getChildren() {
			return new ArrayList<PathNode>(this.children);
		}

		private void addChild(PathNode node) {
			this.children.add(node);
		}

		private void removeChild(PathNode node) {
			this.children.remove(node);
		}

		private void clearChildren(PathNode node) {
			this.children.clear();
		}

		/**
		 * @return the name
		 */
		private String getName() {
			return this.name;
		}
	}

	private final PathNode root;

	/**
	 * @param root
	 */
	Path(String rootName) {
		this.root = new PathNode(rootName, null);
	}
}

/**
 * @author Luigi Iannone
 * 
 */
public class VariableXPathBuilder implements OWLAxiomVisitorEx<List<String>> {
	private static final String OWLXML_NAMESPACE_URI_STRING = "http://www.w3.org/2006/12/owl2-xml#";
	private static final String OWLXML_NAMESPACE_ABBREVIATION = "owl2xml";
	private Path path = null;
	private final OWLAxiomVocabulary vocabulary = new OWLAxiomVocabulary();

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

	private void initialisePath(OWLAxiom axiom) {
		String name = "/" + OWLXML_NAMESPACE_ABBREVIATION + ":Ontology/"
				+ axiom.accept(this.vocabulary);
		this.path = new Path(name);
	}

	public List<String> visit(OWLSubClassAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLReflexiveObjectPropertyAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLDisjointClassesAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLDataPropertyDomainAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLImportsDeclaration axiom) {
		return Collections.emptyList();
	}

	public List<String> visit(OWLAxiomAnnotationAxiom axiom) {
		return Collections.emptyList();
	}

	public List<String> visit(OWLObjectPropertyDomainAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLDifferentIndividualsAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLDisjointDataPropertiesAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLDisjointObjectPropertiesAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLObjectPropertyRangeAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLObjectPropertyAssertionAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLFunctionalObjectPropertyAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLObjectSubPropertyAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLDisjointUnionAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLDeclarationAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLEntityAnnotationAxiom axiom) {
		return Collections.emptyList();
	}

	public List<String> visit(OWLOntologyAnnotationAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLSymmetricObjectPropertyAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLDataPropertyRangeAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLFunctionalDataPropertyAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLEquivalentDataPropertiesAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLClassAssertionAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLEquivalentClassesAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLDataPropertyAssertionAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLTransitiveObjectPropertyAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLDataSubPropertyAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLSameIndividualsAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(OWLInverseObjectPropertiesAxiom axiom) {
		List<String> toReturn = new ArrayList<String>();
		this.initialisePath(axiom);
		return toReturn;
	}

	public List<String> visit(SWRLRule rule) {
		return Collections.emptyList();
	}
}
