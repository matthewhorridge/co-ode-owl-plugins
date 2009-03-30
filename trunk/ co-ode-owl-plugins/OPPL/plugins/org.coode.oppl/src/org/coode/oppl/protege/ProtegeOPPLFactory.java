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

import java.io.StringWriter;
import java.util.List;

import org.coode.oppl.OPPLAbstractFactory;
import org.coode.oppl.OPPLException;
import org.coode.oppl.OPPLQuery;
import org.coode.oppl.OPPLQueryImpl;
import org.coode.oppl.OPPLScript;
import org.coode.oppl.OPPLScriptImpl;
import org.coode.oppl.rendering.ManchesterSyntaxRenderer;
import org.coode.oppl.rendering.VariableOWLEntityRenderer;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.ProtegeScopeVariableChecker;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.VariableScopeChecker;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.semanticweb.owl.expression.OWLEntityChecker;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLDataFactory;

import uk.ac.manchester.cs.owl.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;

/**
 * @author Luigi Iannone
 * 
 */
public class ProtegeOPPLFactory implements OPPLAbstractFactory {
	private OWLModelManager modelManager;
	private ConstraintSystem constraintSystem;
	private ProtegeScopeVariableChecker variableScopeVariableChecker = null;

	/**
	 * @param modelManager
	 * @param constraintSystem
	 * @param dataFactory
	 */
	public ProtegeOPPLFactory(OWLModelManager modelManager) {
		this.modelManager = modelManager;
	}

	/**
	 * @see org.coode.oppl.OPPLAbstractFactory#getOWLEntityChecker()
	 */
	public OWLEntityChecker getOWLEntityChecker() {
		return new RenderingOWLEntityChecker(this.modelManager);
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
	public OWLEntityRenderer getOWLEntityRenderer(ConstraintSystem cs) {
		if (cs == null) {
			throw new NullPointerException(
					"The constraint system cannot be null");
		}
		OWLModelManagerEntityRenderer defaultEntityRenderer = this.modelManager
				.getOWLEntityRenderer();
		return new VariableOWLEntityRenderer(cs, defaultEntityRenderer);
	}

	/**
	 * @see org.coode.oppl.OPPLAbstractFactory#getOWLEntityFactory()
	 */
	public OWLEntityFactory getOWLEntityFactory() {
		return this.modelManager.getOWLEntityFactory();
	}

	public OPPLScript buildOPPLScript(ConstraintSystem constraintSystem,
			List<Variable> variables, OPPLQuery opplQuery,
			List<OWLAxiomChange> actions) {
		ProtegeOPPLScript toReturn = new ProtegeOPPLScript(new OPPLScriptImpl(
				constraintSystem, variables, opplQuery, actions),
				this.modelManager);
		return toReturn;
	}

	public OPPLQuery buildNewQuery(ConstraintSystem constraintSystem) {
		OPPLQuery opplQuery = new OPPLQueryImpl(constraintSystem);
		return new ProtegeOPPLQuery(opplQuery, this.modelManager);
	}

	public ManchesterOWLSyntaxObjectRenderer getOWLObjectRenderer(
			StringWriter writer) {
		ManchesterOWLSyntaxObjectRenderer renderer = new ManchesterOWLSyntaxObjectRenderer(
				writer);
		renderer
				.setShortFormProvider(new ProtegeSimpleVariableShortFormProvider(
						this.modelManager, this.getConstraintSystem()));
		return renderer;
	}

	public ConstraintSystem createConstraintSystem() {
		this.constraintSystem = new ConstraintSystem(this.modelManager
				.getActiveOntology(),
				this.modelManager.getOWLOntologyManager(), this.modelManager
						.getReasoner());
		return this.constraintSystem;
	}

	/**
	 * @return the constraintSystem
	 */
	private ConstraintSystem getConstraintSystem() {
		return this.constraintSystem == null ? this.createConstraintSystem()
				: this.constraintSystem;
	}

	/**
	 * @return the OWLDataFactory exposed by this ProtegeOPPLFactory's internal
	 *         OWLModelManager instance
	 * @see org.coode.oppl.OPPLAbstractFactory#getOWLDataFactory()
	 */
	public OWLDataFactory getOWLDataFactory() {
		return this.modelManager.getOWLDataFactory();
	}

	/**
	 * @see org.coode.oppl.OPPLAbstractFactory#getManchesterSyntaxRenderer()
	 */
	public ManchesterSyntaxRenderer getManchesterSyntaxRenderer(
			ConstraintSystem cs) {
		if (cs == null) {
			throw new NullPointerException(
					"The constraint system cannot be null");
		}
		return new ManchesterSyntaxRenderer(this.modelManager
				.getOWLOntologyManager(), this.getOWLEntityRenderer(cs));
	}
}
