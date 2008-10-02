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
package org.coode.oppl.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.coode.oppl.ChangeExtractor;
import org.coode.oppl.syntax.OPPLStart;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

import com.owldl.pellet.PelletReasonerFactory;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLTest extends TestCase {
	protected Set<OPPLTestStart> tests = new HashSet<OPPLTestStart>();
	private static OPPLTestParser parser = null;
	private static OWLOntologyManager ontologyManager = OWLManager
			.createOWLOntologyManager();
	private OWLOntology ontology;

	/**
	 * @param ontology
	 */
	public OPPLTest(OWLOntology ontology) {
		this.ontology = ontology;
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		File testFile = new File("tests.txt");
		this.loadTests(testFile);
	}

	private void loadTests(File testFile) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(testFile));
			String line;
			while ((line = in.readLine()) != null) {
				StringReader reader = new StringReader(line);
				this.initParser(reader);
				try {
					OPPLTestStart test = (OPPLTestStart) OPPLTestParser.Start();
					this.tests.add(test);
				} catch (ParseException e) {
					System.out
							.println("The following test did not parse succesfully, it will not be extecuted: "
									+ line);
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Load tests from file impossible: "
					+ e.getMessage());
		} catch (IOException e) {
			System.out.println("Error while reading test file:");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void initParser(StringReader reader) {
		ConstraintSystem cs = new ConstraintSystem(this.ontology,
				ontologyManager);
		OWLReasoner reasoner = this.getReasoner();
		if (parser == null) {
			parser = reasoner == null ? new OPPLTestParser(reader,
					ontologyManager, cs) : new OPPLTestParser(reader,
					ontologyManager, cs, reasoner);
		} else {
			if (reasoner == null) {
				OPPLTestParser.ReInit(this.ontology, reader, ontologyManager,
						cs);
			} else {
				OPPLTestParser.ReInit(this.ontology, reader, ontologyManager,
						cs, reasoner);
			}
		}
	}

	public void test() throws Exception {
		ChangeExtractor changeExtractor;
		for (OPPLTestStart test : this.tests) {
			OWLOntology ontology = test.getOWLOntology();
			changeExtractor = new ChangeExtractor(ontology, ontologyManager);
			OPPLStart statement = test.getStatement();
			statement.jjtAccept(changeExtractor, null);
			List<OWLAxiomChange> changes = changeExtractor.getChanges();
			Set<OWLAxiom> changeAxioms = new HashSet<OWLAxiom>();
			for (OWLAxiomChange axiomChange : changes) {
				OWLAxiom changeAxiom = axiomChange.getAxiom();
				assertTrue("One of the actually affected axioms" + changeAxiom
						+ " is not amongst the expected ones "
						+ test.getAffectedAxioms(), test.getAffectedAxioms()
						.contains(changeAxiom));
				changeAxioms.add(changeAxiom);
			}
			assertTrue(
					"One of the expected axioms is not contained amongst the actual ones",
					changeAxioms.containsAll(test.getAffectedAxioms()));
		}
	}

	protected OWLReasoner getReasoner() {
		PelletReasonerFactory pelletReasonerFactory = new PelletReasonerFactory();
		return pelletReasonerFactory.createReasoner(ontologyManager);
	}
}
