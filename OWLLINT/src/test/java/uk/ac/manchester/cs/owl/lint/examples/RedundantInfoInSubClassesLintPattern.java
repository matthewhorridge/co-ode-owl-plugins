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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import uk.ac.manchester.cs.owl.lint.commons.Match;
import uk.ac.manchester.cs.owl.lint.commons.OntologyWiseLintPattern;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 13, 2008
 */
public class RedundantInfoInSubClassesLintPattern extends
		OntologyWiseLintPattern<OWLClass> {
	/**
	 * @return the Set of OWLObject that have redundant information in their
	 *         subclasses
	 * @see org.semanticweb.owlapi.lint.LintPattern#matches(org.semanticweb.owl.model.OWLOntology)
	 */
	@Override
	public Set<Match<OWLClass>> matches(OWLOntology target) {
		Set<Match<OWLClass>> toReturn = new HashSet<>();
		for (OWLClass owlClass : target.getClassesInSignature()) {
			boolean hasRedundancy = hasRedundantInfoInSubclasses(owlClass,
					target);
			if (hasRedundancy) {
				toReturn
						.add(new Match<>(owlClass, target,
								"It contains redundant information about its superclasses"));
			}
		}
		return toReturn;
	}

	private boolean hasRedundantInfoInSubclasses(OWLClass c, OWLOntology ont) {
		boolean toReturn = false;
        Collection<OWLClassExpression> csubS = EntitySearcher.getSubClasses(c,
                ont);
		if (!csubS.isEmpty()) {
			Iterator<OWLClassExpression> csubSIT = csubS.iterator();
			OWLClass firstsubC = csubSIT.next().asOWLClass();
            Set<OWLClassExpression> firstsuperS = new HashSet<>(
                    EntitySearcher.getSuperClasses(firstsubC, ont));
			firstsuperS.remove(c);
			while (csubSIT.hasNext()) {
				OWLClass csubC = csubSIT.next().asOWLClass();
                Collection<OWLClassExpression> nextsuperS = EntitySearcher
                        .getSuperClasses(csubC.asOWLClass(), ont);
				Iterator<OWLClassExpression> firstsuperSIT = firstsuperS
						.iterator();
				while (firstsuperSIT.hasNext() && !firstsuperS.isEmpty()) {
					OWLClassExpression firstsuperC = firstsuperSIT.next();
					if (!nextsuperS.contains(firstsuperC)) {
						firstsuperSIT.remove();
					}
				}
			}
			toReturn = !firstsuperS.isEmpty();
		}
		return toReturn;
	}

	@Override
    public boolean isInferenceRequired() {
		return false;
	}
}
