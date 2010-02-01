package org.coode.oppl.test;

import junit.framework.TestResult;
import junit.framework.TestSuite;

public class OPPLSuite extends TestSuite {
	public static void main(String[] args) throws Throwable {
		OPPLSuite s = new OPPLSuite();
		s.addTestSuite(ExhaustingTestCase.class);
		s.addTestSuite(SearchTest.class);
		s.addTestSuite(SpecificInferenceQueries.class);
		s.addTestSuite(SpecificQueriesTest.class);
		s.addTestSuite(TestQueries.class);
		s.run(new TestResult());
		System.out
				.println("OPPLSuite.main() now just waiting for you to capture the snapshot...");
		while (true) {
		}
	}
}
