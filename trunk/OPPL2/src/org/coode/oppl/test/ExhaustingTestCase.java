package org.coode.oppl.test;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import net.sf.saxon.Configuration;
import net.sf.saxon.xqj.SaxonXQDataSource;

import org.coode.oppl.OPPLScript;
import org.coode.oppl.rendering.xquery.XQueryRenderer;
import org.w3c.dom.Document;

public class ExhaustingTestCase extends AbstractTestCase {
	public void testParseAllMissing() {
		// test contained in the static initialization; the script containing
		// only ";" must parse correctly
		OPPLScript result = parse(";");
		expectedCorrect(result);
	}

	public void testParseMissingVariableDeclaration() {
		OPPLScript result = parse("SELECT ASSERTED Asinara InstanceOf ContinentalIsland BEGIN ADD Asinara InstanceOf ContinentalIsland END;");
		expectedCorrect(result);
		execute(result);
		result = parse("SELECT ASSERTED Asinara InstanceOf ContinentalIsland BEGIN REMOVE Asinara InstanceOf ContinentalIsland END;");
		expectedCorrect(result);
		execute(result);
	}

	public void testParseMissingQuery() {
		OPPLScript result = parse("?island:INDIVIDUAL BEGIN ADD Asinara InstanceOf ContinentalIsland END;");
		expectedCorrect(result);
		execute(result);
		result = parse("?island:INDIVIDUAL BEGIN REMOVE Asinara InstanceOf ContinentalIsland END;");
		expectedCorrect(result);
		execute(result);
	}

	public void testParseMissingAction() {
		OPPLScript result = parse("?island:INDIVIDUAL SELECT ASSERTED Asinara InstanceOf ContinentalIsland;");
		expectedCorrect(result);
		execute(result);
	}

	public void testParseMissingVariableDeclarationQuery() {
		OPPLScript result = parse("BEGIN ADD Asinara InstanceOf ContinentalIsland END;");
		expectedCorrect(result);
		execute(result);
	}

	public void testParseMissingVariableDeclarationAction() {
		OPPLScript result = parse("SELECT ASSERTED Asinara InstanceOf ContinentalIsland;");
		expectedCorrect(result);
		execute(result);
	}

	public void testParseMissingQueryAction() {
		OPPLScript result = parse("?island:INDIVIDUAL;");
		expectedCorrect(result);
		execute(result);
	}

	public void testParseVariableDeclarationError() {
		OPPLScript result = parse("?island:INDIVIDUAL, ?otherIsland:INDIVIDUAL;");
		expectedCorrect(result);
		execute(result);
		String correctPortion = "?island:INDIVIDUAL, ?otherIsland:";
		String script = correctPortion + "INDIVIDU;";
		result = parse(script);
		assertNull(result);
		String expected = "Encountered \" <ENTITYNAMES> \"INDIVIDU \"\" at line 1, column ";
		checkProperStackTrace(expected, correctPortion.length());
		correctPortion = "?island:";
		script = correctPortion + "INDIVIDU, ?otherIsland:INDIVIDUAL;";
		result = parse(script);
		assertNull(result);
		checkProperStackTrace(expected, correctPortion.length());
		correctPortion = "?island:";
		script = correctPortion + "INDIVIDU, ?otherIsland:INDIVIDU;";
		result = parse(script);
		assertNull(result);
		checkProperStackTrace(expected, correctPortion.length());
	}

	public void testParseQueryError() {
		String correctPortion = "SELECT ASSERTED Asinara InstanceOf ContinentalIsland;";
		OPPLScript result = parse(correctPortion);
		expectedCorrect(result);
		correctPortion = "SELECT ASSERTED Asinara InstanceOf ";
		String script = correctPortion + "Continental;";
		result = parse(script);
		checkProperStackTrace("Encountered Continental at line 1 column ",
				correctPortion.length());
		assertNull(result);
		script = correctPortion + "?test;";
		result = parse(script);
		checkProperStackTrace("Encountered ?test at line 1 column ",
				correctPortion.length());
		assertNull(result);
		// TODO the error is generic, does not mention variables at all; needs
		// to
		// be more detailed
	}

