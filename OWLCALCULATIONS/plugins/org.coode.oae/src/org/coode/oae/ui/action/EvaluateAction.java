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
import org.semanticweb.owl.inference.OWLReasonerException;

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
	public void dispose() throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * @see org.protege.editor.core.plugin.ProtegePluginInstance#initialise()
	 */
	public void initialise() throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		ProtegeEvaluator protegeEvaluator = null;
		try {
			protegeEvaluator = new ProtegeEvaluator(this.getOWLModelManager());
			protegeEvaluator.evaluate(true);
			OWLCalculations.setLastEvaluationReport(protegeEvaluator
					.getReport());
			this.getOWLWorkspace().showResultsView(
					"org.coode.oae.EvaluationResults", true,
					Workspace.BOTTOM_RESULTS_VIEW);
			// this.showReport(protegeEvaluator.getReport());
		} catch (OWLReasonerException e) {
			JOptionPane
					.showMessageDialog(this.getWorkspace(),
							"Exception in creating the reasoner, impossible to evaluate the formulas");
			e.printStackTrace();
		} finally {
			if (protegeEvaluator != null) {
				protegeEvaluator.getReport();
			}
		}
	}
}
