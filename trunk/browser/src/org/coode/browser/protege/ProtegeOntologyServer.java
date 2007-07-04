package org.coode.browser.protege;

import org.apache.log4j.Logger;
import org.coode.html.OntologyServer;
import org.coode.html.OWLNameMapper;
import org.coode.html.url.EntityURLMapper;
import org.coode.html.url.OWLDocURLMapper;
import org.coode.html.renderer.EntityRenderer;
import org.coode.html.util.OWLObjectComparator;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.inference.NoOpReasoner;
import org.semanticweb.owl.inference.OWLClassReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.ToldClassHierarchyReasoner;

import java.util.Comparator;
import java.util.Set;
import java.net.URL;
import java.net.MalformedURLException;
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
 * Author: Nick Drummond<br>
 *
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 7, 2007<br><br>
 * <p/>
 */
public class ProtegeOntologyServer implements OntologyServer {

    // as all URLs in links should be relative, this should not matter
    private static URL DEFAULT_BASE;

    static {
        try {
            DEFAULT_BASE = new URL("http://www.co-ode.org/ontologies/");
        }
        catch (MalformedURLException e) {
            Logger.getLogger(ProtegeOntologyServer.class).error(e);
        }
    }

    private OWLModelManager mngr;

    private OWLClassReasoner toldClassReasoner;

    private OWLObjectComparator<OWLObject> comp;

    private ProtegeOWLEntityRenderer ren;

    private EntityURLMapper urlMapper;
    
    private OWLNameMapper nameMapper;

    public ProtegeOntologyServer(OWLModelManager mngr) {
        this.mngr = mngr;
        this.ren = new ProtegeOWLEntityRenderer();
    }

    public EntityRenderer getOWLEntityRenderer() {
        return ren;
    }

    public OWLOntology getActiveOntology() {
        return mngr.getActiveOntology();
    }

    public Set<OWLOntology> getActiveOntologies() {
        return mngr.getActiveOntologies();
    }

    public OWLOntologyManager getOWLOntologyManager() {
        return mngr.getOWLOntologyManager();
    }

    public OWLClassReasoner getOWLClassReasoner() {
        OWLClassReasoner r = mngr.getReasoner();
        if (r instanceof NoOpReasoner){
            if (toldClassReasoner == null){
                toldClassReasoner = new ToldClassHierarchyReasoner(mngr.getOWLOntologyManager());
                try {
                    toldClassReasoner.loadOntologies(mngr.getActiveOntologies());
                }
                catch (OWLReasonerException e) {
                    Logger.getLogger(ProtegeOntologyServer.class).error(e);
                }
            }
            r = toldClassReasoner;
        }
        return r;
    }

    public Comparator<OWLObject> getComparator() {
        if (comp == null){
            comp = new OWLObjectComparator<OWLObject>(this);
        }
        return comp;
    }

    public EntityURLMapper getURLMapper() {
        if (urlMapper == null){
            urlMapper = new OWLDocURLMapper(this, DEFAULT_BASE);
        }
        return urlMapper;
    }

    public OWLNameMapper getNameMapper() {
        if (nameMapper == null){
            nameMapper = new ProtegeNameMapperWrapper(mngr);
        }
        return nameMapper;
    }

    public URL getBaseURL() {
        return DEFAULT_BASE;
    }

    public void loadOntology(URI ontPhysicalURI) throws OWLOntologyCreationException {
        mngr.getOWLOntologyManager().loadOntologyFromPhysicalURI(ontPhysicalURI);
    }

    class ProtegeOWLEntityRenderer implements EntityRenderer{
        public String render(OWLEntity entity) {
            return mngr.getOWLEntityRenderer().render(entity);
        }
    }
}
