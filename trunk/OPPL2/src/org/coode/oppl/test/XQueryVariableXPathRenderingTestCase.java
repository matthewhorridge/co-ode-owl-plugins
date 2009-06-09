/**
 * 
 */
package org.coode.oppl.test;

import java.net.URI;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.coode.oppl.OPPLScript;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.utils.ParserFactory;
import org.coode.oppl.utils.VariableXPathBuilder;
import org.coode.oppl.variablemansyntax.Variable;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLAxiom;
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
	private final String opplScript = "?x:CLASS, ?y:CLASS SELECT gender and ?x subClassOf gender, ?y subClassOf gender WHERE ?x!=?y BEGIN ADD ?x disjointWith ?y END;";
	private static OPPLScript statement;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		OWLOntology ontology = manager.loadOntology(MIKELS_FAMILY_ONTOLOGY);
		ParserFactory.initParser(this.opplScript, ontology, manager, null);
		statement = OPPLParser.Start();
	}

	public void testXPathRendering() {
		VariableXPathBuilder builder = new VariableXPathBuilder(statement
				.getConstraintSystem());
		List<OWLAxiom> axioms = statement.getQuery().getAxioms();
		for (OWLAxiom axiom : axioms) {
			Map<Variable, List<String>> allPaths = axiom.accept(builder);
			for (Variable v : allPaths.keySet()) {
				List<String> variablePaths = allPaths.get(v);
				System.out.println("Variable: " + v.getName());
				for (String string : variablePaths) {
					System.out.println(string);
				}
			}
		}
	}
}
