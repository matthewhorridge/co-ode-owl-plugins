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
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintFactory;
import org.semanticweb.owl.lint.LintManager;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 13, 2008
 */
public final class LintManagerImpl implements LintManager {
	private final OWLOntologyManager ontologyManager;
	private final OWLReasoner reasoner;

	/**
	 * @param ontologyManager
	 * @param reasoner
	 */
	public LintManagerImpl(OWLOntologyManager ontologyManager, OWLReasoner reasoner) {
		assert ontologyManager != null;
		this.ontologyManager = ontologyManager;
		this.reasoner = reasoner;
	}

	/**
	 * @see org.semanticweb.owl.lint.LintManager#run(java.util.Set,
	 *      java.util.Set)
	 */
	public Set<LintReport<?>> run(Collection<? extends Lint<?>> lints,
			Collection<? extends OWLOntology> targets) throws LintException {
		Set<LintReport<?>> toReturn = new HashSet<LintReport<?>>();
		for (Lint<?> lint : lints) {
			LintReport<?> lintReport = lint.detected(targets);
			if (!lintReport.getAffectedOntologies().isEmpty()) {
				toReturn.add(lintReport);
			}
		}
		return toReturn;
	}

	public LintFactory getLintFactory() {
		return new LintFactoryImpl(this.ontologyManager, this.reasoner);
	}
}
