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
package uk.ac.manchester.mae;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxDescriptionParser;
import org.coode.oae.utils.ParserFactory;
import org.coode.xml.OWLOntologyNamespaceManager;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.expression.ShortFormEntityChecker;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owl.util.OWLEntitySetProvider;
import org.semanticweb.owl.util.ReferencedEntitySetProvider;
import org.semanticweb.owl.util.ShortFormProvider;
import org.semanticweb.owl.vocab.XSDVocabulary;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 6, 2008
 */
public class Evaluator implements ArithmeticsParserVisitor {
	protected Set<OWLOntology> ontologies;
	protected OWLOntology startingOntology;
	protected Map<OWLIndividual, Map<String, List<Object>>> bindings = new HashMap<OWLIndividual, Map<String, List<Object>>>();
	protected OWLOntologyManager ontologyManager = OWLManager
			.createOWLOntologyManager();
	private OWLOntologyNamespaceManager nsm;
	private ShortFormProvider shortFormProvider;
	private OWLDescription classDescription = this.ontologyManager
			.getOWLDataFactory().getOWLThing();
	private String reasonerClassName = "org.mindswap.pellet.owlapi.Reasoner";
	private OWLReasoner reasoner;
	private Set<OWLIndividual> instances;
	private Set<OWLIndividual> selectedIndividuals = new HashSet<OWLIndividual>();
	private Map<MAEStart, ConflictStrategy> conflictStrategyMap = new HashMap<MAEStart, ConflictStrategy>();

	public Evaluator(URI physicalURI) throws OWLOntologyCreationException,
			OWLReasonerException {
		OWLOntology ontology = this.ontologyManager
				.loadOntologyFromPhysicalURI(physicalURI);
		this.init(ontology);
	}

	public Evaluator(OWLOntology ontology, String reasonerClassName)
			throws OWLReasonerException {
		this.reasonerClassName = reasonerClassName;
		this.init(ontology);
	}

	public Evaluator(OWLOntology ontology) throws OWLReasonerException {
		this.init(ontology);
	}

