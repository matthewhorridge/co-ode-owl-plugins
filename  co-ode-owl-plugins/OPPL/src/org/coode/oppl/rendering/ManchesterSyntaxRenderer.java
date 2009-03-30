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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLConstantAnnotation;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataCardinalityRestriction;
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
import org.semanticweb.owl.model.OWLDataVisitor;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointUnionAxiom;
import org.semanticweb.owl.model.OWLEntity;
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
import org.semanticweb.owl.model.OWLObjectCardinalityRestriction;
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
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLRestriction;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.model.OWLUntypedConstant;
import org.semanticweb.owl.model.SWRLAtom;
import org.semanticweb.owl.model.SWRLAtomConstantObject;
import org.semanticweb.owl.model.SWRLAtomDObject;
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
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;
import org.semanticweb.owl.util.SimpleURIShortFormProvider;
import org.semanticweb.owl.util.URIShortFormProvider;
import org.semanticweb.owl.vocab.OWLRestrictedDataRangeFacetVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

/**
 * @author Luigi Iannone
 * 
 */
public class ManchesterSyntaxRenderer implements OWLObjectVisitor {
	private static class BracketWriter extends OWLDescriptionVisitorAdapter
			implements OWLDataVisitor {
		boolean nested = false;

		public boolean writeBrackets() {
			return this.nested;
		}

		@Override
		public void visit(OWLObjectIntersectionOf owlAnd) {
			this.nested = true;
		}

		@Override
		public void visit(OWLDataAllRestriction owlDataAllRestriction) {
			this.nested = true;
		}

		@Override
		public void visit(OWLDataSomeRestriction owlDataSomeRestriction) {
			this.nested = true;
		}

		@Override
		public void visit(OWLDataValueRestriction owlDataValueRestriction) {
			this.nested = true;
		}

		@Override
		public void visit(OWLObjectAllRestriction owlObjectAllRestriction) {
			this.nested = true;
		}

		@Override
		public void visit(OWLObjectSomeRestriction owlObjectSomeRestriction) {
			this.nested = true;
		}

		@Override
		public void visit(OWLObjectValueRestriction owlObjectValueRestriction) {
			this.nested = true;
		}

		@Override
		public void visit(OWLObjectComplementOf owlNot) {
			this.nested = true;
		}

		@Override
		public void visit(OWLObjectUnionOf owlOr) {
			this.nested = true;
		}

		@Override
		public void visit(OWLClass owlClass) {
			this.nested = false;
		}

		@Override
		public void visit(OWLObjectOneOf owlObjectOneOf) {
			this.nested = false;
		}

		@Override
		public void visit(OWLObjectMinCardinalityRestriction desc) {
			this.nested = true;
		}

		@Override
		public void visit(OWLObjectExactCardinalityRestriction desc) {
			this.nested = true;
		}

		@Override
		public void visit(OWLObjectMaxCardinalityRestriction desc) {
			this.nested = true;
		}

		@Override
		public void visit(OWLObjectSelfRestriction desc) {
			this.nested = true;
		}

		@Override
		public void visit(OWLDataMinCardinalityRestriction desc) {
			this.nested = true;
		}

		@Override
		public void visit(OWLDataExactCardinalityRestriction desc) {
			this.nested = true;
		}

		@Override
		public void visit(OWLDataMaxCardinalityRestriction desc) {
			this.nested = true;
		}

		public void visit(OWLDataType node) {
			this.nested = false;
		}

		public void visit(OWLDataComplementOf node) {
			this.nested = false;
		}

		public void visit(OWLDataOneOf node) {
			this.nested = false;
		}

		public void visit(OWLDataRangeRestriction node) {
			this.nested = true;
		}

		public void visit(OWLTypedConstant node) {
			this.nested = false;
		}

		public void visit(OWLUntypedConstant node) {
			this.nested = false;
		}

		public void visit(OWLDataRangeFacetRestriction node) {
			this.nested = false;
		}
	}

