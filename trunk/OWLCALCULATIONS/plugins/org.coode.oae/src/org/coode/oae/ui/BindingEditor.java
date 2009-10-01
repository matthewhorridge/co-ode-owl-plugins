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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.coode.oae.ui.StaticListModel.StaticListItem;
import org.coode.oae.ui.VariableListModel.VariableListItem;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;

import uk.ac.manchester.mae.evaluation.BindingModel;
import uk.ac.manchester.mae.evaluation.PropertyChainCell;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;

public class BindingEditor extends JPanel implements ActionListener {
	private class MyMList extends MList {
		@Override
		protected void handleDelete() {
			BindingEditor.this.propertyChainCells
					.remove(((VariableListItem<PropertyChainCell>) getSelectedValue())
							.getItem());
			handlePropertyChainsUpdate();
		}
	}

	private BindingModel editedBindingModel;
	private GraphicalFormulaEditor editor;
	private OWLEditorKit kit;
	private JTextField identifier = new JTextField();
	private final MyMList propertychainsView = new MyMList();
	protected MList objectPropertiesView = new MList();
	protected MList dataPropertiesView = new MList();
	protected MList facetClassView = new MList();
	protected final List<PropertyChainCell> propertyChainCells = new ArrayList<PropertyChainCell>();
	protected List<OWLObjectProperty> objectProperties = new ArrayList<OWLObjectProperty>();
	protected List<OWLDataProperty> dataProperties = new ArrayList<OWLDataProperty>();
	protected List<OWLClass> facetClasses = new ArrayList<OWLClass>();
	protected final VariableListModel<PropertyChainCell> propertychainsModel = new VariableListModel<PropertyChainCell>(
			this.propertyChainCells, "Property chain elements", false);
	protected StaticListModel<OWLObjectProperty> objectPropertiesModel = new StaticListModel<OWLObjectProperty>(
			this.objectProperties, null);
	protected StaticListModel<OWLDataProperty> dataPropertiesModel = new StaticListModel<OWLDataProperty>(
			this.dataProperties, null);
	protected StaticListModel<OWLClass> facetClassesModel = new StaticListModel<OWLClass>(
			this.facetClasses, null);
	protected JButton commit = new JButton("Commit changes");
	protected JButton commitObjProp = new JButton("Add object property");
	protected JButton commitDataProp = new JButton("Add datatype property");
	protected JLabel errorsLabel = new JLabel();

	public boolean canAcceptObjectOrDatatypeProperty() {
		if (this.propertyChainCells.size() == 0) {
			return true;
		}
		PropertyChainCell c = this.propertyChainCells
				.get(this.propertyChainCells.size() - 1);
		return c.getProperty() instanceof OWLObjectProperty;
	}

	public boolean isCorrect() {
		if (this.propertyChainCells.size() == 0) {
			return false;
		}
		boolean correct = true;
		for (int i = 0; i < this.propertyChainCells.size() - 1; i++) {
			if (!(this.propertyChainCells.get(i).getProperty() instanceof OWLObjectProperty)) {
				correct = false;
			}
		}
		if (this.propertyChainCells.get(this.propertyChainCells.size() - 1)
				.getProperty() instanceof OWLObjectProperty) {
			correct = false;
		}
		return correct;
	}

	/**
	 * @param k
	 *            the {@link OWLEditorKit} to use
	 * @param model
	 *            the {@link BindingModel} to use; must not be null but can be
	 *            empty
	 */
	public BindingEditor(OWLEditorKit k, GraphicalFormulaEditor main) {
		super(new BorderLayout());
		setPreferredSize(new Dimension(400, 300));
		this.kit = k;
		this.editor = main;
		this.propertychainsView.setModel(this.propertychainsModel);
		this.propertychainsView
				.setCellRenderer(new RenderableObjectCellRenderer(this.kit));
		this.objectPropertiesView
				.setCellRenderer(new RenderableObjectCellRenderer(this.kit));
		this.dataPropertiesView
				.setCellRenderer(new RenderableObjectCellRenderer(this.kit));
		this.facetClassView.setCellRenderer(new RenderableObjectCellRenderer(
				this.kit));
		this.objectPropertiesView.setModel(this.objectPropertiesModel);
		this.dataPropertiesView.setModel(this.dataPropertiesModel);
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
		OWLObjectHierarchyProvider<OWLDataProperty> dpp = this.kit
				.getOWLModelManager().getOWLHierarchyManager()
				.getOWLDataPropertyHierarchyProvider();
		for (OWLDataProperty dp : dpp.getRoots()) {
			this.dataProperties.add(dp);
			for (OWLDataProperty dpd : dpp.getDescendants(dp)) {
				this.dataProperties.add(dpd);
			}
		}
		this.objectPropertiesModel.init();
		this.dataPropertiesModel.init();
		this.facetClassesModel.init();
		init();
	}

