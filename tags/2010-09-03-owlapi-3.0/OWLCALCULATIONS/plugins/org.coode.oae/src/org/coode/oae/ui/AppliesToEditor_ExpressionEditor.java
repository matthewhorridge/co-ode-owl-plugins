package org.coode.oae.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDescriptionChecker;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;

import uk.ac.manchester.mae.evaluation.BindingModel;

public class AppliesToEditor_ExpressionEditor extends
		ExpressionEditor<OWLDescription> {
	private static final long serialVersionUID = -339908555692710213L;

	/**
	 * @param k
	 *            the {@link OWLEditorKit} to use
	 * @param model
	 *            the {@link BindingModel} to use; must not be null but can be
	 *            empty
	 */
	public AppliesToEditor_ExpressionEditor(OWLEditorKit k) {
		super(k, new OWLDescriptionChecker(k.getOWLModelManager()));
	}

	public void setAppliesTo(OWLDescription f) {
		this.setExpressionObject(f);
	}

	public OWLDescription getAppliesTo() {
		if (this.getText().trim().length() == 0) {
			return null;
		}
		try {
			return this.createObject();
		} catch (OWLException e) {
			return null;
		}
	}

	public void clear() {
		this.setExpressionObject(null);
	}
}