	private Map<OWLRestrictedDataRangeFacetVocabulary, String> facetMap;
	private final OWLOntologyManager ontologyManager;
	private Map<URI, Boolean> simpleRenderDatatypes;
	private int currentIndex = 0;
	private StringBuilder buffer;
	int lastNewLineIndex = 0;
	private BracketWriter bracketWriter;
	private final OWLEntityRenderer entityRenderer;

	/**
	 * Builds a renderer in Manchester OWL Syntax <b>non frame based</b>
	 * 
	 * @param ontologyManager
	 *            Cannot be {@code null}.
	 * @param entityRenderer
	 *            the renderer according to which the named entities will be
	 *            rendered. Cannot be {@code null}.
	 * @throws NullPointerException
	 *             if either of the inputs is {@code null}.
	 */
	public ManchesterSyntaxRenderer(OWLOntologyManager ontologyManager,
			OWLEntityRenderer entityRenderer) {
		if (ontologyManager == null) {
			throw new NullPointerException(
					"The ontology manager cannot be null");
		}
		if (entityRenderer == null) {
			throw new NullPointerException("The entity renderer cannot be null");
		}
		this.ontologyManager = ontologyManager;
		this.entityRenderer = entityRenderer;
		this.buffer = new StringBuilder();
		this.bracketWriter = new BracketWriter();
		this.facetMap = new HashMap<OWLRestrictedDataRangeFacetVocabulary, String>();
		this.facetMap.put(OWLRestrictedDataRangeFacetVocabulary.MIN_EXCLUSIVE,
				">");
		this.facetMap.put(OWLRestrictedDataRangeFacetVocabulary.MAX_EXCLUSIVE,
				"<");
		this.facetMap.put(OWLRestrictedDataRangeFacetVocabulary.MIN_INCLUSIVE,
				">=");
		this.facetMap.put(OWLRestrictedDataRangeFacetVocabulary.MAX_INCLUSIVE,
				"<=");
		this.simpleRenderDatatypes = new HashMap<URI, Boolean>();
		this.simpleRenderDatatypes.put(XSDVocabulary.INT.getURI(), false);
		this.simpleRenderDatatypes.put(XSDVocabulary.FLOAT.getURI(), false);
		this.simpleRenderDatatypes.put(XSDVocabulary.DOUBLE.getURI(), false);
		this.simpleRenderDatatypes.put(XSDVocabulary.STRING.getURI(), true);
		this.simpleRenderDatatypes.put(XSDVocabulary.BOOLEAN.getURI(), false);
	}

	public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
		this.write("AntiSymmetric: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLAxiomAnnotationAxiom axiom) {
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getAnnotation().accept(this);
	}

	public void visit(OWLClass node) {
		this.write(this.getRendering(node));
	}

	public void visit(OWLClassAssertionAxiom axiom) {
		axiom.getIndividual().accept(this);
		this.write(" types ");
		axiom.getDescription().accept(this);
	}

	public void visit(OWLConstantAnnotation annotation) {
		URIShortFormProvider uriShortFormProvider = new SimpleURIShortFormProvider();
		this.write(uriShortFormProvider.getShortForm(annotation
				.getAnnotationURI()));
		this.write(" ");
		this.write(annotation.getAnnotationValue().toString());
	}

	public void visit(OWLDataAllRestriction node) {
		node.getProperty().accept(this);
		this.write(" ");
		this.write(this.getAllKeyWord());
		this.write(" ");
		node.getFiller().accept(this);
	}

	public void visit(OWLDataComplementOf owlDataComplementOf) {
		this.write("not(");
		owlDataComplementOf.getDataRange().accept(this);
		this.write(")");
	}

	public void visit(OWLDataExactCardinalityRestriction desc) {
		this.writeCardinality(desc, this.getExactlyKeyWord());
	}

	public void visit(OWLDataMaxCardinalityRestriction desc) {
		this.writeCardinality(desc, this.getMaxKeyWord());
	}

	public void visit(OWLDataMinCardinalityRestriction desc) {
		this.writeCardinality(desc, this.getMinKeyWord());
	}

