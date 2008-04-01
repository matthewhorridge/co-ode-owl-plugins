/*
* Copyright (C) 2007, University of Manchester
*/
package org.coode.html.doclet;

import org.coode.html.OWLHTMLServer;
import org.coode.html.hierarchy.TreeFragment;
import org.coode.html.renderer.OWLHTMLRenderer;
import org.semanticweb.owl.model.OWLNamedObject;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jan 23, 2008<br><br>
 */
public class OWLObjectHierarchyFragmentDoclet<O extends OWLNamedObject> extends AbstractHTMLDoclet<O> {

    private int subThreshold = 3;

    private boolean autoExpand = false;

    private boolean renderHiddenSubs = true;

    private boolean renderExpandLinks = false;

    private boolean superLinksAutoExpand = false;

    private TreeFragment<O> model;

    private OWLHTMLServer server;

    public OWLObjectHierarchyFragmentDoclet(OWLHTMLServer server, TreeFragment<O> model) {
        this.server = server;
        this.model = model;
    }

    public String getID() {
        return "doclet." + getTitle();
    }

    public String getTitle() {
        return model.getTitle();
    }

    /**
     * the number of subclasses below the current class that you can see without expanding - default is 3
     * @param subThreshold
     */
    public void setSubThreshold(int subThreshold) {
        this.subThreshold = subThreshold;
    }

    /**
     * whether or not the subclasses are shown at all - default is true
     * @param renderSubs
     */
    public void setRenderHiddenSubs(boolean renderSubs){
        this.renderHiddenSubs = renderSubs;
    }

    /**
     * whether or not the subclasses are expanded by default (setRenderHiddenSubs() must be true)
     * default is false - only the first few are shown
     * @param autoExpand
     */
    public void setAutoExpandSubs(boolean autoExpand){
        this.autoExpand = autoExpand;
    }

    /**
     * whether or not subclasses show expansion links - default is false
     * @param expand
     */
    public void setRenderSubExpandLinks(boolean expand){
        this.renderExpandLinks = expand;
    }

    /**
     * whether or not superclass links automatically expand on target page - default is false
     * @param expand
     */
    public void setSuperLinksAutoExpand(boolean expand){
        this.superLinksAutoExpand = expand;
    }

    public final OWLHTMLServer getServer(){
        return server;
    }

    public void renderHeader(URL pageURL, PrintWriter out) {
        renderBoxStart(getTitle(), out);
    }

    public void renderFooter(URL pageURL, PrintWriter out){
        OWLHTMLRenderer objRenderer = new OWLHTMLRenderer(getServer());
        int indent = 0;
        for (O root : model.getRoots()){
            indent = renderAncestorTree(0, root, objRenderer, pageURL, out);
        }
        renderDescendantTree(getUserObject(), objRenderer, pageURL, out); //only render the children for the final use
//        renderOutdent(indent, out);
        renderBoxEnd(getTitle(), out);
    }

    private void renderOutdent(int indent, PrintWriter out) {
        for (int i=0; i<indent; i++){
            out.println("</li></ul>");
        }
    }

    public final void setUserObject(O object) {
        super.setUserObject(object); // must call super()
        model.setFocus(object);
    }

    private int renderAncestorTree(int indent, O node, OWLHTMLRenderer objRenderer, URL pageURL, PrintWriter out) {
        out.println("<ul style='list-style-type: disc;'><li>");
        indent++;

        objRenderer.render(node, pageURL, out);

        if (node.equals(getUserObject())){
        }
        else{
            List<O> children = model.getChildren(node);
            if (children != null){
                if (superLinksAutoExpand){
                    server.getURLScheme().setAdditionalLinkArguments("&expanded=true"); // @@TODO not sure this is threadsafe
                    objRenderer.render(node, pageURL, out);
                    server.getURLScheme().clearAdditionalLinkArguments();
                }
                for (O sub : children){
                    indent = renderAncestorTree(indent, sub, objRenderer, pageURL, out);
                }
            }
        }

        out.println("</li></ul>");

        return indent;
    }

    private void renderDescendantTree(O node, OWLHTMLRenderer objRenderer, URL pageURL, PrintWriter out) {
        List<O> children = model.getChildren(node);

        if (!children.isEmpty()){

            out.println("<ul>");

            //+1 takes into account additional line used for link
            final boolean doExpand = !autoExpand && children.size() > subThreshold + 1;

            for (int i=0; i<children.size(); i++){
                if (doExpand && i == subThreshold){
                    if (renderHiddenSubs){
                        out.println("<span id=\"subs\" style=\"display: none;\">");
                    }
                    else{
                        break;
                    }
                }
                renderChild(children.get(i), objRenderer, pageURL, out);
            }

            if (doExpand){
                if (renderHiddenSubs){
                    out.println("</span><!-- subs -->");
                }
                int count = children.size() - subThreshold;
                out.println("<span id='subexpand'>" +
                            "<a class='subsexpand' id=\"showSubs\" href=\"#\" onClick=\"showSubs();\">"
                            + count + " more...</a></span>");
            }

            out.println("</ul>");
        }
    }


    private void renderChild(O sub, OWLHTMLRenderer objRenderer, URL pageURL, PrintWriter out) {
        out.print("<li>");
        objRenderer.render(sub, pageURL, out);
        if (!model.isLeaf(sub)){
            printExpandLink(sub, out);
        }
        out.print("</li>");
    }

    private void printExpandLink(O node, PrintWriter out) {
        try {
            if (renderExpandLinks){
                // @@TODO should be relative - should use renderLink()
                URL expandLinkURL = new URL(server.getURLScheme().getURLForNamedObject(node) + "&expanded=true");
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
}
