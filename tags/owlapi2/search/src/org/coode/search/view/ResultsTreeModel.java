package org.coode.search.view;

import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.*;/*
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
 * Date: Jun 26, 2008<br><br>
 */
public class ResultsTreeModel implements TreeModel {

    private Comparator comparator;

    private List<Set<OWLAxiom>> results;

    final Map<OWLEntity, Set<OWLEntityAnnotationAxiom>> entityAnnotationsMap = new HashMap<OWLEntity, Set<OWLEntityAnnotationAxiom>>();

    private List<OWLEntity> sortedEntities;


    /**
     *
     * @param results evaluation is by the order of results
     * @param comparator for displaying the tree
     */
    ResultsTreeModel(List<Set<OWLAxiom>> results, Comparator comparator) {
        this.results = results;
        this.comparator = comparator;

        rebuildTree();
    }


    private void rebuildTree() {
        entityAnnotationsMap.clear();
        boolean started = false;
        // generate the intersection of results by subject of the annotations
        for (Set<OWLAxiom> result : results){
            if (result != null){ // ignore those that have not yet run
            if (!started){
                for (OWLAxiom ax : result){
                    if (ax.getAxiomType().equals(AxiomType.ENTITY_ANNOTATION)){
                        final OWLEntity entity = ((OWLEntityAnnotationAxiom) ax).getSubject();
                        Set<OWLEntityAnnotationAxiom> annotations = entityAnnotationsMap.get(entity);
                        if (annotations == null){
                            annotations = new HashSet<OWLEntityAnnotationAxiom>();
                        }
                        annotations.add((OWLEntityAnnotationAxiom)ax);
                        entityAnnotationsMap.put(entity, annotations);
                    }
                }
                started = true;
            }
            else{
                if (!entityAnnotationsMap.isEmpty()){ // don't bother getting any more unless there is something already in the set
                    Set<OWLEntity> markedEntities = new HashSet<OWLEntity>();
                    for (OWLAxiom ax : result){
                        if (ax.getAxiomType().equals(AxiomType.ENTITY_ANNOTATION)){
                            OWLEntity entity = ((OWLEntityAnnotationAxiom)ax).getSubject();
                            Set<OWLEntityAnnotationAxiom> annotations = entityAnnotationsMap.get(entity);
                            if (annotations != null){
                                annotations.add((OWLEntityAnnotationAxiom)ax);
                                entityAnnotationsMap.put(entity, annotations);
                                markedEntities.add(entity);
                            }
                        }
                    }

                    Iterator<OWLEntity> it = entityAnnotationsMap.keySet().iterator();
                    while (it.hasNext()){
                        OWLEntity entity = it.next();
                        if (!markedEntities.contains(entity)){
                            it.remove();
                        }
                    }
                }
            }
            }
        }

        // sort the entities once (as this will always have to be done)
        sortedEntities = new ArrayList<OWLEntity>(entityAnnotationsMap.keySet());
        Collections.sort(sortedEntities, comparator);
    }


    public Object getRoot() {
        return "results (" + entityAnnotationsMap.size() + ")";
    }


    public Object getChild(Object o, int i) {
        if (o instanceof OWLEntity){
            final List<OWLEntityAnnotationAxiom> l = new ArrayList<OWLEntityAnnotationAxiom>(entityAnnotationsMap.get(o));
            Collections.sort(l, comparator);
            return l.get(i);
        }
        else if (o instanceof OWLEntityAnnotationAxiom){
            return null;
        }
        return sortedEntities.get(i);
    }


    public int getChildCount(Object o) {
        if (o instanceof OWLEntity){
            return entityAnnotationsMap.get(o).size();
        }
        else if (o instanceof OWLEntityAnnotationAxiom){
            return 0;
        }
        return entityAnnotationsMap.keySet().size();
    }


    public boolean isLeaf(Object o) {
        return o instanceof OWLEntityAnnotationAxiom || entityAnnotationsMap.isEmpty();
    }


    public void valueForPathChanged(TreePath treePath, Object o) {
        // do nothing
    }


    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof OWLEntity){
            final List<OWLEntityAnnotationAxiom> l = new ArrayList<OWLEntityAnnotationAxiom>(entityAnnotationsMap.get(parent));
            Collections.sort(l, comparator);
            return l.indexOf(child);
        }
        else if (parent instanceof OWLEntityAnnotationAxiom){
            return 0;
        }
        return sortedEntities.indexOf(child);
    }


    public void addTreeModelListener(TreeModelListener treeModelListener) {
        // do nothing
    }


    public void removeTreeModelListener(TreeModelListener treeModelListener) {
        // do nothing
    }
}