	public void testParseActionsError() {
		OPPLScript result = parse("SELECT ASSERTED Asinara InstanceOf ContinentalIsland BEGIN ADD Asinara InstanceOf ContinentalIsland END;");
		expectedCorrect(result);
		execute(result);
		String correctPortion = "SELECT ASSERTED Asinara InstanceOf ContinentalIsland BEGIN ADD ";
		String script = correctPortion
				+ "Asin InstanceOf ContinentalIsland END;";
		result = parse(script);
		assertNull(result);
		checkProperStackTrace("Encountered Asin at line 1 column ",
				correctPortion.length());
	}

	public void testParseVariableDeclarationAdvanced() {
		OPPLScript result = parse("?island:INDIVIDUAL;");
		expectedCorrect(result);
		execute(result);
		result = parse("?island:INDIVIDUAL=create(\"TestIndividual\");");
		expectedCorrect(result);
		execute(result);
		result = parse("?island:INDIVIDUAL=create(\"TestIndividual\"+\"No2\");");
		expectedCorrect(result);
		execute(result);
		result = parse("?someClass:CLASS[subClassOf Country], ?island:CLASS=CreateIntersection(?someClass.VALUES);");
		expectedCorrect(result);
		execute(result);
		result = parse("?someClass:CLASS[subClassOf Country], ?island:CLASS=CreateUnion(?someClass.VALUES);");
		expectedCorrect(result);
		execute(result);
		result = parse("?island:INDIVIDUAL[instanceOf Island];");
		expectedCorrect(result);
		execute(result);
		result = parse("?island:CLASS[subClassOf Country];");
		expectedCorrect(result);
		execute(result);
		result = parse("?test:DATAPROPERTY[subPropertyOf hasHeight];");
		expectedCorrect(result);
		execute(result);
		result = parse("?test:DATAPROPERTY[superPropertyOf hasHeight];");
		expectedCorrect(result);
		execute(result);
		result = parse("?someClass:CLASS[subClassOf Thing], ?island:CLASS=CreateIntersection(?someClass.VALUES);");
		expectedCorrect(result);
		execute(result);
		result = parse("?someClass:CLASS[SuperClassOf Island];");
		expectedCorrect(result);
		execute(result);
		result = parse("?someIndividual:INDIVIDUAL[instanceOf Thing];");
		expectedCorrect(result);
		execute(result);
	}

	public void testParseVariableDeclarationAdvancedErrors() {
		String correctPortion = "?island:";
		String script = correctPortion + "INDIVIDUAL_;";
		OPPLScript result = parse(script);
		assertNull(result);
		// reportUnexpectedStacktrace(popStackTrace());
		checkProperStackTrace(
				"Encountered \" <ENTITYNAMES> \"INDIVIDUAL_ \"\" at line 1, column ",
				correctPortion.length());
		correctPortion = "?someClass:INDIVIDUAL[";
		result = parse(correctPortion
				+ "subClassOf Country], ?island:CLASS=CreateIntersection(?someClass.VALUES);");
		assertNull(result);
		checkProperStackTrace(
				"Type mismatch for variable ?someClass: type CLASS needed instead of the actual INDIVIDUAL",
				correctPortion.length());
		correctPortion = "?island:INDIVIDUAL=";
		script = correctPortion + "createe(\"TestIndividual\");";
		result = parse(script);
		assertNull(result);
		checkProperStackTrace("Encountered createe at line 1 column ",
				correctPortion.length());
		correctPortion = "?someClass:CLASS[subClassOf ";
		script = correctPortion
				+ "__Country], ?island:CLASS=CreateUnion(?someClass.VALUES);";
		result = parse(script);
		assertNull(result);
		checkProperStackTrace("Encountered __Country at line 1 column ",
				correctPortion.length());
		correctPortion = "?island:CLASS[subClassOf hasHeight";
		result = parse(correctPortion + "];");
		assertNull(result);
		checkProperStackTrace("Encountered <EOF> at line 1 column ",
				correctPortion.length());
		correctPortion = "?test:OBJECTPROPERTY[subPropertyOf ";
		result = parse(correctPortion + "hasHeight];");
		assertNull("hasHeight is a datatype property, should not be allowed",
				result);
		checkProperStackTrace("Encountered hasHeight at line 1 column ",
				correctPortion.length());
	}

