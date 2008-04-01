/*
* Copyright (C) 2007, University of Manchester
*/
package org.coode.html.doclet;

import org.coode.html.OWLHTMLServer;
import org.coode.html.hierarchy.TreeFragment;
import org.coode.html.renderer.OWLHTMLRenderer;
import org.coode.owl.mngr.ServerConstants;
import org.semanticweb.owl.model.OWLNamedObject;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Feb 7, 2008<br><br>
 */
public abstract class AbstractHierarchyNodeDoclet<O extends OWLNamedObject> extends AbstractOWLDocDoclet<O>{

    private final TreeFragment<O> model;

    private boolean autoExpandSubs = false;

    public AbstractHierarchyNodeDoclet(OWLHTMLServer server, TreeFragment<O> model) {
        super(server);
        setPinned(true); // you will never change the subs as they will be regenerated each time this changed
        this.model = model;
    }


    public void setAutoExpandEnabled(boolean enabled) {
        this.autoExpandSubs = enabled;
        // @@TODO percolate down the hierarchy
    }


    protected TreeFragment<O> getModel() {
        return model;
    }


    protected boolean isAutoExpandSubs() {
        return autoExpandSubs;
    }

    public String getID() {
        return getServer().getNameRenderer().getShortForm(getUserObject());
    }

    protected void renderNode(O node, OWLHTMLRenderer objRenderer, URL pageURL, PrintWriter out) {
        if (!model.isLeaf(node)){
            out.print("<li class='expandable'>");
            printExpandLink(node, out);
        }
        else{
            out.print("<li>");
        }
        objRenderer.render(node, pageURL, out);
        out.print("</li>");
    }

    protected void printExpandLink(O node, PrintWriter out) {
        try {
            if (isRenderSubExpandLinksEnabled()){
                // @@TODO should be relative - should use renderLink()
                URL expandLinkURL = new URL(getServer().getURLScheme().getURLForNamedObject(node) + "&expanded=true");
                out.println(" <a href='" + expandLinkURL + "'>[+]</a>");
            }
            else{
                out.println(" +");
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected boolean isRenderSubsEnabled() {
        return getServer().getProperties().isSet(ServerConstants.OPTION_RENDER_SUBS);
    }


    protected boolean isRenderSubExpandLinksEnabled() {
        return getServer().getProperties().isSet(ServerConstants.OPTION_RENDER_SUB_EXPAND_LINKS);
    }
}