	/**
	 * @param ontology
	 * @throws OWLReasonerException
	 */
	private void init(OWLOntology ontology) throws OWLReasonerException {
		this.startingOntology = ontology;
		this.nsm = new OWLOntologyNamespaceManager(this.ontologyManager,
				this.startingOntology);
		this.ontologies = this.ontologyManager.getImportsClosure(ontology);
		this.shortFormProvider = new ShortFormProvider() {
			public String getShortForm(OWLEntity entity) {
				return Evaluator.this.nsm.getQName(entity.getURI().toString());
			}

			public void dispose() {
			}
		};
		this.reasoner = this.createReasoner();
		this.reasoner.loadOntologies(this.ontologies);
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.SimpleNode,
	 *      java.lang.Object)
	 */
	public Object visit(SimpleNode node, Object data) {
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEStart,
	 *      java.lang.Object)
	 */
	public Object visit(MAEStart node, Object data) {
		Map<OWLIndividual, Object> evaluationResult = new HashMap<OWLIndividual, Object>();
		// Visit all the bindings first
		for (Node child : node.children) {
			try {
				if (this.instances != null) { // Have to compute the
					// individuals to which the
					// formula can be applied
					for (OWLIndividual individual : this.instances) {
						if (child instanceof MAEBinding) {
							// Compute the bindings for the individual(s)
							child.jjtAccept(this, individual);
						} else {
							Object result = child.jjtAccept(this, individual);
							if (result != null) {
								evaluationResult.put(individual, result);
							}
						}
					}
				} else {
					child.jjtAccept(this, null);
				}
				if (child instanceof MAEmanSyntaxClassExpression) {
					this.determineIndividuals();
				}
			} catch (OWLReasonerException e) {
				e.printStackTrace();
				return null;
			}
		}
		node.setSymbolic(evaluationResult.isEmpty() && this.instances != null
				&& !this.instances.isEmpty());
		return evaluationResult;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEAdd,
	 *      java.lang.Object)
	 */
	public Object visit(MAEAdd node, Object data) {
		List<Double> firstValues = (List<Double>) node.children[0].jjtAccept(
				this, data);
		List<Double> secondValues = (List<Double>) node.children[1].jjtAccept(
				this, data);
		List<Double> result = null;
		if (firstValues == null || secondValues == null) {
			node.setSymbolic(true);
		} else {
			result = new ArrayList<Double>();
			for (Double aValue : firstValues) {
				for (Double anotherValue : secondValues) {
					result.add(node.isSum() ? aValue + anotherValue : aValue
							- anotherValue);
				}
			}
		}
		return result;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEMult,
	 *      java.lang.Object)
	 */
	public Object visit(MAEMult node, Object data) {
		List<Double> result = null;
		List<Double> firstValues = (List<Double>) node.children[0].jjtAccept(
				this, data);
		List<Double> secondValues = (List<Double>) node.children[1].jjtAccept(
				this, data);
		if (firstValues == null || secondValues == null) {
			node.setSymbolic(true);
		} else {
			for (Double aValue : firstValues) {
				for (Double anotherValue : secondValues) {
					result = new ArrayList<Double>();
					if (node.isMultiplication()) {
						result.add(aValue * anotherValue);
					} else if (node.isPercentage()) {
						result.add(aValue * anotherValue / 100);
					} else {
						result.add(aValue / anotherValue);
					}
				}
			}
		}
		return result;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEPower,
	 *      java.lang.Object)
	 */
	public Object visit(MAEPower node, Object data) {
		List<Double> result = null;
		OWLIndividual currentIndividual = (OWLIndividual) data;
		if (this.bindings.get(currentIndividual) != null) {
			result = new ArrayList<Double>();
			List<Object> values = this.bindings.get(currentIndividual).get(
					node.getBaseIdentifier());
			for (Object value : values) {
				Double base = (Double) (node.isSymbolic() ? value : node
						.getBase());
				if (base != null) {
					result.add(Math.pow(base, node.getExp()));
				}
			}
		}
		return result;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEInteger,
	 *      java.lang.Object)
	 */
	public Object visit(MAEIntNode node, Object data) {
		List<Double> toReturn = null;
		if (!node.isSymbolic()) {
			toReturn = new ArrayList<Double>();
			toReturn.add(node.getValue());
		}
		return toReturn;
	}

