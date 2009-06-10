/**
 * 
 */
package org.coode.oppl.test;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
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
	private final String opplScript = "?x:CLASS, ?y:CLASS SELECT ?x subClassOf ?x and gender, ?y subClassOf gender WHERE ?x!=?y BEGIN ADD ?x disjointWith ?y END;";
	private static OPPLScript statement;
	private final static String NAMESPACE_DECLARATION = "declare namespace owl2xml = \"http://www.w3.org/2006/12/owl2-xml#\";";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		OWLOntology ontology = manager.loadOntology(MIKELS_FAMILY_ONTOLOGY);
		ParserFactory.initParser(this.opplScript, ontology, manager, null);
		statement = OPPLParser.Start();
	}

	public void testXPathRendering() {
		StringWriter writer = new StringWriter();
		writer.append(NAMESPACE_DECLARATION);
		writer.append("\n");
		VariableXPathBuilder builder = new VariableXPathBuilder(statement
				.getConstraintSystem());
		List<OWLAxiom> axioms = statement.getQuery().getAxioms();
		String axiomName;
		int i = 0;
		List<String> whereConditions = new ArrayList<String>();
		for (OWLAxiom axiom : axioms) {
			axiomName = "$axiom_" + ++i;
			writer.append("for ");
			writer.append(axiomName);
			writer.append(" in ");
			writer.append(this.getContext());
			String axiomQuery = axiom.accept(builder);
			writer.append(axiomQuery);
			writer.append("\n");
			Map<Variable, List<String>> allPaths = builder.getVariablePaths();
			for (Variable v : allPaths.keySet()) {
				List<String> variablePaths = allPaths.get(v);
				String variableReference = v.getName().replace('?', '$');
				boolean first = true;
				for (String string : variablePaths) {
					if (first) {
						writer.append("let ");
						writer.append(variableReference);
						writer.append(" = ");
						writer.append(string);
						writer.append("\n");
						first = false;
					} else {
						whereConditions.add(variableReference + " = " + string);
					}
				}
			}
			whereConditions.addAll(builder.getWhereConditions());
		}
		writer.append("\n");
		writer.append("WHERE\n");
		boolean first = true;
		for (String string : whereConditions) {
			String andString = first ? "" : " and \n";
			first = first ? false : first;
			writer.append(andString);
			writer.append(string);
		}
		System.out.println(writer.toString());
	}

	private String getContext() {
		return "doc(\"mikelsFamily.owl\")/";
	}
}
