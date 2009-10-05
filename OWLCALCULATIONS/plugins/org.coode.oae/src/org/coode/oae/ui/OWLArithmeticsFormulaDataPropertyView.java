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
package org.coode.oae.ui;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.protege.editor.owl.ui.view.AbstractOWLDataPropertyViewComponent;
import org.semanticweb.owl.model.OWLDataProperty;

/**
 * This view displays the formulas related to the selected class
 * 
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 2, 2008
 */
public class OWLArithmeticsFormulaDataPropertyView extends
		AbstractOWLDataPropertyViewComponent {
	/**
	 * 
	 */
	private OWLFrameList2<OWLDataProperty> list;

	/**
	 * @see org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent#disposeView()
	 */
	@Override
	public void disposeView() {
		this.list.dispose();
	}

	@Override
	protected OWLDataProperty updateView(OWLDataProperty property) {
		this.list.setRootObject(property);
		return property;
	}

	@Override
	public void initialiseView() throws Exception {
		this.list = new OWLFrameList2<OWLDataProperty>(getOWLEditorKit(),
				new OWLCalculationsFormulaDataPropertyFrame(getOWLEditorKit()));
		setLayout(new BorderLayout());
		JScrollPane sp = new JScrollPane(this.list);
		sp
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(sp);
		this.list.setCellRenderer(new ViewFormulaCellRederer(false,
				getOWLEditorKit()));
	}
}
