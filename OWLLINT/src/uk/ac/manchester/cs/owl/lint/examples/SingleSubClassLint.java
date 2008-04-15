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

import java.util.Set;

import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintPattern;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.lint.PatternBasedLint;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.cs.owl.lint.LintManagerFactory;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Feb 20, 2008
 */
public class SingleSubClassLint implements PatternBasedLint {
	private PatternBasedLint instance;

	public SingleSubClassLint() {
		this.instance = (PatternBasedLint) LintManagerFactory.getLintManager()
				.getLintFactory().createLint(new SingleSubClassLintPattern());
	}

	public String getDescription() {
		return "This lint is detected when there is a class in the ontology with only one subclass";
	}

	public Set<LintPattern> getPatterns() {
		return this.instance.getPatterns();
	}

	public LintReport detected(Set<OWLOntology> targets) throws LintException {
		LintReport instanceReport = this.instance.detected(targets);
		instanceReport.setLint(this);
		return instanceReport;
	}

	public String getName() {
		return this.instance.getName();
	}

	public void setName(String name) {
		this.instance.setName(name);
	}
}
