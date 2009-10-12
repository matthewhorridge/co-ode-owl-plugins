package org.coode.oae.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.semanticweb.owl.model.OWLException;

import uk.ac.manchester.mae.evaluation.BindingModel;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;

public class StoreToEditor extends ExpressionEditor<PropertyChainModel> {
	private static final long serialVersionUID = 3420300348376487738L;

	/**
	 * @param k
	 *            the {@link OWLEditorKit} to use
	 * @param model
	 *            the {@link BindingModel} to use; must not be null but can be
	 *            empty
	 */
	public StoreToEditor(OWLEditorKit k) {
		super(k, new PropertyChainExpressionChecker(k, true, true));
	}

	public void setStoreTo(PropertyChainModel _pcm) {
		if (_pcm != null) {
			setText(_pcm.render(getOWLEditorKit().getModelManager()));
		} else {
			setText("");
		}
	}

	public PropertyChainModel getPropertyChainModel() {
		if (getText().trim().isEmpty()) {
			return null;
		}
		try {
			return createObject();
		} catch (OWLException e) {
			return null;
		}
	}

	public void clear() {
		setStoreTo(null);
	}
}
