package org.coode.oae.ui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLObject;

public class BindingTreeCellRenderer implements TreeCellRenderer {
	protected DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer();
	protected OWLCellRenderer owlCellRenderer;
	protected OWLEditorKit owlEditorKit;

	public BindingTreeCellRenderer(OWLEditorKit owlEditorKit) {
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
		if (nodeUserObject instanceof OWLObject) {
			toReturn = this.owlCellRenderer.getTreeCellRendererComponent(tree,
					nodeUserObject, selected, expanded, leaf, row, hasFocus);
		}
		return toReturn;
	}
}
