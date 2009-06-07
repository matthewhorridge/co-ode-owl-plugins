/**
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.coode.oppl.protege.ui.rendering;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public class BindingTreeRenderer extends OWLCellRenderer {
	private final DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer();

	public BindingTreeRenderer(OWLEditorKit owlEditorKit) {
		super(owlEditorKit);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component toReturn = super.getTreeCellRendererComponent(tree, value,
				selected, expanded, leaf, row, hasFocus);
		if (value instanceof DefaultMutableTreeNode) {
			Object userObject = ((DefaultMutableTreeNode) value)
					.getUserObject();
			if (userObject instanceof OWLObject) {
				toReturn = super.getTreeCellRendererComponent(tree, userObject,
						selected, expanded, leaf, row, hasFocus);
			} else {
				toReturn = this.defaultTreeCellRenderer
						.getTreeCellRendererComponent(tree, value, selected,
								expanded, leaf, row, hasFocus);
			}
		}
		return toReturn;
	}
}
