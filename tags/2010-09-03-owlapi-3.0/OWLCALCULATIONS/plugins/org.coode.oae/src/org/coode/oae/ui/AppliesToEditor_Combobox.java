package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owl.model.OWLClass;

import uk.ac.manchester.mae.evaluation.BindingModel;

public class AppliesToEditor_Combobox extends JPanel implements ActionListener,
		VerifiedInputEditor {
	private static final String NONE = "none";
	private static final long serialVersionUID = -339908555692710213L;
	private OWLEditorKit kit;
	// private OWLClassSelector classSelector;
	protected OWLClass facet;
	protected DefaultComboBoxModel myComboBoxModel = new DefaultComboBoxModel();
	protected JComboBox selectedFacet = new JComboBox(this.myComboBoxModel);

	// protected JButton commit = new JButton("Select Applies to");
	/**
	 * @param k
	 *            the {@link OWLEditorKit} to use
	 * @param model
	 *            the {@link BindingModel} to use; must not be null but can be
	 *            empty
	 */
	public AppliesToEditor_Combobox(OWLEditorKit k) {
		super(new BorderLayout());
		this.kit = k;
		OWLObjectHierarchyProvider<OWLClass> provider = this.kit
				.getModelManager().getOWLHierarchyManager()
				.getOWLClassHierarchyProvider();
		List<OWLClass> facetClasses = new ArrayList<OWLClass>();
		for (OWLClass c : provider.getRoots()) {
			facetClasses.add(c);
			facetClasses.addAll(provider.getDescendants(c));
		}
		this.myComboBoxModel.addElement(NONE);
		for (OWLClass c : facetClasses) {
			this.myComboBoxModel.addElement(c);
		}
		this.selectedFacet
				.setPreferredSize(GraphicalEditorConstants.LONG_FIELD_DIMENSION);
		this.selectedFacet.setEditable(false);
		this.selectedFacet.setSelectedItem(NONE);
		// this.selectedFacet.setRenderer(new RenderableObjectCellRenderer(
		// this.kit));
		this.selectedFacet.addActionListener(this);
		this.selectedFacet.validate();
		init();
	}

	public void setAppliesTo(OWLClass f) {
		// intentional pointer check, avoid changes if the same object is set
		// and avoids null checks to call equals
		this.facet = f;
		if (this.facet != null) {
			this.selectedFacet.setSelectedItem(this.facet);
		} else {
			this.selectedFacet.setSelectedItem(NONE);
		}
		// this.selectedFacet.setVisible(false);
		// this.selectedFacet.dispatchEvent(new MouseEvent(this.selectedFacet,
		// 0,
		// System.currentTimeMillis(), 0, 1, 1, 1, false,
		// java.awt.event.MouseEvent.BUTTON1));
		// this.selectedFacet.setVisible(true);
		setOk();
	}

	private void setOk() {
		for (InputVerificationStatusChangedListener i : this.listeners) {
			i.verifiedStatusChanged(true);
		}
	}

	public OWLClass getAppliesTo() {
		return this.facet;
	}

	public void init() {
		this.add(this.selectedFacet, BorderLayout.NORTH);
		// this.commit.addActionListener(this);
		// this.add(this.commit, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.selectedFacet)) {
			if (this.selectedFacet.getSelectedIndex() == 0) {
				this.facet = null;
			} else {
				this.facet = (OWLClass) this.selectedFacet.getSelectedItem();
			}
			setOk();
		}
	}

	public void clear() {
		setAppliesTo(null);
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
}
