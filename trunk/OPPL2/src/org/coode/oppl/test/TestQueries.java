package org.coode.oppl.test;

import java.net.URI;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.coode.oppl.ChangeExtractor;
import org.coode.oppl.OPPLQuery;
import org.coode.oppl.OPPLScript;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.syntax.ParseException;
import org.coode.oppl.utils.ParserFactory;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.PartialOWLObjectInstantiator;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.oppl.variablemansyntax.bindingtree.Assignment;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.coode.oppl.variablemansyntax.bindingtree.LeafBrusher;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;

public class TestQueries extends TestCase {
	private enum DescriptionType {
		// visit(OWLObjectSelfRestriction)
		CLASS, OWLDATARESTRICTION, OWLDATAEXACTCARDINALITYRESTRICTION, OWLDATAMINCARDINALITYRESTRICTION, OWLDATAMAXCARDINALITYRESTRICTION, OWLDATASOMERESTRICTION, OWLDATAVALUERESTRICTION, OWLOBJECTALLRESTRICTION, OWLOBJECTCOMPLEMENTOF, OWLOBJECTEXACTCARDINALITYRESTRICTION, OWLOBJECTINTERSECTIONOF, OWLOBJECTMAXCARDINALITYRESTRCTION, OWLOBJECTMAXCARDINALITYRESTRICTION, OWLOBJECTMINCARDINALITYRESTRICTION, OWLOBJECTONEOF, OWLOBJECTSOMERESTRICTION, OWLOBJECTUNIONOF, OWLOBJECTVALUERESTRICTION
	}

	private final int valuesCount = 10;
	private final static URI TEST_NS = URI
			.create("http://www.co-ode.org/opp/test#");

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	private void buildOntologyForQuery(OPPLScript opplScript,
			OWLOntology testOntology, OWLOntologyManager manager) {
		List<Variable> inputVariables = opplScript.getInputVariables();
		for (Variable variable : inputVariables) {
			this.generateValues(variable, manager);
		}
		BindingNode root = new BindingNode(new HashSet<Assignment>(),
				new HashSet<Variable>(inputVariables));
		LeafBrusher leafBrusher = new LeafBrusher();
		root.accept(leafBrusher);
		Set<BindingNode> leaves = leafBrusher.getLeaves();
		OPPLQuery query = opplScript.getQuery();
		try {
			if (query != null) {
				List<OWLAxiom> axioms = query.getAssertedAxioms();
				axioms.addAll(query.getAxioms());
				for (BindingNode bindingNode : leaves) {
					PartialOWLObjectInstantiator partialOWLObjectInstantiator = new PartialOWLObjectInstantiator(
							bindingNode, opplScript.getConstraintSystem());
					for (OWLAxiom owlAxiom : axioms) {
						manager.addAxiom(testOntology, (OWLAxiom) owlAxiom
								.accept(partialOWLObjectInstantiator));
					}
				}
			}
		} catch (OWLOntologyChangeException e) {
			fail(e.getMessage());
		}
	}

	private void generateValues(Variable variable, OWLOntologyManager manager) {
		int questionMarkIndex = variable.getName().indexOf('?');
		String fragment = questionMarkIndex != -1 ? variable.getName()
				.substring(questionMarkIndex) : variable.getName();
		for (int i = 0; i < this.valuesCount; i++) {
			try {
				variable.addPossibleBinding(this.generateValue(fragment + "_"
						+ i, variable, manager.getOWLDataFactory()));
			} catch (OWLReasonerException e) {
				fail(e.getMessage());
			}
		}
	}

	private OWLObject generateValue(String string, Variable variable,
			OWLDataFactory dataFactory) {
		switch (variable.getType()) {
		case CLASS:
			return dataFactory.getOWLClass(URI.create(TEST_NS.toString()
					+ string));
		case OBJECTPROPERTY:
			return dataFactory.getOWLObjectProperty(URI.create(TEST_NS
					.toString()
					+ string));
		case DATAPROPERTY:
			return dataFactory.getOWLDataProperty(URI.create(TEST_NS.toString()
					+ string));
		case INDIVIDUAL:
			return dataFactory.getOWLIndividual(URI.create(TEST_NS.toString()
					+ string));
		case CONSTANT:
			return dataFactory.getOWLUntypedConstant(string);
		default:
			return null;
		}
	}

