package org.coode.pattern.valuepartition.ui;

import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProviderListener;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;

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
public class OWLObjectHierarchyProviderAdapter implements OWLObjectHierarchyProvider<OWLClass> {
    
    private OWLObjectHierarchyProvider<OWLClass> provider;
    private Set<OWLClass> roots;

    public OWLObjectHierarchyProviderAdapter(OWLObjectHierarchyProvider<OWLClass> provider, Set<OWLClass> roots) {
        this.provider = provider;
        this.roots = roots;
    }

    public void setOntologies(Set<OWLOntology> ontologies) {
        provider.setOntologies(ontologies);
    }

    public void dispose() {
        provider.dispose();
    }

    public Set<OWLClass> getRoots() {
        return roots;
    }

    public Set<OWLClass> getChildren(OWLClass object) {
        return provider.getChildren(object);
    }

    public Set<OWLClass> getDescendants(OWLClass object) {
        return provider.getDescendants(object);
    }

    public Set<OWLClass> getParents(OWLClass object) {
        return provider.getParents(object);
    }

    public Set<OWLClass> getAncestors(OWLClass object) {
        return provider.getAncestors(object);
    }

    public Set<OWLClass> getEquivalents(OWLClass object) {
        return provider.getEquivalents(object);
    }

    public Set<java.util.List<OWLClass>> getPathsToRoot(OWLClass object) {
        return provider.getPathsToRoot(object);
    }

    public boolean containsReference(OWLClass object) {
        return provider.containsReference(object);
    }

    public void addListener(OWLObjectHierarchyProviderListener<OWLClass> owlObjectHierarchyProviderListener) {
        provider.addListener(owlObjectHierarchyProviderListener);
    }

    public void removeListener(OWLObjectHierarchyProviderListener<OWLClass> owlObjectHierarchyProviderListener) {
        provider.removeListener(owlObjectHierarchyProviderListener);
    }
}
