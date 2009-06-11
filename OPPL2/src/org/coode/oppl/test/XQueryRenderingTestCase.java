/**
 * 
 */
package org.coode.oppl.test;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import junit.framework.TestCase;
import net.sf.saxon.Configuration;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.xqj.SaxonXQDataSource;

import org.coode.oppl.OPPLScript;
import org.coode.oppl.rendering.xquery.XQueryRenderer;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.utils.ParserFactory;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Luigi Iannone
 * 
 */
public class XQueryRenderingTestCase extends TestCase {
	private final static OWLOntologyManager manager = OWLManager
			.createOWLOntologyManager();
	private final static URI MIKELS_FAMILY_ONTOLOGY = URI
			.create("http://www.cs.man.ac.uk/~iannonel/oppl/ontologies/mikelsFamily.owl");
	private final String opplScript = "?x:CLASS, ?y:CLASS SELECT ?x subClassOf gender, ?y subClassOf gender WHERE ?x != ?y BEGIN ADD ?x disjointWith ?y END;";
	private static OPPLScript statement;
	private final static String owlXMLFilePath = "/Users/luigi/Documents/OxygenXMLEditor/OWL/mikelsFamily.owl";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		OWLOntology ontology = manager.loadOntology(MIKELS_FAMILY_ONTOLOGY);
		ParserFactory.initParser(this.opplScript, ontology, manager, null);
		statement = OPPLParser.Start();
	}

	public void testXQueryRendering() {
		XQueryRenderer renderer = new XQueryRenderer(
				"doc(\"mikelsFamily.owl\")/");
		System.out.println(renderer.render(statement));
	}

	public void testXQueryExecution() {
		Configuration configuration = new Configuration();
		try {
			XQueryRenderer renderer = new XQueryRenderer("");
			String xQueryString = renderer.render(statement);
			System.out.println(xQueryString);
			File inputFile = new File(owlXMLFilePath);
			Document document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(inputFile);
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
		} catch (XQException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (SAXException e) {
			fail(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			fail(e.getMessage());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			fail(e.getMessage());
			e.printStackTrace();
		} catch (XPathException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}
}