	// TRANSITIVE_OBJECT_PROPERTY : AxiomType<OWLTransitiveObjectPropertyAxiom>
	public void testTransitiveObjectPropertyQuery() {
		String opplString = "?x:OBJECTPROPERTY SELECT ASSERTED Transitive ?x BEGIN ADD transitive ?x END;";
		this.testQuery(opplString);
	}

	public void testSymmetricObjectPropertyQuery() {
		String opplString = "?x:OBJECTPROPERTY SELECT ASSERTED symmetric ?x BEGIN ADD symmetric ?x END;";
		this.testQuery(opplString);
	}

	public void testReflexiveObjectPropertyQuery() {
		String opplString = "?x:OBJECTPROPERTY SELECT ASSERTED reflexive ?x BEGIN ADD reflexive ?x END;";
		this.testQuery(opplString);
	}

	public void testIrreflexiveObjectPropertyQuery() {
		String opplString = "?x:OBJECTPROPERTY SELECT ASSERTED Irreflexive ?x BEGIN ADD Irreflexive ?x END;";
		this.testQuery(opplString);
	}

	public void testInverseObjectPropertiesQuery() {
		String opplString = "?x:OBJECTPROPERTY, ?y:OBJECTPROPERTY SELECT ASSERTED ?x InverseOf (?y) BEGIN ADD ?x InverseOf (?y) END;";
		this.testQuery(opplString);
	}

	public void testInverseFunctionalObjectPropertiesQuery() {
		String opplString = "?x:OBJECTPROPERTY SELECT ASSERTED InverseFunctional (?x) BEGIN ADD InverseFunctional (?x) END;";
		this.testQuery(opplString);
	}

	public void testFunctionalDataPropertiesQuery() {
		String opplString = "?x:DATAPROPERTY SELECT ASSERTED Functional ?x BEGIN ADD Functional ?x END;";
		this.testQuery(opplString);
	}

	public void testFunctionalObjectPropertiesQuery() {
		String opplString = "?x:OBJECTPROPERTY SELECT ASSERTED Functional ?x BEGIN ADD Functional ?x END;";
		this.testQuery(opplString);
	}

	public void testEquivalentObjectPropertiesQuery() {
		String opplString = "?x:OBJECTPROPERTY,?y:OBJECTPROPERTY SELECT ASSERTED ?x equivalentTo ?y BEGIN ADD ?x equivalentTo ?y END;";
		this.testQuery(opplString);
	}

	public void testEquivalentDataPropertiesQuery() {
		String opplString = "?x:DATAPROPERTY,?y:DATAPROPERTY SELECT ASSERTED ?x equivalentTo ?y BEGIN ADD ?x equivalentTo ?y END;";
		this.testQuery(opplString);
	}

	public void testEquivalentClassesQuery() {
		String opplString = "?x:CLASS,?y:CLASS SELECT ASSERTED ?x equivalentTo ?y BEGIN ADD ?x equivalentTo ?y END;";
		this.testQuery(opplString);
	}

	public void testDisjointObjectPropertiesQuery() {
		String opplString = "?x:OBJECTPROPERTY,?y:OBJECTPROPERTY SELECT ASSERTED ?x disjointWith ?y BEGIN ADD ?x disjointWith ?y END;";
		this.testQuery(opplString);
	}

	public void testDisjointDataPropertiesQuery() {
		String opplString = "?x:DATAPROPERTY,?y:DATAPROPERTY SELECT ASSERTED ?x disjointWith ?y BEGIN ADD ?x disjointWith ?y END;";
		this.testQuery(opplString);
	}

	public void testDisjointClassesQuery() {
		String opplString = "?x:CLASS,?y:CLASS SELECT ASSERTED ?x disjointWith ?y BEGIN ADD ?x disjointWith ?y END;";
		this.testQuery(opplString);
	}

	public void testDataPropertyRangeQuery() {
		String opplString = "?x:DATAPROPERTY SELECT ?x range int BEGIN ADD ?x range int END;";
		this.testQuery(opplString);
	}

