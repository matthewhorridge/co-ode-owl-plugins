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
package uk.ac.manchester.cs.owl.lint.test;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 14, 2008
 */
public abstract class LintTestCase extends TestCase {
	private OWLOntologyManager manager;
	protected Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	protected Lint<?> lint;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.manager = OWLManager.createOWLOntologyManager();
		URI physicalURI = URI.create(this.getPhysicalOntologyURI());
		OWLOntology ontology = this.manager.loadOntologyFromPhysicalURI(physicalURI);
		this.ontologies.add(ontology);
		this.lint = this.createLint();
	}

	protected abstract Lint<?> createLint();

	protected abstract String getPhysicalOntologyURI();

	public abstract void testDetected() throws Exception;

	/**
	 * @return the manager
	 */
	public OWLOntologyManager getManager() {
		return this.manager;
	}
}
