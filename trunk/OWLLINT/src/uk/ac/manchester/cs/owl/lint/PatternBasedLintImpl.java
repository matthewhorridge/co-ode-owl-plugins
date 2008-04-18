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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintPattern;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.lint.PatternBasedLint;
import org.semanticweb.owl.lint.PatternReport;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Feb 12, 2008
 */
public class PatternBasedLintImpl implements PatternBasedLint {
	protected Set<LintPattern> patterns = new HashSet<LintPattern>();
	protected String name = null;

	protected PatternBasedLintImpl(LintPattern... lintPatterns) {
		for (LintPattern lintPattern : lintPatterns) {
			this.patterns.add(lintPattern);
		}
	}

	/**
	 * @see org.semanticweb.owl.lint.PatternBasedLint#getPatterns()
	 */
	public Set<LintPattern> getPatterns() {
		return this.patterns;
	}

	/**
	 * Detects if the conjunction of the patterns applies to any set of
	 * OWLObject elements in the input set of OWLOntology
	 * 
	 * 
	 * @return the {@link LintReport} after the execution of this {@link Lint}
	 *         on the input Set of OWLOntology
	 * @throws LintException
	 * @see org.semanticweb.owl.lint.Lint#detected(java.util.Set)
	 */
	public LintReport detected(Set<OWLOntology> targets) throws LintException {
		Iterator<LintPattern> it = this.patterns.iterator();
		LintPattern lintPattern;
		boolean unsatisfiablePatternConjunction = false;
		PatternReport patternMatches;
		LintReportImpl lintReport = new LintReportImpl(this);
		while (!unsatisfiablePatternConjunction && it.hasNext()) {
			lintPattern = it.next();
			patternMatches = lintPattern.matches(targets);
			lintReport.chainPatternReport(patternMatches);
			unsatisfiablePatternConjunction = lintReport
					.getAffectedOntologies().isEmpty();
		}
		return lintReport;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name == null ? this.toString() : this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		String toReturn = "(Generic Pattern-based Lint Description)\nThis Lint is detected if "
				+ "and only all its patterns are matched on the same OWLObject(s) "
				+ "\nThe patterns are: \n";
		for (LintPattern pattern : this.patterns) {
			toReturn += pattern.getClass().getSimpleName() + "\n";
		}
		return toReturn;
	}

	@Override
	public String toString() {
		String toReturn = PatternBasedLint.class.getSimpleName() + "(";
		int i = 1;
		for (LintPattern pattern : this.patterns) {
			toReturn += pattern.getClass().getSimpleName();
			if (i < this.patterns.size()) {
				toReturn += ",";
			}
			i++;
		}
		toReturn += ")";
		return toReturn;
	}
}
