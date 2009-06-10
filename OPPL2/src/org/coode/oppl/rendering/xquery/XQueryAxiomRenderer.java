package org.coode.oppl.rendering.xquery;

import static org.semanticweb.owl.vocab.OWLXMLVocabulary.CLASS;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.owlapi.owlxml.renderer.OWLXMLObjectRenderer;
import org.coode.owlapi.owlxml.renderer.OWLXMLWriter;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;

public class XQueryAxiomRenderer extends OWLXMLObjectRenderer {
	private final ConstraintSystem constraintSystem;

	public XQueryAxiomRenderer(OWLOntology ontology, OWLXMLWriter writer,
			ConstraintSystem constraintSystem) {
		super(ontology, writer);
		this.constraintSystem = constraintSystem;
	}

	@Override
	public void visit(OWLClass desc) {
		if (this.constraintSystem.isVariable(desc)) {
			this.writer.writeStartElement(CLASS.getURI());
			this.writer.writeNameAttribute(desc.getURI());
			this.writer.writeEndElement();
		} else {
			super.visit(desc);
		}
	}
}
