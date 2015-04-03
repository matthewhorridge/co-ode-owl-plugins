package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.coode.oae.ui.StaticListModel.StaticListItem;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.search.EntitySearcher;

import uk.ac.manchester.mae.evaluation.PropertyChainCell;

public class ObjectPropertySelector extends JPanel implements
		VerifiedInputEditor {
	private static final long serialVersionUID = 1699848613778095787L;
	protected MList objectPropertiesView = new MList();
	protected MList facetClassView = new MList();
	protected List<OWLObjectProperty> objectProperties = new ArrayList<OWLObjectProperty>();
	protected List<OWLClass> facetClasses = new ArrayList<OWLClass>();
	protected StaticListModel<OWLObjectProperty> objectPropertiesModel = new StaticListModel<OWLObjectProperty>(
			objectProperties, null);
	protected StaticListModel<OWLClass> facetClassesModel = new StaticListModel<OWLClass>(
			facetClasses, null);
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
	private OWLEditorKit kit;

	@Override
    public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		listeners.add(listener);
	}

	@Override
    public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		listeners.remove(listener);
	}

	public ObjectPropertySelector(OWLEditorKit k) {
		super(new BorderLayout());
		kit = k;
		objectPropertiesView
				.setPreferredSize(GraphicalEditorConstants.LIST_PREFERRED_SIZE);
		facetClassView
				.setPreferredSize(GraphicalEditorConstants.LIST_PREFERRED_SIZE);
		objectPropertiesView
				.setCellRenderer(new RenderableObjectCellRenderer(kit));
		facetClassView.setCellRenderer(new RenderableObjectCellRenderer(
				kit));
		objectPropertiesView.setModel(objectPropertiesModel);
		facetClassView.setModel(facetClassesModel);
		OWLObjectHierarchyProvider<OWLObjectProperty> opp = kit
				.getOWLModelManager().getOWLHierarchyManager()
				.getOWLObjectPropertyHierarchyProvider();
		for (OWLObjectProperty op : opp.getRoots()) {
			objectProperties.add(op);
			for (OWLObjectProperty opd : opp.getDescendants(op)) {
				objectProperties.add(opd);
			}
		}
		objectPropertiesModel.init();
		facetClassesModel.init();
		JScrollPane spobj = ComponentFactory
				.createScrollPane(objectPropertiesView);
		spobj.setBorder(ComponentFactory
				.createTitledBorder("Object property selection"));
		JScrollPane spobjf = ComponentFactory
				.createScrollPane(facetClassView);
		spobjf
				.setBorder(ComponentFactory
						.createTitledBorder("Facet selection"));
		this.add(spobj, BorderLayout.WEST);
		this.add(spobjf, BorderLayout.EAST);
		objectPropertiesView
				.addListSelectionListener(new ListSelectionListener() {
					@Override
                    @SuppressWarnings("unchecked")
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							if (objectPropertiesView
									.getSelectedIndex() > -1) {
								// then facets get loaded and status is OK
								OWLObjectProperty p = ((StaticListItem<OWLObjectProperty>) objectPropertiesView
										.getSelectedValue()).getItem();
								facetClasses
										.clear();
								facetClasses
										.addAll(getOWLClasses(p));
								facetClassesModel
										.init();
								notifyVerified();
							}
						}
					}
				});
	}

	protected Set<OWLClass> getOWLClasses(OWLObjectProperty op) {
		Set<OWLClass> ranges = new HashSet<OWLClass>();
        for (OWLClassExpression d : EntitySearcher.getRanges(op, kit
                .getOWLModelManager().getActiveOntology())) {
			if (d instanceof OWLClass) {
				ranges.add((OWLClass) d);
			}
		}
		return ranges;
	}

	public void clear() {
		objectPropertiesView.getSelectionModel().clearSelection();
		facetClassView.getSelectionModel().clearSelection();
		facetClasses.clear();
		facetClassesModel.init();
	}

	@SuppressWarnings("unchecked")
	public PropertyChainCell getCell() {
		if (objectPropertiesView.getSelectedIndex() > -1) {
			OWLObjectProperty p = ((StaticListItem<OWLObjectProperty>) objectPropertiesView
					.getSelectedValue()).getItem();
			OWLClass facet = null;
			if (facetClassView.getSelectedIndex() > -1) {
				facet = ((StaticListItem<OWLClass>) facetClassView
						.getSelectedValue()).getItem();
			}
			return new PropertyChainCell(p, facet);
		}
		return null;
	}

	protected void notifyVerified() {
		for (InputVerificationStatusChangedListener i : listeners) {
			i.verifiedStatusChanged(true);
		}
	}
}
