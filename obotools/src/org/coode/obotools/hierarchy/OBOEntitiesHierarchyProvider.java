package org.coode.obotools.hierarchy;

import org.semanticweb.owl.model.*;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProviderListener;
import org.protege.editor.owl.model.OWLModelManager;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.net.URI;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 15, 2009<br><br>
 */
public class OBOEntitiesHierarchyProvider<N extends OWLEntity> implements OWLObjectHierarchyProvider<N> {

    private Set<N> filtered = new HashSet<N>();

    private OWLModelManager mngr;

    private URI annotationURI;

    private OWLObject annotationValue;

    private OWLObjectHierarchyProvider<N> delegate;

    private List<OWLObjectHierarchyProviderListener<N>> listeners = new ArrayList<OWLObjectHierarchyProviderListener<N>>();

    private OWLObjectHierarchyProviderListener<N> delegateListener = new OWLObjectHierarchyProviderListener<N>() {
        public void nodeChanged(N node) {
            if (passesFilter(node)){
                for (OWLObjectHierarchyProviderListener<N> l : listeners){
                    l.nodeChanged(node);
                }
            }
        }


        public void hierarchyChanged() {
            for (OWLObjectHierarchyProviderListener<N> l : listeners){
                l.hierarchyChanged();
            }
        }
    };


    public OBOEntitiesHierarchyProvider(OWLObjectHierarchyProvider<N> delegate, URI annotationURI, OWLModelManager mngr) {
        this(delegate, annotationURI, null, mngr);
    }


    public OBOEntitiesHierarchyProvider(OWLObjectHierarchyProvider<N> delegate,
                                               URI annotationURI,
                                               OWLObject annotationValue,
                                               OWLModelManager mngr) {
        this.delegate = delegate;
        this.delegate.addListener(delegateListener);
        this.annotationURI = annotationURI;
        this.annotationValue = annotationValue;
        this.mngr = mngr;
    }

    
    protected boolean passesFilter(N entity) {
        if (filtered.contains(entity)){
            return false;
        }
        for (OWLOntology ont : mngr.getActiveOntologies())
            for (OWLAnnotation annot : entity.getAnnotations(ont)){
                if (annot.getAnnotationURI().equals(annotationURI)){
                    if (annotationValue == null || annot.getAnnotationValue().equals(annotationValue)){
                        filtered.add(entity);
                        return false;
                    }
                }
            }
        return true;
    }


    public void setOntologies(Set<OWLOntology> ontologies) {
        filtered.clear();
        delegate.setOntologies(ontologies);
    }


    public Set<N> getRoots() {
        return delegate.getRoots();
    }


    public Set<N> getChildren(N object) {
        Set<N> children = delegate.getChildren(object);
        if (object.equals(mngr.getOWLDataFactory().getOWLThing())){

            Set<N> filteredChildren = new HashSet<N>();
            for (N n : children){
                if (passesFilter(n)){
                    filteredChildren.add(n);
                }
            }
            children = filteredChildren;
        }
        return children;
    }


    public Set<N> getDescendants(N object) {
        return delegate.getDescendants(object);
    }


    public Set<N> getParents(N object) {
        return delegate.getParents(object);
    }


    public Set<N> getAncestors(N object) {
        return delegate.getAncestors(object);
    }


    public Set<N> getEquivalents(N object) {
        return delegate.getEquivalents(object);
    }


    public Set<List<N>> getPathsToRoot(N object) {
        return delegate.getPathsToRoot(object);
    }


    public boolean containsReference(N object) {
        return (passesFilter(object) && delegate.containsReference(object));
    }


    public void addListener(OWLObjectHierarchyProviderListener<N> listener) {
        listeners.add(listener);
    }


    public void removeListener(OWLObjectHierarchyProviderListener<N> listener) {
        listeners.remove(listener);
    }


    public void dispose() {
        delegate.removeListener(delegateListener);
        delegate.dispose();
    }
}
