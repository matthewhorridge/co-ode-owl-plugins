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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRowObjectEditor;

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
	private OWLEditorKit owlEditorKit;
	protected FormulaModel formulaModel;
	private final GraphicalEditor grapheditor;
	private JPanel mainPanel = new JPanel(new BorderLayout());
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();

	/**
	 * @param owlEditorKit
	 * @param formulaAnnotationURIs
	 */
	public OWLCalculationsFormulaEditor(OWLEditorKit owlEditorKit) {
		this.owlEditorKit = owlEditorKit;
		this.grapheditor = new GraphicalEditor(this.owlEditorKit);
		this.grapheditor.addStatusChangedListener(this);
		this.mainPanel.add(this.grapheditor);
	}

	public void clear() {
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
		this.grapheditor.initFormula(formulaModel);
	}

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
		listener.verifiedStatusChanged(isValid());
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	public JComponent getEditorComponent() {
		return this.mainPanel;
	}

	public void handleChange(boolean state) {
		boolean valid = isValid() && state;
		for (InputVerificationStatusChangedListener listener : this.listeners) {
			listener.verifiedStatusChanged(valid);
		}
	}

	private boolean isValid() {
		return this.formulaModel != null ? this.formulaModel.getFormulaURI() != null
				: false;
	}

	public void verifiedStatusChanged(boolean newState) {
		this.formulaModel = this.grapheditor.getFormulaModel();
		handleChange(newState);
	}
}
