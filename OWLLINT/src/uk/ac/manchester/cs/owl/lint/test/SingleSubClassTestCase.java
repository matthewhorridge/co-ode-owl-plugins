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

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.lint.Lint;
import org.semanticweb.owlapi.lint.LintReport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.lint.LintManagerFactory;
import uk.ac.manchester.cs.owl.lint.examples.SingleSubClassLintPattern;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 13, 2008
 */
public class SingleSubClassTestCase extends LintTestCase {
	private static final String ONTOLOGY_URI = "http://www.cs.man.ac.uk/owlapi/lint/test/SingleSubClass.owl";

	@Override
	public void testDetected() throws Exception {
		LintReport<?> detected = this.lint.detected(this.getAllOntologies());
		assertTrue(
				"Lint does not detect anything and it really shoud not happen",
				!detected.getAffectedOntologies().isEmpty());
		OWLDataFactory factory = this.getManager().getOWLDataFactory();
		OWLClass clsA = factory.getOWLClass(IRI.create(ONTOLOGY_URI + "#A"));
		OWLClass clsB = factory.getOWLClass(IRI.create(ONTOLOGY_URI + "#B"));
		OWLClass clsC = factory.getOWLClass(IRI.create(ONTOLOGY_URI + "#C"));
		System.out.println(detected);
		OWLOntology ontology = this.getOntology(IRI.create(ONTOLOGY_URI));
		Set<?> affectedOWLObjects = detected.getAffectedOWLObjects(ontology);
		assertTrue("The result content is not the one expected",
				affectedOWLObjects.contains(clsA));
		assertFalse(detected.getAffectedOWLObjects(ontology).contains(clsB));
		assertFalse(detected.getAffectedOWLObjects(ontology).contains(clsC));
	}

	@Override
	protected Lint<?> createLint() {
		return LintManagerFactory.getInstance().getLintManager()
				.getLintFactory().createLint(
						Collections.singleton(new SingleSubClassLintPattern()));
	}

	@Override
	protected String getPhysicalOntologyIRIString() {
		URL resource = this.getClass().getResource("SingleSubClass.owl");
		String toReturn = null;
		if (resource == null) {
			fail("Could not load the ontology");
		} else {
			try {
				toReturn = resource.toURI().toString();
			} catch (URISyntaxException e) {
				fail(e.getMessage());
			}
		}
		return toReturn;
	}
}
