package org.coode.change;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;

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
public class ChangeView extends AbstractOWLViewComponent {
    private static final long serialVersionUID = 1L;
    private JTree tree;
    protected ChangesTreeModel changesModel;

    private OWLOntologyChangeListener ontChangeListener = new OWLOntologyChangeListener(){
        @Override
        public void ontologiesChanged(List<? extends OWLOntologyChange> owlOntologyChanges) {
            changesModel.reload();
        }
    };


    @Override
    protected void initialiseOWLView() {
        final OWLModelManager mngr = getOWLModelManager();
        
        changesModel = new ChangesTreeModel(mngr.getHistoryManager());
        tree = new JTree(changesModel);
        tree.setRowHeight(-1); // forces the renderer to be asked for row height (needed for text-wrapped expressions)
        tree.setCellRenderer(new ChangeTreeCellRenderer(getOWLEditorKit()));

        setLayout(new BorderLayout());
        add(new JScrollPane(tree), BorderLayout.CENTER);

        mngr.addOntologyChangeListener(ontChangeListener);
    }


    @Override
    protected void disposeOWLView() {
        getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
    }
}
