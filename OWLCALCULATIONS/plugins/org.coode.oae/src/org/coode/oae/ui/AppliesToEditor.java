package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.coode.oae.ui.StaticListModel.StaticListItem;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owl.model.OWLClass;

import uk.ac.manchester.mae.evaluation.BindingModel;

public class AppliesToEditor extends JPanel implements ActionListener {
	private GraphicalFormulaEditor editor;
	private OWLEditorKit kit;
	protected MList facetClassView = new MList();
	protected List<OWLClass> facetClasses = new ArrayList<OWLClass>();
	protected StaticListModel<OWLClass> facetClassesModel = new StaticListModel<OWLClass>(
			this.facetClasses, null);
	protected OWLClass facet;
	protected JButton commit = new JButton("Select Applies to");

	/**
	 * @param k
	 *            the {@link OWLEditorKit} to use
	 * @param model
	 *            the {@link BindingModel} to use; must not be null but can be
	 *            empty
	 */
	public AppliesToEditor(OWLEditorKit k, GraphicalFormulaEditor main) {
		super(new BorderLayout());
		setPreferredSize(new Dimension(140, 120));
		this.kit = k;
		this.editor = main;
		this.facetClassView.setCellRenderer(new RenderableObjectCellRenderer(
				this.kit));
		OWLObjectHierarchyProvider<OWLClass> provider = this.kit
				.getModelManager().getOWLHierarchyManager()
				.getOWLClassHierarchyProvider();
		for (OWLClass c : provider.getRoots()) {
			this.facetClasses.add(c);
			this.facetClasses.addAll(provider.getDescendants(c));
		}
		this.facetClassView.setModel(this.facetClassesModel);
		this.facetClassesModel.init();
		init();
	}

	@SuppressWarnings("unchecked")
	public OWLClass getAppliesTo() {
		if (this.facetClassView.getSelectionModel().isSelectionEmpty()) {
			return null;
		}
		return ((StaticListItem<OWLClass>) this.facetClassView
				.getSelectedValue()).getItem();
	}

	public void init() {
		JScrollPane spobjf = new JScrollPane(this.facetClassView);
		// spobjf.setPreferredSize(new Dimension(140, 100));
		spobjf.setBorder(new TitledBorder(""));
		this.add(spobjf, BorderLayout.CENTER);
		this.commit.addActionListener(this);
		this.add(this.commit, BorderLayout.SOUTH);
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.commit)) {
			if (this.facetClassView.getSelectedIndex() > -1) {
				this.facet = ((StaticListItem<OWLClass>) this.facetClassView
						.getSelectedValue()).getItem();
			} else {
				this.facet = null;
			}
			handleCommit();
		}
	}

	private void handleCommit() {
		this.editor.handleAppliesToCommit();
		this.facetClassView.getSelectionModel().clearSelection();
	}
}
