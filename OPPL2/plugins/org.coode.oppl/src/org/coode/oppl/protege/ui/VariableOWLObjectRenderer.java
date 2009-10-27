package org.coode.oppl.protege.ui;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.oppl.rendering.AbstractRenderer;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLDescriptionComparator;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLObjectRenderer;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLConstantAnnotation;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectAnnotation;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLRestriction;
import org.semanticweb.owl.model.OWLUntypedConstant;
import org.semanticweb.owl.vocab.OWLRestrictedDataRangeFacetVocabulary;

/**
 * Author: Luigi Iannone<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 15, 2008<br>
 * <br>
 * <br>
 * <p/>
 * A renderer that renders objects possibly containing variables using the
 * Manchester OWL Syntax. Axiom level OWLObjects are rendered in Manchester
 * "style"
 */
public class VariableOWLObjectRenderer extends AbstractRenderer implements
		OWLObjectVisitor, OWLObjectRenderer {
	private StringBuilder buffer;
	private Map<OWLRestrictedDataRangeFacetVocabulary, String> facetMap;
	private Map<URI, Boolean> simpleRenderDatatypes;
	private OWLObject focusedObject;
	private OWLModelManager owlModelManager;
	private int lastNewLineIndex = 0;
	private int currentIndex = 0;
	private OWLDescriptionComparator comparator;
	private OWLEntityRenderer entityRenderer;

	public VariableOWLObjectRenderer(OWLModelManager owlModelManager) {
		this.owlModelManager = owlModelManager;
		this.comparator = new OWLDescriptionComparator(this.owlModelManager);
		this.entityRenderer = owlModelManager.getOWLEntityRenderer();
		init();
	}

	public void dispose() {
	}

	public OWLObject getFocusedObject() {
		return this.focusedObject;
	}

	public String getText() {
		return this.buffer.toString();
	}

	public void initialise() {
	}

	public String render(OWLObject object, OWLEntityRenderer entityRenderer1) {
		this.entityRenderer = entityRenderer1;
		reset();
		try {
			object.accept(this);
			return this.buffer.toString();
		} catch (Exception e) {
			return "<Error! " + e.getMessage() + ">";
		}
	}

	private void reset() {
		this.lastNewLineIndex = 0;
		this.currentIndex = 0;
		this.buffer = new StringBuilder();
	}

	public void setFocusedObject(OWLObject focusedObject) {
		this.focusedObject = focusedObject;
		if (focusedObject instanceof OWLDescription) {
			this.comparator
					.setFocusedDescription((OWLDescription) focusedObject);
		}
	}

	public void setup(OWLModelManager owlModelManager) {
	}

	private List<OWLDescription> sort(Set<OWLDescription> descriptions) {
		List<OWLDescription> sortedDescs = new ArrayList<OWLDescription>(
				descriptions);
		Collections.sort(sortedDescs, this.comparator);
		return sortedDescs;
	}

	public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
		write("AntiSymmetric: ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLAxiomAnnotationAxiom axiom) {
		axiom.getSubject().accept(this);
		write(" ");
		axiom.getAnnotation().accept(this);
	}

	public void visit(OWLClass node) {
		write(getRendering(node));
	}

	public void visit(OWLClassAssertionAxiom axiom) {
		axiom.getIndividual().accept(this);
		write(" instanceOf ");
		axiom.getDescription().accept(this);
	}

	public void visit(OWLConstantAnnotation annotation) {
		write(this.owlModelManager.getURIRendering(annotation
				.getAnnotationURI()));
		write(" ");
		write(annotation.getAnnotationValue().toString());
	}

	public void visit(OWLDataPropertyDomainAxiom axiom) {
		axiom.getProperty().accept(this);
		write(" hasDomain ");
		axiom.getDomain().accept(this);
	}

	public void visit(OWLDataPropertyRangeAxiom axiom) {
		axiom.getProperty().accept(this);
		write(" hasRange ");
		axiom.getRange().accept(this);
	}

	public void visit(OWLEquivalentClassesAxiom node) {
		List<OWLDescription> orderedDescs = sort(node.getDescriptions());
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
				write(" equivalentTo ");
			}
		}
	}

	public void visit(OWLImportsDeclaration axiom) {
		writeOntologyURI(axiom.getImportedOntologyURI());
		if (this.owlModelManager.getOWLOntologyManager().getImportedOntology(
				axiom) == null) {
			write("      (Not Loaded)");
		}
	}

	public void visit(OWLIndividual node) {
		if (node.isAnonymous()) {
			write("Anonymous : [");
			for (OWLOntology ont : this.owlModelManager.getActiveOntologies()) {
				for (OWLDescription desc : node.getTypes(ont)) {
					write(" ");
					desc.accept(this);
				}
			}
			write(" ]");
		} else {
			write(getRendering(node));
		}
	}

	public void visit(OWLObjectAnnotation owlObjectAnnotation) {
		write(this.owlModelManager.getURIRendering(owlObjectAnnotation
				.getAnnotationURI()));
		write(" ");
		owlObjectAnnotation.getAnnotationValue().accept(this);
	}

	public void visit(OWLObjectIntersectionOf node) {
		int indent = getIndent();
		List<OWLDescription> ops = sort(node.getOperands());
		for (int i = 0; i < ops.size(); i++) {
			OWLDescription curOp = ops.get(i);
			curOp.accept(this);
			if (i < ops.size() - 1) {
				write("\n");
				insertIndent(indent);
				if (curOp instanceof OWLClass
						&& ops.get(i + 1) instanceof OWLRestriction<?>
						&& OWLRendererPreferences.getInstance()
								.isUseThatKeyword()) {
					write("that ");
				} else {
					writeAndKeyword();
				}
			}
		}
	}

	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		if (!OWLRendererPreferences.getInstance().isRenderDomainAxiomsAsGCIs()) {
			axiom.getDomain().accept(this);
			write(" domainOf ");
			axiom.getProperty().accept(this);
		} else {
			axiom.getProperty().accept(this);
			write(" some ");
			this.owlModelManager.getOWLDataFactory().getOWLThing().accept(this);
			write(" subClassOf ");
			axiom.getDomain().accept(this);
		}
	}

	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		axiom.getRange().accept(this);
		write(" rangeOf ");
		axiom.getProperty().accept(this);
	}

	public void visit(OWLObjectUnionOf node) {
		int indent = getIndent();
		for (Iterator<OWLDescription> it = sort(node.getOperands()).iterator(); it
				.hasNext();) {
			OWLDescription curOp = it.next();
			writeOpenBracket(curOp);
			curOp.accept(this);
			writeCloseBracket(curOp);
			if (it.hasNext()) {
				write("\n");
				insertIndent(indent);
				writeOrKeyword();
			}
		}
	}

	public void visit(OWLUntypedConstant node) {
		write("\"");
		write(node.getLiteral());
		write("\"");
		if (node.hasLang()) {
			write("@");
			write(node.getLang());
		}
	}

	@Override
	protected void writeOntologyURI(URI uri) {
		String shortName = this.owlModelManager.getURIRendering(uri);
		if (shortName != null) {
			write(shortName);
			write(" (");
			write(uri.toString());
			write(")");
		} else {
			write(uri.toString());
		}
	}
}
