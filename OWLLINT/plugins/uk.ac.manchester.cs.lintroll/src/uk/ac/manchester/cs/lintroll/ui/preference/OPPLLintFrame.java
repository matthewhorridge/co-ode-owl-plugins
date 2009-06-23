package uk.ac.manchester.cs.lintroll.ui.preference;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.semanticweb.owl.model.OWLOntology;

public class OPPLLintFrame extends AbstractOWLFrame<OWLOntology> {
	public OPPLLintFrame(OWLEditorKit owlEditorKit) {
		super(owlEditorKit.getOWLModelManager().getOWLOntologyManager());
		this.addSection(new OPPLLintFrameSection(owlEditorKit, this));
	}
}
