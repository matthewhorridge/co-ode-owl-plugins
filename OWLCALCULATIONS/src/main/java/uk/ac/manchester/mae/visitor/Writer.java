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
package uk.ac.manchester.mae.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import uk.ac.manchester.mae.ConflictStrategy;
import uk.ac.manchester.mae.ConflictStrategyFactory;
import uk.ac.manchester.mae.Constants;
import uk.ac.manchester.mae.EvaluationException;
import uk.ac.manchester.mae.MoreThanOneValueForFunctionalPropertyException;
import uk.ac.manchester.mae.UnsupportedDataTypeException;
import uk.ac.manchester.mae.parser.ArithmeticsParserVisitor;
import uk.ac.manchester.mae.parser.MAEAdd;
import uk.ac.manchester.mae.parser.MAEBigSum;
import uk.ac.manchester.mae.parser.MAEBinding;
import uk.ac.manchester.mae.parser.MAEConflictStrategy;
import uk.ac.manchester.mae.parser.MAEIdentifier;
import uk.ac.manchester.mae.parser.MAEIntNode;
import uk.ac.manchester.mae.parser.MAEMult;
import uk.ac.manchester.mae.parser.MAEPower;
import uk.ac.manchester.mae.parser.MAEStart;
import uk.ac.manchester.mae.parser.MAEStoreTo;
import uk.ac.manchester.mae.parser.MAEmanSyntaxClassExpression;
import uk.ac.manchester.mae.parser.MAEpropertyChainCell;
import uk.ac.manchester.mae.parser.MAEpropertyChainExpression;
import uk.ac.manchester.mae.parser.SimpleNode;
import uk.ac.manchester.mae.report.EvaluationReport;
import uk.ac.manchester.mae.report.ExceptionReportWriter;
import uk.ac.manchester.mae.report.FormulaReportWriter;

/**
 * @author Luigi Iannone The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 22, 2008
 */
public class Writer implements ArithmeticsParserVisitor {

    protected ConflictStrategy conflictStrategy = null;
    protected OWLOntologyManager ontologyManager;
    protected EvaluationReport evaluationReport;
    protected OWLOntology startingOntology;
    protected Set<OWLOntology> ontologies;
    protected OWLNamedIndividual currentIndividual;
    protected OWLDataProperty dataProperty;
    protected Object results;
    protected MAEStart startingFormula;
    protected OWLReasoner reasoner;

