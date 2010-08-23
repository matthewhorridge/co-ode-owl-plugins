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

import java.util.Collection;

import org.semanticweb.owlapi.lint.LintFactory;
import org.semanticweb.owlapi.lint.LintPattern;
import org.semanticweb.owlapi.lint.PatternBasedLint;
import org.semanticweb.owlapi.lint.PatternReport;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.owl.lint.commons.SimpleMatchBasedPatternReport;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 14, 2008
 */
public final class LintFactoryImpl implements LintFactory {
	private final OWLOntologyManager ontologyManager;
	private final OWLReasoner reasoner;

	/**
	 * @param ontologyManager
	 * @param reasoner
	 */
	public LintFactoryImpl(OWLOntologyManager ontologyManager,
			OWLReasoner reasoner) {
		assert ontologyManager != null;
		this.ontologyManager = ontologyManager;
		this.reasoner = reasoner;
	}

	/**
	 * Creates a {@link PatternBasedLintImpl} starting from a variable number of
	 * {@link LintPattern} elements
	 * 
	 * 
	 * @param lintPatterns
	 * @return a {@link PatternBasedLintImpl}
	 */
	public <O extends OWLObject> PatternBasedLint<O> createLint(
			Collection<? extends LintPattern<O>> lintPatterns) {
		return new PatternBasedLintImpl<O>(lintPatterns);
	}

	/**
	 * @see org.semanticweb.owlapi.lint.LintFactory#createPatternReport(org.semanticweb.owlapi.lint.LintPattern)
	 */
	public <O extends OWLObject> PatternReport<O> createPatternReport(
			LintPattern<O> pattern) {
		return new SimpleMatchBasedPatternReport<O>(pattern);
	}

	public OWLOntologyManager getOntologyManager() {
		return this.ontologyManager;
	}

	public OWLReasoner getOWLReasoner() {
		return this.reasoner;
	}
}
