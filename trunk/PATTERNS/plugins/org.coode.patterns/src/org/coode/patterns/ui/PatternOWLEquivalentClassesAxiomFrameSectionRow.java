package org.coode.patterns.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLEquivalentClassesAxiomFrameSectionRow;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLOntology;

public class PatternOWLEquivalentClassesAxiomFrameSectionRow extends
		OWLEquivalentClassesAxiomFrameSectionRow {
	/**
	 * @param owlEditorKit
	 * @param section
	 * @param ontology
	 * @param rootObject
	 * @param axiom
	 */
	public PatternOWLEquivalentClassesAxiomFrameSectionRow(
			OWLEditorKit owlEditorKit,
			PatternOWLEquivalentClassesAxiomFrameSection section,
			OWLOntology ontology, OWLClass rootObject,
			OWLEquivalentClassesAxiom axiom) {
		super(owlEditorKit, section, ontology, rootObject, axiom);
	}

	@Override
	public boolean isDeleteable() {
		return false;
	}

	@Override
	public boolean isEditable() {
		return false;
	}
}
