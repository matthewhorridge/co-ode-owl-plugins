package org.coode.pattern.valuepartition;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;
import org.protege.editor.owl.model.OWLModelManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
/*
* Copyright (C) 2007, University of Manchester
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

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 15, 2007<br><br>
 */
public class EntityCreator {

    private OWLModelManager mngr;

    public EntityCreator(OWLModelManager mngr) {
        this.mngr = mngr;
    }

    // @@TODO hacked - should use Protege entity creation mechanisms
    public OWLObjectProperty createProperty(String name) {
        if (name == null || name.length() == 0){
            throw new IllegalArgumentException("invalid property name for Value Partition: " + name);
        }
        OWLOntology ont = mngr.getActiveOntology();
        OWLEntity entity = mngr.getOWLEntity(name);
        if (entity != null && entity instanceof OWLObjectProperty) {
            return (OWLObjectProperty) entity;
        }
        else {
            try {
                return mngr.getOWLDataFactory().getOWLObjectProperty(new URI(ont.getURI().toString() + "#" + name));
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException("cannot create value partition property", e);
            }
        }
    }

    // @@TODO hacked - should use Protege entity creation mechanisms
    public OWLClass createClass(String name) {
        if (name == null || name.length() == 0){
            throw new IllegalArgumentException("invalid name for Value Partition: " + name);
        }
        OWLOntology ont = mngr.getActiveOntology();
        OWLEntity entity = mngr.getOWLEntity(name);
        if (entity != null && entity instanceof OWLClass) {
            return (OWLClass) entity;
        }
        else {
            try {
                return mngr.getOWLDataFactory().getOWLClass(new URI(ont.getURI().toString() + "#" + name));
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException("cannot create value partition root", e);
            }
        }
    }


    public List<OWLOntologyChange> renameEntity(OWLEntity entity, String name) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        final OWLDataFactory df = mngr.getOWLDataFactory();

        // remove the existing labels
        for (OWLOntology ont : mngr.getActiveOntologies()){
            for (OWLAnnotation annot : entity.getAnnotations(ont)){
                if (annot.getAnnotationURI().equals(OWLRDFVocabulary.RDFS_LABEL.getURI())){
                    changes.add(new RemoveAxiom(ont, df.getOWLEntityAnnotationAxiom(entity, annot)));
                }
            }
        }

        // add the new label
        final OWLUntypedConstant value = df.getOWLUntypedConstant(name);
        OWLAnnotation label = df.getOWLConstantAnnotation(OWLRDFVocabulary.RDFS_LABEL.getURI(), value);
        changes.add(new AddAxiom(mngr.getActiveOntology(), df.getOWLEntityAnnotationAxiom(entity, label)));
        return changes;
    }
}
