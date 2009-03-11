package org.coode.oae.ui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLObjectProperty;

import uk.ac.manchester.mae.evaluation.PropertyChainModel;

public class StorageTreeCellRenderer implements TreeCellRenderer {
	protected DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer();
	protected OWLCellRenderer owlCellRenderer;
	protected OWLEditorKit owlEditorKit;

	public StorageTreeCellRenderer(OWLEditorKit owlEditorKit) {
		this.owlEditorKit = owlEditorKit;
		this.owlCellRenderer = new OWLCellRenderer(this.owlEditorKit);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Object nodeUserObject = ((DefaultMutableTreeNode) value)
				.getUserObject();
		Component toReturn = this.defaultTreeCellRenderer
				.getTreeCellRendererComponent(tree, value, selected, expanded,
						leaf, row, hasFocus);
		if (nodeUserObject instanceof PropertyChainModel) {
			PropertyChainModel propertyChainModel = (PropertyChainModel) nodeUserObject;
			String propertyRendering = this.owlEditorKit.getModelManager()
					.getRendering(propertyChainModel.getProperty());
			String facetRendering = propertyChainModel.getFacet() == null ? ""
					: "["
							+ this.owlEditorKit.getModelManager().getRendering(
									propertyChainModel.getFacet()) + "]";
			String iconName = propertyChainModel.getProperty() instanceof OWLObjectProperty ? "property.object.png"
					: "property.data.png";
			this.defaultTreeCellRenderer.setOpenIcon(new ImageIcon(this
					.getClass().getClassLoader().getResource(iconName)));
			this.defaultTreeCellRenderer.setClosedIcon(new ImageIcon(this
					.getClass().getClassLoader().getResource(iconName)));
			this.defaultTreeCellRenderer.setLeafIcon(new ImageIcon(this
					.getClass().getClassLoader().getResource(iconName)));
			toReturn = this.defaultTreeCellRenderer
					.getTreeCellRendererComponent(tree, propertyRendering
							+ facetRendering, selected, expanded, leaf, row,
							hasFocus);
			this.defaultTreeCellRenderer
					.setOpenIcon(this.defaultTreeCellRenderer
							.getDefaultOpenIcon());
			this.defaultTreeCellRenderer
					.setClosedIcon(this.defaultTreeCellRenderer
							.getDefaultClosedIcon());
			this.defaultTreeCellRenderer
					.setLeafIcon(this.defaultTreeCellRenderer
							.getDefaultLeafIcon());
		}
		if (((DefaultMutableTreeNode) value).isRoot()) {
			this.defaultTreeCellRenderer.setOpenIcon(new ImageIcon(
					StorageTreeCellRenderer.class.getClassLoader().getResource(
							"storage.png")));
			this.defaultTreeCellRenderer.setClosedIcon(new ImageIcon(
					StorageTreeCellRenderer.class.getClassLoader().getResource(
							"storage.png")));
			String message = "Store to";
			if (((DefaultMutableTreeNode) value).isLeaf()) {
				this.defaultTreeCellRenderer.setLeafIcon(new ImageIcon(
						StorageTreeCellRenderer.class.getClassLoader()
								.getResource("storage.png")));
				message = "No storage set";
			}
			toReturn = this.defaultTreeCellRenderer
					.getTreeCellRendererComponent(tree, message, selected,
							expanded, leaf, row, hasFocus);
			this.defaultTreeCellRenderer
					.setOpenIcon(this.defaultTreeCellRenderer
							.getDefaultOpenIcon());
			this.defaultTreeCellRenderer
					.setClosedIcon(this.defaultTreeCellRenderer
							.getDefaultClosedIcon());
		}
		return toReturn;
	}
}
