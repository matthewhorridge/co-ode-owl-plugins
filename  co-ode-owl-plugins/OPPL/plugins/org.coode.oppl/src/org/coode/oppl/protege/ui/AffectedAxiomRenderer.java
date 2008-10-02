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
package org.coode.oppl.protege.ui;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLAxiomChange;

/**
 * @author Luigi Iannone
 * 
 */
public class AffectedAxiomRenderer implements ListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6631014357184600262L;
	private OWLCellRenderer owlRenderer;

	public AffectedAxiomRenderer(OWLEditorKit owlEditorKit) {
		this.owlRenderer = new OWLCellRenderer(owlEditorKit);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		OWLAxiomChange change = ((ActionListItem) value).getAxiomChange();
		this.owlRenderer.setTransparent();
		this.owlRenderer.setHighlightKeywords(true);
		this.owlRenderer.setWrap(true);
		Component listCellRendererComponent = this.owlRenderer
				.getListCellRendererComponent(list, change.getAxiom(), index,
						isSelected, cellHasFocus);
		return listCellRendererComponent;
	}
}
