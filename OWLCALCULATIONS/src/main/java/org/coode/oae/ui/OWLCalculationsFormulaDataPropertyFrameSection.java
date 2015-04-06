package org.coode.oae.ui;

import java.util.Collection;
import java.util.Comparator;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import uk.ac.manchester.mae.evaluation.FormulaModel;
import uk.ac.manchester.mae.parser.ParseException;

public class OWLCalculationsFormulaDataPropertyFrameSection
		extends
        AbstractOWLFrameSection<OWLDataProperty, OWLAnnotationAssertionAxiom, FormulaModel> {
	private static final String LABEL = "Formulas";
	protected boolean inferredFormulas = true;

	protected OWLCalculationsFormulaDataPropertyFrameSection(
			OWLEditorKit editorKit, OWLFrame<? extends OWLDataProperty> frame) {
		super(editorKit, LABEL, frame);
	}

	@Override
	protected void clear() {
		// TODO cleaning to be implemented
	}

	@Override
    protected OWLAnnotationAssertionAxiom createAxiom(FormulaModel object) {
        OWLAnnotationAssertionAxiom toReturn = null;
		OWLDataProperty dataProperty = getRootObject();
		OWLDataFactory odf = getOWLDataFactory();
		if (dataProperty != null) {
            IRI uri = object.getFormulaURI();
			if (uri != null) {
				try {
                    toReturn = odf.getOWLAnnotationAssertionAxiom(
                            odf.getOWLAnnotationProperty(uri),
                            dataProperty.getIRI(),
                            odf.getOWLLiteral(MAENodeAdapter.toFormula(object,
                                    getOWLModelManager()).toString()));
				} catch (ParseException e) {
					// Impossible
					e.printStackTrace();
				}
			}
		}
		return toReturn;
	}

	@Override
    public OWLObjectEditor<FormulaModel> getObjectEditor() {
		// return new OWLArithmeticFormulaEditor(this.getOWLEditorKit(), null,
		// true, this.formulaAnnotationURIs);
		return new OWLCalculationsFormulaEditor(getOWLEditorKit());
	}

	@Override
	protected void refill(OWLOntology ontology) {
        Collection<OWLAnnotationAssertionAxiom> annotationAxioms = EntitySearcher
                .getAnnotationAssertionAxioms(getRootObject().getIRI(),
                        ontology);
        for (OWLAnnotationAssertionAxiom annotationAxiom : annotationAxioms) {
			OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
					null, getOWLModelManager());
			annotationAxiom.accept(visitor);
			if (visitor.getExtractedFormula() != null) {
				addRow(new OWLCalculationsFormulaDataPropertyFrameSectionRow(
						getOWLEditorKit(), this, ontology, getRootObject(),
						annotationAxiom));
			}
		}
	}

	@Override
    protected void refillInferred() {
		if (inferredFormulas) {
            for (OWLDataProperty superProperty : getOWLModelManager()
                    .getReasoner()
                    .getSuperDataProperties(getRootObject(), false)
                    .getFlattened()) {
					for (OWLOntology ontology : getOWLEditorKit()
							.getModelManager().getOntologies()) {
                    Collection<OWLAnnotationAssertionAxiom> annotationAxioms = EntitySearcher
                            .getAnnotationAssertionAxioms(
                                    superProperty.getIRI(), ontology);
                    for (OWLAnnotationAssertionAxiom annotationAxiom : annotationAxioms) {
							OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
									null, getOWLModelManager());
							annotationAxiom.accept(visitor);
							if (visitor.getExtractedFormula() != null) {
								addRow(new OWLCalculationsFormulaDataPropertyFrameSectionRow(
										getOWLEditorKit(), this, null,
										getRootObject(), annotationAxiom));
							}
						}
					}
				}

		}
	}

    @Override
    public
            Comparator<OWLFrameSectionRow<OWLDataProperty, OWLAnnotationAssertionAxiom, FormulaModel>>
            getRowComparator() {
		return null;
	}

	@Override
    public void visit(OWLAnnotationAssertionAxiom axiom) {
		OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
				null, getOWLModelManager());
		axiom.accept(visitor);
		if (visitor.getExtractedFormula() != null) {
			reset();
		}
	}
}
