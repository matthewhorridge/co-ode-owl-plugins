/*
* Copyright (C) 2007, University of Manchester
*/
package org.coode.html.doclet;

import org.coode.html.OWLHTMLServer;
import org.coode.html.impl.OWLHTMLConstants;
import org.coode.html.renderer.ElementRenderer;
import org.coode.html.renderer.OWLHTMLRenderer;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

import java.net.URL;
import java.util.Collection;
import java.util.Set;

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jan 25, 2008<br><br>
 */
public abstract class AbstractOWLElementsDoclet<O extends OWLObject, E extends OWLObject> extends ElementsDoclet<O, E> {

    private OWLHTMLServer server;

    private OWLHTMLConstants.LinkTarget linkTarget;

    private Set<OWLOntology> ontologies;

    public AbstractOWLElementsDoclet(String name, Format format, OWLHTMLServer server) {
        super(name, format);
        this.server = server;
        setComparator(server.getComparator());
    }

    protected final OWLHTMLServer getServer(){
        return server;
    }

    public void setOntologies(Set<OWLOntology> onts){
        this.ontologies = onts;
    }

    public void setTarget(OWLHTMLConstants.LinkTarget target){
        this.linkTarget = target;
    }

    protected final Collection<E> getElements(){
        if (ontologies == null){
            return getElements(getServer().getVisibleOntologies());
        }
        else{
            return getElements(ontologies);
        }
    }

    protected abstract Collection<E> getElements(Set<OWLOntology> ontologies);

    protected final ElementRenderer<? super E> getElementRenderer() {
        OWLHTMLRenderer ren = new OWLHTMLRenderer(server);
        if (linkTarget != null){
            ren.setContentTargetWindow(linkTarget);
        }
        return ren;
    }
}
