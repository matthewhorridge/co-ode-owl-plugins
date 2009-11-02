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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.coode.oppl.exceptions.InvalidVariableNameException;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.syntax.ParseException;
import org.coode.oppl.syntax.TokenMgrError;
import org.coode.oppl.utils.ProtegeParserFactory;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owl.model.OWLException;

/**
 * @author Luigi Iannone
 * 
 */
public class GeneratedVariableEditor extends AbstractVariableEditor {
	private final class VariableOWLExpressionChecker implements
			OWLExpressionChecker<GeneratedVariable> {
		public VariableOWLExpressionChecker() {
		}

		private GeneratedVariable lastEditedObject = null;

		public void check(String text) throws OWLExpressionParserException {
			this.lastEditedObject = null;
			ProtegeParserFactory
					.initParser(text, GeneratedVariableEditor.this.owlEditorKit
							.getModelManager());
			try {
				String variableName = GeneratedVariableEditor.this.variableNameExpressionEditor
						.createObject();
				Object selectedValue = GeneratedVariableEditor.this.jRadioButtonTypeMap
						.get(findSelectedButton());
				if (selectedValue instanceof VariableType) {
					GeneratedVariable variableDefinition = OPPLParser
							.opplFunction(
									variableName,
									(VariableType) selectedValue,
									GeneratedVariableEditor.this.constraintSystem);
					this.lastEditedObject = variableDefinition;
				} else {
					this.lastEditedObject = null;
					throw new OWLExpressionParserException(new Exception(
							"Undefined variable type"));
				}
			} catch (ParseException e) {
				this.lastEditedObject = null;
				throw new OWLExpressionParserException(e);
			} catch (TokenMgrError e) {
				this.lastEditedObject = null;
				throw new OWLExpressionParserException(e);
			} catch (OWLException e) {
				this.lastEditedObject = null;
				throw new OWLExpressionParserException(e);
			}
		}

		public GeneratedVariable createObject(String text)
				throws OWLExpressionParserException {
			this.check(text);
			return this.lastEditedObject;
		}
	}

	private class ChangeTypeActionListener implements ActionListener {
		public ChangeTypeActionListener() {
		}

		public void actionPerformed(ActionEvent e) {
			handleChange();
		}
	}

	private static final long serialVersionUID = 8899160597858126563L;
	private static Map<VariableType, String> typeLabelStringMap;
	protected final OWLEditorKit owlEditorKit;
	protected final ConstraintSystem constraintSystem;
	protected final Map<JRadioButton, VariableType> jRadioButtonTypeMap = new HashMap<JRadioButton, VariableType>();
	private final Map<VariableType, JRadioButton> typeJRadioButonMap = new HashMap<VariableType, JRadioButton>();
	private final ExpressionEditor<GeneratedVariable> opplFunctionEditor;
	static {
		typeLabelStringMap = new HashMap<VariableType, String>();
		EnumSet<VariableType> types = EnumSet.allOf(VariableType.class);
		for (VariableType variableType : types) {
			typeLabelStringMap.put(variableType, variableType.name());
		}
	}

