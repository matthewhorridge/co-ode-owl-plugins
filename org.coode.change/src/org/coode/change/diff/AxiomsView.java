package org.coode.change.diff;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.OWLAxiomTypeFramePanel;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeListener;

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
 * Date: Dec 24, 2008<br><br>
 */
public class AxiomsView extends AbstractOWLViewComponent {

    private OWLAxiomTypeFramePanel list;

    private OWLModelManagerListener listener = new OWLModelManagerListener(){

        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.getType().equals(EventType.ACTIVE_ONTOLOGY_CHANGED)){
                setOntology(getOWLModelManager().getActiveOntology());
            }
        }
    };

    private OWLOntologyChangeListener ontChangeListener = new OWLOntologyChangeListener(){

        public void ontologiesChanged(java.util.List<? extends OWLOntologyChange> changes) throws OWLException {
            boolean requiresReload = false;
            for (OWLOntologyChange chg : changes){
                if (chg.getOntology().equals(currentOntology)){
                    // would be more efficient if we just add/remove as required
                    requiresReload = true;
                }
            }
            if (requiresReload){
                setOntology(currentOntology);
            }
        }
    };

    private OWLOntology currentOntology;


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());

        list = new OWLAxiomTypeFramePanel(getOWLEditorKit());

        add(new JScrollPane(list), BorderLayout.CENTER);

        setOntology(getOWLModelManager().getActiveOntology());

        getOWLModelManager().addListener(listener);
        getOWLModelManager().addOntologyChangeListener(ontChangeListener);
    }


    protected void setOntology(OWLOntology ontology) {
        currentOntology = ontology;
        list.setRoot(ontology.getAxioms());
    }


    protected void disposeOWLView() {
        getOWLModelManager().removeListener(listener);
        getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
        list = null;
    }
}
