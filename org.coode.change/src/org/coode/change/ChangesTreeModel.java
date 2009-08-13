package org.coode.change;

import org.protege.editor.owl.model.history.HistoryManager;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

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
 * Date: Apr 10, 2008<br><br>
 */
public class ChangesTreeModel implements TreeModel {

    private List<List<OWLOntologyChange>> changes;

    private HistoryManager historyManager;

    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

    private String ROOT = "Changes since startup";


    public ChangesTreeModel(HistoryManager historyManager) {
        this.historyManager = historyManager;
        load();
    }


    public Object getRoot() {
        return ROOT;
    }


    public Object getChild(Object o, int i) {
        if (o.equals(ROOT)){
            return changes.get(i);
        }
        else{
            return ((List<OWLOntologyChange>)o).get(i);
        }
    }


    public int getChildCount(Object o) {
        if (o.equals(ROOT)){
            return changes.size();
        }
        else{
            return ((List<OWLOntologyChange>)o).size();
        }
    }


    public boolean isLeaf(Object o) {
        return o instanceof OWLOntologyChange;
    }


    public void valueForPathChanged(TreePath treePath, Object o) {
        for (TreeModelListener l : listeners){
            l.treeNodesChanged(new TreeModelEvent(this, treePath));
        }
    }


    public int getIndexOfChild(Object parent, Object child) {
        if (parent.equals(ROOT)){
            return changes.indexOf(child);
        }
        else{
            return ((List<OWLOntologyChange>)parent).indexOf(child);
        }
    }


    public void addTreeModelListener(TreeModelListener treeModelListener) {
        listeners.add(treeModelListener);
    }


    public void removeTreeModelListener(TreeModelListener treeModelListener) {
        listeners.remove(treeModelListener);
    }


    public void load(){
        changes = historyManager.getLoggedChanges();
    }

    public void reload() {
        load();
        for (TreeModelListener l : listeners){
            l.treeStructureChanged(new TreeModelEvent(this, new Object[]{getRoot()}));
        }
    }
}
