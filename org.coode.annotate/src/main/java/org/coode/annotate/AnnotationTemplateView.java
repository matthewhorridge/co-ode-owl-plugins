package org.coode.annotate;

import java.awt.BorderLayout;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
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

import javax.swing.JScrollPane;

import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 16, 2007<br><br>
 */
public class AnnotationTemplateView extends AbstractOWLSelectionViewComponent {
    private static final long serialVersionUID = 1L;
    private TemplateModel model;
    private Template form;

    protected boolean updateRequired = false;

    private HierarchyListener hListener = new HierarchyListener(){
        @Override
        public void hierarchyChanged(HierarchyEvent hierarchyEvent) {
            if (updateRequired){
                updateView();
            }
        }
    };

    @Override
    public void initialiseView() {
        setLayout(new BorderLayout());
        model = new TemplateModel(getOWLModelManager());
        form = new Template(model);
        JScrollPane scroller = new JScrollPane(form);
        add(scroller, BorderLayout.CENTER);
        addHierarchyListener(hListener);
    }

    @Override
    public void disposeView() {
        model.dispose();
    }

    @Override
    protected OWLObject updateView() {
        OWLEntity selectedEntity = getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
        if (isShowing()){
            form.setSubject(selectedEntity != null ? selectedEntity.getIRI() : null);
            updateRequired = false;
        }
        else{
            updateRequired = true;
        }
        return selectedEntity;
    }

    @Override
    protected boolean isOWLClassView() {
        return true;
    }


    @Override
    protected boolean isOWLObjectPropertyView() {
        return true;
    }


    @Override
    protected boolean isOWLDataPropertyView() {
        return true;
    }


    @Override
    protected boolean isOWLIndividualView() {
        return true;
    }


    @Override
    protected boolean isOWLDatatypeView() {
        return true;
    }


    @Override
    protected boolean isOWLAnnotationPropertyView() {
        return true;
    }
}
