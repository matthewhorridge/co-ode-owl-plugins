/**
 * 
 */
package uk.ac.manchester.cs.lintroll.ui.preference;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.coode.oppl.lint.AbstractParserFactory;
import org.coode.oppl.lint.OPPLLintScript;
import org.coode.oppl.lint.syntax.OPPLLintParser;
import org.coode.oppl.lint.syntax.ParseException;
import org.coode.oppl.protege.ui.OPPLLintEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLLintFrameSectionRow
		extends
		AbstractOWLFrameSectionRow<OWLOntology, OWLOntologyAnnotationAxiom, OPPLLintScript> {
	private OPPLLintScript lint;

	protected OPPLLintFrameSectionRow(
			OWLEditorKit owlEditorKit,
			OWLFrameSection<OWLOntology, OWLOntologyAnnotationAxiom, OPPLLintScript> section,
			OWLOntology ontology, OWLOntology rootObject,
			OWLOntologyAnnotationAxiom axiom) {
		super(owlEditorKit, section, ontology, rootObject, axiom);
		OWLAnnotation<? extends OWLObject> annotation = axiom.getAnnotation();
		String value = annotation.getAnnotationValueAsConstant().getLiteral();
		AbstractParserFactory.getInstance().initParser(value);
		try {
			this.lint = OPPLLintParser.Start();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected OWLOntologyAnnotationAxiom createAxiom(OPPLLintScript editedObject) {
		String name = editedObject.getName().replaceAll("\\s", "_");
		URI annotationURI = URI
				.create(LintRollPreferences.OPPL_LINT_NAMESPACE_URI_STRING
						+ name);
		OWLAnnotation<OWLConstant> annotation = this.getOWLDataFactory()
				.getOWLConstantAnnotation(
						annotationURI,
						this.getOWLDataFactory().getOWLTypedConstant(
								editedObject.toString()));
		return this.getOWLDataFactory().getOWLOntologyAnnotationAxiom(
				this.getRootObject(), annotation);
	}

	@Override
	protected OWLFrameSectionRowObjectEditor<OPPLLintScript> getObjectEditor() {
		OPPLLintEditor editor = new OPPLLintEditor(this.getOWLEditorKit());
		editor.setOPPLLintScript(this.lint);
		return editor;
	}

	public List<? extends OWLObject> getManipulatableObjects() {
		return new ArrayList<OWLObject>(Collections.singleton(this.axiom));
	}

	/**
	 * @return the lint
	 */
	public OPPLLintScript getLint() {
		return this.lint;
	}
}
