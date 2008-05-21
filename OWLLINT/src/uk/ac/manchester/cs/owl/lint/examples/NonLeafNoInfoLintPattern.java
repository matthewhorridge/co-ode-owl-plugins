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
package uk.ac.manchester.cs.owl.lint.examples;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.lint.commons.OntologyWiseLintPattern;

/**
 * Matches with non-leaf subclasses that do not have any new assertions
 * 
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Feb 13, 2008
 */
public class NonLeafNoInfoLintPattern extends OntologyWiseLintPattern {
	public NonLeafNoInfoLintPattern(OWLOntologyManager ontologyManager) {
		super(ontologyManager);
	}

	@Override
	public Set<OWLObject> matches(OWLOntology target) {
		Set<OWLObject> toReturn = new HashSet<OWLObject>();
		for (OWLClass owlClass : target.getReferencedClasses()) {
			if (this.matches(owlClass, target)) {
				toReturn.add(owlClass);
			}
		}
		return toReturn;
	}

	private boolean matches(OWLClass owlClass, OWLOntology target) {
		boolean toReturn = false;
		// If this class is a leaf we just do not bother
		if (!owlClass.getSubClasses(target).isEmpty()) {
			Set<OWLDescription> superClasses = owlClass.getSuperClasses(target);
			Iterator<OWLDescription> it = superClasses.iterator();
			boolean found = false;
			OWLDescription anOWLDescription;
			while (!found && it.hasNext()) {
				anOWLDescription = it.next();
				found = anOWLDescription.isAnonymous();
			}
			if (found) {
				toReturn = false;
			} else {
				it = owlClass.getEquivalentClasses(target).iterator();
				while (!found && it.hasNext()) {
					anOWLDescription = it.next();
					found = anOWLDescription.isAnonymous();
				}
				if (found) {
					toReturn = false;
				} else {
					it = owlClass.getDisjointClasses(target).iterator();
					while (!found && it.hasNext()) {
						anOWLDescription = it.next();
						found = anOWLDescription.isAnonymous();
					}
					toReturn = !found;
				}
			}
		}
		return toReturn;
	}
}
