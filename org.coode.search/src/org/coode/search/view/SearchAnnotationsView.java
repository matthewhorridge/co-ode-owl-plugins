package org.coode.search.view;

import org.protege.editor.owl.ui.OWLObjectComparator;
import org.protege.editor.owl.ui.tree.OWLLinkedObjectTree;
import org.protege.editor.owl.ui.tree.OWLObjectTreeCellRenderer;
import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
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
 * Date: Mar 18, 2008<br><br>
 */
public class SearchAnnotationsView extends AbstractActiveOntologyViewComponent implements ResultsView {

    private List<AnnotationFilterUI> filters = new ArrayList<AnnotationFilterUI>();

    private OWLLinkedObjectTree resultsTree;

    private JComponent filterPanel;

    private Map<AnnotationFilterUI, Set<OWLAxiom>> resultsMap = new HashMap<AnnotationFilterUI, Set<OWLAxiom>>();


    protected void initialiseOntologyView() throws Exception {
        setLayout(new BorderLayout());

        resultsTree = new OWLLinkedObjectTree(getOWLEditorKit());
        resultsTree.setDrawNodeSeperators(true);
        resultsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("results (0)")));
        resultsTree.setCellRenderer(new OWLObjectTreeCellRenderer(getOWLEditorKit()));

        add(new JScrollPane(resultsTree), BorderLayout.CENTER);

        filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
        add(filterPanel, BorderLayout.NORTH);

        addFilter(0);
    }


    protected void disposeOntologyView() {
        for (AnnotationFilterUI filter : filters){
            filter.dispose();
        }
    }


    protected synchronized void updateView(OWLOntology activeOntology) throws Exception {
        for (AnnotationFilterUI filter : filters){
            filter.startSearch();
        }
    }


    private void addFilter(int index) {
        final AnnotationFilterUI filter = new AnnotationFilterUI(this, getOWLModelManager());
        final JComponent filterHolder = new JPanel();
        filterHolder.setLayout(new BoxLayout(filterHolder, BoxLayout.LINE_AXIS));
        filterHolder.add(filter);
        filterHolder.add(new MyButton(new AbstractAction("-"){
            public void actionPerformed(ActionEvent event) {
                if (filters.size() > 1){
                    filters.remove(filter);
                    filterPanel.remove(filterHolder);
                    filterPanel.revalidate();
                    resultsMap.remove(filter);
                    filter.dispose();
                    refresh();
                }
            }
        }));
        filterHolder.add(new MyButton(new AbstractAction("+"){
            public void actionPerformed(ActionEvent event) {
                // add after the current one
                addFilter(filters.indexOf(filter)+1);
            }
        }));

        filterPanel.add(filterHolder);
        filters.add(index, filter);

        // bump the earlier ones down
        for (int i=index+1; i<filters.size(); i++){
            final Container moveFilterHolder = filters.get(i).getParent();
            filterPanel.remove(moveFilterHolder);
            filterPanel.add(moveFilterHolder);
        }

        validate();
    }


    public void resultsChanged(AnnotationFilterUI filterUI) {
        resultsMap.put(filterUI, filterUI.getResults());
        refresh();
    }


    private void refresh() {
        List<Set<OWLAxiom>> results = new ArrayList<Set<OWLAxiom>>();
        for (AnnotationFilterUI filter : filters){
            results.add(resultsMap.get(filter));
        }
        resultsTree.setModel(new ResultsTreeModel(results, new OWLObjectComparator(getOWLModelManager())));
    }


    private class MyButton extends JButton {

        public final Border MOUSE_OVER_BORDER = new LineBorder(Color.BLACK, 1);
        public final Border MOUSE_OUT_BORDER = new EmptyBorder(1, 1, 1, 1);
        private Color normalBackground;

        public MyButton(AbstractAction action) {
            super(action);
            addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent event) {
                    setBorder(MOUSE_OVER_BORDER);
                    setBackground(Color.DARK_GRAY);
                }
                public void mouseExited(MouseEvent event) {
                    setBorder(MOUSE_OUT_BORDER);
                    setBackground(normalBackground);
                }
            });
            setPreferredSize(new Dimension(20, 20));
            setBorder(MOUSE_OUT_BORDER);
            normalBackground = getBackground();
        }
    }
}
