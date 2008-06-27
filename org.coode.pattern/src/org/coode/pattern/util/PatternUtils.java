package org.coode.pattern.util;

import org.coode.pattern.api.PatternException;
import org.semanticweb.owl.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * Date: Nov 28, 2007<br><br>
 */
public class PatternUtils {

    public static List<OWLOntologyChange> getAddAxiomsChanges(Set<OWLAxiom> axioms,
                                                              OWLOntology activeOnt,
                                                              Set<OWLOntology> activeOnts) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        // if the axiom does not already appear in one of the active ontologies, add it to the active ontology
        for (OWLAxiom axiom : axioms){
            boolean found = false;
            for (OWLOntology ont : activeOnts){
                if (ont.containsAxiom(axiom)){
                    found = true;
                }
            }
            if (!found){
                changes.add(new AddAxiom(activeOnt,  axiom));
            }
        }
        return changes;
    }

    public static List<OWLOntologyChange> getRemoveAxiomsChanges(Set<OWLAxiom> axioms, Set<OWLOntology> onts) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : onts){
            for (OWLAxiom axiom : axioms){
                if (ont.containsAxiom(axiom)){
                    changes.add(new RemoveAxiom(ont, axiom));
                }
            }
        }
        return changes;
    }

    // nasty nasty code    
    public static OWLClass getNamedClass(String name, OWLOntology ont, OWLDataFactory df){
        return df.getOWLClass(URI.create(ont.getURI() + "#" + name));
    }

    // nasty nasty code
    public static OWLObjectProperty getNamedObjectProperty(String name, OWLOntology ont, OWLDataFactory df){
        return df.getOWLObjectProperty(URI.create(ont.getURI() + "#" + name));
    }

    // nasty nasty code
    public static Set<OWLClass> createClasses(String s, OWLOntology ont, OWLDataFactory df) throws PatternException {
        Set<OWLClass> classes = new HashSet<OWLClass>();
        String stem = ont.getURI().toString() + "#";
        for (String line : s.split("\n")) {
            try {
                classes.add(df.getOWLClass(new URI(stem + line)));
            }
            catch (URISyntaxException e) {
                throw new PatternException("Could not create class " + stem + line, e);
            }
        }
        return classes;
    }

}
