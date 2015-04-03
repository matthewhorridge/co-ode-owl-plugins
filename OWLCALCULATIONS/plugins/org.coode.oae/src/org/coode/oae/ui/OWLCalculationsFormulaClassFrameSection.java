package org.coode.oae.ui;

import java.net.URI;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import uk.ac.manchester.mae.evaluation.FormulaModel;
import uk.ac.manchester.mae.parser.MAEStart;

public class OWLCalculationsFormulaClassFrameSection
		extends
        AbstractOWLFrameSection<OWLClass, OWLAnnotationAssertionAxiom, FormulaModel> {
	private static final String LABEL = "Formulas";
	protected Map<MAEStart, URI> formulaAnnotationURIs = new HashMap<MAEStart, URI>();
	protected Map<MAEStart, OWLDataProperty> formulaProperties = new HashMap<MAEStart, OWLDataProperty>();
	protected boolean inferredFormulas = true;

	protected OWLCalculationsFormulaClassFrameSection(OWLEditorKit editorKit,
			OWLFrame<? extends OWLClass> frame) {
		super(editorKit, LABEL, frame);
	}

	@Override
	protected void clear() {
	}


	@Override
    protected OWLAnnotationAssertionAxiom createAxiom(
			FormulaModel object) {
		return null;
	}

	@Override
    public OWLObjectEditor<FormulaModel> getObjectEditor() {
		// return new OWLArithmeticFormulaEditor(this.getOWLEditorKit(), this
		// .getRootObject(), false, this.formulaAnnotationURIs);
		return new OWLCalculationsFormulaEditor(getOWLEditorKit());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void refill(OWLOntology ontology) {
		for (OWLDataProperty dataProperty : ontology
                .getDataPropertiesInSignature()) {
            Collection<OWLAnnotationAssertionAxiom> annotationAxioms = ontology
                    .getAnnotationAssertionAxioms(dataProperty.getIRI());
            for (OWLAnnotationAssertionAxiom annotation : annotationAxioms) {
				OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
						getRootObject(), getOWLModelManager());
                annotation.accept(visitor);
				if (visitor.getExtractedFormula() != null) {
					addRow(new OWLCalculationsFormulaClassFrameSectionRow(
							getOWLEditorKit(), this, ontology, getRootObject(),
                            annotation));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
    protected void refillInferred() {
		if (inferredFormulas) {
			boolean isSatisfiable = getOWLModelManager().getReasoner()
					.isSatisfiable(getRootObject());
			if (isSatisfiable) {
                for (OWLClass superClass : getOWLModelManager().getReasoner()
                        .getSuperClasses(getRootObject(), false).getFlattened()) {
						for (OWLOntology ontology : getOWLModelManager()
								.getOntologies()) {
							for (OWLDataProperty dataProperty : ontology
                                    .getDataPropertiesInSignature()) {
                            for (OWLAnnotationAssertionAxiom annotationAxiom : EntitySearcher
                                    .getAnnotationAssertionAxioms(
                                            dataProperty.getIRI(), ontology)) {
									OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
											superClass, getOWLModelManager());
									annotationAxiom.accept(visitor);
									if (visitor.getExtractedFormula() != null) {
										addRow(new OWLCalculationsFormulaClassFrameSectionRow(
												getOWLEditorKit(), this, null,
												getRootObject(),
												annotationAxiom));
									}
								}
							}
						}
				}
			}
		}
	}

    @Override
    public
            Comparator<OWLFrameSectionRow<OWLClass, OWLAnnotationAssertionAxiom, FormulaModel>>
            getRowComparator() {
		return null;
	}

	@Override
    public void visit(OWLAnnotationAssertionAxiom axiom) {
		OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
				getRootObject(), getOWLModelManager());
		axiom.accept(visitor);
		if (visitor.getExtractedFormula() != null) {
			reset();
		}
	}
}
