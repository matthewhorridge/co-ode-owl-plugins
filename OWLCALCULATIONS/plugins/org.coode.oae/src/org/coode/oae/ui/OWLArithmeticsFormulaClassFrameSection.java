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
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.mae.MAEStart;

public class OWLArithmeticsFormulaClassFrameSection extends
		AbstractOWLFrameSection<OWLClass, OWLAnnotationAxiom, MAEStart> {
	private static final String LABEL = "Formulas";
	protected Map<MAEStart, URI> formulaAnnotationURIs = new HashMap<MAEStart, URI>();
	protected Map<MAEStart, OWLDataProperty> formulaProperties = new HashMap<MAEStart, OWLDataProperty>();
	protected boolean inferredFormulas = true;

	protected OWLArithmeticsFormulaClassFrameSection(OWLEditorKit editorKit,
			OWLFrame<? extends OWLClass> frame) {
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
		return new OWLArithmeticFormulaEditor(this.getOWLEditorKit(), this
				.getRootObject(), false);
	}

	@Override
	protected void refill(OWLOntology ontology) {
		for (OWLDataProperty dataProperty : ontology
				.getReferencedDataProperties()) {
			Set<OWLAnnotationAxiom> annotationAxioms = dataProperty
					.getAnnotationAxioms(ontology);
			for (OWLAnnotationAxiom annotationAxiom : annotationAxioms) {
				OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
						this.getRootObject(), this.getOWLModelManager());
				annotationAxiom.accept(visitor);
				if (visitor.getExtractedFormula() != null) {
					this.addRow(new OWLArithmeticsFormulaClassFrameSectionRow(this
							.getOWLEditorKit(), this, ontology, this
							.getRootObject(), annotationAxiom));
				}
			}
		}
	}

	@Override
	protected void refillInferred() throws OWLReasonerException {
		if (this.inferredFormulas) {
			boolean isSatisfiable = this.getOWLModelManager().getReasoner()
					.isSatisfiable(this.getRootObject());
			if (isSatisfiable) {
				for (Set<OWLClass> superClassSet : this.getOWLModelManager()
						.getReasoner().getAncestorClasses(this.getRootObject())) {
					for (OWLClass superClass : superClassSet) {
						for (OWLOntology ontology : this.getOWLModelManager()
								.getOntologies()) {
							for (OWLDataProperty dataProperty : ontology
									.getReferencedDataProperties()) {
								Set<OWLAnnotationAxiom> annotationAxioms = dataProperty
										.getAnnotationAxioms(ontology);
								for (OWLAnnotationAxiom annotationAxiom : annotationAxioms) {
									OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
											superClass, this
													.getOWLModelManager());
									annotationAxiom.accept(visitor);
									if (visitor.getExtractedFormula() != null) {
										this
												.addRow(new OWLArithmeticsFormulaClassFrameSectionRow(
														this.getOWLEditorKit(),
														this, null,
														this.getRootObject(),
														annotationAxiom));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public Comparator<OWLFrameSectionRow<OWLClass, OWLAnnotationAxiom, MAEStart>> getRowComparator() {
		return null;
	}

	@Override
	public void visit(OWLEntityAnnotationAxiom axiom) {
		OWLArithmeticsAxiomFormulaExtractor visitor = new OWLArithmeticsAxiomFormulaExtractor(
				this.getRootObject(), this.getOWLModelManager());
		axiom.accept(visitor);
		if (visitor.getExtractedFormula() != null) {
			this.reset();
		}
	}
}
