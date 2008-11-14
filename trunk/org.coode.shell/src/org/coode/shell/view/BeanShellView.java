package org.coode.shell.view;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.core.plugin.PluginUtilities;
import org.eclipse.osgi.framework.internal.core.OSGi;
import org.eclipse.core.internal.registry.osgi.OSGIUtils;
import bsh.util.JConsole;
import bsh.Interpreter;
import bsh.EvalError;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;/*
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
 * Date: Jun 18, 2008<br><br>
 */
public class BeanShellView extends AbstractOWLViewComponent {


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        JConsole console = new JConsole();
        Interpreter interpreter = new Interpreter(console);
        interpreter.setClassLoader(getClass().getClassLoader());
        interpreter.set("eKit", getOWLEditorKit());
        interpreter.set("mngr", getOWLModelManager());

        interpreter.eval("import org.protege.editor.core.*;");
        interpreter.eval("import org.semanticweb.owl.model.*;");
        
        new Thread(interpreter).start();

        add(console, BorderLayout.CENTER);
    }


    protected void disposeOWLView() {
        // do nothing
    }
}
