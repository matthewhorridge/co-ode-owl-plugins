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
package uk.ac.manchester.mae.evaluation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.coode.xml.OWLOntologyNamespaceManager;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.manchester.mae.MAEStart;
import uk.ac.manchester.mae.PropertyVisitor;
import uk.ac.manchester.mae.UnresolvedSymbolsException;
import uk.ac.manchester.mae.report.EvaluationReport;
import uk.ac.manchester.mae.report.ExceptionReportWriter;
import uk.ac.manchester.mae.report.ResultReportWriter;
import uk.ac.manchester.mae.visitor.ClassExtractor;
import uk.ac.manchester.mae.visitor.DescriptionFacetExtractor;
import uk.ac.manchester.mae.visitor.Writer;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Apr 30, 2008
 */
public class Evaluator {
	private Set<OWLOntology> ontologies;
	private OWLOntologyManager ontologyManager;
	private OWLReasoner reasoner;
	private EvaluationReport report;
	private OWLOntology ontology;

	/**
	 * @param ontologyManager
	 * @throws OWLReasonerException
	 */
	public Evaluator(OWLOntology ontology, OWLOntologyManager ontologyManager,
			OWLReasoner reasoner) throws OWLReasonerException {
		this.ontology = ontology;
		this.ontologyManager = ontologyManager;
		this.report = new EvaluationReport();
		new OWLOntologyNamespaceManager(this.ontologyManager, ontology);
		this.ontologies = this.ontologyManager.getImportsClosure(ontology);
		this.reasoner = reasoner;
		this.reasoner.loadOntologies(this.ontologies);
	}

	public IndividualEvaluationResult evaluate(OWLIndividual individual,
			OWLDataProperty dataProperty, MAEStart formula, boolean write) {
		IndividualEvaluationResult toReturn = null;
		ClassExtractor classExtractor = new ClassExtractor(this.ontologies,
				this.ontologyManager);
		formula.jjtAccept(classExtractor, null);
		OWLDescription description = classExtractor.getClassDescription();
		try {
			if (!this.reasoner.isClassified()) {
				this.reasoner.classify();
			}
			if (this.reasoner.hasType(individual, description, false)) {
				// Must pick only one formula for each individual
				AlternativeFormulaPicker picker = new AlternativeFormulaPicker(individual, formula,
						this.ontologies, this.reasoner, this.ontologyManager);
				dataProperty.accept(picker);
				MAEStart pickedFormula = picker.pickFormula();
				if (pickedFormula == null) {
					BindingAssigner assigner = new BindingAssigner(individual,
							this.ontologyManager, this.ontologies,
							this.reasoner);
					formula.jjtAccept(assigner, null);
					Set<BindingAssignment> bindingAssignments = assigner
							.getBindingAssignments();
					SimpleFormulaEvaluator simpleFormulaEvaluator = new SimpleFormulaEvaluator(
							bindingAssignments);
					formula.jjtAccept(simpleFormulaEvaluator, null);
					EvaluationResult evaluationResults = simpleFormulaEvaluator
							.getEvaluationResults();
					if (evaluationResults != null) {
						toReturn = new IndividualEvaluationResult(individual,
								evaluationResults);
						ResultReportWriter rrw = new ResultReportWriter(
								dataProperty, formula, toReturn);
						this.report.accept(rrw, null);
						if (write) {
							Writer writer = new Writer(individual,
									dataProperty,
									evaluationResults.getValues(),
									this.ontology, this.reasoner,
									this.ontologyManager, this.report,
									new DescriptionFacetExtractor(
											this.ontologyManager,
											this.ontologies));
							formula.jjtAccept(writer, null);
						}
					} else {
						ExceptionReportWriter erw = new ExceptionReportWriter(
								dataProperty, formula,
								new UnresolvedSymbolsException(
										"There are some unresolved variables for the individual "
												+ individual));
						this.report.accept(erw, null);
					}
				}
			}
		} catch (Exception e) {
			EvaluationReport evaluationReport = this.getReport();
			ExceptionReportWriter erw = new ExceptionReportWriter(dataProperty,
					formula, e);
			evaluationReport.accept(erw, null);
			e.printStackTrace();
		}
		return toReturn;
	}