	public void testParseWhereClauses() {
		OPPLScript result = parse("?island:INDIVIDUAL SELECT ASSERTED ?island InstanceOf ContinentalIsland WHERE ?island != Asinara;");
		expectedCorrect(result);
		execute(result);
		result = parse("?island:INDIVIDUAL SELECT ASSERTED ?island InstanceOf Island WHERE ?island IN {Asinara};");
		expectedCorrect(result);
		execute(result);
	}

	// public void testRegExp() {
	// OPPLScript result = parse("?island:CLASS=Match(\"[iI]sland\");");
	// expectedCorrect(result);
	// execute(result);
	// result = parse("?island:CLASS=Match(\"[iI]s*land\");");
	// expectedCorrect(result);
	// execute(result);
	// result = parse("?island:CLASS=Match(\"[iI]s**land\");");
	// assertNull("the reg expr is broken, should not be allowed", result);
	// checkProperStackTrace("Encountered [iI]s**land", 22);
	// }
	public void testRegExpConstraints() {
		String correct = "?island:CLASS SELECT ASSERTED ?island subClassOf Thing WHERE ?island Match";
		OPPLScript result = parse(correct + " \"Island\";");
		expectedCorrect(result);
		execute(result);
		result = parse(correct + " \"Is**land\";");
		assertNull("the reg expr is broken, should not be allowed", result);
		checkProperStackTrace("Encountered Is**land", correct.length());
	}

	public void testRobertsScripts1() {
		String script = "?island:INDIVIDUAL[instanceOf Island],\n"
				+ "?height:CONSTANT\n"
				+ "SELECT ASSERTED ?island hasHeight ?height\n" + "BEGIN\n"
				+ " 	REMOVE ?island hasHeight ?height,\n"
				+ " 	ADD ?island !hasMaximumHeight ?height\n" + "END;";
		OPPLScript result = parse(script);
		expectedCorrect(result);
		execute(result);
	}

	public void testXQueryExecution() {
		// OWLOntologyManager tempmanager =
		// OWLManager.createOWLOntologyManager();
		// URI MIKELS_FAMILY_ONTOLOGY = URI
		// .create("http://www.cs.man.ac.uk/~iannonel/oppl/ontologies/mikelsFamily.owl");
		OPPLScript statement;
		String owlXMLFilePath = "ontologies/mikelsFamily.owl";
		// OWLOntology ontology;
		try {
			// ontology = tempmanager.loadOntology(MIKELS_FAMILY_ONTOLOGY);
			// ParserFactory
			// .initParser(
			// "?x:CLASS, ?y:CLASS SELECT ?x subClassOf gender, ?y subClassOf gender WHERE ?x != ?y BEGIN ADD ?x disjointWith ?y END;",
			// ontology, tempmanager, null);
			// statement = OPPLParser.Start();
			statement = parse("?x:CLASS, ?y:CLASS SELECT ?x subClassOf Island, ?y subClassOf Island WHERE ?x != ?y BEGIN ADD ?x disjointWith ?y END;");
			XQueryRenderer testrenderer = new XQueryRenderer(
					"doc(\"mikelsFamily.owl\")/");
			System.out.println(testrenderer.render(statement));
			Configuration configuration = new Configuration();
			XQueryRenderer renderer = new XQueryRenderer("");
			String xQueryString = renderer.render(statement);
			// System.out.println(xQueryString);
			Document document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(new File(owlXMLFilePath));
			DOMSource source = new DOMSource(document);
			configuration.buildDocument(source);
			SaxonXQDataSource dataSource = new SaxonXQDataSource(configuration);
			XQConnection connection = dataSource.getConnection();
			XQPreparedExpression expression = connection
					.prepareExpression(xQueryString);
			expression.bindDocument(XQConstants.CONTEXT_ITEM, source,
					connection.createDocumentType());
			XQResultSequence result = expression.executeQuery();
			while (result.next()) {
				System.out.println(result.getItemAsString(null));
			}
			execute(statement);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
