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
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;

import uk.ac.manchester.mae.evaluation.PropertyChainCell;

public class ObjectPropertySelector extends JPanel implements
		VerifiedInputEditor {
	private static final long serialVersionUID = 1699848613778095787L;
	protected MList objectPropertiesView = new MList();
	protected MList facetClassView = new MList();
	protected List<OWLObjectProperty> objectProperties = new ArrayList<OWLObjectProperty>();
	protected List<OWLClass> facetClasses = new ArrayList<OWLClass>();
	protected StaticListModel<OWLObjectProperty> objectPropertiesModel = new StaticListModel<OWLObjectProperty>(
			this.objectProperties, null);
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

	public ObjectPropertySelector(OWLEditorKit k) {
		super(new BorderLayout());
		this.kit = k;
		this.objectPropertiesView
				.setPreferredSize(GraphicalFormulaEditor.LIST_PREFERRED_SIZE);
		this.facetClassView
				.setPreferredSize(GraphicalFormulaEditor.LIST_PREFERRED_SIZE);
		this.objectPropertiesView
				.setCellRenderer(new RenderableObjectCellRenderer(this.kit));
		this.facetClassView.setCellRenderer(new RenderableObjectCellRenderer(
				this.kit));
		this.objectPropertiesView.setModel(this.objectPropertiesModel);
		this.facetClassView.setModel(this.facetClassesModel);
		OWLObjectHierarchyProvider<OWLObjectProperty> opp = this.kit
				.getOWLModelManager().getOWLHierarchyManager()
				.getOWLObjectPropertyHierarchyProvider();
		for (OWLObjectProperty op : opp.getRoots()) {
			this.objectProperties.add(op);
			for (OWLObjectProperty opd : opp.getDescendants(op)) {
				this.objectProperties.add(opd);
			}
		}
		this.objectPropertiesModel.init();
		this.facetClassesModel.init();
		JScrollPane spobj = new JScrollPane(this.objectPropertiesView);
		spobj.setBorder(new TitledBorder("Object property selection"));
		JScrollPane spobjf = new JScrollPane(this.facetClassView);
		spobjf.setBorder(new TitledBorder("Facet selection"));
		this.add(spobj, BorderLayout.WEST);
		this.add(spobjf, BorderLayout.EAST);
		this.objectPropertiesView
				.addListSelectionListener(new ListSelectionListener() {
					@SuppressWarnings("unchecked")
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							if (ObjectPropertySelector.this.objectPropertiesView
									.getSelectedIndex() > -1) {
								// then facets get loaded and status is OK
								OWLObjectProperty p = ((StaticListItem<OWLObjectProperty>) ObjectPropertySelector.this.objectPropertiesView
										.getSelectedValue()).getItem();
								ObjectPropertySelector.this.facetClasses
										.clear();
								ObjectPropertySelector.this.facetClasses
										.addAll(getOWLClasses(p));
								ObjectPropertySelector.this.facetClassesModel
										.init();
								notifyVerified();
							}
						}
					}
				});
	}

	protected Set<OWLClass> getOWLClasses(OWLObjectProperty op) {
		Set<OWLClass> ranges = new HashSet<OWLClass>();
		for (OWLDescription d : op.getRanges(this.kit.getOWLModelManager()
				.getActiveOntology())) {
			if (d instanceof OWLClass) {
				ranges.add((OWLClass) d);
			}
		}
		return ranges;
	}

	public void clear() {
		this.objectPropertiesView.getSelectionModel().clearSelection();
		this.facetClassView.getSelectionModel().clearSelection();
		this.facetClasses.clear();
		this.facetClassesModel.init();
	}

	@SuppressWarnings("unchecked")
	public PropertyChainCell getCell() {
		if (this.objectPropertiesView.getSelectedIndex() > -1) {
			OWLObjectProperty p = ((StaticListItem<OWLObjectProperty>) this.objectPropertiesView
					.getSelectedValue()).getItem();
			OWLClass facet = null;
			if (this.facetClassView.getSelectedIndex() > -1) {
				facet = ((StaticListItem<OWLClass>) this.facetClassView
						.getSelectedValue()).getItem();
			}
			return new PropertyChainCell(p, facet);
		}
		return null;
	}

	protected void notifyVerified() {
		for (InputVerificationStatusChangedListener i : this.listeners) {
			i.verifiedStatusChanged(true);
		}
	}
}
