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

import org.coode.oae.utils.ParserFactory;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.manchester.mae.report.EvaluationReport;
import uk.ac.manchester.mae.report.ExceptionReportWriter;
import uk.ac.manchester.mae.report.FormulaReportWriter;
import uk.ac.manchester.mae.report.ResultReportWriter;
import uk.ac.manchester.mae.visitor.protege.ProtegeClassExtractor;
import uk.ac.manchester.mae.visitor.protege.ProtegeDescriptionFacetExtractor;
import uk.ac.manchester.mae.visitor.protege.ProtegeWriter;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 6, 2008
 */
public class ProtegeEvaluator implements ArithmeticsParserVisitor {
	protected Set<OWLOntology> ontologies;
	protected OWLOntology startingOntology;
	protected Map<OWLIndividual, Map<String, List<Object>>> bindings = new HashMap<OWLIndividual, Map<String, List<Object>>>();
	protected OWLModelManager modelManager;
	private OWLDescription appliesToClassDescription;
	private OWLReasoner reasoner;
	private Set<OWLIndividual> instances;
	private Set<OWLIndividual> selectedIndividuals = new HashSet<OWLIndividual>();
	private Map<MAEStart, ConflictStrategy> conflictStrategyMap = new HashMap<MAEStart, ConflictStrategy>();
	protected EvaluationReport report = null;

	public ProtegeEvaluator(OWLModelManager modelManager)
			throws OWLReasonerException {
		this.init(modelManager);
	}

