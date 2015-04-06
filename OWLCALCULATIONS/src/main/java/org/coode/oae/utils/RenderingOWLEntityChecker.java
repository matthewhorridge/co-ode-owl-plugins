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

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

/**
 * @author Luigi Iannone The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Aug 12, 2008
 */
public class RenderingOWLEntityChecker implements OWLEntityChecker {

    protected OWLModelManager owlModelManager = null;
    protected ShortFormEntityChecker shortFormEntityChecker = null;

    /**
     * @param owlModelManager
     */
    public RenderingOWLEntityChecker(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        shortFormEntityChecker = new ShortFormEntityChecker(
                new BidirectionalShortFormProviderAdapter(
                        owlModelManager.getOntologies(),
                        new SimpleShortFormProvider()));
    }

    @Override
    public OWLClass getOWLClass(String name) {
        IRI iri = IRI.create(name);
        OWLClass toReturn = shortFormEntityChecker.getOWLClass(name);
        if (name.length() > 0 && toReturn == null) {
            toReturn = shortFormEntityChecker.getOWLClass(iri.getFragment());
        }
        if (toReturn == null) {
            toReturn = owlModelManager.getOWLDataFactory().getOWLClass(iri);
        }
        return toReturn;
    }

    @Override
    public OWLDataProperty getOWLDataProperty(String name) {
        IRI iri = IRI.create(name);
        OWLDataProperty toReturn = shortFormEntityChecker
                .getOWLDataProperty(name);
        if (name.length() > 0 && toReturn == null) {
            toReturn = shortFormEntityChecker
                    .getOWLDataProperty(iri.getFragment());
        }
        if (toReturn == null) {
            toReturn = owlModelManager.getOWLDataFactory()
                    .getOWLDataProperty(iri);
        }
        return toReturn;
    }

    @Override
    public OWLDatatype getOWLDatatype(String name) {
        return shortFormEntityChecker.getOWLDatatype(name);
    }

    @Override
    public OWLNamedIndividual getOWLIndividual(String name) {
        IRI iri = IRI.create(name);
        OWLNamedIndividual toReturn = shortFormEntityChecker
                .getOWLIndividual(name);
        if (name.length() > 0 && toReturn == null) {
            toReturn = shortFormEntityChecker
                    .getOWLIndividual(iri.getFragment());
        }
        if (toReturn == null) {
            toReturn = owlModelManager.getOWLDataFactory()
                    .getOWLNamedIndividual(iri);
        }
        return toReturn;
    }

    @Override
    public OWLObjectProperty getOWLObjectProperty(String name) {
        IRI iri = IRI.create(name);
        OWLObjectProperty toReturn = shortFormEntityChecker
                .getOWLObjectProperty(name);
        if (name.length() > 0 && toReturn == null) {
            toReturn = shortFormEntityChecker
                    .getOWLObjectProperty(iri.getFragment());
        }
        if (toReturn == null) {
            toReturn = owlModelManager.getOWLDataFactory()
                    .getOWLObjectProperty(iri);
        }
        return toReturn;
    }

    @Override
    public OWLAnnotationProperty getOWLAnnotationProperty(String name) {
        IRI iri = IRI.create(name);
        OWLAnnotationProperty toReturn = shortFormEntityChecker
                .getOWLAnnotationProperty(name);
        if (name.length() > 0 && toReturn == null) {
            toReturn = shortFormEntityChecker
                    .getOWLAnnotationProperty(iri.getFragment());
        }
        if (toReturn == null) {
            toReturn = owlModelManager.getOWLDataFactory()
                    .getOWLAnnotationProperty(iri);
        }
        return toReturn;
    }
}
