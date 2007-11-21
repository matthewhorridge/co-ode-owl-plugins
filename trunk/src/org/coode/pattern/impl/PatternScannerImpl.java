package org.coode.pattern.impl;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;
import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.api.PatternScanner;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 1, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
class PatternScannerImpl implements PatternScanner {

    public Set<Pattern> scanForPatterns(OWLModelManager mngr,
                                           Set<OWLOntology> ontologies,
                                           Set<PatternDescriptor> patternDescrs) {
        Set<Pattern> patterns = new HashSet<Pattern>();

        Set<OWLClass> owlClasses = null;
        Set<OWLProperty> owlProperties = null;
        Set<OWLIndividual> owlIndividuals = null;

        // first initialise the caches if they are needed by the loaded descriptors
        for (PatternDescriptor patternDescriptor : patternDescrs) {
            if (patternDescriptor.isOWLClassPattern() && owlClasses == null) {
                owlClasses = new HashSet<OWLClass>();
            }
            if (patternDescriptor.isOWLPropertyPattern() && owlProperties == null) {
                owlProperties = new HashSet<OWLProperty>();
            }
            if (patternDescriptor.isOWLIndividualPattern() && owlIndividuals == null) {
                owlIndividuals = new HashSet<OWLIndividual>();
            }
        }

        // then build up the caches
        for (OWLOntology ontology : ontologies){
            if (owlClasses != null) {
                owlClasses = ontology.getReferencedClasses();
            }
            if (owlProperties != null) {
                owlProperties.addAll(ontology.getReferencedObjectProperties());
                owlProperties.addAll(ontology.getReferencedDataProperties());
            }
            if (owlIndividuals != null) {
                owlIndividuals = new HashSet<OWLIndividual>();
                owlIndividuals.addAll(ontology.getReferencedIndividuals());
            }
        }

        // then run through the ontology looking for matches using the pattern descriptors 
        for (PatternDescriptor patternDescriptor : patternDescrs) {

            if (patternDescriptor.isOWLClassPattern()) {
                for (OWLClass cls : owlClasses) {
                    Pattern pattern = patternDescriptor.getPattern(cls, mngr);
                    if (pattern != null) {
                        patterns.add(pattern);
                    }
                }
            }

            if (patternDescriptor.isOWLPropertyPattern()) {
                for (OWLProperty property : owlProperties) {
                    Pattern pattern = patternDescriptor.getPattern(property, mngr);
                    if (pattern != null) {
                        patterns.add(pattern);
                    }
                }
            }

            if (patternDescriptor.isOWLIndividualPattern()) {
                for (OWLIndividual individual : owlIndividuals) {
                    Pattern pattern = patternDescriptor.getPattern(individual, mngr);
                    if (pattern != null) {
                        patterns.add(pattern);
                    }
                }
            }
        }

        return patterns;
    }
}
