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
package org.coode.oae.utils;

import java.util.Set;

import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

/**
 * @author Luigi Iannone The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Aug 12, 2008
 */
public class RenderingOWLEntityCheckerNoModelManager
        implements OWLEntityChecker {

    Set<OWLOntology> ontologies;
    protected ShortFormEntityChecker shortFormEntityChecker = null;

    /**
     * @param owlModelManager
     */
    public RenderingOWLEntityCheckerNoModelManager(
            Set<OWLOntology> ontologies) {
        this.ontologies = ontologies;
        shortFormEntityChecker = new ShortFormEntityChecker(
                new BidirectionalShortFormProviderAdapter(ontologies,
                        new SimpleShortFormProvider()));
    }

    private OWLClass getOWLClassByFullURI(String uri) {
        for (OWLOntology o : ontologies) {
            Set<OWLClass> classes = o.getClassesInSignature();
            for (OWLClass c : classes) {
                if (c.getIRI().toString().equals(uri)) {
                    return c;
                }
            }
        }
        return null;
    }

    private OWLDataProperty getOWLDataPropertyByFullURI(String uri) {
        for (OWLOntology o : ontologies) {
            Set<OWLDataProperty> props = o.getDataPropertiesInSignature();
            for (OWLDataProperty c : props) {
                if (c.getIRI().toString().equals(uri)) {
                    return c;
                }
            }
        }
        return null;
    }

    private OWLObjectProperty getOWLObjectPropertyByFullURI(String uri) {
        for (OWLOntology o : ontologies) {
            Set<OWLObjectProperty> props = o.getObjectPropertiesInSignature();
            for (OWLObjectProperty c : props) {
                if (c.getIRI().toString().equals(uri)) {
                    return c;
                }
            }
        }
        return null;
    }

    private OWLAnnotationProperty
            getOWLAnnotationPropertyByFullURI(String uri) {
        for (OWLOntology o : ontologies) {
            Set<OWLAnnotationProperty> props = o
                    .getAnnotationPropertiesInSignature();
            for (OWLAnnotationProperty c : props) {
                if (c.getIRI().toString().equals(uri)) {
                    return c;
                }
            }
        }
        return null;
    }

    private OWLNamedIndividual getOWLIndividualByFullURI(String uri) {
        for (OWLOntology o : ontologies) {
            Set<OWLNamedIndividual> props = o.getIndividualsInSignature();
            for (OWLNamedIndividual c : props) {
                if (c.getIRI().toString().equals(uri)) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * @see org.semanticweb.owl.expression.OWLEntityChecker#getOWLClass(java.lang.String)
     */
    @Override
    public OWLClass getOWLClass(String name) {
        OWLClass toReturn = getOWLClassByFullURI(name);
        if (toReturn == null) {
            toReturn = shortFormEntityChecker.getOWLClass(name);
        }
        if (name.length() > 0 && toReturn == null) {
            toReturn = shortFormEntityChecker
                    .getOWLClass(XMLUtils.getNCNameSuffix(name));
        }
        return toReturn;
    }

    /**
     * @see org.semanticweb.owl.expression.OWLEntityChecker#getOWLDataProperty(java.lang.String)
     */
    @Override
    public OWLDataProperty getOWLDataProperty(String name) {
        OWLDataProperty toReturn = getOWLDataPropertyByFullURI(name);
        if (toReturn == null) {
            toReturn = shortFormEntityChecker.getOWLDataProperty(name);
        }
        if (name.length() > 0 && toReturn == null) {
            toReturn = shortFormEntityChecker
                    .getOWLDataProperty(XMLUtils.getNCNameSuffix(name));
        }
        return toReturn;
    }

    @Override
    public OWLDatatype getOWLDatatype(String name) {
        return shortFormEntityChecker.getOWLDatatype(name);
    }

    @Override
    public OWLNamedIndividual getOWLIndividual(String name) {
        OWLNamedIndividual toReturn = getOWLIndividualByFullURI(name);
        if (toReturn == null) {
            toReturn = shortFormEntityChecker.getOWLIndividual(name);
        }
        if (name.length() > 0 && toReturn == null) {
            toReturn = shortFormEntityChecker
                    .getOWLIndividual(XMLUtils.getNCNameSuffix(name));
        }
        return toReturn;
    }

    @Override
    public OWLObjectProperty getOWLObjectProperty(String name) {
        OWLObjectProperty toReturn = getOWLObjectPropertyByFullURI(name);
        if (toReturn == null) {
            toReturn = shortFormEntityChecker.getOWLObjectProperty(name);
        }
        if (name.length() > 0 && toReturn == null) {
            toReturn = shortFormEntityChecker
                    .getOWLObjectProperty(XMLUtils.getNCNameSuffix(name));
        }
        return toReturn;
    }

    @Override
    public OWLAnnotationProperty getOWLAnnotationProperty(String name) {
        OWLAnnotationProperty toReturn = getOWLAnnotationPropertyByFullURI(
                name);
        if (toReturn == null) {
            toReturn = shortFormEntityChecker.getOWLAnnotationProperty(name);
        }
        if (name.length() > 0 && toReturn == null) {
            toReturn = shortFormEntityChecker
                    .getOWLAnnotationProperty(XMLUtils.getNCNameSuffix(name));
        }
        return toReturn;
    }
}
