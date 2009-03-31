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

import java.util.HashSet;
import java.util.Set;

import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.oppl.variablemansyntax.VariableTypeVisitorEx;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 */
public class ValueExtractor implements VariableTypeVisitorEx<Set<OWLObject>> {
	protected Set<OWLOntology> ontologies;

	/**
	 * @param ontologies
	 */
	public ValueExtractor(Set<OWLOntology> ontologies) {
		this.ontologies = ontologies;
	}

	public Set<OWLObject> visit(VariableType type) {
		Set<OWLObject> toReturn = new HashSet<OWLObject>();
		if (type.equals(VariableType.CLASS)) {
			for (OWLOntology ontology : this.ontologies) {
				toReturn.addAll(ontology.getReferencedClasses());
			}
		} else if (type.equals(VariableType.OBJECTPROPERTY)) {
			for (OWLOntology ontology : this.ontologies) {
				toReturn.addAll(ontology.getReferencedObjectProperties());
			}
		} else if (type.equals(VariableType.DATAPROPERTY)) {
			for (OWLOntology ontology : this.ontologies) {
				toReturn.addAll(ontology.getReferencedDataProperties());
			}
		} else if (type.equals(VariableType.INDIVIDUAL)) {
			for (OWLOntology ontology : this.ontologies) {
				toReturn.addAll(ontology.getReferencedIndividuals());
			}
		} else if (type.equals(VariableType.CONSTANT)) {
			// TODO fix the constants
		}
		return toReturn;
	}
}
