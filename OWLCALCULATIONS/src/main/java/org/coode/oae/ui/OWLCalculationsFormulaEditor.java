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
import org.protege.editor.owl.ui.editor.AbstractOWLObjectEditor;

import uk.ac.manchester.mae.evaluation.FormulaModel;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 4, 2008
 */
public class OWLCalculationsFormulaEditor extends
        AbstractOWLObjectEditor<FormulaModel> implements
		VerifiedInputEditor, InputVerificationStatusChangedListener {
	private OWLEditorKit owlEditorKit;
	protected FormulaModel formulaModel;
	private final GraphicalEditor grapheditor;
	private JPanel mainPanel = new JPanel(new BorderLayout());
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<>();

	/**
	 * @param owlEditorKit
	 * @param formulaAnnotationURIs
	 */
	public OWLCalculationsFormulaEditor(OWLEditorKit owlEditorKit) {
		this.owlEditorKit = owlEditorKit;
		grapheditor = new GraphicalEditor(this.owlEditorKit);
		grapheditor.addStatusChangedListener(this);
		mainPanel.add(grapheditor);
	}

	@Override
    public void dispose() {
		listeners.clear();
	}

	@Override
    public FormulaModel getEditedObject() {
		return formulaModel;
	}

	@Override
	public Set<FormulaModel> getEditedObjects() {
		return Collections.singleton(getEditedObject());
	}

	public void setFormula(FormulaModel formulaModel) {
		grapheditor.initFormula(formulaModel);
	}

	@Override
    public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		listeners.add(listener);
		listener.verifiedStatusChanged(isValid());
	}

	@Override
    public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		listeners.remove(listener);
	}

	@Override
    public JComponent getEditorComponent() {
		return mainPanel;
	}

	public void handleChange(boolean state) {
		boolean valid = isValid() && state;
		for (InputVerificationStatusChangedListener listener : listeners) {
			listener.verifiedStatusChanged(valid);
		}
	}

	private boolean isValid() {
		return formulaModel != null ? formulaModel.getFormulaURI() != null
				: false;
	}

	@Override
    public void verifiedStatusChanged(boolean newState) {
		formulaModel = grapheditor.getFormulaModel();
		handleChange(newState);
	}

    @Override
    public String getEditorTypeName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean canEdit(Object object) {
        return object instanceof FormulaModel;
    }

    @Override
    public boolean setEditedObject(FormulaModel editedObject) {
        setFormula(editedObject);
        return true;
    }
}
