package org.coode.annotate;

import org.protege.editor.core.ui.util.Icons;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.SimpleURIShortFormProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 21, 2007<br><br>
 */
public class AnnotationForm extends JComponent implements Scrollable {

    private static final Color LABEL_COLOUR = new Color(0, 0, 180);

    private AnnotateViewModel model;

    SimpleURIShortFormProvider uriShortFormProvider;

    private AnnotationModelListener modelListener = new AnnotationModelListener(){
        public void modelStructureChanged() {
            rebuildUI();
        }
    };

    public AnnotationForm(AnnotateViewModel model) {
        super();
        setLayout(new GridBagLayout());
        setVisible(true);
        this.model = model;
        uriShortFormProvider = new SimpleURIShortFormProvider();
        model.addModelListener(modelListener);
    }

    public void setEntity(OWLEntity entity){
        model.setEntity(entity);
        rebuildUI();
    }

    private void rebuildUI() {
        removeAll();

        if (model.getEntity() != null){
            GridBagConstraints gbConstr = new GridBagConstraints();
            gbConstr.gridy = 0;
            gbConstr.insets = new Insets(2, 2, 2, 2);
            gbConstr.anchor = GridBagConstraints.FIRST_LINE_END;
            gbConstr.fill = GridBagConstraints.HORIZONTAL;

            for (AnnotationRow c : model.getRows()){
                gbConstr.gridx = 0;
                gbConstr.weightx = 0.5;

                final JLabel jLabel = new JLabel(uriShortFormProvider.getShortForm(c.getURI()));
                jLabel.setToolTipText(c.getURI().toString());
                jLabel.setAlignmentX(1.0f);
                jLabel.setForeground(LABEL_COLOUR);
                add(jLabel, gbConstr);

                gbConstr.gridx = 1;
                gbConstr.weightx = 0.5;
                JComponent editor = c.getEditor();
                editor.setToolTipText(getToolTipText(c));
                add(editor, gbConstr);

                gbConstr.gridx = 2;
                final JLabel deleteButton = new JLabel(Icons.getIcon("object.delete.gif"));
                deleteButton.addMouseListener(new RemoveAxiomListener(c));
                add(deleteButton, gbConstr);

                gbConstr.gridx = 3;
                final JLabel addButton = new JLabel(Icons.getIcon("object.add.gif"));
                addButton.addMouseListener(new AddFieldListener(c));
                add(addButton, gbConstr);

                gbConstr.gridy++;
            }
        }
        getParent().validate();
    }


    private String getToolTipText(AnnotationRow row) {
        String str = "";
        for (OWLOntology ont : model.getOntologiesContainingAnnotation(row)){
            if (str.length() > 0){
                str += "\n";
            }
            str += ont.getURI().toString();
        }
        return str.length() == 0 ? null : "Asserted in: " + str;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(300, 200);
    }

    public int getScrollableUnitIncrement(Rectangle rectangle, int i, int i1) {
        return getFontMetrics(getFont()).getHeight();
    }

    public int getScrollableBlockIncrement(Rectangle rectangle, int i, int i1) {
        return 100;
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    class RemoveAxiomListener extends MouseAdapter {

        private AnnotationRow c;

        public RemoveAxiomListener(AnnotationRow c) {
            this.c = c;
        }

        public void mouseReleased(MouseEvent mouseEvent) {
            model.removeRow(c);
        }
    }

    class AddFieldListener extends MouseAdapter {

        private AnnotationRow c;

        public AddFieldListener(AnnotationRow c) {
            this.c = c;
        }

        public void mouseReleased(MouseEvent mouseEvent) {
            model.addRow(c.getURI());
        }
    }
}
