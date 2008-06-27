package org.coode.taxonomy;

import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.view.AbstractOWLClassViewComponent;
import org.semanticweb.owl.model.OWLClass;

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
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 10, 2007<br><br>
 * <p/>
 *
 * Shows all the descendants of the selected class in a text window for cut and paste
 */
public class DescendantsView extends AbstractOWLClassViewComponent {

    private JTextArea namesComponent;

    public void initialiseClassView() throws Exception {
        setLayout(new BorderLayout(6, 6));
        namesComponent = new JTextArea();
        add(new JScrollPane(namesComponent), BorderLayout.CENTER);
    }

    protected OWLClass updateView(OWLClass selectedClass) {
        namesComponent.setText("");
        OWLObjectHierarchyProvider<OWLClass> hp = getOWLModelManager().getOWLClassHierarchyProvider();
        for (OWLClass sub: hp.getDescendants(selectedClass)){
            namesComponent.append(getOWLModelManager().getRendering(sub));
            namesComponent.append("\n");
        }
        return selectedClass;
    }

    public void disposeView() {
        //@@TODO implement
    }
}
