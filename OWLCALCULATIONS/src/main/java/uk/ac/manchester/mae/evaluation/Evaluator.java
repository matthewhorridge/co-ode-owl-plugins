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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.mae.PropertyVisitor;
import uk.ac.manchester.mae.UnresolvedSymbolsException;
import uk.ac.manchester.mae.parser.MAEStart;
import uk.ac.manchester.mae.report.EvaluationReport;
import uk.ac.manchester.mae.report.ExceptionReportWriter;
import uk.ac.manchester.mae.report.ResultReportWriter;
import uk.ac.manchester.mae.visitor.ClassExtractor;
import uk.ac.manchester.mae.visitor.Writer;

/**
 * @author Luigi Iannone The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 30, 2008
 */
public class Evaluator {

    private final Set<OWLOntology> ontologies;
    private final OWLOntologyManager ontologyManager;
    private final OWLReasoner reasoner;
    private final EvaluationReport report;
    private final OWLOntology ontology;

    /**
     * @param ontologyManager
     */
    public Evaluator(OWLOntology ontology, OWLOntologyManager ontologyManager,
            OWLReasonerFactory reasonerFactory) {
        this(ontology, ontologyManager,
                reasonerFactory.createReasoner(ontology));
    }

    /**
     * @param ontologyManager
     */
    public Evaluator(OWLOntology ontology, OWLOntologyManager ontologyManager,
            OWLReasoner reasoner) {
        this.ontology = ontology;
        this.ontologyManager = ontologyManager;
        report = new EvaluationReport();
        // new OWLOntologyNamespaceManager(this.ontologyManager, ontology);
        ontologies = this.ontologyManager.getImportsClosure(ontology);
        this.reasoner = reasoner;
    }

    public IndividualEvaluationResult evaluate(OWLNamedIndividual individual,
            OWLDataProperty dataProperty, MAEStart formula, boolean write) {
        IndividualEvaluationResult toReturn = null;
        ClassExtractor classExtractor = new ClassExtractor(ontologies,
                ontologyManager);
        formula.jjtAccept(classExtractor, null);
        OWLClassExpression description = classExtractor.getClassDescription();
        try {
            reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
            if (reasoner.isEntailed(ontologyManager.getOWLDataFactory()
                    .getOWLClassAssertionAxiom(description, individual))) {
                // Must pick only one formula for each individual
                AlternativeFormulaPicker picker = new AlternativeFormulaPicker(
                        individual, formula, ontologies, reasoner,
                        ontologyManager);
                dataProperty.accept(picker);
                MAEStart pickedFormula = picker.pickFormula();
                if (pickedFormula == null) {
                    BindingAssigner assigner = new BindingAssigner(individual,
                            ontologyManager, ontologies, reasoner);
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
                        report.accept(rrw, null);
                        if (write) {
                            Writer writer = new Writer(individual, dataProperty,
                                    evaluationResults.getValues(), ontology,
                                    reasoner, ontologyManager, report);
                            formula.jjtAccept(writer, null);
                        }
                    } else {
                        ExceptionReportWriter erw = new ExceptionReportWriter(
                                dataProperty, formula,
                                new UnresolvedSymbolsException(
                                        "There are some unresolved variables for the individual "
                                                + individual));
                        report.accept(erw, null);
                    }
                }
            }
        } catch (Exception e) {
            EvaluationReport evaluationReport = getReport();
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
        Set<OWLNamedIndividual> individuals = new HashSet<>();
        for (OWLOntology onto : ontologies) {
            individuals.addAll(onto.getIndividualsInSignature());
        }
        Set<IndividualEvaluationResult> individualEvaluationResults = new HashSet<>();
        for (OWLNamedIndividual individual : individuals) {
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
        PropertyVisitor propertyVisitor = new PropertyVisitor(ontologies);
        dataProperty.accept(propertyVisitor);
        Set<MAEStart> formulas = propertyVisitor.getExtractedFormulas();
        if (!formulas.isEmpty()) {
            Set<FormulaEvaluationResult> formulaEvaluationResults = new HashSet<>(
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
        Set<OWLDataProperty> properties = new HashSet<>();
        for (OWLOntology onto : ontologies) {
            properties.addAll(onto.getDataPropertiesInSignature());
        }
        Set<PropertyEvaluationResult> toReturn = new HashSet<>(
                properties.size());
        for (OWLDataProperty dataProperty : properties) {
            PropertyEvaluationResult propertyEvaluationResult = this
                    .evaluate(dataProperty, write);
            if (propertyEvaluationResult != null) {
                toReturn.add(propertyEvaluationResult);
            }
        }
        return toReturn;
    }

    public EvaluationReport getReport() {
        return report == null ? new EvaluationReport() : report;
    }

    public static void main(String[] args) {
        String ontologyURI = args[0];
        String reasonerClassName = args[1];
        OWLOntologyManager ontologyManager = OWLManager
                .createOWLOntologyManager();
        try {
            OWLOntology ontology = ontologyManager
                    .loadOntologyFromOntologyDocument(IRI.create(ontologyURI));
            OWLReasonerFactory reasonerFactory = (OWLReasonerFactory) Class
                    .forName(reasonerClassName).newInstance();
            Evaluator evaluator = new Evaluator(ontology, ontologyManager,
                    reasonerFactory);
            Set<PropertyEvaluationResult> evaluation = evaluator
                    .evaluate(false);
            System.out.println("Evaluation carried out: ");
            for (PropertyEvaluationResult propertyEvaluationResult : evaluation) {
                System.out.println(propertyEvaluationResult);
            }
        } catch (OWLOntologyCreationException e) {
            System.out.println("Problems in loading the ontlogy formt the URI: "
                    + ontologyURI);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(
                    "Problem in loading the ontlogies on to the reasoner ");
            e.printStackTrace();
        }
    }
}
