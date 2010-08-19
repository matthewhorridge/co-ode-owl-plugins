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

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.lint.LintManager;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 13, 2008
 */
public class LintManagerFactory {
	private final OWLOntologyManager ontologyManager;
	private final OWLReasoner reasoner;
	private static LintManagerFactory instance = new LintManagerFactory(
			OWLManager.createOWLOntologyManager(), null);

	/**
	 * @param ontologyManager
	 * @param reasoner
	 */
	private LintManagerFactory(OWLOntologyManager ontologyManager, OWLReasoner reasoner) {
		assert ontologyManager != null;
		this.ontologyManager = ontologyManager;
		this.reasoner = reasoner;
	}

	public LintManager getLintManager() {
		return new LintManagerImpl(this.getOntologyManager(), this.getReasoner());
	}

	/**
	 * @return the ontologyManager
	 */
	public OWLOntologyManager getOntologyManager() {
		return this.ontologyManager;
	}

	/**
	 * @return the reasoner
	 */
	public OWLReasoner getReasoner() {
		return this.reasoner;
	}

	/**
	 * @return the instance
	 */
	public static LintManagerFactory getInstance() {
		return instance;
	}

	/**
	 * Default factory method
	 * 
	 * @param ontologyManager
	 *            The OWLOntologyManager. Cannot be {@code null}.
	 * @param reasoner
	 *            The reasoner
	 * @return the default LintManagerFactory
	 * @throws NullPointerException
	 *             if the input OWLOntologyManager is {@code null}.
	 */
	public static LintManagerFactory getInstance(OWLOntologyManager ontologyManager,
			OWLReasoner reasoner) {
		instance = new LintManagerFactory(ontologyManager, reasoner);
		return instance;
	}

	/**
	 * @param instance
	 *            the instance to set
	 */
	public static void setInstance(LintManagerFactory instance) {
		LintManagerFactory.instance = instance;
	}
}
