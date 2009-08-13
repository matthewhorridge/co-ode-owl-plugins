package org.coode.change.diff;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * Date: Dec 24, 2008<br><br>
 */
public class DiffView extends AbstractOWLViewComponent {

    private AxiomsPanel left;

    private AxiomsPanel right;

    private JCheckBox showAnnotationsCheckbox;
    private JCheckBox showDisjointsCheckbox;


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());

        left = new AxiomsPanel(getOWLEditorKit());
        left.setName("LEFT");

        right = new AxiomsPanel(getOWLEditorKit());
        right.setName("RIGHT");

        left.setDiff(right);
        right.setDiff(left);

        JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitter.setContinuousLayout(true);
        splitter.setDividerLocation(0.5f);

        showAnnotationsCheckbox = new JCheckBox("Show annotations");
        showAnnotationsCheckbox.setSelected(true);
        showAnnotationsCheckbox.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent event) {
                left.setShowAnnotations(showAnnotationsCheckbox.isSelected());
                right.setShowAnnotations(showAnnotationsCheckbox.isSelected());
            }
        });

        showDisjointsCheckbox = new JCheckBox("Show disjoints");
        showDisjointsCheckbox.setSelected(true);
        showDisjointsCheckbox.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent event) {
                left.setShowDisjointClasses(showDisjointsCheckbox.isSelected());
                right.setShowDisjointClasses(showDisjointsCheckbox.isSelected());
            }
        });

        Box controls = new Box(BoxLayout.LINE_AXIS);
        controls.add(showAnnotationsCheckbox);
        controls.add(showDisjointsCheckbox);

        add(controls, BorderLayout.NORTH);
        add(splitter, BorderLayout.CENTER);

    }


    protected void disposeOWLView() {
        try {
            left.dispose();
            right.dispose();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
