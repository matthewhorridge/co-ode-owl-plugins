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

import org.protege.editor.core.ui.list.MListItem;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiomChange;

/**
 * @author Luigi Iannone
 * 
 */
public class ActionListItem implements MListItem {
	protected OWLAxiomChange axiomChange;

	/**
	 * @param axiomChange
	 */
	public ActionListItem(OWLAxiomChange axiomChange) {
		this.axiomChange = axiomChange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protege.editor.core.ui.list.MListItem#getTooltip()
	 */
	public String getTooltip() {
		String addOrRemove = this.axiomChange instanceof AddAxiom ? "ADD "
				: "ROMOVE ";
		return addOrRemove + " the axiom from the active ontology";
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#handleDelete()
	 */
	public boolean handleDelete() {
		return false;
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#handleEdit()
	 */
	public void handleEdit() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#isDeleteable()
	 */
	public boolean isDeleteable() {
		return true;
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#isEditable()
	 */
	public boolean isEditable() {
		return false;
	}

	/**
	 * @return the axiomChange
	 */
	public OWLAxiomChange getAxiomChange() {
		return this.axiomChange;
	}
}