	public void testObjectPropertyDomainQuery() {
		String opplString = "?x:OBJECTPROPERTY, ?y:CLASS SELECT ?x domain ?y BEGIN ADD ?x domain ?y END;";
		this.testQuery(opplString);
	}

	public void testObjectPropertyRangeQuery() {
		String opplString = "?x:OBJECTPROPERTY, ?y:CLASS SELECT ?x range ?y BEGIN ADD ?x range ?y END;";
		this.testQuery(opplString);
	}

	public void testDataPropertyDomainQuery() {
		String opplString = "?x:DATAPROPERTY, ?y:CLASS SELECT ?x domain ?y  BEGIN ADD ?x domain ?y END;";
		this.testQuery(opplString);
	}

	public void testSameIndividualsQuery() {
		String opplString = "?x:INDIVIDUAL,?y:INDIVIDUAL SELECT ASSERTED ?x  SameAs  ?y BEGIN ADD ?x  DifferentFrom ?y END;";
		this.testQuery(opplString);
	}

	public void testDifferentIndividualsQuery() {
		String opplString = "?x:INDIVIDUAL,?y:INDIVIDUAL SELECT ASSERTED ?x  DifferentFrom  ?y BEGIN ADD ?x  DifferentFrom ?y END;";
		this.testQuery(opplString);
	}

	public void testNegativeObjectPropertyAssertionQuery() {
		String opplString = "?x:INDIVIDUAL,?y:INDIVIDUAL,?z:OBJECTPROPERTY SELECT ASSERTED not ?x ?z ?y BEGIN ADD ?x ?z ?y END;";
		this.testQuery(opplString);
	}

	public void testObjectPropertyAssertionQuery() {
		String opplString = "?x:INDIVIDUAL,?y:INDIVIDUAL,?z:OBJECTPROPERTY SELECT ASSERTED ?x ?z ?y BEGIN ADD ?x ?z ?y END;";
		this.testQuery(opplString);
	}

	public void testNegativeDataPropertyAssertionQuery() {
		String opplString = "?x:INDIVIDUAL,?y:CONSTANT,?z:DATAPROPERTY SELECT ASSERTED not ?x ?z ?y BEGIN ADD ?x ?z ?y END;";
		this.testQuery(opplString);
	}

	public void testDataPropertyAssertionQuery() {
		String opplString = "?x:INDIVIDUAL,?y:CONSTANT,?z:DATAPROPERTY SELECT ASSERTED ?x ?z ?y BEGIN ADD ?x ?z ?y END;";
		this.testQuery(opplString);
	}

	public void testSubClassQuery() {
		String opplString = "?x:CLASS,?y:CLASS SELECT ASSERTED ?x subClassOf ?y BEGIN ADD ?x subClassOf ?y END;";
		this.testQuery(opplString);
	}

	public void testSubDataPropertyQuery() {
		String opplString = "?x:DATAPROPERTY,?y:DATAPROPERTY SELECT ASSERTED ?x subPropertyOf ?y BEGIN ADD ?x subPropertyOf ?y END;";
		this.testQuery(opplString);
	}

	public void testSubObjectPropertyQuery() {
		String opplString = "?x:OBJECTPROPERTY,?y:OBJECTPROPERTY SELECT ASSERTED ?x subPropertyOf ?y BEGIN ADD ?x subPropertyOf ?y END;";
		this.testQuery(opplString);
	}