	/**
	 * @param ontology
	 * @throws OWLReasonerException
	 */
	private void init(OWLModelManager modelManager) throws OWLReasonerException {
		this.modelManager = modelManager;
		this.startingOntology = modelManager.getActiveOntology();
		this.ontologies = this.modelManager.getOntologies();
		this.appliesToClassDescription = modelManager.getOWLDataFactory()
				.getOWLThing();
		this.reasoner = this.modelManager.getReasoner();
		if (!this.reasoner.isClassified()) {
			this.reasoner.classify();
		}
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
					child.jjtAccept(this, data);
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
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
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

	public Object evaluate(boolean storeValues) throws EvaluationException,
			OWLOntologyChangeException {
		this.report = new EvaluationReport();
		Map<OWLDataProperty, Map<OWLIndividual, Object>> result = new HashMap<OWLDataProperty, Map<OWLIndividual, Object>>();
		for (OWLOntology ontology : this.ontologies) {
			for (OWLIndividual individual : new HashSet<OWLIndividual>(ontology
					.getReferencedIndividuals())) {
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
					}
				}
			}
		}
		return result;
	}

	public Map<OWLDataProperty, Map<OWLIndividual, Object>> evaluate(
			String individualURIString, boolean storeValues)
			throws OWLOntologyChangeException {
		Map<OWLDataProperty, Map<OWLIndividual, Object>> result = new HashMap<OWLDataProperty, Map<OWLIndividual, Object>>();
		OWLIndividual individual = this.modelManager.getOWLDataFactory()
				.getOWLIndividual(URI.create(individualURIString));
		this.bindings.clear();
		this.instances = null;
		if (this.report == null) {
			this.report = new EvaluationReport();
		}
		for (OWLOntology ontology : this.ontologies) {
			for (OWLDataProperty dataProperty : ontology
					.getReferencedDataProperties()) {
				Set<SimpleNode> formulas;
				SimpleNode formula = null;
				try {
					PropertyVisitor aPropertyVisitor = new PropertyVisitor(
							this.ontologies);
					dataProperty.accept(aPropertyVisitor);
					Set<String> formulaBodies = aPropertyVisitor
							.getExtractedFormulaStrings();
					formulas = this.extractFormula(formulaBodies);
					formula = this.pickFormula(individual, dataProperty,
							formulas);
					if (formula != null) {
						Map<OWLIndividual, Object> individualResults = result
								.get(dataProperty);
						if (individualResults == null) {
							individualResults = new HashMap<OWLIndividual, Object>();
						}
						Object evaluation = this.evaluate(dataProperty,
								formula, individualURIString);
						if (evaluation != null) {
							individualResults.put(individual, evaluation);
							if (!individualResults.isEmpty()) {
								result.put(dataProperty, individualResults);
								if (storeValues) {
									ProtegeWriter protegeWriter = new ProtegeWriter(
											individual, dataProperty,
											individualResults.get(individual),
											this.modelManager, this.report);
									formula.jjtAccept(protegeWriter, null);
								}
							}
						}
					}
				} catch (Exception e) {
					FormulaReportWriter resultReportWriter = new ExceptionReportWriter(
							dataProperty, (MAEStart) formula, e);
					this.report.accept(resultReportWriter, null);
				}
			}
		}
		return result;
	}

	private SimpleNode pickFormula(OWLIndividual individual,
			OWLDataProperty dataProperty, Set<SimpleNode> formulas)
			throws OWLReasonerException,
			MoreThanOneFormulaPerIndividualException {
		Set<SimpleNode> applicableFormulas = new HashSet<SimpleNode>();
		if (!this.reasoner.isClassified()) {
			this.reasoner.classify();
		}
		boolean isApplicable;
		OWLDescription classDescription = null, anotherClassDescription = null;
		for (SimpleNode formula : formulas) {
			ProtegeClassExtractor protegeClassExtractor = new ProtegeClassExtractor(
					this.modelManager);
			formula.jjtAccept(protegeClassExtractor, null);
			classDescription = protegeClassExtractor.getExtractedClass();
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
			ProtegeClassExtractor protegeClassExtractor = new ProtegeClassExtractor(
					this.modelManager);
			anApplicableFormula.jjtAccept(protegeClassExtractor, null);
			classDescription = protegeClassExtractor.getExtractedClass();
			for (SimpleNode anotherApplicableFormula : new HashSet<SimpleNode>(
					applicableFormulas)) {
				if (!anApplicableFormula.equals(anotherApplicableFormula)) {
					anotherApplicableFormula.jjtAccept(protegeClassExtractor,
							null);
					anotherClassDescription = protegeClassExtractor
							.getExtractedClass();
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
	public Object evaluate(OWLDataProperty dataProperty, SimpleNode simpleNode,
			String individualURIString) throws EvaluationException {
		if (this.report == null) {
			this.report = new EvaluationReport();
		}
		OWLIndividual individual = this.modelManager.getOWLDataFactory()
				.getOWLIndividual(URI.create(individualURIString));
		this.selectedIndividuals.clear();
		this.selectedIndividuals.add(individual);
		Object result = simpleNode.jjtAccept(this, dataProperty);
		if (simpleNode.isSymbolic()) {
			UnresolvedSymbolsException unresolvedSymbolsException = new UnresolvedSymbolsException(
					"One or more variable could not be bound in this formula for the individual: "
							+ individualURIString);
			throw unresolvedSymbolsException;
		}
		if (result != null) {
			ResultReportWriter resultReportWriter = new ResultReportWriter(
					dataProperty, (MAEStart) simpleNode,
					(Map<OWLIndividual, Object>) result);
			this.report.accept(resultReportWriter, null);
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

	@SuppressWarnings("unchecked")
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
			String propertyName, boolean isDatatype,
			OWLDescription facetDescription) throws URISyntaxException,
			UnsupportedDataTypeException, OWLReasonerException {
		Collection<Object> toReturn = null;
		Iterator<OWLOntology> it = this.ontologies.iterator();
		boolean found = false;
		OWLOntology ontology;
		while (!found && it.hasNext()) {
			ontology = it.next();
			if (isDatatype) {
				OWLDataProperty dataProperty = this.modelManager
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
				if (!this.reasoner.isClassified()) {
					this.reasoner.classify();
				}
				OWLObjectProperty objectProperty = this.modelManager
						.getOWLDataFactory().getOWLObjectProperty(
								new URI(propertyName));
				Set<OWLIndividual> fillers = currentIndividual
						.getObjectPropertyValues(ontology).get(objectProperty);
				toReturn = new HashSet<Object>();
				if (!(fillers == null || fillers.isEmpty())) {
					for (OWLIndividual filler : fillers) {
						if (this.reasoner.hasType(filler, facetDescription,
								false)) {
							toReturn.add(filler);
						}
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
		try {
			this.appliesToClassDescription = this.modelManager
					.getOWLDescriptionParser().createOWLDescription(
							node.getContent());
		} catch (OWLExpressionParserException e) {
			ExceptionReportWriter erw = new ExceptionReportWriter(
					(OWLDataProperty) data, (MAEStart) data, e);
			this.report.accept(erw, data);
			e.printStackTrace();
		}
		return null;
	}

	private void determineIndividuals() throws OWLReasonerException {
		if (!this.reasoner.isClassified()) {
			this.reasoner.classify();
		}
		this.instances = this.reasoner.getIndividuals(
				this.appliesToClassDescription, false);
		if (!this.selectedIndividuals.isEmpty()) {
			this.instances.retainAll(this.selectedIndividuals);
		}
	}

	@SuppressWarnings("unchecked")
	public Object visit(MAEPropertyChain node, Object data) {
		List<Object> toReturn = null;
		Set<OWLIndividual> individuals = (Set<OWLIndividual>) data;
		String propertyName = node.getPropertyName();
		ProtegeDescriptionFacetExtractor facetExtractor = new ProtegeDescriptionFacetExtractor(
				this.modelManager);
		node.jjtAccept(facetExtractor, data);
		OWLDescription extractedDescription = facetExtractor
				.getExtractedDescription() == null ? this.modelManager
				.getOWLDataFactory().getOWLThing() : facetExtractor
				.getExtractedDescription();
		if (node.isEnd()) {
			toReturn = new ArrayList<Object>();
			for (OWLIndividual individual : individuals) {
				try {
					toReturn.addAll(this.fetch(individual, propertyName, true,
							extractedDescription));
				} catch (UnsupportedDataTypeException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (OWLReasonerException e) {
					e.printStackTrace();
				}
			}
		} else {
			for (OWLIndividual individual : individuals) {
				for (Node child : node.children) {
					Set<Object> fillers;
					try {
						fillers = (Set<Object>) this.fetch(individual,
								propertyName, false, extractedDescription);
						toReturn = new ArrayList<Object>();
						if (child instanceof MAEPropertyChain) {
							toReturn.addAll((List<Object>) child.jjtAccept(
									this, fillers));
						}
					} catch (UnsupportedDataTypeException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					} catch (OWLReasonerException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
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

	/**
	 * @return the report
	 */
	public EvaluationReport getReport() {
		return this.report;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEStoreTo,
	 *      java.lang.Object)
	 */
	public Object visit(MAEStoreTo node, Object data) {
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEPropertyFacet,
	 *      java.lang.Object)
	 */
	public Object visit(MAEPropertyFacet node, Object data) {
		return null;
	}
}
