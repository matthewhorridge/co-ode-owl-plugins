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
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

import org.coode.oae.utils.ParserFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;

import uk.ac.manchester.mae.parser.ArithmeticsParser;
import uk.ac.manchester.mae.parser.ArithmeticsParserVisitor;
import uk.ac.manchester.mae.parser.MAEAdd;
import uk.ac.manchester.mae.parser.MAEBigSum;
import uk.ac.manchester.mae.parser.MAEBinding;
import uk.ac.manchester.mae.parser.MAEConflictStrategy;
import uk.ac.manchester.mae.parser.MAEIdentifier;
import uk.ac.manchester.mae.parser.MAEIntNode;
import uk.ac.manchester.mae.parser.MAEMult;
import uk.ac.manchester.mae.parser.MAEPower;
import uk.ac.manchester.mae.parser.MAEStart;
import uk.ac.manchester.mae.parser.MAEStoreTo;
import uk.ac.manchester.mae.parser.MAEmanSyntaxClassExpression;
import uk.ac.manchester.mae.parser.MAEpropertyChainExpression;
import uk.ac.manchester.mae.parser.ParseException;
import uk.ac.manchester.mae.parser.SimpleNode;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 28, 2008
 */
@SuppressWarnings("serial")
public class ViewFormulaCellRederer extends JPanel implements ListCellRenderer,
		ArithmeticsParserVisitor {
	protected OWLEditorKit owlEditorKit;
	protected DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
	protected String formulaString = "";
	private boolean isClassView;
	private JLabel formulaURILabel;
	private JTextArea formulaContentArea;
	private JLabel iconLabel;
	private static final Color LABEL_COLOR = Color.BLUE.darker();

	/**
	 * @param owlEditorKit
	 */
	public ViewFormulaCellRederer(boolean isClassView, OWLEditorKit owlEditorKit) {
		this.owlEditorKit = owlEditorKit;
		this.isClassView = isClassView;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
		formulaURILabel = new JLabel();
		formulaURILabel.setForeground(LABEL_COLOR);
		formulaContentArea = new JTextArea();
		formulaContentArea.setFont(new Font("lucida grande", Font.PLAIN,
				12));
		formulaContentArea.setLineWrap(true);
		formulaContentArea.setWrapStyleWord(true);
		this.add(formulaURILabel, BorderLayout.NORTH);
		JPanel contentPanel = new JPanel(new BorderLayout(3, 3));
		contentPanel.add(formulaContentArea, BorderLayout.CENTER);
		this.add(contentPanel, BorderLayout.SOUTH);
		formulaContentArea.setOpaque(false);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(2, 20, 2, 2));
		contentPanel.setOpaque(false);
		iconLabel = new JLabel();
		contentPanel.add(iconLabel, BorderLayout.WEST);
		iconLabel.setIcon(OWLIcons.getIcon("property.data.png"));
	}

	/**
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
	 *      java.lang.Object, int, boolean, boolean)
	 */
	@Override
    @SuppressWarnings("unchecked")
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component toReturn = defaultListCellRenderer
				.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
		if (value instanceof AbstractOWLFrameSectionRow) {
            AbstractOWLFrameSectionRow<Object, OWLAnnotationAssertionAxiom, ? extends Object> row = (AbstractOWLFrameSectionRow) value;
            OWLAnnotationAssertionAxiom axiom = row.getAxiom();
			OWLAnnotation annotation = axiom.getAnnotation();
            String localName = annotation.getProperty().getIRI().getFragment();
            String annotationValue = ((OWLLiteral) annotation.getValue())
					.getLiteral();
			ParserFactory.initParser(annotationValue, owlEditorKit
					.getModelManager());
			try {
				SimpleNode formula = ArithmeticsParser.Start();
				formula.jjtAccept(this, null);
				String rendering = formulaString;
				String propertyName = owlEditorKit.getModelManager()
						.getRendering(axiom.getSubject());
				if (isClassView) {
					formulaURILabel
							.setText(propertyName + " " + localName);
				} else {
					formulaURILabel.setText(localName);
				}
				formulaContentArea.setText(rendering);
				if (isSelected) {
					formulaContentArea.setForeground(list
							.getSelectionForeground());
					formulaURILabel.setForeground(list
							.getSelectionForeground());
				} else {
					formulaContentArea.setForeground(list.getForeground());
					formulaURILabel.setForeground(LABEL_COLOR);
				}
				iconLabel.setVisible(isClassView);
				toReturn = this;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	@Override
    public Object visit(MAEStart node, Object data) {
		formulaString = node.toString();
		return formulaString;
		// String toReturn = "";
		// Node child, previousChild = null;
		// for (int i = 0; i < node.jjtGetNumChildren(); i++) {
		// child = node.jjtGetChild(i);
		// if (previousChild != null && !(previousChild instanceof MAEBinding)
		// && child instanceof MAEBinding) {
		// toReturn += "{";
		// this.formulaString += "{";
		// } else if (previousChild != null
		// && previousChild instanceof MAEBinding
		// && child instanceof MAEBinding) {
		// toReturn += ",";
		// this.formulaString += ",";
		// } else if (!(child instanceof MAEBinding)
		// && previousChild instanceof MAEBinding) {
		// toReturn += "}->";
		// this.formulaString += "}->";
		// }
		// toReturn += child.jjtAccept(this, data);
		// previousChild = child;
		// }
		// return toReturn;
	}

	@Override
    public Object visit(MAEConflictStrategy node, Object data) {
		formulaString += node.toString();
		return node.toString();
	}

	@Override
    public Object visit(MAEStoreTo node, Object data) {
		String toReturn = " STORETO <";
		formulaString += toReturn;
		toReturn += node.childrenAccept(this, data);
		formulaString += ">";
		return toReturn + ">";
	}

	@Override
    public Object visit(MAEmanSyntaxClassExpression node, Object data) {
		// XXX
		String toReturn = " APPLESTO <" + node.getContent() + ">";
		formulaString += toReturn;
		return toReturn;
	}

	@Override
    public Object visit(MAEBinding node, Object data) {
		String toReturn = node.getIdentifier() + "=";
		formulaString += node.getIdentifier() + "=";
		toReturn += node.childrenAccept(this, data);
		return toReturn;
	}

	@Override
    public Object visit(MAEAdd node, Object data) {
		formulaString += node.toString();
		return node.toString();
	}

	@Override
    public Object visit(MAEMult node, Object data) {
		formulaString += node.toString();
		return node.toString();
	}

	@Override
    public Object visit(MAEPower node, Object data) {
		formulaString += node.toString();
		return node.toString();
	}

	@Override
    public Object visit(MAEIntNode node, Object data) {
		formulaString += node.toString();
		return node.toString();
	}

	@Override
    public Object visit(MAEIdentifier node, Object data) {
		formulaString += node.toString();
		return node.toString();
	}

	@Override
    public Object visit(MAEBigSum node, Object data) {
		formulaString += node.toString();
		return node.toString();
	}

	public String getFormulaString() {
		return formulaString;
	}

	@Override
    public Object visit(MAEpropertyChainExpression node, Object data) {
		formulaString += node.toString();
		return node.toString();
	}
}