    /**
     * @param currentIndividual
     * @param dataProperty
     * @param results
     * @param startingOntology
     * @param ontologyManager
     */
    public Writer(OWLNamedIndividual currentIndividual,
            OWLDataProperty dataProperty, Object results,
            OWLOntology startingOntology, OWLReasoner reasoner,
            OWLOntologyManager ontologyManager,
            EvaluationReport evaluationReport) {
        this.evaluationReport = evaluationReport == null
                ? new EvaluationReport() : evaluationReport;
        this.currentIndividual = currentIndividual;
        this.dataProperty = dataProperty;
        this.results = results;
        this.startingOntology = startingOntology;
        this.reasoner = reasoner;
        this.ontologyManager = ontologyManager;
        ontologies = ontologyManager.getImportsClosure(startingOntology);
    }

    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEStart node, Object data) {
        startingFormula = node;
        ConflictStrategyExtractor conflictStrategyExtractor = new ConflictStrategyExtractor();
        node.jjtAccept(conflictStrategyExtractor, data);
        conflictStrategy = conflictStrategyExtractor
                .getExtractedConflictStrategy();
        MAEpropertyChainExpression storageChainModel = null;
        StorageExtractor storageExtractor = new StorageExtractor();
        node.jjtAccept(storageExtractor, data);
        storageChainModel = storageExtractor.getExtractedStorage();
        if (storageChainModel != null) {
            this.visit(storageChainModel, data);
        } else {
            try {
                write(currentIndividual, dataProperty, results);
            } catch (OWLOntologyChangeException e) {
                FormulaReportWriter resultReportWriter = new ExceptionReportWriter(
                        dataProperty, node, e);
                evaluationReport.accept(resultReportWriter, null);
            } catch (UnsupportedDataTypeException e) {
                FormulaReportWriter resultReportWriter = new ExceptionReportWriter(
                        dataProperty, node, e);
                evaluationReport.accept(resultReportWriter, null);
            } catch (EvaluationException e) {
                FormulaReportWriter resultReportWriter = new ExceptionReportWriter(
                        dataProperty, node, e);
                evaluationReport.accept(resultReportWriter, null);
            }
        }
        return storageChainModel;
    }

    @Override
    public Object visit(MAEConflictStrategy node, Object data) {
        String strategyName = node.getStrategyName();
        ConflictStrategyFactory.getStrategy(strategyName);
        return null;
    }

    @Override
    public Object visit(MAEStoreTo node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEmanSyntaxClassExpression node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEBinding node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEpropertyChainExpression node, Object data) {
        List<MAEpropertyChainCell> cells = node.getCells();
        try {
            for (int i = 0; i < cells.size() - 1; i++) {
                String propertyName = cells.get(i).getPropertyName();
                String facetString = cells.get(i).getFacet();
                OWLClassExpression facet = null;
                if (facetString != null) {
                    ManchesterOWLSyntaxParser parser = MAEAdapter.getParser(
                            ontologies, ontologyManager.getOWLDataFactory());
                    parser.setStringToParse(facetString);
                    facet = parser.parseClassExpression();
                }
                walkProperty(propertyName, facet);
            }
            dataProperty = MAEAdapter.getChecker(ontologies).getOWLDataProperty(
                    cells.get(cells.size() - 1).getPropertyName());
            write(currentIndividual, dataProperty, results);
        } catch (Exception e) {
            ExceptionReportWriter erw = new ExceptionReportWriter(dataProperty,
                    startingFormula, e);
            evaluationReport.accept(erw, null);
        }
        return null;
    }

    private void walkProperty(String propertyName,
            OWLClassExpression facetDescription) throws 
                    UnsupportedDataTypeException, OWLOntologyChangeException {
        Collection<Object> fillers = fetch(currentIndividual, propertyName,
                false, facetDescription);
        if (fillers != null && !fillers.isEmpty()) {
            currentIndividual = (OWLNamedIndividual) fillers.iterator().next();
        } else {
            ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
            String newIndividualName = shortFormProvider
                    .getShortForm(currentIndividual);
            if (!facetDescription
                    .equals(ontologyManager.getOWLDataFactory().getOWLThing())
                    && !facetDescription.isAnonymous()) {
                newIndividualName += shortFormProvider
                        .getShortForm(facetDescription.asOWLClass());
            } else {
                newIndividualName += propertyName + "Filler";
            }
            OWLNamedIndividual newFiller = ontologyManager.getOWLDataFactory()
                    .getOWLNamedIndividual(
                            IRI.create(Constants.FORMULA_NAMESPACE_URI_STRING
                                    + newIndividualName));
            AddAxiom addAxiom = new AddAxiom(startingOntology,
                    ontologyManager.getOWLDataFactory()
                            .getOWLObjectPropertyAssertionAxiom(
                                    ontologyManager.getOWLDataFactory()
                                            .getOWLObjectProperty(
                                                    IRI.create(propertyName)),
                                    currentIndividual, newFiller));
            ontologyManager.applyChange(addAxiom);
            addAxiom = new AddAxiom(startingOntology,
                    ontologyManager.getOWLDataFactory()
                            .getOWLClassAssertionAxiom(facetDescription,
                                    newFiller));
            ontologyManager.applyChange(addAxiom);
            currentIndividual = newFiller;
        }
    }

    @Override
    public Object visit(MAEAdd node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEMult node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEPower node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEIntNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEIdentifier node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEBigSum node, Object data) {
        return null;
    }

    private void write(OWLNamedIndividual individual, OWLDataProperty dataProp,
            Object object) throws EvaluationException,
                    OWLOntologyChangeException, UnsupportedDataTypeException {
        Collection<OWLLiteral> oldValues = EntitySearcher
                .getDataPropertyValues(individual, dataProp, startingOntology);
        if (!EntitySearcher.isFunctional(dataProp, ontologies)
                || oldValues.isEmpty()) {
            if (object instanceof Collection<?>) {
                for (Object newValue : (Collection<?>) object) {
                    writeSingleValue(individual, dataProp, newValue);
                }
            } else {
                writeSingleValue(individual, dataProp, object);
            }
        } else if (EntitySearcher.isFunctional(dataProp, ontologies)) {
            if (object instanceof Collection<?>
                    && ((Collection<?>) object).size() > 1) {
                throw new MoreThanOneValueForFunctionalPropertyException(
                        "More than one value for the functional property "
                                + dataProp + " for the individual "
                                + individual.getIRI().toString());
            }
            if (!oldValues.isEmpty()) {
                OWLDataPropertyAssertionAxiom oldAssertion = ontologyManager
                        .getOWLDataFactory()
                        .getOWLDataPropertyAssertionAxiom(dataProp, individual,
                                oldValues.iterator().next());
                if (conflictStrategy != null) {
                    conflictStrategy.solve(individual, oldAssertion,
                            convert2OWLConstant(object instanceof Collection<?>
                                    ? ((Collection<?>) object).iterator().next()
                                    : object),
                            ontologies, ontologyManager);
                } else {
                    writeSingleValue(individual, dataProp,
                            object instanceof Collection<?>
                                    ? ((Collection<?>) object).iterator().next()
                                    : object);
                }
            }
        }
    }

    private void writeSingleValue(OWLIndividual individual,
            OWLDataProperty dataProp, Object newValue)
                    throws UnsupportedDataTypeException,
                    OWLOntologyChangeException {
        OWLLiteral valueAsOWLConstant = convert2OWLConstant(newValue);
        AddAxiom addAxiom = new AddAxiom(startingOntology,
                ontologyManager.getOWLDataFactory()
                        .getOWLDataPropertyAssertionAxiom(dataProp, individual,
                                valueAsOWLConstant));
        ontologyManager.applyChange(addAxiom);
    }

    private OWLLiteral convert2OWLConstant(Object newValue)
            throws UnsupportedDataTypeException {
        OWLLiteral toReturn = null;
        if (newValue instanceof Double) {
            toReturn = ontologyManager.getOWLDataFactory()
                    .getOWLLiteral((Double) newValue);
        } else {
            throw new UnsupportedDataTypeException(
                    newValue.getClass().getName());
        }
        return toReturn;
    }

    private Collection<Object> fetch(OWLIndividual currentInd,
            String propertyName, boolean isDatatype,
            OWLClassExpression facetDescription)
                    throws UnsupportedDataTypeException {
        Collection<Object> toReturn = null;
        reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
        Iterator<OWLOntology> it = ontologies.iterator();
        boolean found = false;
        OWLOntology ontology;
        while (!found && it.hasNext()) {
            ontology = it.next();
            if (isDatatype) {
                OWLDataProperty dataProp = ontologyManager.getOWLDataFactory()
                        .getOWLDataProperty(IRI.create(propertyName));
                Collection<OWLLiteral> values = EntitySearcher
                        .getDataPropertyValues(currentInd, dataProp, ontology);
                toReturn = new ArrayList<>();
                if (!values.isEmpty()) {
                    for (OWLLiteral value : values) {
                        toReturn.add(convertValue(value));
                    }
                }
            } else {
                OWLObjectProperty objectProperty = ontologyManager
                        .getOWLDataFactory()
                        .getOWLObjectProperty(IRI.create(propertyName));
                Collection<OWLIndividual> fillers = EntitySearcher
                        .getObjectPropertyValues(currentInd, objectProperty,
                                ontology);
                toReturn = new HashSet<>();
                if (!fillers.isEmpty()) {
                    for (OWLIndividual filler : fillers) {
                        if (reasoner.isEntailed(ontologyManager
                                .getOWLDataFactory().getOWLClassAssertionAxiom(
                                        facetDescription, filler))) {
                            toReturn.add(filler);
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    private static Object convertValue(OWLLiteral typedConstant)
            throws UnsupportedDataTypeException {
        OWLDatatype type = typedConstant.getDatatype();
        Object toReturn = null;
        // Rough conversion big if
        if (type.getIRI().equals(XSDVocabulary.INT.getIRI())
                || type.getIRI().equals(XSDVocabulary.INTEGER.getIRI())
                || type.getIRI().equals(XSDVocabulary.DOUBLE.getIRI())
                || type.getIRI().equals(XSDVocabulary.DECIMAL.getIRI())
                || type.getIRI().equals(XSDVocabulary.SHORT.getIRI())) {
            toReturn = new Double(
                    Double.parseDouble(typedConstant.getLiteral()));
        } else {
            throw new UnsupportedDataTypeException(
                    "Currently unsuported data type - " + type);
        }
        return toReturn;
    }
}
