package changeServerPackage;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitorEx;
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
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.model.SetOntologyID;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Jan-2007<br><br>
 */
public class ReverseChangeGenerator implements OWLOntologyChangeVisitorEx<OWLOntologyChange> {

    @Override
    public OWLOntologyChange visit(AddAxiom change) {
        return new RemoveAxiom(change.getOntology(), change.getAxiom());
    }


    @Override
    public OWLOntologyChange visit(RemoveAxiom change) {
        return new AddAxiom(change.getOntology(), change.getAxiom());
    }


    @Override
    public OWLOntologyChange visit(SetOntologyID change) {
        return new SetOntologyID(change.getOntology(),
                change.getOriginalOntologyID());
    }

    @Override
    public OWLOntologyChange visit(AddImport change) {
        return new RemoveImport(change.getOntology(),
                change.getImportDeclaration());
    }

    @Override
    public OWLOntologyChange visit(RemoveImport change) {
        return new AddImport(change.getOntology(),
                change.getImportDeclaration());
    }

    @Override
    public OWLOntologyChange visit(AddOntologyAnnotation change) {
        return new RemoveOntologyAnnotation(change.getOntology(),
                change.getAnnotation());
    }

    @Override
    public OWLOntologyChange visit(RemoveOntologyAnnotation change) {
        return new AddOntologyAnnotation(change.getOntology(),
                change.getAnnotation());
    }
}