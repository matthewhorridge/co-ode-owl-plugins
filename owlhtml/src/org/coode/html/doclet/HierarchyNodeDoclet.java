/*
* Copyright (C) 2007, University of Manchester
*/
package org.coode.html.doclet;

import org.coode.html.OWLHTMLServer;
import org.coode.html.hierarchy.TreeFragment;
import org.coode.html.renderer.OWLHTMLRenderer;
import org.semanticweb.owl.model.OWLNamedObject;

import java.io.PrintWriter;
import java.net.URL;

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Feb 7, 2008<br><br>
 */
public class HierarchyNodeDoclet<O extends OWLNamedObject> extends AbstractHierarchyNodeDoclet<O>{

    public HierarchyNodeDoclet(OWLHTMLServer server, TreeFragment<O> model) {
        super(server, model);
    }

    protected void renderHeader(URL pageURL, PrintWriter out) {
        renderNode(getUserObject(), new OWLHTMLRenderer(getServer()), pageURL, out);
        if (getSubDocletCount() > 0){
            out.println("<ul style='list-style-type: disc;'>");
        }
    }

    protected void renderFooter(URL pageURL, PrintWriter out) {
        if (getSubDocletCount() > 0){
            out.println("</ul>");
        }
    }

    public void setUserObject(O object) {
        clear();
        super.setUserObject(object);
        if (getUserObject() != null){
            System.out.println("obj = " + getUserObject());
            if (getUserObject().equals(getModel().getFocus())){
                final HierarchyNodeSubsDoclet<O> nodeSubsDoclet = new HierarchyNodeSubsDoclet<O>(getServer(), getModel());
                nodeSubsDoclet.setPinned(true);
                nodeSubsDoclet.setAutoExpandEnabled(isAutoExpandSubs());
                nodeSubsDoclet.setUserObject(object);
                addDoclet(nodeSubsDoclet);
            }
            else{
                for (O child : getModel().getChildren(object)){
                    final HierarchyNodeDoclet<O> subDoclet = new HierarchyNodeDoclet<O>(getServer(), getModel());
                    subDoclet.setPinned(true); // you will never change the subs as they will be regenerated each time this changed
                    subDoclet.setAutoExpandEnabled(isAutoExpandSubs());
                    subDoclet.setUserObject(child);
                    addDoclet(subDoclet);
                }
            }
        }
    }
}
