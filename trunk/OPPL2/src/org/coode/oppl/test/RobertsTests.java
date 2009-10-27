package org.coode.oppl.test;

import org.coode.oppl.OPPLScript;

public class RobertsTests extends AbstractTestCase {
	public void testRobertsScripts2() {
		String script = "?region:INDIVIDUAL[instanceOf TierOneAdministrativeDivision],\n"
				+ "?neighbour:INDIVIDUAL[instanceOf TierOneAdministrativeDivision],\n"
				+ "?boundaryFragment:INDIVIDUAL = create(?region.RENDERING+\"_\"+ ?neighbour.RENDERING+ \"BoundaryFragment\")\n"
				+ "SELECT ASSERTED ?region directPartOf italy,\n"
				+ "ASSERTED ?region nextTo ?neighbour\n"
				+ "BEGIN\n"
				+ "REMOVE ?region nextTo ?neighbour,\n"
				+ "ADD ?region hasBoundary ?boundaryFragment\n" + "END;";
		OPPLScript result = parse(script);
		expectedCorrect(result);
		execute(result);
	}

	public void testRobertsScripts3() {
		String script = "?region:INDIVIDUAL[instanceOf TierTwoAdministrativeRegion],\n"
				+ "?neighbour:INDIVIDUAL[instanceOf TierTwoAdministrativeRegion],\n"
				+ "?boundaryFragment:INDIVIDUAL = create(?region.RENDERING+\"_\"+ ?neighbour.RENDERING+ \"BoundaryFragment\")\n"
				+ "SELECT  ?region partOf united_kingdom,\n"
				+ "ASSERTED ?region nextTo ?neighbour\n"
				+ "BEGIN\n"
				+ "REMOVE ?region nextTo ?neighbour,\n"
				+ "ADD ?region hasBoundary ?boundaryFragment\n" + "END;";
		OPPLScript result = parse(script);
		expectedCorrect(result);
		execute(result);
	}
}
