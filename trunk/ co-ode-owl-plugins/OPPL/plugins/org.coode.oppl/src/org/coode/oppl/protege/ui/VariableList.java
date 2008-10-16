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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.VariableScope;
import org.coode.oppl.variablemansyntax.VariableType;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.owl.OWLEditorKit;

/**
 * @author Luigi Iannone
 * 
 */
public class VariableList extends MList {
	class VariableListCellRenderer implements ListCellRenderer {
		private final DefaultListCellRenderer defaultCellRenderer = new DefaultListCellRenderer();

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (value instanceof VariableListItem) {
				Variable variable = ((VariableListItem) value).getVariable();
				VariableScope variableScope = variable.getVariableScope();
				String variableScopeString = variableScope == null ? "" : "["
						+ variableScope.getDirection()
						+ " "
						+ VariableList.this.owlEditorKit.getModelManager()
								.getRendering(variableScope.getScopingObject())
						+ "] ";
				return this.defaultCellRenderer.getListCellRendererComponent(
						list, variableScopeString + variable.getName(), index,
						isSelected, cellHasFocus);
			}
			return this.defaultCellRenderer.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6135780833694887712L;
	private final VariableListCellRenderer variableListCellRenderer = new VariableListCellRenderer();
	private final OWLEditorKit owlEditorKit;

	public VariableList(OWLEditorKit owlEditorKit) {
		this.owlEditorKit = owlEditorKit;
		this.setModel(new DefaultListModel());
		this.setCellRenderer(this.variableListCellRenderer);
	}

	@Override
	protected Border createListItemBorder(JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		Border border = super.createListItemBorder(list, value, index,
				isSelected, cellHasFocus);
		return BorderFactory.createCompoundBorder(border, new VariableBorder(
				((VariableListItem) value).getVariable()));
	}

	@Override
	protected void handleDelete() {
		super.handleDelete();
		DefaultListModel model = (DefaultListModel) this.getModel();
		Object selectedValue = this.getSelectedValue();
		if (selectedValue != null) {
			model.removeElement(selectedValue);
		}
	}

	private static class VariableBorder implements Border {
		private Variable variable;

		public VariableBorder(Variable axiomChange) {
			this.variable = axiomChange;
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(0, c.getFontMetrics(c.getFont()).getStringBounds(
					VariableType.OBJECTPROPERTY.toString(), c.getGraphics())
					.getBounds().width + 8, 0, 0);
		}

		public boolean isBorderOpaque() {
			return false;
		}

		String getString() {
			return this.variable.getType().toString();
		}

		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			Color oldColor = g.getColor();
			g.setColor(Color.DARK_GRAY);
			g.drawString(this.getString(), x + 4, y + 2
					+ g.getFontMetrics().getAscent()
					+ g.getFontMetrics().getLeading());
			g.setColor(oldColor);
		}
	}
}
