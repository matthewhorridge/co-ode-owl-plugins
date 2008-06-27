package org.coode.pattern.valuepartition;

import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.impl.AbstractPattern;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.util.CoveringAxiomFactory;
import org.protege.editor.owl.ui.renderer.OWLEntityAnnotationValueRenderer;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLEntityRemover;
import org.semanticweb.owl.util.OWLEntityRenamer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 1, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class ValuePartition extends AbstractPattern {

//    private OWLClass baseClass;

//    private OWLObjectProperty property;

//    private Set<OWLClass> values = new HashSet<OWLClass>();

//    private boolean functional = true;

    private OWLObjectProperty oldProperty;
    private OWLClass oldBase;
    private String newBaseName;
    private String newPropName;

    private EntityCreator entityCreator;

    private OWLModelManager mngr; // @@TODO remove Protege dependencies

    private Params params;

    public static class Params {
        public OWLClass superCls;
        public OWLClass base;
        public Set<OWLClass> values;
        public OWLObjectProperty superProperty;
        public OWLObjectProperty property;
        public boolean functional;
    }

//    public ValuePartition(OWLClass cls, OWLObjectProperty property,
//                          PatternDescriptor<ValuePartition> descriptor,
//                          OWLModelManager mngr, EntityCreator entityCreator) {
//        this(cls, property, mngr.getOWLClassHierarchyProvider().getChildren(cls), descriptor, mngr, entityCreator);
//    }
//
//    public ValuePartition(String name, String propName,
//                          Set<OWLClass> values,
//                          PatternDescriptor<ValuePartition> descriptor,
//                          OWLModelManager mngr, EntityCreator entityCreator) {
//        this(entityCreator.createClass(name), entityCreator.createProperty(propName), values, descriptor, mngr, entityCreator);
//    }

    public ValuePartition(Params params,
                          PatternDescriptor<ValuePartition> descriptor,
                          OWLModelManager mngr,
                          EntityCreator entityCreator) {
        super(descriptor);

        this.entityCreator = entityCreator;
        this.mngr = mngr;

        this.params = params;
    }

    // @@TODO remove the Protege binding of the below methods

    private OWLObjectHierarchyProvider<OWLClass> getOWLClassHierarchyProvider() {
        return mngr.getOWLClassHierarchyProvider();
    }

    public List<OWLOntologyChange> delete(OWLOntologyManager mngr, Set<OWLOntology> onts) {
        OWLEntityRemover deleter = new OWLEntityRemover(mngr, onts);
        params.property.accept(deleter);
        params.base.accept(deleter);
        for (OWLClass value : params.values){
            value.accept(deleter);
        }
        params = null;

        return deleter.getChanges();
    }

////////////////////////////// Pattern implementation

    public ValuePartitionDescriptor getDescriptor() {
        return (ValuePartitionDescriptor) super.getDescriptor();
    }

    public boolean isValid() {
        return params.base != null && params.property != null && getValues().size() > 0;
    }

    public OWLEntity getBase() {
        return params.base;
    }

    public List<OWLClass> getParts() {
        return new ArrayList<OWLClass>(getValues());
    }

    public Set toOWL(OWLOntology ont, OWLDataFactory df) {
        return null;  //@@TODO implement
    }

    public List deleteFromOntologies(Set onts) {
        return null;  //@@TODO implement
    }

    public List addToOntology(OWLOntology activeOnt, Set activeOnts) {
        return null;  //@@TODO implement
    }

    public List<OWLOntologyChange> getChanges(OWLOntologyManager mngr,
                                              OWLOntology activeOnt,
                                              Set<OWLOntology> activeOntologies) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        if (isValid()) {
            final OWLDataFactory df = mngr.getOWLDataFactory();

            if (newBaseName != null){
                changes.addAll(entityCreator.renameEntity(params.base, newBaseName));
                newBaseName = null;
            }

            if (newPropName != null){
                changes.addAll(entityCreator.renameEntity(params.property, newPropName));
                newPropName = null;
            }

            OWLEntityRenamer renamer = new OWLEntityRenamer(mngr, activeOntologies);
            if (oldBase != null){
                changes.addAll(renamer.changeURI(oldBase.getURI(), params.base.getURI()));
                oldBase = null;
            }

            if (oldProperty != null){
                changes.addAll(renamer.changeURI(oldProperty.getURI(), params.property.getURI()));
                oldProperty = null;
            }

            // object params.property range
            Set<OWLDescription> ranges = new HashSet<OWLDescription>();
            OWLOntology ontologyCOntainingFunctional = null; // assumes the functional assertion is in one place
            for (OWLOntology ont : activeOntologies){
                ranges.addAll(params.property.getRanges(ont));
                if (params.property.isFunctional(ont)){
                    ontologyCOntainingFunctional = ont;
                }
            }
            if(!ranges.contains(params.base)){
                changes.add(new AddAxiom(activeOnt, df.getOWLObjectPropertyRangeAxiom(params.property, params.base)));
            }

            if (params.functional && ontologyCOntainingFunctional == null){
                changes.add(new AddAxiom(activeOnt, df.getOWLFunctionalObjectPropertyAxiom(params.property)));
            }
            else if (!params.functional && ontologyCOntainingFunctional != null){
                changes.add(new RemoveAxiom(ontologyCOntainingFunctional,
                                            df.getOWLFunctionalObjectPropertyAxiom(params.property)));
            }

            Set<OWLClass> oldValues = getOWLClassHierarchyProvider().getChildren(params.base);
            if (!params.values.equals(oldValues)){
                changes.addAll(updateValues(mngr, activeOnt, activeOntologies, oldValues));
            }
        }
        else {
            System.out.println("CANNOT CREATE CHANGES FOR VP");
        }
        return changes;
    }

    private List<OWLOntologyChange> updateValues(OWLOntologyManager mngr,
                                                 OWLOntology activeOnt,
                                                 Set<OWLOntology> activeOntologies,
                                                 Set<OWLClass> oldValues) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        final OWLDataFactory df = mngr.getOWLDataFactory();

        // remove old subclass assertions from all active ontologies
        for (OWLClass value : oldValues){
            if (!params.values.contains(value)){
                for (OWLOntology ont : activeOntologies){
                    final OWLSubClassAxiom owlSubClassAxiom = df.getOWLSubClassAxiom(value, params.base);
                    if (ont.containsAxiom(owlSubClassAxiom)){
                        changes.add(new RemoveAxiom(ont, owlSubClassAxiom));
                    }
                }
            }
        }
        
        // add subclass params.values
        for (OWLClass value : params.values) {
            if (!oldValues.contains(value)){
                changes.add(new AddAxiom(activeOnt, df.getOWLSubClassAxiom(value, params.base)));
            }
        }

        // create a pattern root if required
        OWLClass patternRoot = getDefaultPatternRoot();

        Set<OWLClass> superClasses = getOWLClassHierarchyProvider().getParents(params.base);

        if (superClasses.size() == 1){
            OWLClass superclass = superClasses.iterator().next();
            if (superclass.equals(df.getOWLThing())){
                for (OWLOntology ont : activeOntologies){
                    OWLSubClassAxiom axiom = df.getOWLSubClassAxiom(params.base, df.getOWLThing());
                    if (ont.containsAxiom(axiom)){
                        changes.add(new RemoveAxiom(ont, axiom));
                    }
                }
                superClasses.clear();
            }
        }

        if (superClasses.isEmpty()){
            changes.add(new AddAxiom(activeOnt, df.getOWLSubClassAxiom(params.base, patternRoot)));
        }

        // handle disjoints
        for (OWLOntology ont : activeOntologies){
            for(OWLClassAxiom ax : ont.getClassAxioms()) {
                if(ax instanceof OWLDisjointClassesAxiom) {
                    OWLDisjointClassesAxiom disj = (OWLDisjointClassesAxiom)ax;
                    for (OWLDescription d : disj.getDescriptions()){
                        if (oldValues.contains(d)){
                            changes.add(new RemoveAxiom(ont, ax));
                            break;
                        }
                    }
                }
            }
        }
        changes.add(new AddAxiom(activeOnt, df.getOWLDisjointClassesAxiom(params.values)));

        changes.addAll(updateCovering(df, activeOnt, activeOntologies));

        return changes;
    }

    private List<OWLOntologyChange> updateCovering(OWLDataFactory df,
                                                   OWLOntology activeOnt,
                                                   Set<OWLOntology> activeOnts) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        // remove any existing covering axioms
        CoveringAxiomFactory cf = new CoveringAxiomFactory(df, getOWLClassHierarchyProvider());
        params.base.accept(cf);
        OWLDescription existingCovering = cf.getCoveringAxiom();

        if (existingCovering != null){
            // check for existing existingCovering
            Set<OWLDescription> equivs = new HashSet<OWLDescription>();
            equivs.add(existingCovering);
            equivs.add(params.base);
            OWLEquivalentClassesAxiom axiom = df.getOWLEquivalentClassesAxiom(equivs);
            for (OWLOntology ont : activeOnts){
                if (ont.containsAxiom(axiom)){ // @@TODO what if the equivalent axiom contains more than just the 2 descriptions??
                    changes.add(new RemoveAxiom(ont, axiom));
                }
            }
        }

        // create covering axiom
        Set<OWLDescription> and = new HashSet<OWLDescription>();
        and.add(params.base);
        and.add(df.getOWLObjectUnionOf(params.values));
        changes.add(new AddAxiom(activeOnt, df.getOWLEquivalentClassesAxiom(and)));
        return changes;
    }

    private OWLClass getDefaultPatternRoot() {
        return entityCreator.createClass("ValuePartition");
    }

