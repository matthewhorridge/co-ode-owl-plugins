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

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.coode.oae.utils.ParserFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor;
import org.semanticweb.owlapi.search.EntitySearcher;

import uk.ac.manchester.mae.parser.ArithmeticsParser;
import uk.ac.manchester.mae.parser.MAEStart;
import uk.ac.manchester.mae.parser.ParseException;

/**
 * @author Luigi Iannone The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Mar 12, 2008
 */
public class PropertyVisitor implements OWLPropertyExpressionVisitor {

    protected Set<String> extractedFormulaStrings = new HashSet<>();
    private final Set<OWLOntology> ontologies;

    public PropertyVisitor(Set<OWLOntology> ontologies) {
        this.ontologies = ontologies;
    }

    /**
     * No formula can be attached to an OWLObjectProperty. This visitor will not
     * act on it
     */
    @Override
    public void visit(OWLObjectProperty property) {}

    @Override
    public void visit(OWLObjectInverseOf property) {}

    /**
     * Will extract the annotations containing the formulas
     */
    @Override
    public void visit(OWLDataProperty property) {
        Set<OWLAnnotation> annotations = new HashSet<>();
        for (OWLOntology ontology : ontologies) {
            annotations.addAll(
                    EntitySearcher.getAnnotations(property.getIRI(), ontology));
        }
        for (OWLAnnotation annotation : annotations) {
            IRI annotationURI = annotation.getProperty().getIRI();
            String annotationNameSpace = annotationURI.getNamespace();
            if (Constants.FORMULA_NAMESPACE_URI_STRING
                    .equals(annotationNameSpace)) {
                String annotationBody = ((OWLLiteral) annotation.getValue())
                        .getLiteral();
                extractedFormulaStrings.add(annotationBody);
            }
        }
    }

    @Deprecated
    /**
     * @return the extractedFormulas
     */
    public Set<String> getExtractedFormulaStrings() {
        return extractedFormulaStrings;
    }

    public Set<MAEStart> getExtractedFormulas() {
        Set<MAEStart> toReturn = new HashSet<>(extractedFormulaStrings.size());
        for (String formulaBody : extractedFormulaStrings) {
            ParserFactory.initParser(formulaBody, ontologies);
            try {
                toReturn.add((MAEStart) ArithmeticsParser.Start());
            } catch (ParseException e) {
                Logger.getLogger(this.getClass().toString())
                .warn("The formula body " + formulaBody
                        + " could not be correctly parsed it will be skipped\n"
                        + e.getMessage(), e);
            }
        }
        return toReturn;
    }

    @Override
    public void visit(OWLAnnotationProperty property) {}
}