	public void visit(OWLDataOneOf node) {
		this.write("{");
		for (Iterator<OWLConstant> it = node.getValues().iterator(); it
				.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(", ");
			}
		}
		this.write("}");
	}

	public void visit(OWLDataProperty node) {
		this.write(this.getRendering(node));
	}

	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getProperty().accept(this);
		this.write(" ");
		axiom.getObject().accept(this);
	}

	public void visit(OWLDataPropertyDomainAxiom axiom) {
		axiom.getProperty().accept(this);
		this.write(" domain ");
		axiom.getDomain().accept(this);
	}

	public void visit(OWLDataPropertyRangeAxiom axiom) {
		axiom.getProperty().accept(this);
		this.write(" range ");
		axiom.getRange().accept(this);
	}

	public void visit(OWLDataRangeFacetRestriction node) {
		String rendering = this.facetMap.get(node.getFacet());
		if (rendering == null) {
			rendering = node.getFacet().getShortName();
		}
		this.write(rendering);
		this.write(" ");
		node.getFacetValue().accept(this);
	}

	public void visit(OWLDataRangeRestriction node) {
		// writeOpenBracket(node);
		node.getDataRange().accept(this);
		this.write("[");
		for (Iterator<OWLDataRangeFacetRestriction> it = node
				.getFacetRestrictions().iterator(); it.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(", ");
			}
		}
		this.write("]");
		// writeCloseBracket(node);
	}

	public void visit(OWLDataSomeRestriction node) {
		node.getProperty().accept(this);
		this.write(" ");
		this.write(this.getSomeKeyWord());
		this.write(" ");
		node.getFiller().accept(this);
	}

	public void visit(OWLDataSubPropertyAxiom axiom) {
		axiom.getSubProperty().accept(this);
		this.write(" subPropertyOf ");
		axiom.getSuperProperty().accept(this);
	}

	public void visit(OWLDataType node) {
		this.write(node.getURI().getFragment());
	}

	public void visit(OWLDataValueRestriction node) {
		node.getProperty().accept(this);
		this.write(" ");
		this.write(this.getValueKeyWord());
		this.write(" ");
		node.getValue().accept(this);
	}

	public void visit(OWLDeclarationAxiom axiom) {
		OWLEntity entity = axiom.getEntity();
		if (entity.isOWLClass()) {
			this.write("Class(");
		} else if (entity.isOWLObjectProperty()) {
			this.write("Object property(");
		} else if (entity.isOWLDataProperty()) {
			this.write("Data property(");
		} else if (entity.isOWLIndividual()) {
			this.write("Individual(");
		} else {
			this.write("(");
		}
		entity.accept(this);
		this.write(")");
	}

	public void visit(OWLDifferentIndividualsAxiom axiom) {
		this.write("DifferentIndividuals: [");
		for (Iterator<OWLIndividual> it = axiom.getIndividuals().iterator(); it
				.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(", ");
			}
		}
		this.write("]");
	}

	public void visit(OWLDisjointClassesAxiom node) {
		for (Iterator<OWLDescription> it = node.getDescriptions().iterator(); it
				.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(" disjointWith ");
			}
		}
	}

	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		for (Iterator<OWLDataPropertyExpression> it = axiom.getProperties()
				.iterator(); it.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(" disjointWith ");
			}
		}
	}

	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		for (Iterator<OWLObjectPropertyExpression> it = axiom.getProperties()
				.iterator(); it.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(" disjointWith ");
			}
		}
	}

	public void visit(OWLDisjointUnionAxiom axiom) {
		axiom.getOWLClass().accept(this);
		this.write(" disjointUnionOf ");
		this.write("[");
		int indent = this.getIndent();
		for (Iterator<OWLDescription> it = axiom.getDescriptions().iterator(); it
				.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write("\n");
				this.insertIndent(indent);
			}
		}
		this.write("]");
	}

	public void visit(OWLEntityAnnotationAxiom owlEntityAnnotationAxiom) {
		owlEntityAnnotationAxiom.getSubject().accept(this);
		this.write(" ");
		owlEntityAnnotationAxiom.getAnnotation().accept(this);
	}

	public void visit(OWLEquivalentClassesAxiom node) {
		List<OWLDescription> orderedDescs = new ArrayList<OWLDescription>(node
				.getDescriptions());
		for (Iterator<OWLDescription> it = orderedDescs.iterator(); it
				.hasNext();) {
			OWLDescription desc = it.next();
			if (orderedDescs.get(0).isOWLNothing()) {
				it.remove();
				orderedDescs.add(desc);
				break;
			}
		}
		for (Iterator<OWLDescription> it = orderedDescs.iterator(); it
				.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(" equivalentTo ");
			}
		}
	}

	public void visit(OWLEquivalentDataPropertiesAxiom node) {
		for (Iterator<OWLDataPropertyExpression> it = node.getProperties()
				.iterator(); it.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(" equivalentTo ");
			}
		}
	}

	public void visit(OWLEquivalentObjectPropertiesAxiom node) {
		for (Iterator<OWLObjectPropertyExpression> it = node.getProperties()
				.iterator(); it.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(" equivalentTo ");
			}
		}
	}

	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		this.write("Functional: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		this.write("Functional: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLImportsDeclaration axiom) {
		this.writeOntologyURI(axiom.getImportedOntologyURI());
		if (this.ontologyManager.getImportedOntology(axiom) == null) {
			this.write("      (Not Loaded)");
		}
	}

	public void visit(OWLIndividual node) {
		if (node.isAnonymous()) {
			this.write("Anonymous : [");
			for (OWLOntology ont : this.ontologyManager.getOntologies()) {
				for (OWLDescription desc : node.getTypes(ont)) {
					this.write(" ");
					desc.accept(this);
				}
			}
			this.write(" ]");
		} else {
			this.write(this.getRendering(node));
		}
	}

	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		this.write("InverseFunctional: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		axiom.getFirstProperty().accept(this);
		this.write(" inverseOf ");
		axiom.getSecondProperty().accept(this);
	}

	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		this.write("Irreflexive: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		this.write("not(");
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getProperty().accept(this);
		this.write(" ");
		axiom.getObject().accept(this);
		this.write(")");
	}

	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		this.write("not(");
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getProperty().accept(this);
		this.write(" ");
		axiom.getObject().accept(this);
		this.write(")");
	}

	public void visit(OWLObjectAllRestriction node) {
		node.getProperty().accept(this);
		this.write(" ");
		this.write(this.getAllKeyWord());
		this.write(" ");
		this.writeOpenBracket(node.getFiller());
		node.getFiller().accept(this);
		this.writeCloseBracket(node.getFiller());
	}

	public void visit(OWLObjectAnnotation owlObjectAnnotation) {
		URIShortFormProvider uriShortFormProvider = new SimpleURIShortFormProvider();
		this.write(uriShortFormProvider.getShortForm(owlObjectAnnotation
				.getAnnotationURI()));
		this.write(" ");
		owlObjectAnnotation.getAnnotationValue().accept(this);
	}

	public void visit(OWLObjectComplementOf node) {
		this.writeNotKeyword();
		this.write(" ");
		this.writeOpenBracket(node.getOperand());
		node.getOperand().accept(this);
		this.writeCloseBracket(node.getOperand());
	}

	public void visit(OWLObjectExactCardinalityRestriction desc) {
		this.writeCardinality(desc, this.getExactlyKeyWord());
	}

	public void visit(OWLObjectIntersectionOf node) {
		int indent = this.getIndent();
		List<OWLDescription> ops = new ArrayList<OWLDescription>(node
				.getOperands());
		for (int i = 0; i < ops.size(); i++) {
			OWLDescription curOp = ops.get(i);
			curOp.accept(this);
			if (i < ops.size() - 1) {
				this.write("\n");
				this.insertIndent(indent);
				if (curOp instanceof OWLClass
						&& ops.get(i + 1) instanceof OWLRestriction
						&& OWLRendererPreferences.getInstance()
								.isUseThatKeyword()) {
					this.write("that ");
				} else {
					this.writeAndKeyword();
				}
			}
		}
	}

	public void visit(OWLObjectMaxCardinalityRestriction desc) {
		this.writeCardinality(desc, this.getMaxKeyWord());
	}

	public void visit(OWLObjectMinCardinalityRestriction desc) {
		this.writeCardinality(desc, this.getMinKeyWord());
	}

	public void visit(OWLObjectOneOf node) {
		this.write("{");
		for (Iterator<OWLIndividual> it = node.getIndividuals().iterator(); it
				.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(", ");
			}
		}
		this.write("}");
	}

	public void visit(OWLObjectProperty node) {
		this.write(this.getRendering(node));
	}

	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getProperty().accept(this);
		this.write(" ");
		axiom.getObject().accept(this);
	}

	public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
		for (Iterator<OWLObjectPropertyExpression> it = axiom
				.getPropertyChain().iterator(); it.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(" o ");
			}
		}
		this.write(" \u279E ");
		axiom.getSuperProperty().accept(this);
	}

	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		if (!OWLRendererPreferences.getInstance().isRenderDomainAxiomsAsGCIs()) {
			axiom.getProperty().accept(this);
			this.write(" domain ");
			axiom.getDomain().accept(this);
		} else {
			axiom.getProperty().accept(this);
			this.write(" some ");
			this.ontologyManager.getOWLDataFactory().getOWLThing().accept(this);
			this.write(" subClassOf ");
			axiom.getDomain().accept(this);
		}
	}

	public void visit(OWLObjectPropertyInverse property) {
		this.write("inv(");
		property.getInverse().accept(this);
		this.write(")");
	}

	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		axiom.getProperty().accept(this);
		this.write(" range ");
		axiom.getRange().accept(this);
	}

	public void visit(OWLObjectSelfRestriction desc) {
		desc.getProperty().accept(this);
		this.write(" ");
		this.write(this.getSomeKeyWord());
		this.write(" Self");
	}

	public void visit(OWLObjectSomeRestriction node) {
		node.getProperty().accept(this);
		this.write(" ");
		this.write(this.getSomeKeyWord());
		this.write(" ");
		this.writeOpenBracket(node.getFiller());
		node.getFiller().accept(this);
		this.writeCloseBracket(node.getFiller());
	}

	public void visit(OWLObjectSubPropertyAxiom axiom) {
		axiom.getSubProperty().accept(this);
		this.write(" subPropertyOf ");
		axiom.getSuperProperty().accept(this);
	}

	public void visit(OWLObjectUnionOf node) {
		int indent = this.getIndent();
		for (Iterator<OWLDescription> it = node.getOperands().iterator(); it
				.hasNext();) {
			OWLDescription curOp = it.next();
			this.writeOpenBracket(curOp);
			curOp.accept(this);
			this.writeCloseBracket(curOp);
			if (it.hasNext()) {
				this.write("\n");
				this.insertIndent(indent);
				this.writeOrKeyword();
			}
		}
	}

	public void visit(OWLObjectValueRestriction node) {
		node.getProperty().accept(this);
		this.write(" ");
		this.write(this.getValueKeyWord());
		this.write(" ");
		node.getValue().accept(this);
	}

	public void visit(OWLOntology ontology) {
		this.writeOntologyURI(ontology.getURI());
	}

	public void visit(OWLOntologyAnnotationAxiom axiom) {
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getAnnotation().accept(this);
	}

	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		this.write("Reflexive: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLSameIndividualsAxiom axiom) {
		this.write("SameIndividuals: [");
		for (Iterator<OWLIndividual> it = axiom.getIndividuals().iterator(); it
				.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(", ");
			}
		}
		this.write("]");
	}

	public void visit(OWLSubClassAxiom node) {
		node.getSubClass().accept(this);
		this.write(" subClassOf ");
		node.getSuperClass().accept(this);
	}

	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		this.write("Symmetric: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		this.write("Transitive: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLTypedConstant node) {
		if (this.simpleRenderDatatypes.containsKey(node.getDataType().getURI())) {
			boolean renderQuotes = this.simpleRenderDatatypes.get(node
					.getDataType().getURI());
			if (renderQuotes) {
				this.write("\"");
			}
			this.write(node.getLiteral());
			if (renderQuotes) {
				this.write("\"");
			}
		} else {
			this.write("\"");
			this.write(node.getLiteral());
			this.write("\"^^");
			node.getDataType().accept(this);
		}
	}

	public void visit(OWLUntypedConstant node) {
		this.write("\"");
		this.write(node.getLiteral());
		this.write("\"");
		if (node.hasLang()) {
			this.write("@");
			this.write(node.getLang());
		}
	}

	public void visit(SWRLAtomConstantObject swrlAtomConstantObject) {
		swrlAtomConstantObject.getConstant().accept(this);
	}

	public void visit(SWRLAtomDVariable swrlAtomDVariable) {
		this.write("?");
		this.write(swrlAtomDVariable.getURI().getFragment());
	}

	public void visit(SWRLAtomIndividualObject swrlAtomIndividualObject) {
		swrlAtomIndividualObject.getIndividual().accept(this);
	}

	public void visit(SWRLAtomIVariable swrlAtomIVariable) {
		this.write("?");
		this.write(swrlAtomIVariable.getURI().getFragment());
	}

	public void visit(SWRLBuiltInAtom swrlBuiltInAtom) {
		this.write(swrlBuiltInAtom.getPredicate().getShortName());
		this.write("(");
		Iterator<SWRLAtomDObject> it = swrlBuiltInAtom.getArguments()
				.iterator();
		while (it.hasNext()) {
			SWRLAtomDObject argument = it.next();
			argument.accept(this);
			if (it.hasNext()) {
				this.write(", ");
			}
		}
		this.write(")");
	}

	public void visit(SWRLClassAtom swrlClassAtom) {
		OWLDescription desc = swrlClassAtom.getPredicate();
		if (desc.isAnonymous()) {
			this.write("(");
		}
		desc.accept(this);
		if (desc.isAnonymous()) {
			this.write(")");
		}
		this.write("(");
		swrlClassAtom.getArgument().accept(this);
		this.write(")");
	}

	public void visit(SWRLDataRangeAtom swrlDataRangeAtom) {
		swrlDataRangeAtom.getPredicate().accept(this);
		this.write("(");
		swrlDataRangeAtom.getArgument().accept(this);
		this.write(")");
	}

	public void visit(SWRLDataValuedPropertyAtom swrlDataValuedPropertyAtom) {
		swrlDataValuedPropertyAtom.getPredicate().accept(this);
		this.write("(");
		swrlDataValuedPropertyAtom.getFirstArgument().accept(this);
		this.write(", ");
		swrlDataValuedPropertyAtom.getSecondArgument().accept(this);
		this.write(")");
	}

	public void visit(SWRLDifferentFromAtom swrlDifferentFromAtom) {
		swrlDifferentFromAtom.getPredicate().accept(this);
		this.write("(");
		swrlDifferentFromAtom.getFirstArgument().accept(this);
		this.write(", ");
		swrlDifferentFromAtom.getSecondArgument().accept(this);
		this.write(")");
	}

	public void visit(SWRLObjectPropertyAtom swrlObjectPropertyAtom) {
		swrlObjectPropertyAtom.getPredicate().accept(this);
		this.write("(");
		swrlObjectPropertyAtom.getFirstArgument().accept(this);
		this.write(", ");
		swrlObjectPropertyAtom.getSecondArgument().accept(this);
		this.write(")");
	}

	@SuppressWarnings("unchecked")
	public void visit(SWRLRule swrlRule) {
		for (Iterator<SWRLAtom> it = swrlRule.getBody().iterator(); it
				.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(" , ");
			}
		}
		this.write(" -> ");
		for (Iterator<SWRLAtom> it = swrlRule.getHead().iterator(); it
				.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(" \u2227 ");
			}
		}
	}

	public void visit(SWRLSameAsAtom swrlSameAsAtom) {
		swrlSameAsAtom.getPredicate().accept(this);
		this.write("(");
		swrlSameAsAtom.getFirstArgument().accept(this);
		this.write(", ");
		swrlSameAsAtom.getSecondArgument().accept(this);
		this.write(")");
	}

	protected void write(String s) {
		int index = s.indexOf('\n');
		if (index != -1) {
			this.lastNewLineIndex = this.currentIndex + index;
		}
		this.currentIndex = this.currentIndex + s.length();
		this.buffer.append(s);
	}

	protected void writeAndKeyword() {
		this.write(this.getAndKeyWord());
		this.write(" ");
	}

	private void writeCardinality(OWLDataCardinalityRestriction desc,
			String keyword) {
		desc.getProperty().accept(this);
		this.write(" ");
		this.write(keyword);
		this.write(" ");
		this.write(Integer.toString(desc.getCardinality()));
		this.write(" ");
		this.writeOpenBracket(desc.getFiller());
		desc.getFiller().accept(this);
		this.writeCloseBracket(desc.getFiller());
	}

	private void writeCardinality(OWLObjectCardinalityRestriction desc,
			String keyword) {
		desc.getProperty().accept(this);
		this.write(" ");
		this.write(keyword);
		this.write(" ");
		this.write(Integer.toString(desc.getCardinality()));
		this.write(" ");
		this.writeOpenBracket(desc.getFiller());
		desc.getFiller().accept(this);
		this.writeCloseBracket(desc.getFiller());
	}

	private void writeCloseBracket(OWLDataRange dataRange) {
		dataRange.accept(this.bracketWriter);
		if (this.bracketWriter.writeBrackets()) {
			this.write(")");
		}
	}

	private void writeCloseBracket(OWLDescription description) {
		description.accept(this.bracketWriter);
		if (this.bracketWriter.writeBrackets()) {
			this.write(")");
		}
	}

	protected void writeNotKeyword() {
		this.write(this.getNotKeyWord());
	}

	private void writeOntologyURI(URI uri) {
		URIShortFormProvider uriShortFormProvider = new SimpleURIShortFormProvider();
		String shortName = uriShortFormProvider.getShortForm(uri);
		if (shortName != null) {
			this.write(shortName);
			this.write(" (");
			this.write(uri.toString());
			this.write(")");
		} else {
			this.write(uri.toString());
		}
	}

	private void writeOpenBracket(OWLDataRange dataRange) {
		dataRange.accept(this.bracketWriter);
		if (this.bracketWriter.writeBrackets()) {
			this.write("(");
		}
	}

	private void writeOpenBracket(OWLDescription description) {
		description.accept(this.bracketWriter);
		if (this.bracketWriter.writeBrackets()) {
			this.write("(");
		}
	}

	private void writeOrKeyword() {
		this.write(this.getOrKeyWord());
		this.write(" ");
	}

	protected String getAllKeyWord() {
		return "only";
	}

	protected String getAndKeyWord() {
		return "and";
	}

	protected String getExactlyKeyWord() {
		return "exactly";
	}

	protected int getIndent() {
		return this.currentIndex - this.lastNewLineIndex;
	}

	protected String getMaxKeyWord() {
		return "max";
	}

	protected String getMinKeyWord() {
		return "min";
	}

	protected String getNotKeyWord() {
		return "not";
	}

	protected String getOrKeyWord() {
		return "or";
	}

	protected String getRendering(OWLEntity entity) {
		return this.entityRenderer.render(entity);
	}

	protected String getSomeKeyWord() {
		return "some";
	}

	protected String getValueKeyWord() {
		return "value";
	}

	protected void insertIndent(int indent) {
		for (int i = 0; i < indent; i++) {
			this.write(" ");
		}
	}
}
