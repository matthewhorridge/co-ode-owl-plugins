package org.coode.pattern.owllist.namedlist;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectVisitorAdapter;

import java.util.Set;
import java.util.HashSet;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 3, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public class NamedOWLListBuilder extends OWLObjectVisitorAdapter {

    private NamedOWLList list;
    private OWLModelManager mngr;
    private NamedListDescriptor descr;

    public NamedOWLListBuilder(NamedListDescriptor descr, OWLModelManager mngr) {
        this.mngr = mngr;
        this.descr = descr;
    }

    public void visit(OWLClass owlClass) {
        for (OWLAxiom axiom : getUsageOfContentsProperty()) {
            if (axiom instanceof OWLSubClassAxiom){
                if (((OWLSubClassAxiom)axiom).getSubClass().equals(owlClass)) {
                    list = new NamedOWLList(owlClass, descr, mngr);
                    break;
                }
            }
            else if (axiom instanceof OWLEquivalentClassesAxiom){
                if (((OWLEquivalentClassesAxiom)axiom).getDescriptions().contains(owlClass)) {
                    list = new NamedOWLList(owlClass, descr, mngr);
                    break;
                }
            }
        }
    }

    public void reset(){
        list = null;
    }

    public NamedOWLList getList() {
        return list;
    }

    private Set<OWLAxiom> getUsageOfContentsProperty() {
        Set<OWLAxiom> usageRecords = new HashSet<OWLAxiom>();
        final OWLObjectProperty contentsProperty = descr.getListExpressionDescriptor(mngr.getOWLDataFactory()).getDefaultContentsProperty();
        for (OWLOntology ont : mngr.getActiveOntologies()){
            usageRecords.addAll(ont.getReferencingAxioms(contentsProperty));
        }
        return usageRecords;
    }
}
