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

import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.lint.PatternReport;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.cs.owl.lint.examples.SingleSubClassLint;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Feb 18, 2008
 */
public class LintReportImpl implements LintReport {
	private Map<OWLOntology, Set<OWLObject>> matches = new HashMap<OWLOntology, Set<OWLObject>>();
	protected Lint lint;

	/**
	 * @param lint
	 */
	public LintReportImpl(Lint lint) {
		this.lint = lint;
	}

	/**
	 * @see org.semanticweb.owl.lint.LintReport#getAffectedOntologies()
	 */
	public Set<OWLOntology> getAffectedOntologies() {
		return this.matches.keySet();
	}

	/**
	 * 
	 * @see org.semanticweb.owl.lint.LintReport#getAffectedOWLObjects(org.semanticweb.owl.model.OWLOntology)
	 */
	public Set<OWLObject> getAffectedOWLObjects(OWLOntology ontology) {
		return this.matches.get(ontology);
	}

	/**
	 * @see org.semanticweb.owl.lint.LintReport#isAffected(org.semanticweb.owl.model.OWLOntology)
	 */
	public boolean isAffected(OWLOntology ontology) {
		return this.getAffectedOntologies().contains(ontology);
	}

	/**
	 * Adds the input {@link PatternReport} to this LintReport. Notice that for
	 * every ontology that is already in this LintReport this method will
	 * compute the intersection between the previously affected objects and the
	 * ones in the input {@link PatternReport}
	 * 
	 * @param patternReport
	 */
	public void chainPatternReport(PatternReport patternReport) {
		Set<OWLOntology> ontologies = patternReport.getAffectedOntologies();
		for (OWLOntology ontology : ontologies) {
			Set<OWLObject> alreadyAffectedObjects = this.matches.get(ontology);
			Set<OWLObject> affectedOWLObjects = patternReport
					.getAffectedOWLObjects(ontology);
			if (alreadyAffectedObjects == null) {
				alreadyAffectedObjects = new HashSet<OWLObject>(
						affectedOWLObjects);
			} else {
				alreadyAffectedObjects.retainAll(affectedOWLObjects);
			}
			this.matches.put(ontology, alreadyAffectedObjects);
		}
	}

	/**
	 * Adds the input {@link PatternReport} to this LintReport. Notice that for
	 * every ontology that is already in this LintReport this method will
	 * compute the union between the previously affected objects and the ones in
	 * the input {@link PatternReport}
	 * 
	 * @param patternReport
	 */
	public void addPatternReport(PatternReport patternReport) {
		Set<OWLOntology> ontologies = patternReport.getAffectedOntologies();
		for (OWLOntology ontology : ontologies) {
			Set<OWLObject> alreadyAffectedObjects = this.matches.get(ontology);
			Set<OWLObject> affectedOWLObjects = patternReport
					.getAffectedOWLObjects(ontology);
			if (alreadyAffectedObjects == null) {
				alreadyAffectedObjects = new HashSet<OWLObject>(
						affectedOWLObjects);
			} else {
				alreadyAffectedObjects.addAll(affectedOWLObjects);
			}
			this.matches.put(ontology, alreadyAffectedObjects);
		}
	}

	@Override
	public String toString() {
		return this.matches.toString();
	}

	/**
	 * @return the lint
	 */
	public Lint getLint() {
		return this.lint;
	}

	public void setLint(SingleSubClassLint lint) {
		this.lint = lint;
	}
}
