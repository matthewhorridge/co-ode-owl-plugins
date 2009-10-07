package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.coode.oae.ui.StaticListModel.StaticListItem;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owl.model.OWLClass;

public class OWLClassSelector extends JPanel implements VerifiedInputEditor {
	private static final long serialVersionUID = -2876275234475209099L;
	protected MList facetClassView = new MList();
	protected List<OWLClass> facetClasses = new ArrayList<OWLClass>();
	protected StaticListModel<OWLClass> facetClassesModel = new StaticListModel<OWLClass>(
			this.facetClasses, null);
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
	private OWLEditorKit kit;

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	public OWLClassSelector(OWLEditorKit k) {
		super(new BorderLayout());
		this.kit = k;
		this.facetClassView
				.setPreferredSize(GraphicalFormulaEditor.LIST_PREFERRED_SIZE);
		this.facetClassView.setCellRenderer(new RenderableObjectCellRenderer(
				this.kit));
		this.facetClassView.setModel(this.facetClassesModel);
		OWLObjectHierarchyProvider<OWLClass> provider = this.kit
				.getModelManager().getOWLHierarchyManager()
				.getOWLClassHierarchyProvider();
		for (OWLClass c : provider.getRoots()) {
			this.facetClasses.add(c);
			this.facetClasses.addAll(provider.getDescendants(c));
		}
		setOK(false);
		this.facetClassesModel.init();
		JScrollPane spobjf = new JScrollPane(this.facetClassView);
		spobjf.setBorder(new TitledBorder("Facet selection"));
		this.add(spobjf);
		this.facetClassView
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							// then status is OK
							setOK(true);
						}
					}
				});
	}

	protected void setOK(boolean b) {
		for (InputVerificationStatusChangedListener i : this.listeners) {
			i.verifiedStatusChanged(b);
		}
	}

	public void clear() {
		this.facetClassView.getSelectionModel().clearSelection();
	}

	@SuppressWarnings("unchecked")
	public OWLClass getOWLClass() {
		if (this.facetClassView.getSelectedIndex() > -1) {
			OWLClass p = ((StaticListItem<OWLClass>) this.facetClassView
					.getSelectedValue()).getItem();
			return p;
		}
		return null;
	}
}
