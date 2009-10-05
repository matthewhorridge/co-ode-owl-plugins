package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.coode.oae.ui.StaticListModel.StaticListItem;
import org.coode.oae.ui.VariableListModel.VariableListItem;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;

import uk.ac.manchester.mae.evaluation.BindingModel;
import uk.ac.manchester.mae.evaluation.PropertyChainCell;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;

public class StoreToEditor extends JPanel implements ActionListener {
	protected final class MyMList extends MList {
		@Override
		@SuppressWarnings("unchecked")
		protected void handleDelete() {
			StoreToEditor.this.propertyChainCells
					.remove(((VariableListItem<PropertyChainCell>) getSelectedValue())
							.getItem());
			handlePropertyChainsUpdate();
		}
	}

	private GraphicalFormulaEditor editor;
	private OWLEditorKit kit;
	private final MyMList propertychainsView = new MyMList();
	protected MList objectPropertiesView = new MList();
	protected MList facetClassView = new MList();
	protected final List<PropertyChainCell> propertyChainCells = new ArrayList<PropertyChainCell>();
	protected List<OWLObjectProperty> objectProperties = new ArrayList<OWLObjectProperty>();
	protected List<OWLClass> facetClasses = new ArrayList<OWLClass>();
	protected final VariableListModel<PropertyChainCell> propertychainsModel = new VariableListModel<PropertyChainCell>(
			this.propertyChainCells, "Property chain elements", false);
	protected StaticListModel<OWLObjectProperty> objectPropertiesModel = new StaticListModel<OWLObjectProperty>(
			this.objectProperties, null);
	protected StaticListModel<OWLClass> facetClassesModel = new StaticListModel<OWLClass>(
			this.facetClasses, null);
	protected JButton commitObjProp = new JButton("Add object property");
	protected PropertyChainModel pcm;

	public boolean isCorrect() {
		return this.propertyChainCells.size() == 0;
	}

	/**
	 * @param k
	 *            the {@link OWLEditorKit} to use
	 * @param model
	 *            the {@link BindingModel} to use; must not be null but can be
	 *            empty
	 */
	public StoreToEditor(OWLEditorKit k, GraphicalFormulaEditor main) {
		super(new BorderLayout());
		setPreferredSize(new Dimension(140, 300));
		this.kit = k;
		this.editor = main;
		this.propertychainsView.setModel(this.propertychainsModel);
		this.propertychainsView
				.setCellRenderer(new RenderableObjectCellRenderer(this.kit));
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
		JScrollPane jpreport = new JScrollPane(this.propertychainsView);
		jpreport
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpreport.setPreferredSize(new Dimension(130, 80));
		jpreport.setBorder(new TitledBorder(""));
		JPanel pickpanel = new JPanel(new GridLayout(3, 1));
		pickpanel.add(jpreport);
		JScrollPane spobj = new JScrollPane(this.objectPropertiesView);
		spobj.setBorder(new TitledBorder("Object property"));
		JScrollPane spobjf = new JScrollPane(this.facetClassView);
		spobjf.setBorder(new TitledBorder("Facet"));
		// spobj.setPreferredSize(new Dimension(130, 80));
		// spobjf.setPreferredSize(new Dimension(130, 50));
		pickpanel.add(spobj);
		pickpanel.add(spobjf);
		this.add(pickpanel, BorderLayout.CENTER);
		this.commitObjProp.addActionListener(this);
		this.objectPropertiesView
				.addListSelectionListener(new ListSelectionListener() {
					@SuppressWarnings("unchecked")
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							if (StoreToEditor.this.objectPropertiesView
									.getSelectedIndex() > -1) {
								OWLObjectProperty p = ((StaticListItem<OWLObjectProperty>) StoreToEditor.this.objectPropertiesView
										.getSelectedValue()).getItem();
								StoreToEditor.this.facetClasses.clear();
								StoreToEditor.this.facetClasses
										.addAll(getOWLClasses(p));
								StoreToEditor.this.facetClassesModel.init();
							}
						}
					}
				});
		this.add(this.commitObjProp, BorderLayout.SOUTH);
	}

	public void setStoreTo(PropertyChainModel _pcm) {
		this.pcm = _pcm;
		this.propertyChainCells.clear();
		PropertyChainModel temp = this.pcm;
		while (temp != null) {
			this.propertyChainCells.add(temp.getCell());
			temp = temp.getChild();
		}
		handlePropertyChainsUpdate();
	}

	protected void handlePropertyChainsUpdate() {
		this.propertychainsModel.init();
	}

	public PropertyChainModel getPropertyChainModel() {
		return this.pcm;
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.commitObjProp)) {
			if (this.objectPropertiesView.getSelectedIndex() > -1) {
				OWLObjectProperty p = ((StaticListItem<OWLObjectProperty>) this.objectPropertiesView
						.getSelectedValue()).getItem();
				OWLClass facet = null;
				if (this.facetClassView.getSelectedIndex() > -1) {
					facet = ((StaticListItem<OWLClass>) this.facetClassView
							.getSelectedValue()).getItem();
				}
				this.propertyChainCells.add(new PropertyChainCell(p, facet));
				handlePropertyChainsUpdate();
				this.objectPropertiesView.getSelectionModel().clearSelection();
				this.facetClassView.getSelectionModel().clearSelection();
				handleCommit();
			}
		}
	}

	private void handleCommit() {
		// TODO validation bits
		this.pcm = new PropertyChainModel(this.propertyChainCells);
		this.editor.handleStoreToCommit();
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
}
