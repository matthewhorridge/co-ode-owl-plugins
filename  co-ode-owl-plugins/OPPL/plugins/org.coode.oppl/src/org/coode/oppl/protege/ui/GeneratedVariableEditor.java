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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.syntax.ParseException;
import org.coode.oppl.syntax.TokenMgrError;
import org.coode.oppl.utils.ParserFactory;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.InvalidVariableNameException;
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
	private class ChangeTypeActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GeneratedVariableEditor.this.handleChange();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8899160597858126563L;
	private static Map<VariableType, String> typeLabelStringMap;
	private ExpressionEditor<String> variableNameExpressionEditor;
	private ButtonGroup variableTypeButtonGroup;
	private Variable variable = null;
	private final OWLEditorKit owlEditorKit;
	private final ConstraintSystem constraintSystem;
	private final Map<JRadioButton, VariableType> jRadioButtonTypeMap = new HashMap<JRadioButton, VariableType>();
	private final Map<VariableType, JRadioButton> typeJRadioButonMap = new HashMap<VariableType, JRadioButton>();
	private final Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
	private ExpressionEditor<Variable> opplFunctionEditor;
	static {
		typeLabelStringMap = new HashMap<VariableType, String>();
		EnumSet<VariableType> types = EnumSet.allOf(VariableType.class);
		for (VariableType variableType : types) {
			typeLabelStringMap.put(variableType, variableType.name());
		}
	}

	public GeneratedVariableEditor(OWLEditorKit owlEditorKit,
			ConstraintSystem constraintSystem) {
		this.setLayout(new BorderLayout());
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
		this.opplFunctionEditor = new ExpressionEditor<Variable>(
				this.owlEditorKit, new OWLExpressionChecker<Variable>() {
					private Variable lastEditedObject = null;

					public void check(String text)
							throws OWLExpressionParserException {
						this.lastEditedObject = null;
						ParserFactory.initParser(text,
								GeneratedVariableEditor.this.owlEditorKit
										.getModelManager());
						try {
							String variableName = GeneratedVariableEditor.this.variableNameExpressionEditor
									.createObject();
							Object selectedValue = GeneratedVariableEditor.this.jRadioButtonTypeMap
									.get(GeneratedVariableEditor.this
											.findSelectedButton());
							if (selectedValue instanceof VariableType) {
								Variable variableDefinition = OPPLParser
										.opplFunction(
												variableName,
												(VariableType) selectedValue,
												GeneratedVariableEditor.this.constraintSystem);
								this.lastEditedObject = variableDefinition;
							} else {
								this.lastEditedObject = null;
								throw new OWLExpressionParserException(
										new Exception("Undefined variable type"));
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

					public Variable createObject(String text)
							throws OWLExpressionParserException {
						this.check(text);
						return this.lastEditedObject;
					}
				});
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

	public void handleChange() {
		if (this.check()) {
			try {
				this.variableNameExpressionEditor.createObject();
				if (this.variable != null) {
					this.constraintSystem.removeVariable(this.variable);
				}
				this.variable = this.opplFunctionEditor.createObject();
				this.notifyListeners();
			} catch (OWLExpressionParserException e) {
				this.notifyListeners();
				throw new RuntimeException(e);
			} catch (OWLException e) {
				this.notifyListeners();
				throw new RuntimeException(e);
			}
		} else {
			this.notifyListeners();
		}
	}

	private void notifyListeners() {
		for (InputVerificationStatusChangedListener listener : this.listeners) {
			this.notifyListener(listener);
		}
	}

	private void notifyListener(InputVerificationStatusChangedListener listener) {
		listener.verifiedStatusChanged(this.check());
	}

	private boolean check() {
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
	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		listener.verifiedStatusChanged(this.check());
		this.listeners.add(listener);
	}

	@Override
	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public Variable getVariable() {
		return this.variable;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void clear() {
		this.variableNameExpressionEditor.setText("");
		Enumeration<AbstractButton> elements = this.variableTypeButtonGroup
				.getElements();
		while (elements.hasMoreElements()) {
			elements.nextElement().setSelected(false);
		}
	}

	private JRadioButton findSelectedButton() {
		JRadioButton button = null;
		Enumeration<AbstractButton> directions = this.variableTypeButtonGroup
				.getElements();
		boolean found = false;
		while (!found && directions.hasMoreElements()) {
			button = (JRadioButton) directions.nextElement();
			found = button.isSelected();
		}
		return found ? button : null;
	}

	@Override
	public void setVariable(Variable v) {
		this.clear();
		this.variableNameExpressionEditor.setText(v.getName());
		this.typeJRadioButonMap.get(v.getType()).setSelected(true);
		this.opplFunctionEditor.setText(((GeneratedVariable<?>) v)
				.getOPPLFunction());
	}
}
