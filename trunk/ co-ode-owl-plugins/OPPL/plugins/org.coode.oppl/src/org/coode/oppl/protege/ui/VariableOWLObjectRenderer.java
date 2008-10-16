package org.coode.oppl.protege.ui;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLDescriptionComparator;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLObjectRenderer;
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
import org.semanticweb.owl.model.OWLObject;
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
import org.semanticweb.owl.vocab.OWLRestrictedDataRangeFacetVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br>
 * <br>
 * <p/> matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br>
 * <br>
 * <p/> A renderer that renders objects using the Manchester OWL Syntax. Axiom
 * level OWLObjects are rendered in Manchester "style"
 */
public class VariableOWLObjectRenderer implements OWLObjectVisitor,
		OWLObjectRenderer {
	private StringBuilder buffer;
	private BracketWriter bracketWriter;
	private Map<OWLRestrictedDataRangeFacetVocabulary, String> facetMap;
	private Map<URI, Boolean> simpleRenderDatatypes;
	private OWLObject focusedObject;
	private OWLEntityRenderer entityRenderer;
	private OWLModelManager owlModelManager;

	public VariableOWLObjectRenderer(OWLModelManager owlModelManager) {
		this.owlModelManager = owlModelManager;
		this.comparator = new OWLDescriptionComparator(this.owlModelManager);
		this.entityRenderer = owlModelManager.getOWLEntityRenderer();
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

	public void setup(OWLModelManager owlModelManager) {
	}

	public void initialise() {
	}

	public void dispose() {
	}

	public OWLObject getFocusedObject() {
		return this.focusedObject;
	}

	public void setFocusedObject(OWLObject focusedObject) {
		this.focusedObject = focusedObject;
		if (focusedObject instanceof OWLDescription) {
			this.comparator
					.setFocusedDescription((OWLDescription) focusedObject);
		}
	}

	protected String getAndKeyWord() {
		return "and";
	}

	protected String getOrKeyWord() {
		return "or";
	}

	protected String getNotKeyWord() {
		return "not";
	}

	protected String getSomeKeyWord() {
		return "some";
	}

	protected String getAllKeyWord() {
		return "only";
	}

	protected String getValueKeyWord() {
		return "value";
	}

	protected String getMinKeyWord() {
		return "min";
	}

	protected String getMaxKeyWord() {
		return "max";
	}

	protected String getExactlyKeyWord() {
		return "exactly";
	}

	public String render(OWLObject object, OWLEntityRenderer entityRenderer) {
		this.entityRenderer = entityRenderer;
		this.reset();
		try {
			object.accept(this);
			return this.buffer.toString();
		} catch (Exception e) {
			return "<Error! " + e.getMessage() + ">";
		}
	}

	protected String getRendering(OWLEntity entity) {
		return this.entityRenderer.render(entity);
	}

	int lastNewLineIndex = 0;
	int currentIndex = 0;

	protected void write(String s) {
		int index = s.indexOf('\n');
		if (index != -1) {
			this.lastNewLineIndex = this.currentIndex + index;
		}
		this.currentIndex = this.currentIndex + s.length();
		this.buffer.append(s);
	}

	protected int getIndent() {
		return this.currentIndex - this.lastNewLineIndex;
	}

	protected void insertIndent(int indent) {
		for (int i = 0; i < indent; i++) {
			this.write(" ");
		}
	}

	protected void writeAndKeyword() {
		this.write(this.getAndKeyWord());
		this.write(" ");
	}

	public void reset() {
		this.lastNewLineIndex = 0;
		this.currentIndex = 0;
		this.buffer = new StringBuilder();
	}

	public String getText() {
		return this.buffer.toString();
	}

	private OWLDescriptionComparator comparator;

	private List<OWLDescription> sort(Set<OWLDescription> descriptions) {
		List<OWLDescription> sortedDescs = new ArrayList<OWLDescription>(
				descriptions);
		Collections.sort(sortedDescs, this.comparator);
		return sortedDescs;
	}

	public void visit(OWLObjectIntersectionOf node) {
		int indent = this.getIndent();
		List<OWLDescription> ops = this.sort(node.getOperands());
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

	public void visit(OWLDataType node) {
		this.write(node.getURI().getFragment());
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

	public void visit(OWLDataComplementOf owlDataComplementOf) {
		this.write("not(");
		owlDataComplementOf.getDataRange().accept(this);
		this.write(")");
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

	public void visit(OWLObjectSelfRestriction desc) {
		desc.getProperty().accept(this);
		this.write(" ");
		this.write(this.getSomeKeyWord());
		this.write(" Self");
	}

	public void visit(OWLDataAllRestriction node) {
		node.getProperty().accept(this);
		this.write(" ");
		this.write(this.getAllKeyWord());
		this.write(" ");
		node.getFiller().accept(this);
	}

	public void visit(OWLDataProperty node) {
		this.write(this.getRendering(node));
	}

	public void visit(OWLDataSomeRestriction node) {
		node.getProperty().accept(this);
		this.write(" ");
		this.write(this.getSomeKeyWord());
		this.write(" ");
		node.getFiller().accept(this);
	}

	public void visit(OWLDataValueRestriction node) {
		node.getProperty().accept(this);
		this.write(" ");
		this.write(this.getValueKeyWord());
		this.write(" ");
		node.getValue().accept(this);
	}

	public void visit(OWLIndividual node) {
		if (node.isAnonymous()) {
			this.write("Anonymous : [");
			for (OWLOntology ont : this.owlModelManager.getActiveOntologies()) {
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

	public void visit(OWLObjectAllRestriction node) {
		node.getProperty().accept(this);
		this.write(" ");
		this.write(this.getAllKeyWord());
		this.write(" ");
		this.writeOpenBracket(node.getFiller());
		node.getFiller().accept(this);
		this.writeCloseBracket(node.getFiller());
	}

	public void visit(OWLObjectMinCardinalityRestriction desc) {
		this.writeCardinality(desc, this.getMinKeyWord());
	}

	public void visit(OWLObjectExactCardinalityRestriction desc) {
		this.writeCardinality(desc, this.getExactlyKeyWord());
	}

	public void visit(OWLObjectMaxCardinalityRestriction desc) {
		this.writeCardinality(desc, this.getMaxKeyWord());
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

	public void visit(OWLDataMinCardinalityRestriction desc) {
		this.writeCardinality(desc, this.getMinKeyWord());
	}

	public void visit(OWLDataExactCardinalityRestriction desc) {
		this.writeCardinality(desc, this.getExactlyKeyWord());
	}

	public void visit(OWLDataMaxCardinalityRestriction desc) {
		this.writeCardinality(desc, this.getMaxKeyWord());
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

	public void visit(OWLObjectProperty node) {
		this.write(this.getRendering(node));
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

	public void visit(OWLObjectValueRestriction node) {
		node.getProperty().accept(this);
		this.write(" ");
		this.write(this.getValueKeyWord());
		this.write(" ");
		node.getValue().accept(this);
	}

	public void visit(OWLObjectComplementOf node) {
		this.writeNotKeyword();
		this.write(" ");
		this.writeOpenBracket(node.getOperand());
		node.getOperand().accept(this);
		this.writeCloseBracket(node.getOperand());
	}

	protected void writeNotKeyword() {
		this.write(this.getNotKeyWord());
	}

	public void visit(OWLObjectUnionOf node) {
		int indent = this.getIndent();
		for (Iterator<OWLDescription> it = this.sort(node.getOperands())
				.iterator(); it.hasNext();) {
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

	private void writeOrKeyword() {
		this.write(this.getOrKeyWord());
		this.write(" ");
	}

	public void visit(OWLClass node) {
		this.write(this.getRendering(node));
	}

	public void visit(OWLObjectPropertyInverse property) {
		this.write("inv(");
		property.getInverse().accept(this);
		this.write(")");
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

	public void visit(OWLDisjointClassesAxiom node) {
		for (Iterator<OWLDescription> it = this.sort(node.getDescriptions())
				.iterator(); it.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(" disjointWith ");
			}
		}
	}

	public void visit(OWLEquivalentClassesAxiom node) {
		List<OWLDescription> orderedDescs = this.sort(node.getDescriptions());
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

	public void visit(OWLSubClassAxiom node) {
		node.getSubClass().accept(this);
		this.write(" subClassOf ");
		node.getSuperClass().accept(this);
	}

	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		this.write("Functional: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		this.write("InverseFunctional: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		this.write("Irreflexive: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLDataSubPropertyAxiom axiom) {
		axiom.getSubProperty().accept(this);
		this.write(" subPropertyOf ");
		axiom.getSuperProperty().accept(this);
	}

	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		this.write("Reflexive: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		this.write("Symmetric: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		this.write("Transitive: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		if (!OWLRendererPreferences.getInstance().isRenderDomainAxiomsAsGCIs()) {
			axiom.getDomain().accept(this);
			this.write(" domainOf ");
			axiom.getProperty().accept(this);
		} else {
			axiom.getProperty().accept(this);
			this.write(" some ");
			this.owlModelManager.getOWLDataFactory().getOWLThing().accept(this);
			this.write(" subClassOf ");
			axiom.getDomain().accept(this);
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

	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		axiom.getRange().accept(this);
		this.write(" rangeOf ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLClassAssertionAxiom axiom) {
		axiom.getIndividual().accept(this);
		this.write(" instanceOf ");
		axiom.getDescription().accept(this);
	}

	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		this.write("Functional: ");
		axiom.getProperty().accept(this);
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

	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getProperty().accept(this);
		this.write(" ");
		axiom.getObject().accept(this);
	}

	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getProperty().accept(this);
		this.write(" ");
		axiom.getObject().accept(this);
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

	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		axiom.getFirstProperty().accept(this);
		this.write(" inverseOf ");
		axiom.getSecondProperty().accept(this);
	}

	public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
		this.write("AntiSymmetric: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLDataPropertyDomainAxiom axiom) {
		axiom.getProperty().accept(this);
		this.write(" hasDomain ");
		axiom.getDomain().accept(this);
	}

	public void visit(OWLDataPropertyRangeAxiom axiom) {
		axiom.getProperty().accept(this);
		this.write(" hasRange ");
		axiom.getRange().accept(this);
	}

	public void visit(OWLObjectSubPropertyAxiom axiom) {
		axiom.getSubProperty().accept(this);
		this.write(" subPropertyOf ");
		axiom.getSuperProperty().accept(this);
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

	public void visit(OWLImportsDeclaration axiom) {
		this.writeOntologyURI(axiom.getImportedOntologyURI());
		if (this.owlModelManager.getOWLOntologyManager().getImportedOntology(
				axiom) == null) {
			this.write("      (Not Loaded)");
		}
	}

	public void visit(OWLAxiomAnnotationAxiom axiom) {
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getAnnotation().accept(this);
	}

	private void writeOpenBracket(OWLDescription description) {
		description.accept(this.bracketWriter);
		if (this.bracketWriter.writeBrackets()) {
			this.write("(");
		}
	}

	private void writeOpenBracket(OWLDataRange dataRange) {
		dataRange.accept(this.bracketWriter);
		if (this.bracketWriter.writeBrackets()) {
			this.write("(");
		}
	}

	private void writeCloseBracket(OWLDescription description) {
		description.accept(this.bracketWriter);
		if (this.bracketWriter.writeBrackets()) {
			this.write(")");
		}
	}

	private void writeCloseBracket(OWLDataRange dataRange) {
		dataRange.accept(this.bracketWriter);
		if (this.bracketWriter.writeBrackets()) {
			this.write(")");
		}
	}

	public void visit(OWLOntology ontology) {
		this.writeOntologyURI(ontology.getURI());
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

	public void visit(OWLConstantAnnotation annotation) {
		this.write(this.owlModelManager.getURIRendering(annotation
				.getAnnotationURI()));
		this.write(" ");
		this.write(annotation.getAnnotationValue().toString());
	}

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

	public void visit(OWLEntityAnnotationAxiom owlEntityAnnotationAxiom) {
		owlEntityAnnotationAxiom.getSubject().accept(this);
		this.write(" ");
		owlEntityAnnotationAxiom.getAnnotation().accept(this);
	}

	public void visit(OWLOntologyAnnotationAxiom axiom) {
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getAnnotation().accept(this);
	}

	public void visit(OWLObjectAnnotation owlObjectAnnotation) {
		this.write(this.owlModelManager.getURIRendering(owlObjectAnnotation
				.getAnnotationURI()));
		this.write(" ");
		owlObjectAnnotation.getAnnotationValue().accept(this);
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

	public void visit(SWRLObjectPropertyAtom swrlObjectPropertyAtom) {
		swrlObjectPropertyAtom.getPredicate().accept(this);
		this.write("(");
		swrlObjectPropertyAtom.getFirstArgument().accept(this);
		this.write(", ");
		swrlObjectPropertyAtom.getSecondArgument().accept(this);
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

	public void visit(SWRLAtomDVariable swrlAtomDVariable) {
		this.write("?");
		this.write(swrlAtomDVariable.getURI().getFragment());
	}

	public void visit(SWRLAtomIVariable swrlAtomIVariable) {
		this.write("?");
		this.write(swrlAtomIVariable.getURI().getFragment());
	}

	public void visit(SWRLAtomIndividualObject swrlAtomIndividualObject) {
		swrlAtomIndividualObject.getIndividual().accept(this);
	}

	public void visit(SWRLAtomConstantObject swrlAtomConstantObject) {
		swrlAtomConstantObject.getConstant().accept(this);
	}

	public void visit(SWRLDifferentFromAtom swrlDifferentFromAtom) {
		swrlDifferentFromAtom.getPredicate().accept(this);
		this.write("(");
		swrlDifferentFromAtom.getFirstArgument().accept(this);
		this.write(", ");
		swrlDifferentFromAtom.getSecondArgument().accept(this);
		this.write(")");
	}

	public void visit(SWRLSameAsAtom swrlSameAsAtom) {
		swrlSameAsAtom.getPredicate().accept(this);
		this.write("(");
		swrlSameAsAtom.getFirstArgument().accept(this);
		this.write(", ");
		swrlSameAsAtom.getSecondArgument().accept(this);
		this.write(")");
	}

	private void writeOntologyURI(URI uri) {
		String shortName = this.owlModelManager.getURIRendering(uri);
		if (shortName != null) {
			this.write(shortName);
			this.write(" (");
			this.write(uri.toString());
			this.write(")");
		} else {
			this.write(uri.toString());
		}
	}

	private class BracketWriter extends OWLDescriptionVisitorAdapter implements
			OWLDataVisitor {
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
}
