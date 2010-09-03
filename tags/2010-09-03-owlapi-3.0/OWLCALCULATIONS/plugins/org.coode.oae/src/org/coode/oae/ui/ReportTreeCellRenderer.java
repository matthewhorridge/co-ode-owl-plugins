package org.coode.oae.ui;

import java.awt.Component;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLObject;

import uk.ac.manchester.mae.parser.MAEStart;

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
/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 18, 2008
 */
public class ReportTreeCellRenderer implements TreeCellRenderer {
	protected OWLEditorKit owlEditorKit;
	protected OWLCellRenderer owlCellRenderer;
	protected DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer();

	/**
	 * @param owlEditorKit
	 */
	public ReportTreeCellRenderer(OWLEditorKit owlEditorKit) {
		this.owlEditorKit = owlEditorKit;
		this.owlCellRenderer = new OWLCellRenderer(this.owlEditorKit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
	 *      java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
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
		} else if (nodeUserObject instanceof MAEStart) {
			Icon openIcon = this.defaultTreeCellRenderer.getOpenIcon();
			Icon closedIcon = this.defaultTreeCellRenderer.getClosedIcon();
			URL url = ReportTreeCellRenderer.class.getClassLoader()
					.getResource("icon_math.gif");
			Icon formulaIcon = new ImageIcon(url);
			this.defaultTreeCellRenderer.setOpenIcon(formulaIcon);
			this.defaultTreeCellRenderer.setClosedIcon(formulaIcon);
			ViewFormulaCellRederer render = new ViewFormulaCellRederer(false,
					this.owlEditorKit);
			((MAEStart) nodeUserObject).jjtAccept(render, null);
			String rendering = render.getFormulaString();
			this.defaultTreeCellRenderer.getTreeCellRendererComponent(tree,
					rendering, selected, expanded, leaf, row, hasFocus);
			this.defaultTreeCellRenderer.setOpenIcon(openIcon);
			this.defaultTreeCellRenderer.setClosedIcon(closedIcon);
		} else if (nodeUserObject instanceof Exception) {
			Icon icon = this.defaultTreeCellRenderer.getIcon();
			URL url = ReportTreeCellRenderer.class.getClassLoader()
					.getResource("exception.gif");
			Icon exceptionIcon = new ImageIcon(url);
			this.defaultTreeCellRenderer.setLeafIcon(exceptionIcon);
			String exceptionText = ((Exception) nodeUserObject).getMessage() == null ? ((Exception) nodeUserObject)
					.toString()
					: ((Exception) nodeUserObject).getMessage();
			this.defaultTreeCellRenderer.getTreeCellRendererComponent(tree,
					exceptionText, selected, expanded, leaf, row, hasFocus);
			this.defaultTreeCellRenderer.setLeafIcon(icon);
		}
		return toReturn;
	}
}
