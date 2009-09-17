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
import org.semanticweb.owl.model.RemoveAxiom;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 13, 2008
 */
public class OverridingStrategy extends BuiltInConflictStrategy {
	static OverridingStrategy theInstance = null;

	OverridingStrategy() {
	}

	public static ConflictStrategy getInstance() {
		if (theInstance == null) {
			theInstance = new OverridingStrategy();
		}
		return theInstance;
	}

	public void solve(OWLIndividual individual,
			OWLDataPropertyAssertionAxiom oldAssertion, OWLConstant newValue,
			Set<OWLOntology> ontologies, OWLOntologyManager ontologyManager)
			throws OWLOntologyChangeException {
		for (OWLOntology ontology : ontologies) {
			RemoveAxiom removeAxiom = new RemoveAxiom(ontology, oldAssertion);
			ontologyManager.applyChange(removeAxiom);
		}
	}

	public void solve(OWLIndividual individual,
			OWLDataPropertyAssertionAxiom oldAssertion, OWLConstant newValue,
			OWLModelManager modelManager) throws OWLOntologyChangeException,
			ValueAlreadySetException {
		for (OWLOntology ontology : modelManager.getOntologies()) {
			RemoveAxiom removeAxiom = new RemoveAxiom(ontology, oldAssertion);
			modelManager.applyChange(removeAxiom);
		}
	}

	@Override
	public String toString() {
		return "OVERRIDING";
	}
}
