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
package uk.ac.manchester.mae;

import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 13, 2008
 */
public class ExceptionStrategy extends BuiltInConflictStrategy {
	static ExceptionStrategy theInstance = null;

	ExceptionStrategy() {
	}

	/**
	 * Raises an Exception
	 * 
	 * @see uk.ac.manchester.mae.ConflictStrategy#solve(org.semanticweb.owl.model.OWLIndividual,
	 *      org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom,
	 *      org.semanticweb.owl.model.OWLConstant, java.util.Set,
	 *      org.semanticweb.owl.model.OWLOntologyManager)
	 */
	public void solve(OWLIndividual individual,
			OWLDataPropertyAssertionAxiom oldAssertion, OWLConstant newValue,
			Set<OWLOntology> ontologies, OWLOntologyManager ontologyManager)
			throws OWLOntologyChangeException, ValueAlreadySetException {
		this.solve(individual, oldAssertion, newValue);
	}

	/**
	 * @param individual
	 * @param oldAssertion
	 * @param newValue
	 * @throws ValueAlreadySetException
	 */
	private void solve(OWLIndividual individual,
			OWLDataPropertyAssertionAxiom oldAssertion, OWLConstant newValue)
			throws ValueAlreadySetException {
		throw new ValueAlreadySetException("The value for the property "
				+ oldAssertion.getProperty().toString()
				+ " was already set for individual "
				+ individual.getURI().toString()
				+ " and there is a formula that computed a new value ("
				+ newValue.getLiteral() + ")");
	}

	public static ConflictStrategy getInstance() {
		theInstance = theInstance == null ? new ExceptionStrategy()
				: theInstance;
		return theInstance;
	}

	public void solve(OWLIndividual individual,
			OWLDataPropertyAssertionAxiom oldAssertion, OWLConstant newValue,
			OWLModelManager modelManager) throws OWLOntologyChangeException,
			ValueAlreadySetException {
		this.solve(individual, oldAssertion, newValue);
	}

	@Override
	public String toString() {
		return "EXCEPTION";
	}
}
