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
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.mae.evaluation.FormulaModel;
import uk.ac.manchester.mae.parser.ParseException;

@SuppressWarnings("unchecked")
public class OWLCalculationsFormulaDataPropertyFrameSection
		extends
		AbstractOWLFrameSection<OWLDataProperty, OWLAnnotationAxiom<OWLDataProperty>, FormulaModel> {
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
	protected OWLAnnotationAxiom createAxiom(FormulaModel object) {
		OWLAnnotationAxiom toReturn = null;
		OWLDataProperty dataProperty = getRootObject();
		OWLDataFactory odf = getOWLDataFactory();
		if (dataProperty != null) {
			URI uri = object.getFormulaURI();
			if (uri != null) {
				try {
					toReturn = odf.getOWLEntityAnnotationAxiom(dataProperty,
							uri, odf.getOWLTypedConstant(MAENodeAdapter
									.toFormula(object, getOWLModelManager())
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
		return new OWLCalculationsFormulaEditor(getOWLEditorKit());
	}

	@Override
	protected void refill(OWLOntology ontology) {
		Set<OWLAnnotationAxiom> annotationAxioms = getRootObject()
				.getAnnotationAxioms(ontology);
		for (OWLAnnotationAxiom annotationAxiom : annotationAxioms) {
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
	protected void refillInferred() throws OWLReasonerException {
		if (this.inferredFormulas) {
			for (Set<OWLDataProperty> superPropertySet : getOWLModelManager()
					.getReasoner().getAncestorProperties(getRootObject())) {
				for (OWLDataProperty superProperty : superPropertySet) {
					for (OWLOntology ontology : getOWLEditorKit()
							.getModelManager().getOntologies()) {
						Set<OWLAnnotationAxiom> annotationAxioms = superProperty
								.getAnnotationAxioms(ontology);
						for (OWLAnnotationAxiom annotationAxiom : annotationAxioms) {
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
	}

	public Comparator<OWLFrameSectionRow<OWLDataProperty, OWLAnnotationAxiom<OWLDataProperty>, FormulaModel>> getRowComparator() {
		return null;
	}

	@Override
	public void visit(OWLEntityAnnotationAxiom axiom) {
		OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
				null, getOWLModelManager());
		axiom.accept(visitor);
		if (visitor.getExtractedFormula() != null) {
			reset();
		}
	}
}