	public static void main(String[] args) {
		try {
			if (args.length == 3) {
				ParserFactory.initParser(args[0]);
				SimpleNode simpleNode = ArithmeticsParser.Start();
				simpleNode.dump("");
				Evaluator evaluator = new Evaluator(URI.create(args[1]));
				Object evaluation = evaluator.evaluate(simpleNode, args[2]);
				if (evaluation != null) {
					System.out.println(args[0] + " = " + evaluation);
				} else {
					System.out
							.println(args[0]
									+ " is not applicable to the individual "
									+ args[2]);
				}
			}
			if (args.length == 2) {
				Evaluator evaluator = new Evaluator(URI.create(args[0]));
				Object evaluation = evaluator.evaluate(args[1], true);
				if (evaluation != null) {
					System.out.println("Evaluation results:\n" + evaluation);
				} else {
					System.out.println(args[0]
							+ " is not applicable to any individual ");
				}
			}
			if (args.length == 1) {
				Evaluator evaluator = new Evaluator(URI.create(args[0]));
				Object evaluation = evaluator.evaluate(true);
				if (evaluation != null) {
					System.out.println("Evaluation results:\n" + evaluation);
				} else {
					System.out.println(args[0]
							+ " is not applicable to any individual ");
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (MoreThanOneFormulaPerIndividualException e) {
			System.out.println(e.getMessage());
		} catch (ValueAlreadySetException e) {
			System.out.println(e.getMessage());
		} catch (EvaluationException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLReasonerException e) {
			e.printStackTrace();
		} catch (UnknownOWLOntologyException e) {
			System.out.println("Problems in saving the ontology");
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			System.out.println("Problems in saving the ontology");
			e.printStackTrace();
		} catch (OWLOntologyChangeException e) {
			System.out
					.println("Problems in storing the new value(s) into the ontology");
			e.printStackTrace();
		}
	}

	public Object evaluate(boolean storeValues) throws EvaluationException,
			UnknownOWLOntologyException, OWLOntologyStorageException,
			OWLOntologyChangeException {
		Map<OWLDataProperty, Map<OWLIndividual, Object>> result = new HashMap<OWLDataProperty, Map<OWLIndividual, Object>>();
		for (OWLOntology ontology : this.ontologies) {
			for (OWLIndividual individual : ontology.getReferencedIndividuals()) {
				Map<OWLDataProperty, Map<OWLIndividual, Object>> individualEvaluation = this
						.evaluate(individual.getURI().toString(), true);
				if (!individualEvaluation.isEmpty()) {
					for (OWLDataProperty dataProperty : individualEvaluation
							.keySet()) {
						Map<OWLIndividual, Object> storedValues = result
								.get(dataProperty);
						if (storedValues == null) {
							storedValues = new HashMap<OWLIndividual, Object>();
						}
						storedValues.putAll(individualEvaluation
								.get(dataProperty));
						result.put(dataProperty, storedValues);
						if (storeValues) {
							this.ontologyManager
									.saveOntology(this.startingOntology);
						}
					}
				}
			}
		}
		return result;
	}

	public Map<OWLDataProperty, Map<OWLIndividual, Object>> evaluate(
			String individualURIString, boolean storeValues)
			throws EvaluationException, UnknownOWLOntologyException,
			OWLOntologyStorageException, OWLOntologyChangeException {
		Map<OWLDataProperty, Map<OWLIndividual, Object>> result = new HashMap<OWLDataProperty, Map<OWLIndividual, Object>>();
		OWLIndividual individual = this.ontologyManager.getOWLDataFactory()
				.getOWLIndividual(URI.create(individualURIString));
		this.bindings.clear();
		this.instances = null;
		for (OWLOntology ontology : this.ontologies) {
			for (OWLDataProperty dataProperty : ontology
					.getReferencedDataProperties()) {
				Set<SimpleNode> formulas;
				try {
					PropertyVisitor aPropertyVisitor = new PropertyVisitor(
							this.ontologies);
					dataProperty.accept(aPropertyVisitor);
					Set<String> formulaBodies = aPropertyVisitor
							.getExtractedFormulaStrings();
					formulas = this.extractFormula(formulaBodies);
					SimpleNode formula = this.pickFormula(individual,
							dataProperty, formulas);
					if (formula != null) {
						Map<OWLIndividual, Object> individualResults = result
								.get(dataProperty);
						if (individualResults == null) {
							individualResults = new HashMap<OWLIndividual, Object>();
						}
						Object evaluation = this.evaluate(formula,
								individualURIString);
						if (evaluation != null) {
							individualResults.put(individual, evaluation);
							if (!individualResults.isEmpty()) {
								result.put(dataProperty, individualResults);
								if (storeValues) {
									this.write(individual, dataProperty,
											individualResults.get(individual),
											formula);
								}
							}
						}
					}
				} catch (ParseException e) {
					System.out
							.println("Could not parse at least one formula for the property "
									+ dataProperty.getURI().toString());
					e.printStackTrace();
				} catch (OWLReasonerException e) {
					throw new EvaluationException(e);
				}
			}
			if (storeValues) {
				this.ontologyManager.saveOntology(this.startingOntology);
			}
		}
		return result;
	}

	private void write(OWLIndividual individual, OWLDataProperty dataProperty,
			Object object, SimpleNode formula) throws EvaluationException,
			OWLOntologyChangeException {
		Set<OWLConstant> oldValues = individual.getDataPropertyValues(
				this.startingOntology).get(dataProperty);
		if (!dataProperty.isFunctional(this.ontologies) || oldValues == null
				|| oldValues.isEmpty()) {
			if (object instanceof Collection) {
				for (Object newValue : (Collection<Object>) object) {
					this.writeSingleValue(individual, dataProperty, newValue);
				}
			} else {
				this.writeSingleValue(individual, dataProperty, object);
			}
		} else if (dataProperty.isFunctional(this.ontologies)) {
			if (object instanceof Collection
					&& ((Collection<Object>) object).size() > 1) {
				throw new MoreThanOneValueForFunctionalPropertyException(
						"More than one value for the functional property "
								+ dataProperty + " for the individual "
								+ individual.getURI().toString());
			}
			if (oldValues != null && !oldValues.isEmpty()) {
				ConflictStrategy solver = this.conflictStrategyMap.get(formula);
				OWLDataPropertyAssertionAxiom oldAssertion = this.ontologyManager
						.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(
								individual, dataProperty,
								oldValues.iterator().next());
				if (solver != null) {
					solver
							.solve(
									individual,
									oldAssertion,
									this
											.convert2OWLConstant(object instanceof Collection ? ((Collection) object)
													.iterator().next()
													: object), this.ontologies,
									this.ontologyManager);
				} else {
					this
							.writeSingleValue(
									individual,
									dataProperty,
									object instanceof Collection ? ((Collection) object)
											.iterator().next()
											: object);
				}
			}
		}
	}

	private void writeSingleValue(OWLIndividual individual,
			OWLDataProperty dataProperty, Object newValue)
			throws UnsupportedDataTypeException, OWLOntologyChangeException {
		OWLConstant valueAsOWLConstant = this.convert2OWLConstant(newValue);
		AddAxiom addAxiom = new AddAxiom(this.startingOntology,
				this.ontologyManager.getOWLDataFactory()
						.getOWLDataPropertyAssertionAxiom(individual,
								dataProperty, valueAsOWLConstant));
		this.ontologyManager.applyChange(addAxiom);
	}

	private OWLConstant convert2OWLConstant(Object newValue)
			throws UnsupportedDataTypeException {
		OWLConstant toReturn = null;
		if (newValue instanceof Double) {
			toReturn = this.ontologyManager.getOWLDataFactory()
					.getOWLTypedConstant((Double) newValue);
		} else {
			throw new UnsupportedDataTypeException(newValue.getClass()
					.getName());
		}
		return toReturn;
	}

	private SimpleNode pickFormula(OWLIndividual individual,
			OWLDataProperty dataProperty, Set<SimpleNode> formulas)
			throws OWLReasonerException,
			MoreThanOneFormulaPerIndividualException {
		Set<SimpleNode> applicableFormulas = new HashSet<SimpleNode>();
		ClassExatrctor classExtractor = new ClassExatrctor(this.ontologies,
				this.shortFormProvider, this.ontologyManager);
		if (!this.reasoner.isClassified()) {
			this.reasoner.classify();
		}
		boolean isApplicable;
		OWLDescription classDescription = null, anotherClassDescription = null;
		for (SimpleNode formula : formulas) {
			for (Node child : formula.children) {
				classDescription = (OWLDescription) child.jjtAccept(
						classExtractor, classDescription);
			}
			isApplicable = this.reasoner
					.getIndividuals(classDescription, false).contains(
							individual);
			if (isApplicable) {
				applicableFormulas.add(formula);
			}
		}
		// Now I eliminate the applies-to that subsume fellow elements in the
		// set of applicable formulas
		for (SimpleNode anApplicableFormula : new HashSet<SimpleNode>(
				applicableFormulas)) {
			for (Node child : anApplicableFormula.children) {
				classDescription = (OWLDescription) child.jjtAccept(
						classExtractor, classDescription);
			}
			for (SimpleNode anotherApplicableFormula : new HashSet<SimpleNode>(
					applicableFormulas)) {
				if (!anApplicableFormula.equals(anotherApplicableFormula)) {
					for (Node child : anotherApplicableFormula.children) {
						anotherClassDescription = (OWLDescription) child
								.jjtAccept(classExtractor,
										anotherClassDescription);
					}
					if (this.reasoner.isSubClassOf(classDescription,
							anotherClassDescription)
							&& !this.reasoner.isSubClassOf(
									anotherClassDescription, classDescription)) {
						applicableFormulas.remove(anotherApplicableFormula);
					}
				}
			}
		}
		// There must be at most one applicable formula per individual
		if (applicableFormulas.size() > 1) {
			throw new MoreThanOneFormulaPerIndividualException(
					"There is more than one formula for property "
							+ dataProperty + "for individual " + individual);
		} else {
			return applicableFormulas.size() > 0 ? applicableFormulas
					.iterator().next() : null;
		}
	}

	private Set<SimpleNode> extractFormula(Set<String> formulaBodies)
			throws ParseException {
		Set<SimpleNode> toReturn = new HashSet<SimpleNode>();
		for (String aFormulaBody : formulaBodies) {
			ParserFactory.initParser(aFormulaBody);
			toReturn.add(ArithmeticsParser.Start());
		}
		return toReturn;
	}

	/**
	 * @param simpleNode
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object evaluate(SimpleNode simpleNode, String individualURIString)
			throws EvaluationException {
		OWLIndividual individual = this.ontologyManager.getOWLDataFactory()
				.getOWLIndividual(URI.create(individualURIString));
		this.selectedIndividuals.clear();
		this.selectedIndividuals.add(individual);
		Object result = simpleNode.jjtAccept(this, null);
		if (simpleNode.isSymbolic()) {
			throw new UnresolvedSymbolsException();
		}
		return result != null
				&& ((Map<OWLIndividual, Object>) result).isEmpty() ? null
				: ((Map<OWLIndividual, Object>) result).get(individual);
	}

	public Object visit(MAEIdentifier node, Object data) {
		OWLIndividual individual = (OWLIndividual) data;
		Object toReturn = null;
		String identifier = node.getIdentifierName();
		Map<String, List<Object>> individualBindings = this.bindings
				.get(individual);
		if (individualBindings != null) {
			toReturn = individualBindings.get(identifier);
		}
		return toReturn;
	}

	public Object visit(MAEBinding node, Object data) {
		String identifier = node.getIdentifier();
		OWLIndividual startingIndividual = (OWLIndividual) data;
		Set<OWLIndividual> chainStartingIndividuals = new HashSet<OWLIndividual>();
		chainStartingIndividuals.add(startingIndividual);
		for (Node child : node.children) {
			List<Object> values = (List<Object>) child.jjtAccept(this,
					chainStartingIndividuals);
			if (values != null && !values.isEmpty()) {
				Map<String, List<Object>> storedValues = this.bindings
						.get(startingIndividual);
				if (storedValues == null) {
					storedValues = new HashMap<String, List<Object>>();
				}
				storedValues.put(identifier, values);
				this.bindings.put(startingIndividual, storedValues);
			}
		}
		return null;
	}

	private Collection<Object> fetch(OWLIndividual currentIndividual,
			String propertyName, boolean isDatatype) throws URISyntaxException,
			UnsupportedDataTypeException {
		Collection<Object> toReturn = null;
		Iterator<OWLOntology> it = this.ontologies.iterator();
		boolean found = false;
		OWLOntology ontology;
		while (!found && it.hasNext()) {
			ontology = it.next();
			if (isDatatype) {
				OWLDataProperty dataProperty = this.ontologyManager
						.getOWLDataFactory().getOWLDataProperty(
								new URI(propertyName));
				Set<OWLConstant> values = currentIndividual
						.getDataPropertyValues(ontology).get(dataProperty);
				toReturn = new ArrayList<Object>();
				if (!(values == null || values.isEmpty())) {
					for (OWLConstant value : values) {
						toReturn.add(this.convertValue(value
								.asOWLTypedConstant()));
					}
				}
			} else {
				OWLObjectProperty objectProperty = this.ontologyManager
						.getOWLDataFactory().getOWLObjectProperty(
								new URI(propertyName));
				Set<OWLIndividual> fillers = currentIndividual
						.getObjectPropertyValues(ontology).get(objectProperty);
				toReturn = new HashSet<Object>();
				if (!(fillers == null || fillers.isEmpty())) {
					for (OWLIndividual filler : fillers) {
						toReturn.add(filler);
					}
				}
			}
		}
		return toReturn;
	}

	private Object convertValue(OWLTypedConstant typedConstant)
			throws UnsupportedDataTypeException {
		OWLDataType type = typedConstant.getDataType();
		Object toReturn = null;
		// Rough conversion big if
		if (type.getURI().equals(XSDVocabulary.INT.getURI())
				|| type.getURI().equals(XSDVocabulary.INTEGER.getURI())
				|| type.getURI().equals(XSDVocabulary.DOUBLE.getURI())
				|| type.getURI().equals(XSDVocabulary.DECIMAL.getURI())
				|| type.getURI().equals(XSDVocabulary.SHORT.getURI())) {
			toReturn = new Double(Double
					.parseDouble(typedConstant.getLiteral()));
		} else {
			throw new UnsupportedDataTypeException(
					"Currently unsuported data type - "
							+ type.getURI().toString());
		}
		return toReturn;
	}

	public Object visit(MAEmanSyntaxClassExpression node, Object data) {
		BidirectionalShortFormProviderAdapter adapter = new BidirectionalShortFormProviderAdapter(
				this.shortFormProvider);
		OWLEntitySetProvider<OWLEntity> owlEntitySetProvider = new ReferencedEntitySetProvider(
				this.ontologies);
		adapter.rebuild(owlEntitySetProvider);
		ManchesterOWLSyntaxDescriptionParser parser = new ManchesterOWLSyntaxDescriptionParser(
				this.ontologyManager.getOWLDataFactory(),
				new ShortFormEntityChecker(adapter));
		try {
			this.classDescription = parser.parse(node.getContent());
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private OWLReasoner createReasoner() {
		try {
			String reasonerClassName = this.reasonerClassName;
			Class<OWLReasoner> reasonerClass = (Class<OWLReasoner>) Class
					.forName(reasonerClassName);
			Constructor<OWLReasoner> con = reasonerClass
					.getConstructor(OWLOntologyManager.class);
			return con.newInstance(this.ontologyManager);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	private void determineIndividuals() throws OWLReasonerException {
		if (!this.reasoner.isClassified()) {
			this.reasoner.classify();
		}
		this.instances = this.reasoner.getIndividuals(this.classDescription,
				false);
		if (!this.selectedIndividuals.isEmpty()) {
			this.instances.retainAll(this.selectedIndividuals);
		}
	}

	public Object visit(MAEPropertyChain node, Object data) {
		List<Object> toReturn = null;
		Set<OWLIndividual> individuals = (Set<OWLIndividual>) data;
		String propertyName = node.getPropertyName();
		if (node.isEnd()) {
			toReturn = new ArrayList<Object>();
			for (OWLIndividual individual : individuals) {
				try {
					toReturn.addAll(this.fetch(individual, propertyName, true));
				} catch (UnsupportedDataTypeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			for (OWLIndividual individual : individuals) {
				for (Node child : node.children) {
					Set<Object> fillers;
					try {
						fillers = (Set<Object>) this.fetch(individual,
								propertyName, false);
						toReturn = new ArrayList<Object>();
						toReturn.addAll((List<Object>) child.jjtAccept(this,
								fillers));
					} catch (UnsupportedDataTypeException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return toReturn;
	}

	public Object visit(MAEBigSum node, Object data) {
		List<Double> childValues = (List<Double>) node.children[0].jjtAccept(
				this, data);
		Double result = null;
		if (childValues != null) {
			result = 0d;
			for (Double value : childValues) {
				result += value;
			}
		} else {
			node.setSymbolic(true);
		}
		return result;
	}

	public Object visit(MAEConflictStrategy node, Object data) {
		ConflictStrategy conflictStrategy = ConflictStrategyFactory
				.getStrategy(node.getStrategyName());
		this.conflictStrategyMap.put((MAEStart) node.jjtGetParent(),
				conflictStrategy);
		return conflictStrategy;
	}
}
