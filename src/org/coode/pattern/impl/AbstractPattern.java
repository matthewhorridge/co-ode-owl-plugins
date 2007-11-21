package org.coode.pattern.impl;

import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.api.PatternListener;

import java.util.*;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 14, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public abstract class AbstractPattern implements Pattern {

    private final PatternDescriptor descr;

    private Set<PatternListener> listeners = new HashSet<PatternListener>();

    public AbstractPattern(PatternDescriptor descr) {
        this.descr = descr;
    }

    /**
     * Allow overriding as you may want to specialise the return type
     *
     * @return the pattern descriptor for the given pattern
     */
    public PatternDescriptor getDescriptor() {
        return descr;
    }

    public boolean isValid() {
        return false;
    }

    public List getParts() {
        return Collections.emptyList();
    }

    protected void notifyChanged(){
        for (PatternListener l : listeners){
            l.patternChanged(this);
        }
        PatternManagerFactory.getOWLPatternManager().notifyPatternChanged(this);
    }

    public void addChangeListener(PatternListener l) {
        listeners.add(l);
    }

    public void removeChangeListener(PatternListener l) {
        listeners.remove(l);
    }






///////////////////////// OWLObject implementation
//
//    public final OWLObject[] getContainedObjects() throws OWLException {
//        Set<OWLObject> parts = new HashSet<OWLObject>();
//        for (Object part : getParts()) {
//            if (part instanceof OWLEntity) {
//                OWLEntity owlPart = (OWLEntity) part;
//                parts.add(owlPart);
//                parts.addAll(Arrays.asList(owlPart.getContainedObjects()));
//            }
//        }
//        return (OWLObject[]) parts.toArray();
//    }
//
//    public final OWLDataFactory getOWLDataFactory() throws OWLException {
//        return mngr.getOWLDataFactory();
//    }
//
//    public Object clone() throws CloneNotSupportedException {
//        throw new CloneNotSupportedException("Clone not implemented yet for patterns");
//    }
//
//    public final void accept(OWLObjectVisitor owlObjectVisitor) throws OWLException {
//        throw new OWLException("Visitor cannot be called on OWL patterns"){
//
//        };
//    }
//
//    public final Map<Object, Object> getMetadata() throws OWLException {
//        return Collections.emptyMap();
//    }
//
//    public final Set<OWLAnnotation> getAnnotations() throws OWLException {
//        Set<OWLAnnotation> annots = new HashSet<OWLAnnotation>();
//        for (Object part : getParts()) {
//            if (part instanceof OWLEntity) {
//                OWLEntity owlPart = (OWLEntity) part;
//                for (OWLOntology ont : mngr.getActiveOntologies()){
//                    annots.addAll(owlPart.getAnnotations(ont));
//                }
//            }
//        }
//        return annots;
//    }
//
//    public final Set<OWLAnnotation> getAnnotations(OWLOntology owlOntology) throws OWLException {
//        Set<OWLAnnotation> annots = new HashSet<OWLAnnotation>();
//        for (Object part : getParts()) {
//            if (part instanceof OWLEntity) {
//                OWLEntity owlPart = (OWLEntity) part;
//                annots.addAll(owlPart.getAnnotations(owlOntology));
//            }
//        }
//        return annots;
//    }

//////////////////////////////

    /**
     * Naive implementation - a pattern that is the same class (or subclass) that has the same parts is the same
     * @param object
     * @return true if the above holds
     */
    public boolean equals(Object object) {
        return (getClass().isAssignableFrom(object.getClass())
                && getParts().equals(((Pattern)object).getParts()));
    }
}
