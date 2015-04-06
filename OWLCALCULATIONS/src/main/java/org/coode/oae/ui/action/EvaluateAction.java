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
package org.coode.oae.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.coode.oae.ui.OWLCalculations;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.OWLRuntimeException;

import uk.ac.manchester.mae.ProtegeEvaluator;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 17, 2008
 */
public class EvaluateAction extends ProtegeOWLAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see org.protege.editor.core.plugin.ProtegePluginInstance#dispose()
	 */
	@Override
    public void dispose() {
	}

	/**
	 * @see org.protege.editor.core.plugin.ProtegePluginInstance#initialise()
	 */
	@Override
    public void initialise() {
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
    public void actionPerformed(ActionEvent arg0) {
		ProtegeEvaluator protegeEvaluator = null;
		try {
			protegeEvaluator = new ProtegeEvaluator(getOWLModelManager());
			protegeEvaluator.evaluate(true);
			OWLCalculations.setLastEvaluationReport(protegeEvaluator
					.getReport());
			getOWLWorkspace().showResultsView(
					"org.coode.oae.EvaluationResults", true,
					Workspace.BOTTOM_RESULTS_VIEW);
			// this.showReport(protegeEvaluator.getReport());
        } catch (OWLRuntimeException e) {
			JOptionPane
					.showMessageDialog(getWorkspace(),
							"Exception in creating the reasoner, impossible to evaluate the formulas");
			e.printStackTrace();
		} finally {
			if (protegeEvaluator != null) {
				protegeEvaluator.getReport();
			}
		}
	}
}
