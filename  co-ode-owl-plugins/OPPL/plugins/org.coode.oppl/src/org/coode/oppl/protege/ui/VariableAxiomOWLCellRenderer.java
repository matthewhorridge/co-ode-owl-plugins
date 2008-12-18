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

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLObject;

public class VariableAxiomOWLCellRenderer extends OWLCellRenderer {
	private VariableOWLObjectRenderer objectRenderer;
	private VariableOWLEntityRenderer entityRenderer;

	public VariableAxiomOWLCellRenderer(OWLEditorKit owlEditorKit,
			
			VariableOWLEntityRenderer renderer) {
		super(owlEditorKit);
		this.objectRenderer = new VariableOWLObjectRenderer(owlEditorKit.getModelManager());
		this.entityRenderer = renderer;
	}

	@Override
	protected String getRendering(Object object) {
		if (object instanceof OWLObject) {
			return this.objectRenderer.render((OWLObject) object,
					this.entityRenderer);
		}
		return super.getRendering(object);
	}
}