/////////////////////////////////////////////////////

    public OWLClass getBaseClass() {
        return params.base;
    }

    public OWLObjectProperty getProperty() {
        return params.property;
    }

    public Set<OWLClass> getValues() {
        return params.values;
    }

    public void addValue(OWLClass newValue) {
        params.values.add(newValue);
        notifyChanged();
    }

    public void setFunctional(boolean func) {
        if (func != params.functional){
            params.functional = func;
            notifyChanged();
        }
    }

    public boolean isFunctional(){
        return params.functional;
    }

    public void removeValue(OWLClass oldValue) {
        if (params.values.contains(oldValue)){
            params.values.remove(oldValue);
            notifyChanged();
        }
    }

    public OWLObjectProperty setProperty(String name) throws OWLException {
        final String currentName = mngr.getRendering(params.property);
        if (name != null && name.length() > 0 && !name.equals(currentName)){
            if (mngr.getOWLEntityRenderer() instanceof OWLEntityAnnotationValueRenderer){
                newPropName = name;
            }
            else{
                OWLObjectProperty temp = entityCreator.createProperty(name);
                if (oldProperty == null){
                    oldProperty = params.property;
                }
                params.property = temp;
            }
            notifyChanged();
            return params.property;
        }
        return null;
    }

    public OWLClass setName(String name) throws OWLException {
        final String currentName = mngr.getRendering(params.base);
        if (name != null && name.length() > 0 && !name.equals(currentName)){
            if (mngr.getOWLEntityRenderer() instanceof OWLEntityAnnotationValueRenderer){
                newBaseName = name;
            }
            else{
                OWLClass temp = entityCreator.createClass(name);
                if (oldBase == null){
                    oldBase = params.base;
                }
                params.base = temp;
            }
            notifyChanged();
            return params.base;
        }
        return null;
    }
}
