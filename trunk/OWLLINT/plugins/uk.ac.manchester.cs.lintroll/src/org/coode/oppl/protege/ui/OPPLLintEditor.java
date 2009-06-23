/**
 * 
 */
package org.coode.oppl.protege.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.coode.oppl.OPPLScript;
import org.coode.oppl.lint.LintOPPLScriptValidator;
import org.coode.oppl.lint.OPPLLintScript;
import org.coode.oppl.lint.syntax.OPPLLintParser;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.VariableScope;
import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.OWLException;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLLintEditor extends
		AbstractOWLFrameSectionRowObjectEditor<OPPLLintScript> implements
		InputVerificationStatusChangedListener, VerifiedInputEditor {
	class VariableListCellRenderer extends DefaultListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6717057306871665492L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
			if (value instanceof VariableListItem) {
				OPPLScript script = OPPLLintEditor.this.editor.getOPPLScript();
				if (script != null) {
					Variable variable = ((VariableListItem) value)
							.getVariable();
					VariableScope variableScope = variable.getVariableScope();
					String variableScopeString = variableScope == null ? ""
							: "["
									+ new StringBuilder()
											.append(
													variableScope
															.getDirection())
											.append(" ")
											.append(
													OPPLLintEditor.this.owlEditorKit
															.getModelManager()
															.getRendering(
																	variableScope
																			.getScopingObject()))
											.append("] ").toString();
					label
							.setIcon(new ImageIcon(
									this
											.getClass()
											.getClassLoader()
											.getResource(
													variable instanceof GeneratedVariable ? "cog.png"
															: "user-icon.gif")));
					ConstraintSystem constraintSystem = script
							.getConstraintSystem();
					label.setText(constraintSystem.render(variable) + ":"
							+ variable.getType() + variableScopeString);
				} else {
					return super.getListCellRendererComponent(list, value,
							index, isSelected, cellHasFocus);
				}
			}
			return label;
		}
	}

	private OPPLEditor editor;
	private JPanel mainPane;
	private final OWLEditorKit owlEditorKit;
	private ExpressionEditor<String> lintNameEditor;
	private OPPLLintScript editedObject = null;
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
	private JComboBox returnValuesComboBox = new JComboBox();
	private JTextArea descriptionTextArea = new JTextArea();

	public OPPLLintEditor(OWLEditorKit editorKit) {
		this.owlEditorKit = editorKit;
		this.mainPane = new JPanel(new BorderLayout());
		this.lintNameEditor = new ExpressionEditor<String>(editorKit,
				new OWLExpressionChecker<String>() {
					private String lastCreatedObject = null;

					public void check(String text)
							throws OWLExpressionParserException {
						if (text.matches("\\S+")) {
							this.lastCreatedObject = text;
						} else {
							this.lastCreatedObject = null;
							Set<String> empty = Collections.emptySet();
							throw new OWLExpressionParserException(
									"name "
											+ text
											+ " does not match the grammar \\S+",
									0, text.length() + 1, false, false, false,
									false, false, empty);
						}
					}

					public String createObject(String text)
							throws OWLExpressionParserException {
						return this.lastCreatedObject;
					}
				});
		this.lintNameEditor
				.addStatusChangedListener(new InputVerificationStatusChangedListener() {
					public void verifiedStatusChanged(boolean newState) {
						OPPLLintEditor.this.handleChange();
					}
				});
		this.editor = new OPPLEditor(editorKit, new LintOPPLScriptValidator());
		this.editor.setPreferredSize(new Dimension(50, 200));
		this.returnValuesComboBox.setRenderer(new VariableListCellRenderer());
		this.removeKeyListeners();
		this.editor.addStatusChangedListener(this);
		this.returnValuesComboBox.setEnabled(false);
		this.descriptionTextArea.getDocument().addDocumentListener(
				new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						OPPLLintEditor.this.handleChange();
					}

					public void insertUpdate(DocumentEvent e) {
						OPPLLintEditor.this.handleChange();
					}

					public void removeUpdate(DocumentEvent e) {
						OPPLLintEditor.this.handleChange();
					}
				});
		JScrollPane editorPane = ComponentFactory.createScrollPane(this.editor);
		JPanel editorPanel = new JPanel(new BorderLayout());
		editorPanel.setBorder(ComponentFactory
				.createTitledBorder("OPPL Lint body: "));
		editorPanel.add(editorPane);
		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.setBorder(ComponentFactory.createTitledBorder("Name:"));
		namePanel.add(ComponentFactory.createScrollPane(this.lintNameEditor));
		this.descriptionTextArea.setPreferredSize(new Dimension(200, 200));
		JPanel descriptionPanel = new JPanel(new BorderLayout());
		descriptionPanel.add(ComponentFactory
				.createScrollPane(this.descriptionTextArea));
		descriptionPanel.setBorder(ComponentFactory
				.createTitledBorder("Description"));
		JPanel returnVariablePanel = new JPanel(new BorderLayout());
		returnVariablePanel.setBorder(ComponentFactory
				.createTitledBorder("Return Variable"));
		returnVariablePanel.add(ComponentFactory
				.createScrollPane(this.returnValuesComboBox));
		this.mainPane.add(namePanel, BorderLayout.NORTH);
		this.mainPane.add(editorPanel, BorderLayout.CENTER);
		this.mainPane.add(descriptionPanel, BorderLayout.EAST);
		this.mainPane.add(returnVariablePanel, BorderLayout.SOUTH);
		this.mainPane.setPreferredSize(new Dimension(600, 500));
	}

	public void clear() {
		this.editor.clear();
		this.lintNameEditor.setText("");
		this.returnValuesComboBox.setModel(new DefaultComboBoxModel());
	}

	public void dispose() {
		this.editor.removeStatusChangedListener(this);
	}

	public OPPLLintScript getEditedObject() {
		return this.editedObject;
	}

	public JComponent getEditorComponent() {
		return this.mainPane;
	}

	public void setOPPLLintScript(OPPLLintScript opplLintScript) {
		OPPLScript opplScript = opplLintScript.getOPPLScript();
		this.editor.setOPPLScript(opplScript);
		opplLintScript.getName();
		opplLintScript.getReturnVariable();
		this.lintNameEditor.setText(opplLintScript.getName());
	}

	/**
	 * 
	 */
	private void removeKeyListeners() {
		KeyListener[] keyListeners = this.editor.getKeyListeners();
		for (KeyListener keyListener : keyListeners) {
			this.lintNameEditor.removeKeyListener(keyListener);
		}
	}

	public void verifiedStatusChanged(boolean newState) {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		if (newState) {
			// Change the return variable list
			OPPLScript script = this.editor.getOPPLScript();
			assert script != null;
			List<Variable> variables = script.getVariables();
			for (Variable variable : variables) {
				model.addElement(new VariableListItem(variable,
						this.owlEditorKit, false, false));
			}
		}
		this.returnValuesComboBox.setModel(model);
		this.returnValuesComboBox.setEnabled(newState);
		this.handleChange();
	}

	private void handleChange() {
		boolean newState = this.check();
		if (newState) {
			try {
				this.editedObject = OPPLLintParser.getOPPLLintAbstractFactory()
						.buildOPPLLintScript(
								this.lintNameEditor.createObject(),
								this.editor.getOPPLScript(),
								((VariableListItem) this.returnValuesComboBox
										.getSelectedItem()).getVariable(),
								this.descriptionTextArea.getText());
			} catch (OWLException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		this.notifyListeners(newState);
	}

	private boolean check() {
		try {
			String name = this.lintNameEditor.createObject();
			OPPLScript script = this.editor.getOPPLScript();
			Object selectedItem = this.returnValuesComboBox.getSelectedItem();
			return name != null && script != null && selectedItem != null;
		} catch (OWLException e) {
			return false;
		}
	}

	private void notifyListeners(boolean newState) {
		for (InputVerificationStatusChangedListener l : this.listeners) {
			l.verifiedStatusChanged(newState);
		}
	}

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}
}
