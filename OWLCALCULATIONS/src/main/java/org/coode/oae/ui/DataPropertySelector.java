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
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.search.EntitySearcher;

import uk.ac.manchester.mae.evaluation.PropertyChainCell;

public class DataPropertySelector extends JPanel implements VerifiedInputEditor {
	private static final long serialVersionUID = 4118824886564461973L;
	protected List<OWLDataProperty> dataProperties = new ArrayList<>();
	protected StaticListModel<OWLDataProperty> dataPropertiesModel = new StaticListModel<>(
			dataProperties, null);
	protected MList dataPropertiesView = new MList();
	protected Set<InputVerificationStatusChangedListener> listeners = new HashSet<>();
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

	public DataPropertySelector(OWLEditorKit k) {
		super(new BorderLayout());
		kit = k;
		dataPropertiesView
				.setCellRenderer(new RenderableObjectCellRenderer(kit));
		dataPropertiesView.setModel(dataPropertiesModel);
		OWLObjectHierarchyProvider<OWLDataProperty> dpp = kit
				.getOWLModelManager().getOWLHierarchyManager()
				.getOWLDataPropertyHierarchyProvider();
		for (OWLDataProperty dp : dpp.getRoots()) {
			dataProperties.add(dp);
			for (OWLDataProperty dpd : dpp.getDescendants(dp)) {
				dataProperties.add(dpd);
			}
		}
		dataPropertiesModel.init();
		dataPropertiesView
				.addListSelectionListener(new ListSelectionListener() {
					@Override
                    public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							if (dataPropertiesView
									.getSelectedIndex() > -1) {
								// then status is OK
								notifyVerified();
							}
						}
					}
				});
		JScrollPane spd = ComponentFactory
				.createScrollPane(dataPropertiesView);
		spd.setBorder(ComponentFactory
				.createTitledBorder("Data property selection"));
		this.add(spd, BorderLayout.CENTER);
	}

	protected Set<OWLClass> getOWLClasses(OWLObjectProperty op) {
		Set<OWLClass> ranges = new HashSet<>();
        for (OWLClassExpression d : EntitySearcher.getRanges(op, kit
                .getOWLModelManager().getActiveOntology())) {
			if (d instanceof OWLClass) {
				ranges.add((OWLClass) d);
			}
		}
		return ranges;
	}

	public void clear() {
		dataPropertiesView.getSelectionModel().clearSelection();
	}

	@SuppressWarnings("unchecked")
	public PropertyChainCell getCell() {
		if (dataPropertiesView.getSelectedIndex() > -1) {
			OWLDataProperty prop = ((StaticListItem<OWLDataProperty>) dataPropertiesView
					.getSelectedValue()).getItem();
			return new PropertyChainCell(prop, null);
		}
		return null;
	}

	protected void notifyVerified() {
		for (InputVerificationStatusChangedListener i : listeners) {
			i.verifiedStatusChanged(true);
		}
	}
}
