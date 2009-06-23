package uk.ac.manchester.cs.lintroll.ui.preference;

import java.net.URI;
import java.util.Comparator;
import java.util.Set;

import org.coode.oppl.lint.OPPLLintScript;
import org.coode.oppl.lint.ParserFactory;
import org.coode.oppl.lint.syntax.OPPLLintParser;
import org.coode.oppl.lint.syntax.ParseException;
import org.coode.oppl.protege.ui.OPPLLintEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.util.NamespaceUtil;

public class OPPLLintFrameSection
		extends
		AbstractOWLFrameSection<OWLOntology, OWLOntologyAnnotationAxiom, OPPLLintScript> {
	protected OPPLLintFrameSection(OWLEditorKit editorKit,
			OWLFrame<? extends OWLOntology> frame) {
		super(editorKit, "Ontology OPPL Lints: ", frame);
	}

	@Override
	public boolean canAdd() {
		return true;
	}

	@Override
	protected void clear() {
		// TODO Auto-generated method stub
	}

	@Override
	protected OWLOntologyAnnotationAxiom createAxiom(OPPLLintScript object) {
		String name = object.getName().replaceAll("\\s", "_");
		URI annotationURI = URI
				.create(LintRollPreferences.OPPL_LINT_NAMESPACE_URI_STRING
						+ name);
		OWLAnnotation<OWLConstant> annotation = this.getOWLDataFactory()
				.getOWLConstantAnnotation(
						annotationURI,
						this.getOWLDataFactory().getOWLTypedConstant(
								object.toString()));
		return this.getOWLDataFactory().getOWLOntologyAnnotationAxiom(
				this.getRootObject(), annotation);
	}

	@Override
	public OWLFrameSectionRowObjectEditor<OPPLLintScript> getObjectEditor() {
		return new OPPLLintEditor(this.getOWLEditorKit());
	}

	@Override
	protected void refill(OWLOntology ontology) {
		Set<OWLOntologyAnnotationAxiom> ontologyAnnotationAxioms = ontology
				.getOntologyAnnotationAxioms();
		for (OWLOntologyAnnotationAxiom ontologyAnnotationAxiom : ontologyAnnotationAxioms) {
			OWLAnnotation<? extends OWLObject> annotation = ontologyAnnotationAxiom
					.getAnnotation();
			URI annotationURI = annotation.getAnnotationURI();
			NamespaceUtil nsUtil = new NamespaceUtil();
			String[] split = nsUtil.split(annotationURI.toString(), null);
			if (split != null
					&& split.length == 2
					&& split[0]
							.compareTo(LintRollPreferences.OPPL_LINT_NAMESPACE_URI_STRING) == 0) {
				String value = annotation.getAnnotationValueAsConstant()
						.getLiteral();
				ParserFactory.initParser(value);
				try {
					OPPLLintScript lint = OPPLLintParser.Start();
					this.addRow(new OPPLLintFrameSectionRow(this
							.getOWLEditorKit(), this, ontology, this
							.getRootObject(), ontologyAnnotationAxiom));
					LintRollPreferences.addLoadedLint(lint);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Comparator<OWLFrameSectionRow<OWLOntology, OWLOntologyAnnotationAxiom, OPPLLintScript>> getRowComparator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visit(OWLOntologyAnnotationAxiom ontologyAnnotationAxiom) {
		this.reset();
	}
}