	public void setBindingModel(BindingModel model) {
		this.editedBindingModel = model;
		this.propertyChainCells.clear();
		handlePropertyChainsUpdate();
		if (this.editedBindingModel != null) {
			this.identifier.setText(this.editedBindingModel.getIdentifier());
			PropertyChainModel pcm = this.editedBindingModel
					.getPropertyChainModel();
			while (pcm != null) {
				this.propertyChainCells.add(pcm.getCell());
				pcm = pcm.getChild();
			}
			handlePropertyChainsUpdate();
		} else {
			this.identifier.setText("");
		}
	}

	protected void handlePropertyChainsUpdate() {
		this.propertychainsModel.init();
		boolean accept = canAcceptObjectOrDatatypeProperty();
		this.commitObjProp.setEnabled(accept);
		this.commitDataProp.setEnabled(accept);
		boolean correct = isCorrect();
		this.commit.setEnabled(correct);
		if (!correct || this.identifier.getText().length() == 0) {
			StringBuilder b = new StringBuilder();
			if (this.identifier.getText().length() == 0) {
				b.append(" An identifier must be specified; ");
			}
			if (!isCorrect()) {
				b
						.append(" The property chain must be made of object properties and end with a datatype property");
			}
			this.errorsLabel.setText(b.toString());
		} else {
			this.errorsLabel.setText("");
		}
	}

	public BindingModel getBindingModel() {
		return this.editedBindingModel;
	}

	public void init() {
		JPanel report = new JPanel(new BorderLayout());
		report.setPreferredSize(new Dimension(360, 130));
		JScrollPane jpreport = new JScrollPane(report);
		report.setBorder(new TitledBorder(
				"Binding identifier and property chain"));
		report.add(this.identifier, BorderLayout.NORTH);
		report.add(this.propertychainsView, BorderLayout.CENTER);
		this.add(jpreport, BorderLayout.NORTH);
		JPanel pickpanel = new JPanel(new GridLayout(1, 3));
		pickpanel.setPreferredSize(new Dimension(360, 100));
		JPanel objs = new JPanel(new BorderLayout());
		JScrollPane spobj = new JScrollPane(this.objectPropertiesView);
		spobj.setBorder(new TitledBorder("Object property selection"));
		JScrollPane spobjf = new JScrollPane(this.facetClassView);
		spobjf.setBorder(new TitledBorder("Facet selection"));
		objs.add(spobj, BorderLayout.CENTER);
		objs.add(this.commitObjProp, BorderLayout.SOUTH);
		JPanel datas = new JPanel(new BorderLayout());
		JScrollPane spd = new JScrollPane(this.dataPropertiesView);
		spd.setBorder(new TitledBorder("Data property selection"));
		datas.add(spd);
		datas.add(this.commitDataProp, BorderLayout.SOUTH);
		pickpanel.add(objs);
		pickpanel.add(spobjf);
		pickpanel.add(datas);
		this.add(pickpanel, BorderLayout.CENTER);
		this.commit.addActionListener(this);
		this.commitObjProp.addActionListener(this);
		this.commitDataProp.addActionListener(this);
		this.objectPropertiesView
				.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							if (BindingEditor.this.objectPropertiesView
									.getSelectedIndex() > -1) {
								OWLObjectProperty p = ((StaticListItem<OWLObjectProperty>) BindingEditor.this.objectPropertiesView
										.getSelectedValue()).getItem();
								BindingEditor.this.facetClasses.clear();
								BindingEditor.this.facetClasses
										.addAll(getOWLClasses(p));
								BindingEditor.this.facetClassesModel.init();
							}
						}
					}
				});
		JPanel commitBar = new JPanel(new GridLayout(2, 1));
		commitBar.add(this.errorsLabel);
		commitBar.add(this.commit);
		this.add(commitBar, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.commit)) {
			handleCommit();
		}
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
			}
		}
		if (e.getSource().equals(this.commitDataProp)) {
			if (this.dataPropertiesView.getSelectedIndex() > -1) {
				OWLDataProperty prop = ((StaticListItem<OWLDataProperty>) this.dataPropertiesView
						.getSelectedValue()).getItem();
				this.propertyChainCells.add(new PropertyChainCell(prop, null));
				handlePropertyChainsUpdate();
				this.dataPropertiesView.getSelectionModel().clearSelection();
			}
		}
	}

	private void handleCommit() {
		this.editedBindingModel = new BindingModel(this.identifier.getText(),
				new PropertyChainModel(this.propertyChainCells));
		this.editor.handleCommit();
	}

	private Set<OWLClass> getOWLClasses(OWLObjectProperty op) {
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