	public FormulaEvaluationResult evaluate(OWLDataProperty dataProperty,
			MAEStart formula, boolean write) {
		FormulaEvaluationResult toReturn = null;
		Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();
		for (OWLOntology ontology : this.ontologies) {
			individuals.addAll(ontology.getReferencedIndividuals());
		}
		Set<IndividualEvaluationResult> individualEvaluationResults = new HashSet<IndividualEvaluationResult>();
		for (OWLIndividual individual : individuals) {
			IndividualEvaluationResult individualEvaluationResult = this
					.evaluate(individual, dataProperty, formula, write);
			if (individualEvaluationResult != null) {
				individualEvaluationResults.add(individualEvaluationResult);
			}
		}
		toReturn = new FormulaEvaluationResult(formula,
				individualEvaluationResults);
		return toReturn;
	}

	public PropertyEvaluationResult evaluate(OWLDataProperty dataProperty,
			boolean write) {
		PropertyEvaluationResult toReturn = null;
		PropertyVisitor propertyVisitor = new PropertyVisitor(this.ontologies);
		dataProperty.accept(propertyVisitor);
		Set<MAEStart> formulas = propertyVisitor.getExtractedFormulas();
		if (!formulas.isEmpty()) {
			Set<FormulaEvaluationResult> formulaEvaluationResults = new HashSet<FormulaEvaluationResult>(
					formulas.size());
			for (MAEStart start : formulas) {
				FormulaEvaluationResult formulaEvaluationResult = this
						.evaluate(dataProperty, start, write);
				formulaEvaluationResults.add(formulaEvaluationResult);
			}
			toReturn = new PropertyEvaluationResult(dataProperty,
					formulaEvaluationResults);
		}
		return toReturn;
	}

	public Set<PropertyEvaluationResult> evaluate(boolean write) {
		Set<OWLDataProperty> properties = new HashSet<OWLDataProperty>();
		for (OWLOntology ontology : this.ontologies) {
			properties.addAll(ontology.getReferencedDataProperties());
		}
		Set<PropertyEvaluationResult> toReturn = new HashSet<PropertyEvaluationResult>(
				properties.size());
		for (OWLDataProperty dataProperty : properties) {
			PropertyEvaluationResult propertyEvaluationResult = this.evaluate(
					dataProperty, write);
			if (propertyEvaluationResult != null) {
				toReturn.add(propertyEvaluationResult);
			}
		}
		return toReturn;
	}

	public EvaluationReport getReport() {
		return this.report == null ? new EvaluationReport() : this.report;
	}

	@SuppressWarnings("unchecked")
	private static OWLReasoner createReasoner(
			OWLOntologyManager ontologyManager, String reasonerClassName) {
		try {
			Class<OWLReasoner> reasonerClass = (Class<OWLReasoner>) Class
					.forName(reasonerClassName);
			Constructor<OWLReasoner> con = reasonerClass
					.getConstructor(OWLOntologyManager.class);
			return con.newInstance(ontologyManager);
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

	public static void main(String[] args) {
		String ontologyURI = args[0];
		String reasonerClassName = args[1];
		OWLOntologyManager ontologyManager = OWLManager
				.createOWLOntologyManager();
		try {
			OWLOntology ontology = ontologyManager
					.loadOntologyFromPhysicalURI(URI.create(ontologyURI));
			Evaluator evaluator = new Evaluator(ontology, ontologyManager,
					createReasoner(ontologyManager, reasonerClassName));
			Set<PropertyEvaluationResult> evaluation = evaluator
					.evaluate(false);
			System.out.println("Evaluation carried out: ");
			for (PropertyEvaluationResult propertyEvaluationResult : evaluation) {
				System.out.println(propertyEvaluationResult);
			}
		} catch (OWLOntologyCreationException e) {
			System.out
					.println("Problems in loading the ontlogy formt the URI: "
							+ ontologyURI);
			e.printStackTrace();
		} catch (OWLReasonerException e) {
			System.out
					.println("Problem in loading the ontlogies on to the reasoner ");
			e.printStackTrace();
		}
	}
}