	private void testQuery(String opplString) {
		OWLOntologyManager ontologyManager = OWLManager
				.createOWLOntologyManager();
		try {
			OWLOntology testOntology = ontologyManager.createOntology(TEST_NS);
			ParserFactory.initParser(opplString, testOntology, ontologyManager,
					null);
			OPPLScript opplScript = OPPLParser.Start();
			this.buildOntologyForQuery(opplScript, testOntology,
					ontologyManager);
			Set<OWLAxiom> results = this.getOPPLScriptCorrectResults(
					ontologyManager, testOntology, opplScript);
			double expected = Math.pow(this.valuesCount, opplScript
					.getInputVariables().size());
			assertTrue("Actual " + results.size() + " Expected " + expected,
					results.size() == expected);
		} catch (OWLOntologyCreationException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * @param manager
	 * @param ontology
	 * @param opplScript
	 * @return
	 */
	private Set<OWLAxiom> getOPPLScriptCorrectResults(
			final OWLOntologyManager manager, OWLOntology ontology,
			final OPPLScript opplScript) {
		ChangeExtractor changeExtractor = new ChangeExtractor(ontology,
				manager, opplScript.getConstraintSystem(), true);
		opplScript.accept(changeExtractor);
		Set<BindingNode> checkLeaves = opplScript.getConstraintSystem()
				.getLeaves();
		final Set<OWLAxiom> correctResults = new HashSet<OWLAxiom>();
		Set<OWLAxiom> queryAxioms = new HashSet<OWLAxiom>();
		queryAxioms.addAll(opplScript.getQuery().getAssertedAxioms());
		queryAxioms.addAll(opplScript.getQuery().getAxioms());
		for (BindingNode bindingNode : checkLeaves) {
			PartialOWLObjectInstantiator partialOWLObjectInstantiator = new PartialOWLObjectInstantiator(
					bindingNode, opplScript.getConstraintSystem());
			for (OWLAxiom owlAxiom : queryAxioms) {
				correctResults.add((OWLAxiom) owlAxiom
						.accept(partialOWLObjectInstantiator));
			}
		}
		return correctResults;
	}

	private Set<OWLDescription> generateClasses(OWLDataFactory dataFactory,
			ConstraintSystem cs) throws OPPLException {
		EnumSet<DescriptionType> descriptionTypes = EnumSet
				.allOf(DescriptionType.class);
		Set<OWLDescription> toReturn = new HashSet<OWLDescription>();
		for (DescriptionType descriptionType : descriptionTypes) {
			Variable classVariable = cs.createVariable("?aClass",
					VariableType.CLASS);
			Variable dataPropertyVariable = cs.createVariable("?aProperty",
					VariableType.DATAPROPERTY);
			Variable objectPropertyVariable = cs.createVariable("?aProperty",
					VariableType.OBJECTPROPERTY);
			Variable constantVariable = cs.createVariable("?aConstant",
					VariableType.CONSTANT);
			switch (descriptionType) {
			case CLASS:
				toReturn.add(dataFactory.getOWLClass(classVariable.getURI()));
				break;
			case OWLDATAEXACTCARDINALITYRESTRICTION:
				toReturn.add(dataFactory.getOWLDataExactCardinalityRestriction(
						dataFactory.getOWLDataProperty(dataPropertyVariable
								.getURI()), 3));
				break;
			case OWLDATAMAXCARDINALITYRESTRICTION:
				toReturn.add(dataFactory.getOWLDataMaxCardinalityRestriction(
						dataFactory.getOWLDataProperty(dataPropertyVariable
								.getURI()), 3));
				break;
			case OWLDATAMINCARDINALITYRESTRICTION:
				toReturn.add(dataFactory.getOWLDataMinCardinalityRestriction(
						dataFactory.getOWLDataProperty(dataPropertyVariable
								.getURI()), 3));
				break;
			case OWLDATAVALUERESTRICTION:
				toReturn.add(dataFactory.getOWLDataValueRestriction(dataFactory
						.getOWLDataProperty(dataPropertyVariable.getURI()),
						dataFactory.getOWLUntypedConstant(constantVariable
								.getName())));
				break;
			case OWLOBJECTALLRESTRICTION:
				toReturn.add(dataFactory.getOWLObjectAllRestriction(dataFactory
						.getOWLObjectProperty(objectPropertyVariable.getURI()),
						dataFactory.getOWLClass(classVariable.getURI())));
				break;
			case OWLOBJECTCOMPLEMENTOF:
				toReturn.add(dataFactory.getOWLObjectComplementOf(dataFactory
						.getOWLClass(classVariable.getURI())));
				break;
			case OWLOBJECTEXACTCARDINALITYRESTRICTION:
				toReturn.add(dataFactory
						.getOWLObjectExactCardinalityRestriction(dataFactory
								.getOWLObjectProperty(objectPropertyVariable
										.getURI()), 2));
				break;
			case OWLOBJECTINTERSECTIONOF:
			default:
				break;
			}
		}
		return toReturn;
	}
}
