package org.coode.pattern.ui;

import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.impl.AbstractPatternEditor;
import org.protege.editor.core.ui.view.ViewsPane;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLException;

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
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 24, 2007<br><br>
 */
public class EntityEditor extends AbstractPatternEditor {

    private ViewsPane editor;

    protected void initialise(OWLEditorKit eKit, PatternDescriptor descr) {
    }

    public void setPattern(Pattern pattern) {
        removeAll();

        editor = OWLEntityViewFactory.createView(getPattern().getBase(), getOWLEditorKit().getOWLWorkspace());

        add(editor, BorderLayout.CENTER);
    }

    public Pattern createPattern() throws OWLException {
        return null;
    }

    protected void disposePatternEditor() {
        editor.dispose();
        editor = null;
    }

    public JComponent getFocusComponent() {
        return null;
    }
}
