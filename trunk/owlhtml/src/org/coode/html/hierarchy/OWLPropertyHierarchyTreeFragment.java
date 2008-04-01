/*
* Copyright (C) 2007, University of Manchester
*/
package org.coode.html.hierarchy;

import org.coode.html.OWLHTMLServer;
import org.semanticweb.owl.inference.OWLPropertyReasoner;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLDataProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jan 23, 2008<br><br>
 */
public class OWLPropertyHierarchyTreeFragment<O extends OWLProperty> extends AbstractTreeFragment<O> {

    private OWLHTMLServer server;

    private OWLPropertyReasoner hp;

    private O focusEntity;

    private int ancestorLevels = 3;
    private int descendantLevels = 2; // actually needs one more layer than displayed, as this is required for determining if leaf

    private boolean refreshRequired = true;

    public OWLPropertyHierarchyTreeFragment(OWLHTMLServer server, OWLPropertyReasoner hp) {
        this.server = server;
        this.hp = hp;
        setComparator(server.getComparator());
    }

    public void setFocus(O focusProperty){
        this.focusEntity = focusProperty;
        refreshRequired = true;
    }

    public O getFocus() {
        return focusEntity;
    }

    /**
     * the number of levels of the hierarchy above the current class that you can see - default is 3
     * @param threshold
     */
    public void setAncestorLevels(int threshold){
        this.ancestorLevels = threshold;
        refreshRequired = true;
    }

    /**
     * the number of levels of the hierarchy above the current class that you can see - default is 3
     * @param threshold
     */
    public void setDescendantLevels(int threshold){
        this.descendantLevels = threshold;
        refreshRequired = true;
    }


    public String getTitle() {
        return "Asserted Object Property Hierarchy";
    }

    public boolean isEmpty() {
        if (refreshRequired){
            refresh();
        }
        return super.isEmpty();
    }

    public Set<O> getRoots() {
        if (refreshRequired){
            refresh();
        }
        return super.getRoots();
    }

    public List<O> getChildren(O parent) {
        if (refreshRequired){
            refresh();
        }
        return super.getChildren(parent);
    }

    public boolean isLeaf(O node) {
        if (refreshRequired){
            refresh();
        }
        return super.isLeaf(node);
    }

    private void refresh() {
        clear();
        try {
            generateAncestorHierarchy(focusEntity, 0);
            generateDescendantHierarchy(focusEntity, 0);
        }
        catch (OWLReasonerException e) {
            e.printStackTrace();
        }
        refreshRequired = false;
    }

    private void generateDescendantHierarchy(O node, int depth) throws OWLReasonerException {
        if (depth < descendantLevels){
            // search for subclasses of the node
            Set<O> namedSubs = new HashSet<O>();

            if (node instanceof OWLObjectProperty){
                Set<OWLObjectProperty> subs = OWLReasonerAdapter.flattenSetOfSets(hp.getSubProperties((OWLObjectProperty)node));
                for (OWLObjectProperty sub : subs) {
                    namedSubs.add((O) sub);
                }
            }
            else if (node instanceof OWLDataProperty){
                Set<OWLDataProperty> subs = OWLReasonerAdapter.flattenSetOfSets(hp.getSubProperties((OWLDataProperty)node));
                for (OWLDataProperty sub : subs) {
                    namedSubs.add((O) sub);
                }
            }

            // and recurse
            for (O namedSub : namedSubs){
                addChild(namedSub, node);
                generateDescendantHierarchy(namedSub, depth+1);
            }
        }
    }

    private void generateAncestorHierarchy(O node, int depth) throws OWLReasonerException {
        if (depth < ancestorLevels){
            // search for supers of the node
            Set<O> namedSupers = new HashSet<O>();

            if (node instanceof OWLObjectProperty){
                Set<OWLObjectProperty> supers = OWLReasonerAdapter.flattenSetOfSets(hp.getSubProperties((OWLObjectProperty)node));
                for (OWLObjectProperty s : supers) {
                    namedSupers.add((O) s);
                }
            }
            else if (node instanceof OWLDataProperty){
                Set<OWLDataProperty> supers = OWLReasonerAdapter.flattenSetOfSets(hp.getSubProperties((OWLDataProperty)node));
                for (OWLDataProperty s : supers) {
                    namedSupers.add((O) s);
                }
            }

            if (namedSupers.isEmpty()){
                addRoot(node);
            }
            else{
                // recurse
                for (O namedSuper : namedSupers){
                    addChild(node, namedSuper);
                    generateAncestorHierarchy(namedSuper, depth+1);
                }
            }
        }
        else{
            addRoot(node);
        }
    }
}
