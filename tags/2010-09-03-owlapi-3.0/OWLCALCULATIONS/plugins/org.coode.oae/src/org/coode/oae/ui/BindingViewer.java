package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;

import uk.ac.manchester.mae.evaluation.BindingModel;

public class BindingViewer extends JPanel implements VerifiedInputEditor,
		InputVerificationStatusChangedListener {
	private static final long serialVersionUID = -6626947893443460240L;
	private OWLEditorKit kit;
	private final Map<String, BindingEditor> bindingMap = new HashMap<String, BindingEditor>();
	JPanel myPanel = new JPanel();// new GridLayout(1, 1, 0, 0));
	private JScrollPane scrollPane = ComponentFactory
			.createScrollPane(this.myPanel);

	public BindingViewer(OWLEditorKit kit) {
		super(new BorderLayout());
		this.kit = kit;
		this.setBorder(ComponentFactory.createTitledBorder("Bindings"));
		// make space for about 10 bindings
		this.scrollPane.setPreferredSize(new Dimension(
				((int) BindingEditor.PREFERRED_SIZE.getWidth()) + 20,
				(int) BindingEditor.PREFERRED_SIZE.getHeight() * 10));
		this.add(this.scrollPane);
	}

	public void clear() {
		this.bindingMap.clear();
		this.handleChange();
	}

	public void addBinding(String id) {
		if (!this.bindingMap.containsKey(id)) {
			BindingEditor e = new BindingEditor(this.kit);
			e.setBindingModel(id);
			e.addStatusChangedListener(this);
			this.bindingMap.put(id, e);
			this.handleChange();
		} else {
			// if there is one, make sure it looks enabled
			this.bindingMap.get(id).setIdentifierUnneeded(false);
		}
	}

	public void removeBinding(String id) {
		if (this.bindingMap.containsKey(id)) {
			BindingEditor e = this.bindingMap.get(id);
			if (e.isPropertyChainEmpty()) {
				this.bindingMap.remove(id);
				this.handleChange();
			} else {
				e.setIdentifierUnneeded(true);
			}
		}
	}

	public void addBinding(BindingModel b) {
		if (this.bindingMap.containsKey(b.getIdentifier())) {
			// then there is a corresponding binding
			BindingEditor existing = this.bindingMap.get(b.getIdentifier());
			if (!existing.isInputValid()) {
				// then it's empty or broken - replace
				existing.setBindingModel(b);
				this.handleChange();
			} else {
				// then it's trying to replace a valid one with another one -
				// ignore
				System.out
						.println("Error: trying to replace a valid bindingmodel with another: "
								+ existing.getBindingModel().toString()
								+ "\t"
								+ b.toString());
			}
		} else {
			BindingEditor e = new BindingEditor(this.kit);
			e.setBindingModel(b);
			e.addStatusChangedListener(this);
			this.bindingMap.put(b.getIdentifier(), e);
			this.handleChange();
		}
	}

	/**
	 * returns a readonly set of binding names; values are not backed by the
	 * bindings, so bindings can be eliminated while iterating over the set
	 */
	public List<String> getBindingNames() {
		return new ArrayList<String>(this.bindingMap.keySet());
	}

	public Set<BindingModel> getBindingModels() {
		Set<BindingModel> toReturn = new HashSet<BindingModel>();
		for (BindingEditor e : this.bindingMap.values()) {
			if (e.isInputValid()) {
				toReturn.add(e.getBindingModel());
			}
		}
		return toReturn;
	}

	public void handleChange() {
		this.myPanel.removeAll();
		for (BindingEditor e : this.bindingMap.values()) {
			this.myPanel.add(e);
		}
		this.myPanel.setPreferredSize(new Dimension(
				BindingEditor.PREFERRED_SIZE.width + 10,
				BindingEditor.PREFERRED_SIZE.height
						* this.bindingMap.values().size() + 10));
		this.validate();
		this.verifiedStatusChanged(false);
	}

	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	public void verifiedStatusChanged(boolean newState) {
		// newstate is irrelevant, ALL the bindingeditors must be in a valid
		// state
		boolean toPass = true;
		for (BindingEditor e : this.bindingMap.values()) {
			toPass = toPass && e.isInputValid();
		}
		for (InputVerificationStatusChangedListener i : this.listeners) {
			i.verifiedStatusChanged(toPass);
		}
	}

	public void addBindingModels(Set<BindingModel> bindings) {
		for (BindingModel b : bindings) {
			this.addBinding(b);
		}
	}
}
