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
package uk.ac.manchester.cs.owl.lint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.lint.LintPattern;
import org.semanticweb.owl.lint.PatternReport;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 18, 2008
 */
public class PatternReportImpl<O extends OWLObject> implements PatternReport<O> {
	protected Map<OWLOntology, Set<O>> matches = new HashMap<OWLOntology, Set<O>>();
	protected LintPattern<O> lintPattern;

	PatternReportImpl(LintPattern<O> pattern) {
		this.lintPattern = pattern;
	}

	/**
	 * @see org.semanticweb.owl.lint.PatternReport#getAffectedOWLObjects(org.semanticweb.owl.model.OWLOntology)
	 */
	public Set<O> getAffectedOWLObjects(OWLOntology ontology) {
		return this.matches.get(ontology);
	}

	/**
	 * @see org.semanticweb.owl.lint.PatternReport#getAffectedOntologies()
	 */
	public Set<OWLOntology> getAffectedOntologies() {
		return this.matches.keySet();
	}

	/**
	 * @see org.semanticweb.owl.lint.PatternReport#isAffected(org.semanticweb.owl.model.OWLOntology)
	 */
	public boolean isAffected(OWLOntology ontology) {
		return this.getAffectedOntologies().contains(ontology);
	}

	/**
	 * Adds the input Set of OWLObject elements to the pre-existing matching
	 * OWLObject elements for the input ontology
	 * 
	 * @param ontology
	 * @param objects
	 */
	public void add(OWLOntology ontology, Set<O> objects) {
		Set<O> previousMatches = this.matches.get(ontology);
		if (previousMatches == null) {
			previousMatches = new HashSet<O>();
		}
		previousMatches.addAll(objects);
		this.matches.put(ontology, previousMatches);
	}

	/**
	 * @see org.semanticweb.owl.lint.PatternReport#getLintPattern()
	 */
	public LintPattern<O> getLintPattern() {
		return this.lintPattern;
	}
}
