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

import java.util.Set;

import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintPattern;
import org.semanticweb.owl.lint.PatternReport;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.lint.LintManagerFactory;
import uk.ac.manchester.cs.owl.lint.PatternReportImpl;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Feb 15, 2008
 */
public abstract class OntologyWiseLintPattern implements LintPattern {
	protected PatternReportImpl patternReport = null;
	protected OWLOntologyManager ontologyManager;

	public OntologyWiseLintPattern(OWLOntologyManager ontologyManager) {
		this.ontologyManager = ontologyManager;
	}

	/**
	 * Executes the match one OWLOntology at time
	 * 
	 * @see org.semanticweb.owl.lint.LintPattern#matches(java.util.Set)
	 */
	public PatternReport matches(Set<OWLOntology> targets) throws LintException {
		this.patternReport = (PatternReportImpl) LintManagerFactory
				.getLintManager(this.ontologyManager).getLintFactory()
				.createPatternReport(this);
		for (OWLOntology ontology : targets) {
			Set<OWLObject> matches = this.matches(ontology);
			if (!matches.isEmpty()) {
				this.patternReport.add(ontology, matches);
			}
		}
		return this.patternReport;
	}

	/**
	 * @param ontology
	 * @return the Set of OWLObject elements matching in the input OWLOntology
	 * @throws LintException
	 */
	protected abstract Set<OWLObject> matches(OWLOntology ontology)
			throws LintException;
}
