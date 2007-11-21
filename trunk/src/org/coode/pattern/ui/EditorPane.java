package org.coode.pattern.ui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
 * Date: Nov 20, 2007<br><br>
 */
    class EditorPane {

        private static JComponent defaultFocusComponent;

        public static int showDialog(String title, JComponent component,
                                     JComponent focusComponent,
                                     Component parent) {
            defaultFocusComponent = focusComponent;
            JOptionPane optionPane = new JOptionPane(component, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dlg = optionPane.createDialog(parent, title);
            dlg.addWindowListener(new WindowAdapter() {
                public void windowOpened(WindowEvent e) {
                    if (defaultFocusComponent != null) {
                        defaultFocusComponent.requestFocusInWindow();
                    }
                }
                
            });
            dlg.pack();
            dlg.setResizable(true);
            dlg.setVisible(true);
            Object value = optionPane.getValue();
            return (value != null) ? (Integer) value : JOptionPane.CLOSED_OPTION;
        }
    }