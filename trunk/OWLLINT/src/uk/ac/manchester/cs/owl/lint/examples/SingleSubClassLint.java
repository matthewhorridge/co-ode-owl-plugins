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
import java.util.Collections;
import java.util.Set;

import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintPattern;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.lint.LintVisitor;
import org.semanticweb.owl.lint.LintVisitorEx;
import org.semanticweb.owl.lint.PatternBasedLint;
import org.semanticweb.owl.lint.configuration.LintConfiguration;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.cs.owl.lint.LintManagerFactory;
import uk.ac.manchester.cs.owl.lint.commons.NonConfigurableLintConfiguration;
import uk.ac.manchester.cs.owl.lint.commons.SimpleMatchBasedLintReport;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 20, 2008
 */
public final class SingleSubClassLint implements PatternBasedLint<OWLClass> {
	private PatternBasedLint<OWLClass> instance;

	public SingleSubClassLint() {
		this.instance = LintManagerFactory.getInstance().getLintManager()
				.getLintFactory().createLint(
						Collections.singleton(new SingleSubClassLintPattern()));
	}

	public String getDescription() {
		return "This lint is detected when there is a class in the ontology with only one subclass";
	}

	public Set<LintPattern<OWLClass>> getPatterns() {
		return this.instance.getPatterns();
	}

	public LintReport<OWLClass> detected(
			Collection<? extends OWLOntology> targets) throws LintException {
		LintReport<OWLClass> detected = this.instance.detected(targets);
		SimpleMatchBasedLintReport<OWLClass> toReturn = new SimpleMatchBasedLintReport<OWLClass>(
				this, detected);
		return toReturn;
	}

	public String getName() {
		return this.instance.getName();
	}

	public LintConfiguration getLintConfiguration() {
		return NonConfigurableLintConfiguration.getInstance();
	}

	public void accept(LintVisitor visitor) {
		this.instance.accept(visitor);
	}

	public <P> P accept(LintVisitorEx<P> visitor) {
		return this.instance.accept(visitor);
	}
}
