package org.coode.oae.ui;

import java.net.URI;
import java.util.Comparator;
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

import uk.ac.manchester.mae.ParseException;
import uk.ac.manchester.mae.evaluation.FormulaModel;

@SuppressWarnings("unchecked")
public class OWLCalculationsFormulaDataPropertyFrameSection
		extends
		AbstractOWLFrameSection<OWLDataProperty, OWLAnnotationAxiom<OWLDataProperty>, FormulaModel> {
	private static final String LABEL = "Formulas";
	protected boolean inferredFormulas = true;

	protected OWLCalculationsFormulaDataPropertyFrameSection(
			OWLEditorKit editorKit, OWLFrame<? extends OWLDataProperty> frame) {
		super(editorKit, LABEL, frame);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void clear() {
		// TODO Auto-generated method stub
	}

	@Override
	protected OWLAnnotationAxiom createAxiom(FormulaModel object) {
		OWLAnnotationAxiom toReturn = null;
		OWLDataProperty dataProperty = this.getRootObject();
		if (dataProperty != null) {
			URI uri = object.getFormulaURI();
			if (uri != null) {
				try {
					toReturn = this
							.getOWLDataFactory()
							.getOWLEntityAnnotationAxiom(
									dataProperty,
									uri,
									this
											.getOWLDataFactory()
											.getOWLTypedConstant(
													MAENodeAdapter
															.toFormula(
																	object,
																	this
																			.getOWLModelManager())
															.toString()));
				} catch (ParseException e) {
					// Impossible
					e.printStackTrace();
				}
			}
		}
		return toReturn;
	}

	@Override
	public OWLFrameSectionRowObjectEditor<FormulaModel> getObjectEditor() {
		// return new OWLArithmeticFormulaEditor(this.getOWLEditorKit(), null,
		// true, this.formulaAnnotationURIs);
		return new OWLCalculationsFormulaEditor(this.getOWLEditorKit());
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
						.addRow(new OWLCalculationsFormulaDataPropertyFrameSectionRow(
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
							.getModelManager().getOntologies()) {
						Set<OWLAnnotationAxiom> annotationAxioms = superProperty
								.getAnnotationAxioms(ontology);
						for (OWLAnnotationAxiom annotationAxiom : annotationAxioms) {
							OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
									null, this.getOWLModelManager());
							annotationAxiom.accept(visitor);
							if (visitor.getExtractedFormula() != null) {
								this
										.addRow(new OWLCalculationsFormulaDataPropertyFrameSectionRow(
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

	public Comparator<OWLFrameSectionRow<OWLDataProperty, OWLAnnotationAxiom<OWLDataProperty>, FormulaModel>> getRowComparator() {
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
