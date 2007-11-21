package org.coode.pattern.valuepartition;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.util.CoveringAxiomFactory;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectVisitorAdapter;
import org.semanticweb.owl.util.OWLEntityCollector;

import java.util.Set;

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
public class ValuePartitionBuilder extends OWLObjectVisitorAdapter {

    private OWLObjectHierarchyProvider<OWLClass> hierarchy;
    private OWLModelManager mngr;
    private ValuePartition pattern;
    private ValuePartitionDescriptor descr;

    public ValuePartitionBuilder(OWLModelManager mngr, ValuePartitionDescriptor descr) {
        this.mngr = mngr;
        this.descr = descr;
        hierarchy = mngr.getOWLClassHierarchyProvider();
    }

    public void visit(OWLClass cls) {
        Set<OWLClass> children = hierarchy.getChildren(cls);
        if (children.size() > 0) {
            if (getCoveringAxiom(cls) != null) {
                OWLObjectProperty prop = getFirstPropInWhichThisClassIsInRange(cls);
                if (prop != null) {
                    pattern = new ValuePartition(cls, prop, children, descr, mngr, new EntityCreator(mngr));
                }
            }
        }
    }

    public ValuePartition getValuePartition(){
        return pattern;
    }

    private OWLDescription getCoveringAxiom(OWLClass cls) {
        CoveringAxiomFactory fac = new CoveringAxiomFactory(mngr.getOWLDataFactory(), hierarchy);
        cls.accept(fac);
        OWLDescription coveringAxiom = fac.getCoveringAxiom();
        for (OWLOntology ont : mngr.getActiveOntologies()){
            if (cls.getSuperClasses(ont).contains(coveringAxiom)){
                return coveringAxiom;
            }
            if (cls.getEquivalentClasses(ont).contains(coveringAxiom)){
                return coveringAxiom;
            }
        }
        return null;
    }

    private OWLObjectProperty getFirstPropInWhichThisClassIsInRange(OWLClass cls) {
        for (OWLOntology ont : mngr.getActiveOntologies()){
            for (OWLObjectProperty prop : ont.getReferencedObjectProperties()) {
                Set<OWLDescription> ranges = prop.getRanges(ont);
                for (OWLDescription range : ranges) {
                    if (isUsedIn(cls, range)) {
                        return prop;
                    }
                }
            }
        }
        return null;
    }

    private boolean isUsedIn(OWLEntity curEntity, OWLObject owlObject) {
        OWLEntityCollector collector = new OWLEntityCollector();
        owlObject.accept(collector);
        return collector.getObjects().contains(curEntity);
    }
}
