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

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.mae.MoreThanOneFormulaPerIndividualException;
import uk.ac.manchester.mae.PropertyVisitor;
import uk.ac.manchester.mae.parser.MAEStart;
import uk.ac.manchester.mae.visitor.ClassExtractor;

/**
 * Looks for possible alternatives to a formula for a data property
 *
 * @author Luigi Iannone The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         May 1, 2008
 */
public class AlternativeFormulaPicker implements OWLPropertyExpressionVisitor {

    private final Set<OWLOntology> ontologies;
    private final OWLIndividual individual;
    private final MAEStart formula;
    private Set<MAEStart> formulas = null;
    private final OWLReasoner reasoner;
    private final OWLOntologyManager ontologyManager;
    private final OWLDataFactory df;

    /**
     * @param ontologies
     * @param individual
     * @param reasoner
     * @param ontologyManager
     */
    public AlternativeFormulaPicker(OWLIndividual individual, MAEStart formula,
            Set<OWLOntology> ontologies, OWLReasoner reasoner,
            OWLOntologyManager ontologyManager) {
        this.ontologies = ontologies;
        this.individual = individual;
        this.formula = formula;
        this.reasoner = reasoner;
        this.ontologyManager = ontologyManager;
        df = ontologyManager.getOWLDataFactory();
    }

    @Override
    public void visit(OWLObjectProperty property) {}

    @Override
    public void visit(OWLObjectInverseOf property) {}

    @Override
    public void visit(OWLDataProperty property) {
        PropertyVisitor propertyVisitor = new PropertyVisitor(ontologies);
        property.accept(propertyVisitor);
        formulas = propertyVisitor.getExtractedFormulas();
        formulas.remove(formula);
    }

    /**
     * @return null if there are not alternatives, a possible alternative
     *         otherwise
     * @throws OWLReasonerException
     * @throws MoreThanOneFormulaPerIndividualException
     *         if there is more than one alternative
     */
    public MAEStart pickFormula()
            throws MoreThanOneFormulaPerIndividualException {
        ClassExtractor classExtractor = new ClassExtractor(ontologies,
                ontologyManager);
        formula.jjtAccept(classExtractor, null);
        OWLClassExpression classDescription = classExtractor
                .getClassDescription() == null
                        ? ontologyManager.getOWLDataFactory().getOWLThing()
                        : classExtractor.getClassDescription();
        MAEStart toReturn = null;
                        reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
        for (MAEStart anotherFormula : new HashSet<>(formulas)) {
            anotherFormula.jjtAccept(classExtractor, null);
                            OWLClassExpression anotherFormulaClassDescription = classExtractor
                    .getClassDescription() == null
                            ? ontologyManager.getOWLDataFactory().getOWLThing()
                            : classExtractor.getClassDescription();
                                            if (!reasoner.isEntailed(df.getOWLClassAssertionAxiom(
                                                    anotherFormulaClassDescription, individual))
                                                    || reasoner.isEntailed(df.getOWLSubClassOfAxiom(
                                                            classDescription, anotherFormulaClassDescription))
                            && !reasoner.isEntailed(df.getOWLSubClassOfAxiom(
                                    anotherFormulaClassDescription,
                                    classDescription))) {
                formulas.remove(anotherFormula);
            }
        }
        // There must be at most one applicable formula per individual
        if (formulas.size() > 1) {
            throw new MoreThanOneFormulaPerIndividualException(
                    "There is more than one formula for individual "
                            + individual);
        } else {
            toReturn = formulas.size() > 0 ? formulas.iterator().next() : null;
        }
        return toReturn;
    }

    @Override
    public void visit(OWLAnnotationProperty property) {}
}
