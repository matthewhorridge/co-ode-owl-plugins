package org.coode.change;

import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;

import javax.swing.*;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Apr 10, 2008<br><br>
 */
public class ChangeTreeCellRenderer extends OWLCellRenderer {

    private final Icon addIcon = Icons.getIcon("yes.gif");
    private final Icon removeIcon = Icons.getIcon("no.gif");

    private Object realObject;


    public ChangeTreeCellRenderer(OWLEditorKit owlEditorKit) {
        super(owlEditorKit);
        setHighlightKeywords(true);
        setWrap(true);
    }


    public Component getTreeCellRendererComponent(JTree jTree, Object o, boolean b, boolean b1, boolean b2, int i, boolean b3) {
        realObject = o;
        setStrikeThrough(o instanceof RemoveAxiom);
        if (o instanceof OWLOntologyChange){
            o = ((OWLOntologyChange)o).getAxiom();
        }
        else if (o instanceof java.util.List){
            o = "anonymous changeset (" + ((java.util.List)o).size() + ")";
        }
        return super.getTreeCellRendererComponent(jTree, o, b, b1, b2, i, b3);
    }


    protected Icon getIcon(Object object) {
        if (realObject instanceof AddAxiom){
            return addIcon;
        }
        else if (realObject instanceof RemoveAxiom){
            return removeIcon;
        }
        return super.getIcon(object);
    }
}
