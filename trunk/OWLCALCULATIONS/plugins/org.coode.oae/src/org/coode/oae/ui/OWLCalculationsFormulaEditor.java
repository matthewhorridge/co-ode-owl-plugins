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
import java.awt.Dimension;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRowObjectEditor;

import uk.ac.manchester.mae.Constants;
import uk.ac.manchester.mae.evaluation.FormulaModel;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 4, 2008
 */
public class OWLCalculationsFormulaEditor extends
		AbstractOWLFrameSectionRowObjectEditor<FormulaModel> implements
		VerifiedInputEditor, InputVerificationStatusChangedListener {
	private static final Dimension PREFERRED_EDITOR_SIZE = new Dimension(800,
			600);
	private OWLEditorKit owlEditorKit;
	protected FormulaModel formulaModel;
	// private ExpressionEditor<FormulaModel> editor;
	private final GraphicalFormulaEditor grapheditor;
	protected JTextField nameTextField = new JTextField();
	private JPanel mainPanel = new JPanel(new BorderLayout());
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();

	/**
	 * @param owlEditorKit
	 * @param formulaAnnotationURIs
	 */
	public OWLCalculationsFormulaEditor(OWLEditorKit owlEditorKit) {
		this.owlEditorKit = owlEditorKit;
		this.grapheditor = new GraphicalFormulaEditor(this.owlEditorKit);
		init();
	}

	private void init() {
		// this.editor = new ExpressionEditor<FormulaModel>(this.owlEditorKit,
		// new OWLCalculationsExpressionChecker(this.owlEditorKit));
		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.setBorder(ComponentFactory.createTitledBorder("Name"));
		namePanel.add(this.nameTextField);
		// will go away when the completer will be removed from the expression
		// editor
		removeKeyListeners();
		// Now add the appropriate auto-completer
		// new FormulaCompleter(this.owlEditorKit, this.editor, this.editor
		// .getExpressionChecker());
		// this.editor.setPreferredSize(PREFERRED_EDITOR_SIZE);
		// this.editor.addStatusChangedListener(this);
		this.grapheditor.setPreferredSize(PREFERRED_EDITOR_SIZE);
		this.grapheditor.addStatusChangedListener(this);
		// JTabbedPane editorPanel = new JTabbedPane(JTabbedPane.TOP);
		// JScrollPane editorPane = new JScrollPane(this.editor);
		// JScrollPane grapheditorPane = new JScrollPane(this.grapheditor);
		// editorPanel.setBorder(ComponentFactory.createTitledBorder("Formula: "));
		// editorPanel.addTab("Graphical Editor", grapheditorPane);
		// editorPanel.addTab("Text Editor", editorPane);
		this.nameTextField.getDocument().addDocumentListener(
				new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						updateURI();
					}

					/**
					 * 
					 */
					private void updateURI() {
						URI anUri = null;
						try {
							anUri = new URI(
									Constants.FORMULA_NAMESPACE_URI_STRING
											+ OWLCalculationsFormulaEditor.this.nameTextField
													.getText());
						} catch (URISyntaxException e1) {
							// anUri = null;
						} finally {
							if (OWLCalculationsFormulaEditor.this.formulaModel != null) {
								OWLCalculationsFormulaEditor.this.formulaModel
										.setFormulaURI(anUri);
							}
							OWLCalculationsFormulaEditor.this.handleChange();
						}
					}

					public void insertUpdate(DocumentEvent e) {
						updateURI();
					}

					public void removeUpdate(DocumentEvent e) {
						updateURI();
					}
				});
		this.mainPanel.add(namePanel, BorderLayout.NORTH);
		this.mainPanel.add(this.grapheditor, BorderLayout.CENTER);
	}

	public void clear() {
		this.nameTextField.setText("");
		this.formulaModel = null;
		this.grapheditor.clear();
	}

	public void dispose() {
		this.listeners.clear();
	}

	public FormulaModel getEditedObject() {
		return this.formulaModel;
	}

	@Override
	public Set<FormulaModel> getEditedObjects() {
		return Collections.singleton(getEditedObject());
	}

	public void setFormula(FormulaModel formulaModel) {
		String localName = formulaModel.getFormulaURI().getFragment();
		if (localName == null) {
			localName = "";
		}
		this.nameTextField.setText(localName);
		this.grapheditor.initFormula(formulaModel);
		// this.editor.setText(this.grapheditor.getFormulaModel().render(
		// this.owlEditorKit.getModelManager()));
	}

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
		notifyListener(listener);
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	public JComponent getEditorComponent() {
		return this.mainPanel;
	}

	private void removeKeyListeners() {
		// KeyListener[] keyListeners = this.editor.getKeyListeners();
		// for (KeyListener keyListener : keyListeners) {
		// this.editor.removeKeyListener(keyListener);
		// }
	}

	/**
	 * @param listener
	 */
	private void notifyListener(InputVerificationStatusChangedListener listener) {
		try {
			boolean valid = this.formulaModel != null ? this.formulaModel
					.getFormulaURI() != null : false;
			listener.verifiedStatusChanged(valid);
		} catch (RuntimeException e) {
			listener.verifiedStatusChanged(false);
		}
	}

	public void handleChange() {
		for (InputVerificationStatusChangedListener listener : this.listeners) {
			notifyListener(listener);
		}
	}

	public void verifiedStatusChanged(boolean newState) {
		// this.formulaModel = null;
		if (newState) {
			// FormulaModel fm1 = null;
			// try {
			// fm1 = this.editor.createObject();
			// URI anUri = new URI(Constants.FORMULA_NAMESPACE_URI_STRING
			// + OWLCalculationsFormulaEditor.this.nameTextField
			// .getText());
			// if (fm1 != null) {
			// fm1.setFormulaURI(anUri);
			// }
			// } catch (OWLExpressionParserException e) {
			// e.printStackTrace();
			// } catch (OWLException e) {
			// e.printStackTrace();
			// } catch (URISyntaxException e) {
			// e.printStackTrace();
			// if (fm1 != null) {
			// fm1.setFormulaURI(null);
			// }
			// }
			FormulaModel fm2 = null;
			try {
				fm2 = this.grapheditor.getFormulaModel();
				this.formulaModel = fm2;
				URI anUri = new URI(Constants.FORMULA_NAMESPACE_URI_STRING
						+ OWLCalculationsFormulaEditor.this.nameTextField
								.getText());
				fm2.setFormulaURI(anUri);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				if (fm2 != null) {
					fm2.setFormulaURI(null);
				}
			}
			// if (fm2 != null) {
			// // synchronize with the other editor
			// this.editor.setText(fm2.render(this.owlEditorKit
			// .getModelManager()));
			// }
		}
		handleChange();
	}
}
