package org.coode.oae.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.semanticweb.owl.model.OWLException;

import uk.ac.manchester.mae.evaluation.BindingModel;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;

public class BindingEditor extends JPanel implements VerifiedInputEditor,
		InputVerificationStatusChangedListener {
	static final Dimension PREFERRED_SIZE = new Dimension(500, 25);
	private static final Dimension IDENTIFIER_PREFERRED_SIZE = new Dimension(
			(int) (PREFERRED_SIZE.getWidth() * 0.2), (int) (PREFERRED_SIZE
					.getHeight() * 0.8));
	private static final Dimension EDITOR_PREFERRED_SIZE = new Dimension(
			(int) (PREFERRED_SIZE.getWidth() * 0.8), (int) (PREFERRED_SIZE
					.getHeight() * 0.8));
	private static final long serialVersionUID = -2534565329159746844L;
	private JLabel identifier = new JLabel();
	private final ExpressionEditor<PropertyChainModel> editor;
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
	private final OWLEditorKit kit;
	private boolean currentEditorStatus;

	/**
	 * @param k
	 *            the {@link OWLEditorKit} to use
	 * @param model
	 *            the {@link BindingModel} to use; must not be null but can be
	 *            empty
	 */
	public BindingEditor(OWLEditorKit k) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.kit = k;
		setPreferredSize(PREFERRED_SIZE);
		this.identifier.setPreferredSize(IDENTIFIER_PREFERRED_SIZE);
		this.editor = new ExpressionEditor<PropertyChainModel>(k,
				new PropertyChainExpressionChecker(k, false, false));
		this.editor.setPreferredSize(EDITOR_PREFERRED_SIZE);
		this.editor.addStatusChangedListener(this);
		new PropertyChainAutoCompleter(this.kit, this.editor, this.editor
				.getExpressionChecker());
		this.add(this.identifier);
		this.add(this.editor);
	}

	public void setBindingModel(BindingModel model) {
		this.identifier.setText(model.getIdentifier());
		this.editor.setText(model.getPropertyChainModel().render(
				this.kit.getModelManager()));
		checkInputValid();
	}

	public void setBindingModel(String id) {
		this.identifier.setText(id);
	}

	public BindingModel getBindingModel() {
		try {
			return new BindingModel(this.identifier.getText(), this.editor
					.createObject());
		} catch (OWLException e) {
			// should not happen
			e.printStackTrace();
		}
		return null;
	}

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	private void checkInputValid() {
		this.currentEditorStatus = true;
		try {
			this.editor.createObject();
		} catch (OWLException e) {
			this.currentEditorStatus = false;
		}
	}

	public boolean isInputValid() {
		return this.currentEditorStatus;
	}

	public void verifiedStatusChanged(boolean newState) {
		this.currentEditorStatus = newState;
		for (InputVerificationStatusChangedListener i : this.listeners) {
			i.verifiedStatusChanged(isInputValid());
		}
	}

	public boolean isPropertyChainEmpty() {
		return this.editor.getText().length() == 0;
	}

	public void setIdentifierUnneeded(boolean b) {
		if (b) {
			this.editor.setBackground(Color.GRAY.brighter());
		} else {
			this.editor.setBackground(Color.WHITE);
		}
	}
}
