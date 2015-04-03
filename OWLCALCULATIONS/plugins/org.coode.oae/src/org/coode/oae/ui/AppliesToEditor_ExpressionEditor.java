package org.coode.oae.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.ManchesterOWLExpressionCheckerFactory;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;

import uk.ac.manchester.mae.evaluation.BindingModel;

public class AppliesToEditor_ExpressionEditor extends
        ExpressionEditor<OWLClassExpression> {
	private static final long serialVersionUID = -339908555692710213L;

	/**
	 * @param k
	 *            the {@link OWLEditorKit} to use
	 * @param model
	 *            the {@link BindingModel} to use; must not be null but can be
	 *            empty
	 */
	public AppliesToEditor_ExpressionEditor(OWLEditorKit k) {
        super(k, new ManchesterOWLExpressionCheckerFactory(
                k.getOWLModelManager()).getOWLClassExpressionChecker());
	}

    public void setAppliesTo(OWLClassExpression f) {
		setExpressionObject(f);
	}

    public OWLClassExpression getAppliesTo() {
		if (this.getText().trim().length() == 0) {
			return null;
		}
		try {
			return createObject();
		} catch (OWLException e) {
			return null;
		}
	}

	public void clear() {
		setExpressionObject(null);
	}
}
