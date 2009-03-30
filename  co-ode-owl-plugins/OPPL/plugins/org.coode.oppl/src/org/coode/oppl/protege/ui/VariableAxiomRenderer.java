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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public class VariableAxiomRenderer implements ListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6631014357184600262L;
	private OWLEditorKit owlEditorKit;
	private ConstraintSystem constraintSystem;
	VariableOWLEntityRenderer renderer;
	private final DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
	private final VariableAxiomOWLCellRenderer variableAxiomOWLCellRenderer;

	public VariableAxiomRenderer(OWLEditorKit owlEditorKit,
			ConstraintSystem constraintSystem) {
		this.owlEditorKit = owlEditorKit;
		this.constraintSystem = constraintSystem;
		// new VariableOWLObjectRenderer(owlEditorKit.getModelManager());
		this.renderer = new VariableOWLEntityRenderer(this.constraintSystem,
				this.owlEditorKit.getModelManager());
		this.variableAxiomOWLCellRenderer = new VariableAxiomOWLCellRenderer(
				owlEditorKit, constraintSystem);
		this.variableAxiomOWLCellRenderer.setWrap(true);
		this.variableAxiomOWLCellRenderer.setHighlightKeywords(true);
	}

	/**
	 * @param constraintSystem
	 *            the constraintSystem to set
	 */
	public void setConstraintSystem(ConstraintSystem constraintSystem) {
		this.constraintSystem = constraintSystem;
		this.renderer = new VariableOWLEntityRenderer(constraintSystem,
				this.owlEditorKit.getModelManager());
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof ActionListItem) {
			ActionListItem item = (ActionListItem) value;
			OWLAxiomChange axiomChange = item.getAxiomChange();
			OWLAxiom axiom = axiomChange.getAxiom();
			return this.variableAxiomOWLCellRenderer
					.getListCellRendererComponent(list, axiom, index,
							isSelected, cellHasFocus);
		}
		return this.defaultListCellRenderer.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
	}

	public String getRendering(OWLObject owlObject) {
		return this.variableAxiomOWLCellRenderer.getRendering(owlObject);
	}
}
