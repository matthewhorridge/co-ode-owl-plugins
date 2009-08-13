package org.coode.annotate;

import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.util.URIShortFormProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.net.URI;
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
 * Date: Aug 16, 2007<br><br>
 */
public class AnnotationTemplateView extends AbstractOWLSelectionViewComponent {

    private TemplateModel model;
    private Template form;

    private boolean updateRequired = false;

    private HierarchyListener hListener = new HierarchyListener(){
        public void hierarchyChanged(HierarchyEvent hierarchyEvent) {
            if (updateRequired){
                updateView();
            }
        }
    };

    public void initialiseView() throws Exception {
        setLayout(new BorderLayout());
        model = new TemplateModel(getOWLModelManager());
        form = new Template(model);
        form.setURIShortFormProvider(new URIShortFormProvider(){
            public String getShortForm(URI uri) {
                return getOWLModelManager().getURIRendering(uri);
            }
        });
        JScrollPane scroller = new JScrollPane(form);
        add(scroller, BorderLayout.CENTER);
        addHierarchyListener(hListener);
    }

    public void disposeView() {
        model.dispose();
    }

    protected OWLObject updateView() {
        OWLEntity selectedEntity = getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
        if (isShowing()){
            form.setEntity(selectedEntity);
            updateRequired = false;
        }
        else{
            updateRequired = true;
        }
        return selectedEntity;
    }

    protected boolean isOWLClassView() {
        return true;
    }


    protected boolean isOWLObjectPropertyView() {
        return true;
    }


    protected boolean isOWLDataPropertyView() {
        return true;
    }


    protected boolean isOWLIndividualView() {
        return true;
    }
}
