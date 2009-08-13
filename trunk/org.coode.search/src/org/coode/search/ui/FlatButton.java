package org.coode.search.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 29, 2009<br><br>
 */
public class FlatButton extends JButton {

        public final Border MOUSE_OVER_BORDER = new LineBorder(Color.BLACK, 1);
        public final Border MOUSE_OUT_BORDER = new EmptyBorder(1, 1, 1, 1);
        private Color normalBackground;

        public FlatButton(Action action) {
            super(action);
            addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent event) {
                    setBorder(MOUSE_OVER_BORDER);
                    setBackground(Color.LIGHT_GRAY);
                }
                public void mouseExited(MouseEvent event) {
                    setBorder(MOUSE_OUT_BORDER);
                    setBackground(normalBackground);
                }
            });
            setBorder(MOUSE_OUT_BORDER);
            normalBackground = getBackground();
        }


    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width + 10, 20);
    }
}