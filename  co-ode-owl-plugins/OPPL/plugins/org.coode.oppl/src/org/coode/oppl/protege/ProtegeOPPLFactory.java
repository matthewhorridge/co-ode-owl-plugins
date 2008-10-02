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
package org.coode.oppl.protege;

import org.coode.oppl.OPPLAbstractFactory;
import org.coode.oppl.OPPLException;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.ProtegeScopeVariableChecker;
import org.coode.oppl.variablemansyntax.VariableScopeChecker;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.semanticweb.owl.expression.OWLEntityChecker;
import org.semanticweb.owl.model.OWLDataFactory;

/**
 * @author Luigi Iannone
 * 
 */
public class ProtegeOPPLFactory implements OPPLAbstractFactory {
	private OWLModelManager modelManager;
	private ConstraintSystem constraintSystem;
	private OWLDataFactory dataFacotry;
	private ProtegeScopeVariableChecker variableScopeVariableChecker = null;

	/**
	 * @param modelManager
	 * @param constraintSystem
	 * @param dataFactory
	 */
	public ProtegeOPPLFactory(OWLModelManager modelManager,
			ConstraintSystem constraintSystem, OWLDataFactory dataFactory) {
		this.modelManager = modelManager;
		this.constraintSystem = constraintSystem;
		this.dataFacotry = dataFactory;
	}

	/**
	 * @see org.coode.oppl.OPPLAbstractFactory#getOWLEntityChecker()
	 */
	public OWLEntityChecker getOWLEntityChecker() {
		return new ProtegeOWLEntityChecker(this.modelManager);
	}

	/**
	 * @see org.coode.oppl.OPPLAbstractFactory#getVariableScopeChecker()
	 */
	public VariableScopeChecker getVariableScopeChecker() throws OPPLException {
		if (this.variableScopeVariableChecker == null) {
			this.variableScopeVariableChecker = new ProtegeScopeVariableChecker(
					this.modelManager);
		}
		return this.variableScopeVariableChecker;
	}

	/**
	 * @see org.coode.oppl.OPPLAbstractFactory#getOWLEntityRenderer()
	 */
	public OWLEntityRenderer getOWLEntityRenderer() {
		return this.modelManager.getOWLEntityRenderer();
	}

	/**
	 * @see org.coode.oppl.OPPLAbstractFactory#getOWLEntityFactory()
	 */
	public OWLEntityFactory getOWLEntityFactory() {
		return this.modelManager.getOWLEntityFactory();
	}
}
