package org.coode.oae.ui;

import java.net.URI;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.mae.MAEStart;

@SuppressWarnings("unchecked")
public class OWLArithmeticsFormulaDataPropertyFrameSection extends
		AbstractOWLFrameSection<OWLDataProperty, OWLAnnotationAxiom, MAEStart> {
	private static final String LABEL = "Formulas";
	protected Map<MAEStart, URI> formulaAnnotationURIs = new HashMap<MAEStart, URI>();
	protected Map<MAEStart, OWLDataProperty> formulaProperties = new HashMap<MAEStart, OWLDataProperty>();
	protected boolean inferredFormulas = true;

	protected OWLArithmeticsFormulaDataPropertyFrameSection(
			OWLEditorKit editorKit, OWLFrame<? extends OWLDataProperty> frame) {
		super(editorKit, LABEL, frame);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void clear() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean canAddRows() {
		return false;
	}

	@Override
	protected OWLAnnotationAxiom createAxiom(MAEStart object) {
		OWLAnnotationAxiom toReturn = null;
		OWLDataProperty dataProperty = this.formulaProperties.get(object);
		if (dataProperty != null) {
			URI uri = this.formulaAnnotationURIs.get(object);
			if (uri != null) {
				toReturn = this.getOWLDataFactory()
						.getOWLEntityAnnotationAxiom(
								dataProperty,
								uri,
								this.getOWLDataFactory().getOWLTypedConstant(
										object.toString()));
			}
		}
		return toReturn;
	}

	@Override
	public OWLFrameSectionRowObjectEditor<MAEStart> getObjectEditor() {
		return new OWLArithmeticFormulaEditor(this.getOWLEditorKit(), null,
				true);
	}

	@Override
	protected void refill(OWLOntology ontology) {
		Set<OWLAnnotationAxiom> annotationAxioms = this.getRootObject()
				.getAnnotationAxioms(ontology);
		for (OWLAnnotationAxiom annotationAxiom : annotationAxioms) {
			OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
					null, this.getOWLModelManager());
			annotationAxiom.accept(visitor);
			if (visitor.getExtractedFormula() != null) {
				this
						.addRow(new OWLArithmeticsFormulaDataPropertyFrameSectionRow(
								this.getOWLEditorKit(), this, ontology, this
										.getRootObject(), annotationAxiom));
			}
		}
	}

	@Override
	protected void refillInferred() throws OWLReasonerException {
		if (this.inferredFormulas) {
			for (Set<OWLDataProperty> superPropertySet : this
					.getOWLModelManager().getReasoner().getAncestorProperties(
							this.getRootObject())) {
				for (OWLDataProperty superProperty : superPropertySet) {
					for (OWLOntology ontology : this.getOWLEditorKit()
							.getOWLModelManager().getOntologies()) {
						Set<OWLAnnotationAxiom> annotationAxioms = superProperty
								.getAnnotationAxioms(ontology);
						for (OWLAnnotationAxiom annotationAxiom : annotationAxioms) {
							OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
									null, this.getOWLModelManager());
							annotationAxiom.accept(visitor);
							if (visitor.getExtractedFormula() != null) {
								this
										.addRow(new OWLArithmeticsFormulaDataPropertyFrameSectionRow(
												this.getOWLEditorKit(), this,
												null, this.getRootObject(),
												annotationAxiom));
							}
						}
					}
				}
			}
		}
	}

	public Comparator<OWLFrameSectionRow<OWLDataProperty, OWLAnnotationAxiom, MAEStart>> getRowComparator() {
		return null;
	}

	@Override
	public void visit(OWLEntityAnnotationAxiom axiom) {
		OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
				null, this.getOWLModelManager());
		axiom.accept(visitor);
		if (visitor.getExtractedFormula() != null) {
			this.reset();
		}
	}
}
