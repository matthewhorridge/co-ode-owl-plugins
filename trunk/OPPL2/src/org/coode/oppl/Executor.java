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
package org.coode.oppl;

import java.util.ArrayList;
import java.util.List;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.Variable;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 */
public class Executor implements OPPLScriptVisitor {
	private final OWLOntologyManager ontologyManager;
	private final ChangeExtractor changeExtractor;

	/**
	 * @param ontologyManager
	 */
	public Executor(OWLOntology ontology, OWLOntologyManager ontologyManager,
			ConstraintSystem constraintSystem, boolean considerImportClosure) {
		this.ontologyManager = ontologyManager;
		this.changeExtractor = new ChangeExtractor(ontology, ontologyManager,
				constraintSystem, considerImportClosure);
	}

	public void visit(Variable v) {
	}

	public void visit(OPPLQuery q) {
	}

	public void visitActions(List<OWLAxiomChange> changes) {
		List<OWLAxiomChange> actions = new ArrayList<OWLAxiomChange>();
		this.changeExtractor.visitActions(changes, actions);
		try {
			this.ontologyManager.applyChanges(actions);
		} catch (OWLOntologyChangeException e) {
			e.printStackTrace();
		}
	}
}
