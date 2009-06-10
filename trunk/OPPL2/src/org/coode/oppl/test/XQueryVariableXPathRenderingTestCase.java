/**
 * 
 */
package org.coode.oppl.test;

import java.net.URI;

import junit.framework.TestCase;

import org.coode.oppl.OPPLScript;
import org.coode.oppl.rendering.xquery.XQueryRenderer;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.utils.ParserFactory;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 */
public class XQueryVariableXPathRenderingTestCase extends TestCase {
	private final static OWLOntologyManager manager = OWLManager
			.createOWLOntologyManager();
	private final static URI MIKELS_FAMILY_ONTOLOGY = URI
			.create("http://www.cs.man.ac.uk/~iannonel/oppl/ontologies/mikelsFamily.owl");
	private final String opplScript = "?x:CLASS, ?y:CLASS SELECT ?x subClassOf gender, ?y subClassOf gender WHERE ?x != ?y BEGIN ADD ?x disjointWith ?y END;";
	private static OPPLScript statement;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		OWLOntology ontology = manager.loadOntology(MIKELS_FAMILY_ONTOLOGY);
		ParserFactory.initParser(this.opplScript, ontology, manager, null);
		statement = OPPLParser.Start();
	}

	public void testXPathRendering() {
		XQueryRenderer renderer = new XQueryRenderer(
				" doc(\"mikelsFamily.owl\")/");
		System.out.println(renderer.render(statement));
	}
}
