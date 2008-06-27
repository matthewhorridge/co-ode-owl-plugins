package org.coode.pattern.valuepartition;

import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.util.CoveringAxiomFactory;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLEntityCollector;
import org.semanticweb.owl.util.OWLObjectVisitorAdapter;

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

    private OWLObjectHierarchyProvider<OWLClass> hp;
    private OWLOntologyManager mngr;
    private ValuePartitionDescriptor descr;
    private Set<OWLOntology> ontologies;

    private ValuePartition pattern;
    
    public ValuePartitionBuilder(OWLOntologyManager mngr,
                                 ValuePartitionDescriptor descr,
                                 Set<OWLOntology> ontologies,
                                 OWLObjectHierarchyProvider<OWLClass> hp) {
        this.mngr = mngr;
        this.descr = descr;
        this.ontologies = ontologies;
        this.hp = hp;
    }

    public void visit(OWLClass cls) {
        ValuePartition2.Params params = new ValuePartition2.Params();
        params.base = cls;
        params.values = hp.getChildren(cls);
        if (params.values.size() > 0) {
            if (getCoveringAxiom(cls) != null) {
                params.property = getFirstPropInWhichThisClassIsInRange(cls);
                if (params.property != null) {
                    //pattern = new ValuePartition(params, mngr.getOWLDataFactory(), descr);
                }
            }
        }
    }

    public ValuePartition getValuePartition(){
        return pattern;
    }

    private OWLDescription getCoveringAxiom(OWLClass cls) {
        CoveringAxiomFactory fac = new CoveringAxiomFactory(mngr.getOWLDataFactory(), hp);
        cls.accept(fac);
        OWLDescription coveringAxiom = fac.getCoveringAxiom();
        for (OWLOntology ont : ontologies){
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
        for (OWLOntology ont : ontologies){
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
