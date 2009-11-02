package org.coode.oppl.test;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;

import junit.framework.TestCase;
import net.sf.saxon.exslt.Math;

import org.coode.oppl.Executor;
import org.coode.oppl.OPPLScript;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.utils.ParserFactory;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;

public abstract class AbstractTestCase extends TestCase {
	private static final int TOLERANCE = 3;
	// ontology file for tests
	private String ontologyPhysicalURI = "file:///"
			+ new File("../OPPL2/ontologies/test.owl").getAbsolutePath();
	// ontology manager
	private OWLOntologyManager ontologyManager = OWLManager
			.createOWLOntologyManager();
	// ontology for tests
	private OWLOntology ontology1;
	// last generated exception; used to check that the exception being raised
	// is the one being expected
	private StringWriter lastStackTrace = new StringWriter();
	private PrintWriter p = new PrintWriter(this.lastStackTrace);
	// utility methods to set up the ontology, parse a script, check the
	// exceptions
	protected boolean longStackTrace = true;

	protected void execute(OPPLScript script) {
		try {
			Executor exec = new Executor(this.ontology1, this.ontologyManager,
					script.getConstraintSystem(), true);
			exec.visitActions(script.getActions());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		try {
			this.ontology1 = this.ontologyManager.loadOntology(URI
					.create(this.ontologyPhysicalURI));
			ParserFactory.initParser(";", this.ontology1, this.ontologyManager,
					null);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setUp() throws Exception {
		// reload the ontology for each test;
		// tests are independent of each other
		init();
	}

	@Override
	protected void tearDown() throws Exception {
		this.lastStackTrace = new StringWriter();
		this.p = new PrintWriter(this.lastStackTrace);
		super.tearDown();
	}

	private String popStackTrace() {
		String toReturn = this.lastStackTrace.toString();
		this.lastStackTrace = new StringWriter();
		this.p = new PrintWriter(this.lastStackTrace);
		return toReturn;
	}

	// private OWLReasoner initReasoner() throws OWLReasonerException {
	// OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
	// OWLReasoner reasoner = reasonerFactory
	// .createReasoner(this.ontologyManager);
	// reasoner.loadOntologies(Collections.singleton(this.ontology1));
	// reasoner.classify();
	// return reasoner;
	// }
	private OWLReasoner initDummyReasoner() throws OWLReasonerException {
		return null;
	}

	protected OPPLScript parse(String script) {
		try {
			ParserFactory.initParser(script, this.ontology1,
					this.ontologyManager, initDummyReasoner());
			return OPPLParser.Start();
		} catch (Exception e) {
			if (this.longStackTrace) {
				e.printStackTrace(this.p);
			} else {
				this.p.print(e.getMessage().replace("\n", "\t"));
			}
			this.p.flush();
		}
		return null;
	}

	protected void checkProperStackTrace(String expected, int expectedIndex) {
		String stackTrace = popStackTrace();
		if (stackTrace.contains(expected)) {
			int lineStart = stackTrace.indexOf(expected);
			int lineEnd = stackTrace.indexOf("\n", lineStart);
			String line = null;
			if (lineEnd > -1) {
				line = stackTrace.substring(lineStart, lineEnd);
			} else {
				line = stackTrace.substring(lineStart);
			}
			int index = line.indexOf(expected) + expected.length();
			int fullStop = line.indexOf(".", index);
			// if both are valid and the full stop is closer than ten chars,
			// then there is likely a column number to parse; otherwise, the
			// column number is probably missing (and unneeded)
			if (index > -1 && fullStop > -1 && fullStop - index < 10) {
				String columnIndex = line.substring(index, fullStop);
				try {
					int value = Integer.parseInt(columnIndex.trim());
					if (Math.abs(value - expectedIndex) < TOLERANCE) {
						// then the position is close enough
						System.out
								.println("ExhaustingTestCase.testParseDoubleVariableDeclaration() Correct stack trace");
					} else {
						System.out
								.println("ExhaustingTestCase The error type is correct but the column does not match the expected one. Expected error column: "
										+ expectedIndex);
						System.out.println(stackTrace);
					}
				} catch (NumberFormatException e) {
					System.out
							.println("ExhaustingTestCase.checkProperStackTrace() Could not parse a column number to verify the correctness of the stack trace:\nExpected error type: "
									+ expected
									+ "\nExpected error column: "
									+ expectedIndex);
					System.out.println(stackTrace);
				}
			} else {
				// there is no full stop after the expected string. No column
				// number info should be available
				System.out
						.println("ExhaustingTestCase.testParseDoubleVariableDeclaration() No column info checked; stack trace correct unless a column number was expected.");
			}
		} else {
			System.out
					.println("ExhaustingTestCase The stack trace does not correspond to the expected one! \nExpected error type: "
							+ expected
							+ "\nExpected error column: "
							+ expectedIndex);
			System.out.println(stackTrace);
		}
	}

	protected void reportUnexpectedStacktrace(String stackTrace) {
		// assertEquals(0, stackTrace.length());
		if (!stackTrace.isEmpty()) {
			System.out
					.println("ExhaustingTestCase There should not have been a stacktrace!");
			System.out.println(stackTrace);
		}
	}

	protected void expectedCorrect(OPPLScript result) {
		reportUnexpectedStacktrace(popStackTrace());
		assertNotNull(result);
	}
}
