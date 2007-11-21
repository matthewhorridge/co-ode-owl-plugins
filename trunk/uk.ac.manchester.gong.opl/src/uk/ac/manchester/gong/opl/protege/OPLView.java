package uk.ac.manchester.gong.opl.protege;

import org.protege.editor.owl.ui.view.OWLClassAnnotationsViewComponent;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.editor.owl.model.namespace.NamespaceManager;
import org.protege.editor.owl.model.namespace.NamespaceManagerImpl;
import org.protege.editor.owl.model.inference.NoOpReasoner;
import org.protege.editor.core.ui.view.DisposableAction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.NamespaceUtil;
import uk.ac.manchester.gong.opl.io.OPLFileException;
import uk.ac.manchester.gong.opl.io.OPLReader;
import uk.ac.manchester.gong.opl.ReasonerFactory;
import uk.ac.manchester.gong.opl.OPLInstructionsProcessor;

import javax.swing.text.Document;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.*;
import java.util.List;
import java.net.URI;
import java.awt.event.ActionEvent;
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
 * Date: Oct 16, 2007<br><br>
 */
public class OPLView extends AbstractOWLViewComponent {

    private OPLComponent c;

    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());

        c = new OPLComponent(getOWLEditorKit());
        add(c, BorderLayout.CENTER);

        addAction(c.getRunAction(), "A", "A");        
    }


    protected void disposeOWLView() {
        //@@TODO implement
    }
}
