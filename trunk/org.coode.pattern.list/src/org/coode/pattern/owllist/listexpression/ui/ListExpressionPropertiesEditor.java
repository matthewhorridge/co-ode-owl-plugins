package org.coode.pattern.owllist.listexpression.ui;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
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
 * Date: Oct 24, 2007<br><br>
 */
public class ListExpressionPropertiesEditor extends JComponent {

    private JTextComponent nextEditor;
    private JTextComponent followedByEditor;
    private JTextComponent contentsEditor;

    public ListExpressionPropertiesEditor(){
        setLayout(new BorderLayout());
        add(createFeaturesPanel(), BorderLayout.CENTER);
    }

    private JComponent createFeaturesPanel() {
        JPanel c = new JPanel();
        c.setPreferredSize(new Dimension(400, 200));
        setupBorder("Properties", c);

        c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));
        c.add(new JLabel("Directly following elements related by:"));
        nextEditor = new JTextField();
        nextEditor.setEnabled(false);
        c.add(nextEditor);

        c.add(new JLabel("Indirectly following elements are related by:"));
        followedByEditor = new JTextField();
        followedByEditor.setEnabled(false);
        c.add(followedByEditor);

        c.add(new JLabel("Contents are refered to by:"));
        contentsEditor = new JTextField();
        contentsEditor.setEnabled(false);
        c.add(contentsEditor);

        return c;
    }


    private void setupBorder(String name, JComponent component) {
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createTitledBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                                BorderFactory.createEmptyBorder(7, 7, 7, 7)
                        ),
                        name
                )
        ));
    }

}
