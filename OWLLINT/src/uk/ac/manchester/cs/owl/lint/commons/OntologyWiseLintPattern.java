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
package uk.ac.manchester.cs.owl.lint.commons;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.lint.LintException;
import org.semanticweb.owlapi.lint.LintPattern;
import org.semanticweb.owlapi.lint.PatternReport;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 15, 2008
 */
public abstract class OntologyWiseLintPattern<O extends OWLObject> implements
		LintPattern<O> {
	/**
	 * Executes the match one OWLOntology at time
	 * 
	 * @see org.semanticweb.owlapi.lint.LintPattern#matches(Collection)
	 */
	public PatternReport<O> matches(Collection<? extends OWLOntology> targets)
			throws LintException {
		Set<Match<O>> set = new HashSet<Match<O>>(targets.size());
		for (OWLOntology ontology : targets) {
			set.addAll(this.matches(ontology));
		}
		return new SimpleMatchBasedPatternReport<O>(this, set);
	}

	/**
	 * @param ontology
	 * @return the Set of OWLObject elements matching in the input OWLOntology
	 * @throws LintException
	 */
	protected abstract Set<Match<O>> matches(OWLOntology ontology)
			throws LintException;
}
