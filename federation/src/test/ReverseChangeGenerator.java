package test;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitor;
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
public class ReverseChangeGenerator implements OWLOntologyChangeVisitor {

    private OWLOntologyChange reverseChange;


    public OWLOntologyChange getReverseChange() {
        return reverseChange;
    }


    @Override
    public void visit(AddAxiom change) {
        reverseChange = new RemoveAxiom(change.getOntology(), change.getAxiom());
    }


    @Override
    public void visit(RemoveAxiom change) {
        reverseChange = new AddAxiom(change.getOntology(), change.getAxiom());
    }


    @Override
    public void visit(SetOntologyID change) {
        reverseChange = new SetOntologyID(change.getOntology(),
                change.getOriginalOntologyID());
    }

    @Override
    public void visit(AddImport change) {
        reverseChange = new RemoveImport(change.getOntology(),
                change.getImportDeclaration());
    }

    @Override
    public void visit(RemoveImport change) {
        reverseChange = new AddImport(change.getOntology(),
                change.getImportDeclaration());
    }

    @Override
    public void visit(AddOntologyAnnotation change) {
        reverseChange = new RemoveOntologyAnnotation(change.getOntology(),
                change.getAnnotation());
    }

    @Override
    public void visit(RemoveOntologyAnnotation change) {
        reverseChange = new AddOntologyAnnotation(change.getOntology(),
                change.getAnnotation());
    }
}