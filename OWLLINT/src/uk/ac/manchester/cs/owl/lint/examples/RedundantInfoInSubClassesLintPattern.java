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

import uk.ac.manchester.cs.owl.lint.commons.OntologyWiseLintPattern;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Feb 13, 2008
 */
public class RedundantInfoInSubClassesLintPattern extends
		OntologyWiseLintPattern {
	/**
	 * @return the Set of OWLObject that have redundant information in their
	 *         subclasses
	 * @see org.semanticweb.owl.lint.LintPattern#matches(org.semanticweb.owl.model.OWLOntology)
	 */
	@Override
	public Set<OWLObject> matches(OWLOntology target) {
		Set<OWLObject> toReturn = new HashSet<OWLObject>();
		for (OWLClass owlClass : target.getReferencedClasses()) {
			boolean hasRedundancy = this.hasRedundantInfoInSubclasses(owlClass,
					target);
			if (hasRedundancy) {
				toReturn.add(owlClass);
			}
		}
		return toReturn;
	}

	private boolean hasRedundantInfoInSubclasses(OWLClass c, OWLOntology ont) {
		boolean toReturn = false;
		Set<OWLDescription> csubS = c.getSubClasses(ont);
		if (!csubS.isEmpty()) {
			Iterator<OWLDescription> csubSIT = csubS.iterator();
			OWLClass firstsubC = csubSIT.next().asOWLClass();
			HashSet<OWLDescription> firstsuperS = new HashSet<OWLDescription>(
					firstsubC.getSuperClasses(ont));
			firstsuperS.remove(c);
			while (csubSIT.hasNext()) {
				OWLClass csubC = csubSIT.next().asOWLClass();
				Set<OWLDescription> nextsuperS = csubC.asOWLClass()
						.getSuperClasses(ont);
				Iterator<OWLDescription> firstsuperSIT = firstsuperS.iterator();
				while (firstsuperSIT.hasNext() && !firstsuperS.isEmpty()) {
					OWLDescription firstsuperC = firstsuperSIT.next();
					if (!nextsuperS.contains(firstsuperC)) {
						firstsuperSIT.remove();
					}
				}
			}
			toReturn = !firstsuperS.isEmpty();
		}
		return toReturn;
	}
}