	public GeneratedVariableEditor(OWLEditorKit owlEditorKit,
			ConstraintSystem constraintSystem) {
		setLayout(new BorderLayout());
		this.owlEditorKit = owlEditorKit;
		this.constraintSystem = constraintSystem;
		this.variableNameExpressionEditor = new ExpressionEditor<String>(
				owlEditorKit, new OWLExpressionChecker<String>() {
					private String variableName;

					public void check(String text)
							throws OWLExpressionParserException {
						this.variableName = null;
						if (text.matches("(\\?)?(\\w)+")) {
							this.variableName = text.startsWith("?") ? text
									: "?" + text;
						} else {
							throw new OWLExpressionParserException(
									new InvalidVariableNameException(text));
						}
					}

					public String createObject(String text)
							throws OWLExpressionParserException {
						this.check(text);
						return this.variableName;
					}
				});
		JPanel variableNamePanel = new JPanel(new BorderLayout());
		variableNamePanel.setBorder(ComponentFactory
				.createTitledBorder("Variable name:"));
		this.variableNameExpressionEditor
				.addStatusChangedListener(new InputVerificationStatusChangedListener() {
					public void verifiedStatusChanged(boolean newState) {
						if (newState) {
							GeneratedVariableEditor.this.handleChange();
						}
					}
				});
		variableNamePanel.add(this.variableNameExpressionEditor);
		this.add(variableNamePanel, BorderLayout.NORTH);
		this.variableTypeButtonGroup = new ButtonGroup();
		EnumSet<VariableType> types = EnumSet.allOf(VariableType.class);
		JPanel variableTypePanel = new JPanel(new GridLayout(0, types.size()));
		for (VariableType variableType : types) {
			JRadioButton typeRadioButton = new JRadioButton(typeLabelStringMap
					.get(variableType));
			typeRadioButton.addActionListener(new ChangeTypeActionListener());
			this.variableTypeButtonGroup.add(typeRadioButton);
			variableTypePanel.add(typeRadioButton);
			this.jRadioButtonTypeMap.put(typeRadioButton, variableType);
			this.typeJRadioButonMap.put(variableType, typeRadioButton);
		}
		JPanel scopeBorderPanel = new JPanel(new BorderLayout());
		scopeBorderPanel.setBorder(ComponentFactory
				.createTitledBorder("Variable Scope"));
		JPanel variableTypeAndScopePanel = new JPanel(new BorderLayout());
		variableTypeAndScopePanel.add(scopeBorderPanel, BorderLayout.NORTH);
		variableTypeAndScopePanel.add(variableTypePanel, BorderLayout.CENTER);
		variableTypeAndScopePanel.setBorder(ComponentFactory
				.createTitledBorder("Variable Type"));
		this.add(variableTypeAndScopePanel, BorderLayout.CENTER);
		this.opplFunctionEditor = new ExpressionEditor<GeneratedVariable>(
				this.owlEditorKit, new VariableOWLExpressionChecker());
		this.opplFunctionEditor
				.addStatusChangedListener(new InputVerificationStatusChangedListener() {
					public void verifiedStatusChanged(boolean newState) {
						GeneratedVariableEditor.this.handleChange();
					}
				});
		JPanel opplFunctionEditorPanel = new JPanel(new BorderLayout());
		opplFunctionEditorPanel.setBorder(ComponentFactory
				.createTitledBorder("OPPL Function: "));
		opplFunctionEditorPanel.add(ComponentFactory
				.createScrollPane(this.opplFunctionEditor));
		this.add(opplFunctionEditorPanel, BorderLayout.SOUTH);
	}

	protected void handleChange() {
		if (check()) {
			try {
				this.variableNameExpressionEditor.createObject();
				if (this.variable != null) {
					this.constraintSystem.removeVariable(this.variable);
				}
				this.variable = this.opplFunctionEditor.createObject();
				this.constraintSystem.importVariable(this.variable);
				notifyListeners();
			} catch (OWLExpressionParserException e) {
				notifyListeners();
				throw new RuntimeException(e);
			} catch (OWLException e) {
				notifyListeners();
				throw new RuntimeException(e);
			}
		} else {
			notifyListeners();
		}
	}

	@Override
	protected boolean check() {
		try {
			this.variableNameExpressionEditor.createObject();
			this.opplFunctionEditor.createObject();
			return true;
		} catch (OWLExpressionParserException e) {
			return false;
		} catch (OWLException e) {
			return false;
		}
	}

	@Override
	public void setVariable(GeneratedVariable<?> v) {
		clear();
		this.variableNameExpressionEditor.setText(v.getName());
		this.typeJRadioButonMap.get(v.getType()).setSelected(true);
		this.opplFunctionEditor.setText(v.getOPPLFunction());
	}

	@Override
	public void setVariable(Variable variable) {
		if (variable instanceof GeneratedVariable<?>) {
			setVariable((GeneratedVariable<?>) variable);
		}
		throw new RuntimeException(
				"Regular InputVariables not allowed on a GeneratedVariableEditor!");
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
}
