/*
* Copyright (C) 2007, University of Manchester
*/
package org.coode.html.hierarchy;

import org.semanticweb.owl.model.OWLObject;

import java.util.*;

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jan 23, 2008<br><br>
 */
public abstract class AbstractTreeFragment<O extends OWLObject> implements TreeFragment<O> {

    private Set<O> roots = new HashSet<O>();
    private final Map<O, Set<O>> nodeMap = new HashMap<O, Set<O>>();

    private Comparator<OWLObject> comparator;


    public boolean isEmpty() {
        return nodeMap.isEmpty();
    }

    public Set<O> getRoots() {
        return Collections.unmodifiableSet(roots);
    }

    public List<O> getChildren(O parent) {
        final Set<O> subs = nodeMap.get(parent);
        if (subs != null){
            List<O> children = new ArrayList<O>(subs);
            if (comparator != null){
                Collections.sort(children, comparator);
            }
            return Collections.unmodifiableList(children);
        }
        return Collections.emptyList();
    }

    public boolean isLeaf(O node) {
        return nodeMap.get(node) == null || nodeMap.get(node).isEmpty();
    }

    public void addChild(O child, O parent){
        // get node for given class out of the map (or add if none exists)
        Set<O> subs = nodeMap.get(parent);
        if (subs == null){
            subs = new HashSet<O>();
            nodeMap.put(parent, subs);
        }

        // add the sub to the node
        if (child != null){
            subs.add(child);
        }
    }

    public void addRoot(O root) {
        roots.add(root);
    }

    public void clear(){
        roots.clear();
        nodeMap.clear();
    }

    public void setComparator(Comparator<OWLObject> comparator){
        this.comparator = comparator;
    }
}
