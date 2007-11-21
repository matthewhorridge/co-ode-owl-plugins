package org.coode.pattern.ui;

import org.protege.editor.core.ui.view.ViewsPane;
import org.protege.editor.core.ui.view.ViewsPaneMemento;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectVisitorAdapter;

import java.net.URL;
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
 * @@TODO only OWL entities supported currently
 */
public class OWLEntityViewFactory extends OWLObjectVisitorAdapter {

    private ViewsPane owlClassViewPane;
    private ViewsPane owlObjectPropertyViewPane;
    private ViewsPane owlDataPropertyViewPane;
    private ViewsPane owlIndividualViewPane;

    private ViewsPane selectedViewsPane = null;

    private OWLWorkspace workspace;

    public OWLEntityViewFactory(OWLWorkspace workspace) {
        this.workspace = workspace;
    }

    /**
     * Convenience method
     * @param object
     * @param workspace
     * @return
     */
    public static ViewsPane createView(OWLObject object, OWLWorkspace workspace) {
        OWLEntityViewFactory fac = new OWLEntityViewFactory(workspace);
        object.accept(fac);
        return fac.selectedViewsPane;
    }

    public void reset(){
        selectedViewsPane = null;
    }

    public void visit(OWLClass owlClass) {
        if (owlClassViewPane == null){
            owlClassViewPane = createOWLEntityPane("/selected-entity-view-class-panel.xml",
                                                   "org.protege.editor.owl.ui.view.selectedentityview.classes");
        }
        selectedViewsPane = owlClassViewPane;
    }

    public void visit(OWLObjectProperty owlObjectProperty) {
        if (owlObjectPropertyViewPane == null){
            owlObjectPropertyViewPane = createOWLEntityPane("/selected-entity-view-objectproperty-panel.xml",
                                                            "org.protege.editor.owl.ui.view.selectedentityview.objectproperties");
        }
        selectedViewsPane = owlObjectPropertyViewPane;
    }

    public void visit(OWLDataProperty owlDataProperty) {
        if (owlDataPropertyViewPane == null){
            owlDataPropertyViewPane = createOWLEntityPane("/selected-entity-view-dataproperty-panel.xml",
                                                          "org.protege.editor.owl.ui.view.selectedentityview.dataproperties");
        }
        selectedViewsPane = owlDataPropertyViewPane;
    }

    public void visit(OWLIndividual owlIndividual) {
        if (owlIndividualViewPane == null){
            owlIndividualViewPane = createOWLEntityPane("/selected-entity-view-individual-panel.xml",
                                                        "org.protege.editor.owl.ui.view.selectedentityview.individuals");
        }
        selectedViewsPane = owlIndividualViewPane;
    }

    private ViewsPane createOWLEntityPane(String viewConfig, String memoView) {
        URL clsURL = AbstractOWLViewComponent.class.getResource(viewConfig);
        return new ViewsPane(workspace,
                             new ViewsPaneMemento(clsURL,
                                                  memoView,
                                                  false));
    }

    public ViewsPane getViewsPane() {
        return selectedViewsPane;
    }
}
