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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintReport;
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
public final class SimpleMatchBasedLintReport<O extends OWLObject> implements
		LintReport<O> {
	private Set<Match<O>> matches = new HashSet<Match<O>>();
	private final Lint<O> lint;

	/**
	 * Creates a new SimpleMatchBasedLintReport based on the result contained in
	 * another LintReport. This report will have as its Lint the one specified
	 * in the first input parameter. The one in the input LintReport will be
	 * ignored.
	 * 
	 * @param lint
	 *            The Lint of this SimpleMatchBasedLintReport. Cannot be {@code
	 *            null}.
	 * @param report
	 *            The LintReport from which the results will copied. Cannot be
	 *            {@code null}.
	 * @throws NullPointerException
	 *             when either input is {@code null}.
	 */
	public SimpleMatchBasedLintReport(Lint<O> lint, LintReport<O> report) {
		this(lint);
		if (report == null) {
			throw new NullPointerException("The report to copy cannot be null");
		}
		for (OWLOntology owlOntology : report.getAffectedOntologies()) {
			Set<O> affectedOWLObjects = report
					.getAffectedOWLObjects(owlOntology);
			for (O owlClass : affectedOWLObjects) {
				this.add(owlClass, owlOntology, report.getExplanation(owlClass,
						owlOntology));
			}
		}
	}

	public SimpleMatchBasedLintReport(Lint<O> lint) {
		this(lint, Collections.<Match<O>> emptySet());
	}

	/**
	 * @param lint
	 */
	public SimpleMatchBasedLintReport(Lint<O> lint, Set<Match<O>> matches) {
		if (lint == null) {
			throw new NullPointerException("The lint cannot be null");
		}
		if (matches == null) {
			throw new NullPointerException("The matches cannot be null");
		}
		this.lint = lint;
		this.matches.addAll(matches);
	}

	/**
	 * @see org.semanticweb.owl.lint.LintReport#getAffectedOntologies()
	 */
	public Set<OWLOntology> getAffectedOntologies() {
		Set<OWLOntology> toReturn = new HashSet<OWLOntology>();
		for (Match<O> match : this.matches) {
			toReturn.add(match.getOntology());
		}
		return toReturn;
	}

	/**
	 * 
	 * @see org.semanticweb.owl.lint.LintReport#getAffectedOWLObjects(org.semanticweb.owl.model.OWLOntology)
	 */
	public Set<O> getAffectedOWLObjects(OWLOntology ontology) {
		Set<O> toReturn = new HashSet<O>();
		for (Match<O> match : this.matches) {
			if (match.getOntology().equals(ontology)) {
				toReturn.add(match.getOWLObject());
			}
		}
		return toReturn;
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
	 * compute the union between the previously affected objects and the ones in
	 * the input {@link PatternReport}
	 * 
	 * @param patternReport
	 */
	public void addPatternReport(PatternReport<O> patternReport) {
		Set<OWLOntology> ontologies = patternReport.getAffectedOntologies();
		for (OWLOntology ontology : ontologies) {
			Set<O> affectedOWLObjects = patternReport
					.getAffectedOWLObjects(ontology);
			for (O object : affectedOWLObjects) {
				this.matches.add(new Match<O>(object, ontology));
			}
		}
	}

	@Override
	public String toString() {
		return this.matches.toString();
	}

	/**
	 * @return the lint
	 */
	public Lint<O> getLint() {
		return this.lint;
	}

	public void add(O object, OWLOntology affectedOntology) {
		this.matches.add(new Match<O>(object, affectedOntology));
	}

	public void add(O object, OWLOntology affectedOntology, String explanation) {
		this.matches.add(new Match<O>(object, affectedOntology, explanation));
	}

	public String getExplanation(OWLObject object, OWLOntology affectedOntology) {
		String toReturn = null;
		Iterator<Match<O>> it = this.matches.iterator();
		boolean found = false;
		Match<O> match = null;
		while (!found & it.hasNext()) {
			match = it.next();
			found = match.getOntology().equals(affectedOntology)
					&& match.getOWLObject().equals(object);
		}
		if (found) {
			toReturn = match.getExplanation();
		}
		return toReturn;
	}
}
