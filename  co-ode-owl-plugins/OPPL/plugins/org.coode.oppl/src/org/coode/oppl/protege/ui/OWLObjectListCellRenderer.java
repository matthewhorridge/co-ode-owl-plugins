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

class OWLObjectListCellRenderer implements ListCellRenderer {
	private final OWLCellRenderer owlCellRenderer;

	OWLObjectListCellRenderer(OWLEditorKit owlEditorKit) {
		this.owlCellRenderer = new OWLCellRenderer(owlEditorKit);
		this.owlCellRenderer.setWrap(true);
		this.owlCellRenderer.setHighlightKeywords(true);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component toReturn = this.owlCellRenderer.getListCellRendererComponent(
				list, value, index, isSelected, cellHasFocus);
		if (value instanceof OWLObjectListItem) {
			toReturn = this.owlCellRenderer.getListCellRendererComponent(list,
					((OWLObjectListItem) value).getOwlObject(), index,
					isSelected, cellHasFocus);
		}
		return toReturn;
	}
}