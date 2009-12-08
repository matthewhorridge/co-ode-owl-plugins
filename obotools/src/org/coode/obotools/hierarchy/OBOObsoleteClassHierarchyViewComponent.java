package org.coode.obotools.hierarchy;

import org.coode.owlapi.obo.parser.OBOVocabulary;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
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
 * Date: Jun 15, 2009<br><br>
 */
public class OBOObsoleteClassHierarchyViewComponent extends AbstractOWLClassHierarchyViewComponent {

    private OWLObjectHierarchyProvider<OWLClass> hp;


    protected void performExtraInitialisation() throws Exception {
    }


    protected OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
        if (hp == null){
            final OWLModelManager mngr = getOWLModelManager();
            final OWLDataFactory df = mngr.getOWLDataFactory();
            hp = new OBOObsoleteClassHierarchyProvider<OWLClass>(mngr.getOWLHierarchyManager().getOWLClassHierarchyProvider(),
                                                                 df.getOWLAnnotationProperty(OBOVocabulary.IS_OBSOLETE.getIRI()),
                                                                 df.getOWLStringLiteral("true"),
                                                                 mngr);
        }
        return hp;
    }
}