package org.coode.pattern.valuepartition;

import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.impl.AbstractPattern;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.util.CoveringAxiomFactory;
import org.protege.editor.owl.ui.renderer.OWLEntityAnnotationValueRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
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

    private OWLClass baseClass;

    private OWLClass oldBase;

    private OWLObjectProperty property;

    private OWLObjectProperty oldProperty;

    private Set<OWLClass> values = new HashSet<OWLClass>();

    private boolean functional = true;

    private String newBaseName;
    private String newPropName;

    private EntityCreator entityCreator;

    private OWLModelManager mngr; // @@TODO remove Protege dependencies

    public ValuePartition(OWLClass cls, OWLObjectProperty property,
                          PatternDescriptor<ValuePartition> descriptor,
                          OWLModelManager mngr, EntityCreator entityCreator) {
        this(cls, property, mngr.getOWLClassHierarchyProvider().getChildren(cls), descriptor, mngr, entityCreator);
    }

    public ValuePartition(String name, String propName,
                          Set<OWLClass> values,
                          PatternDescriptor<ValuePartition> descriptor,
                          OWLModelManager mngr, EntityCreator entityCreator) {
        this(entityCreator.createClass(name), entityCreator.createProperty(propName), values, descriptor, mngr, entityCreator);
    }

    public ValuePartition(OWLClass cls, OWLObjectProperty property,
                          Set<OWLClass> values,
                          PatternDescriptor<ValuePartition> descriptor,
                          OWLModelManager mngr,
                          EntityCreator entityCreator) {
        super(descriptor);

        this.entityCreator = entityCreator;
        this.mngr = mngr;

        if (values.size() > 0){
            this.values.addAll(values);
        }
        else{
            throw new IllegalArgumentException ("No values given for Value Partition");
        }
        this.baseClass = cls;
        this.property = property;
        for (OWLOntology ont : mngr.getActiveOntologies()){
            if (property.isFunctional(ont)){
                this.functional = true;
            }
        }
    }

    // @@TODO remove the Protege binding of the below methods

    private OWLObjectHierarchyProvider<OWLClass> getOWLClassHierarchyProvider() {
        return mngr.getOWLClassHierarchyProvider();
    }

    private OWLEntityRenderer getOWLEntityRenderer() {
        return mngr.getOWLEntityRenderer();
    }

    public List<OWLOntologyChange> delete(OWLOntologyManager mngr, Set<OWLOntology> onts) {
        OWLEntityRemover deleter = new OWLEntityRemover(mngr, onts);
        property.accept(deleter);
        baseClass.accept(deleter);
        for (OWLClass value : values){
            value.accept(deleter);
        }
        property = null;
        baseClass = null;
        values.clear();

        return deleter.getChanges();
    }

////////////////////////////// Pattern implementation

    public ValuePartitionDescriptor getDescriptor() {
        return (ValuePartitionDescriptor) super.getDescriptor();
    }

    public boolean isValid() {
        return baseClass != null && property != null && getValues().size() > 0;
    }

    public OWLEntity getBase() {
        return baseClass;
    }

    public List<OWLClass> getParts() {
        return new ArrayList<OWLClass>(getValues());
    }

    public OWLObject toOWL(OWLOntology ont, OWLDataFactory df) {
        return baseClass;
    }

    public List<OWLOntologyChange> getChanges(OWLOntologyManager mngr,
                                              OWLOntology activeOnt,
                                              Set<OWLOntology> activeOntologies) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        if (isValid()) {
            final OWLDataFactory df = mngr.getOWLDataFactory();

            if (newBaseName != null){
                changes.addAll(entityCreator.renameEntity(baseClass, newBaseName));
                newBaseName = null;
            }

            if (newPropName != null){
                changes.addAll(entityCreator.renameEntity(property, newPropName));
                newPropName = null;
            }

            OWLEntityRenamer renamer = new OWLEntityRenamer(mngr, activeOntologies);
            if (oldBase != null){
                changes.addAll(renamer.changeURI(oldBase.getURI(), baseClass.getURI()));
                oldBase = null;
            }

            if (oldProperty != null){
                changes.addAll(renamer.changeURI(oldProperty.getURI(), property.getURI()));
                oldProperty = null;
            }

            // object property range
            Set<OWLDescription> ranges = new HashSet<OWLDescription>();
            OWLOntology ontologyCOntainingFunctional = null; // assumes the functional assertion is in one place
            for (OWLOntology ont : activeOntologies){
                ranges.addAll(property.getRanges(ont));
                if (property.isFunctional(ont)){
                    ontologyCOntainingFunctional = ont;
                }
            }
            if(!ranges.contains(baseClass)){
                changes.add(new AddAxiom(activeOnt, df.getOWLObjectPropertyRangeAxiom(property, baseClass)));
            }

            if (functional && ontologyCOntainingFunctional == null){
                changes.add(new AddAxiom(activeOnt, df.getOWLFunctionalObjectPropertyAxiom(property)));
            }
            else if (!functional && ontologyCOntainingFunctional != null){
                changes.add(new RemoveAxiom(ontologyCOntainingFunctional,
                                            df.getOWLFunctionalObjectPropertyAxiom(property)));
            }

            Set<OWLClass> oldValues = getOWLClassHierarchyProvider().getChildren(baseClass);
            if (!values.equals(oldValues)){
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
            if (!values.contains(value)){
                for (OWLOntology ont : activeOntologies){
                    final OWLSubClassAxiom owlSubClassAxiom = df.getOWLSubClassAxiom(value, baseClass);
                    if (ont.containsAxiom(owlSubClassAxiom)){
                        changes.add(new RemoveAxiom(ont, owlSubClassAxiom));
                    }
                }
            }
        }
        
        // add subclass values
        for (OWLClass value : values) {
            if (!oldValues.contains(value)){
                changes.add(new AddAxiom(activeOnt, df.getOWLSubClassAxiom(value, baseClass)));
            }
        }

        // create a pattern root if required
        OWLClass patternRoot = getDefaultPatternRoot();

        Set<OWLClass> superClasses = getOWLClassHierarchyProvider().getParents(baseClass);

        if (superClasses.size() == 1){
            OWLClass superclass = superClasses.iterator().next();
            if (superclass.equals(df.getOWLThing())){
                for (OWLOntology ont : activeOntologies){
                    OWLSubClassAxiom axiom = df.getOWLSubClassAxiom(baseClass, df.getOWLThing());
                    if (ont.containsAxiom(axiom)){
                        changes.add(new RemoveAxiom(ont, axiom));
                    }
                }
                superClasses.clear();
            }
        }

        if (superClasses.isEmpty()){
            changes.add(new AddAxiom(activeOnt, df.getOWLSubClassAxiom(baseClass, patternRoot)));
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
        changes.add(new AddAxiom(activeOnt, df.getOWLDisjointClassesAxiom(values)));

        changes.addAll(updateCovering(df, activeOnt, activeOntologies));

        return changes;
    }

    private List<OWLOntologyChange> updateCovering(OWLDataFactory df,
                                                   OWLOntology activeOnt,
                                                   Set<OWLOntology> activeOnts) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        // remove any existing covering axioms
        CoveringAxiomFactory cf = new CoveringAxiomFactory(df, getOWLClassHierarchyProvider());
        baseClass.accept(cf);
        OWLDescription existingCovering = cf.getCoveringAxiom();

        if (existingCovering != null){
            // check for existing existingCovering
            Set<OWLDescription> equivs = new HashSet<OWLDescription>();
            equivs.add(existingCovering);
            equivs.add(baseClass);
            OWLEquivalentClassesAxiom axiom = df.getOWLEquivalentClassesAxiom(equivs);
            for (OWLOntology ont : activeOnts){
                if (ont.containsAxiom(axiom)){ // @@TODO what if the equivalent axiom contains more than just the 2 descriptions??
                    changes.add(new RemoveAxiom(ont, axiom));
                }
            }
        }

        // create covering axiom
        Set<OWLDescription> and = new HashSet<OWLDescription>();
        and.add(baseClass);
        and.add(df.getOWLObjectUnionOf(values));
        changes.add(new AddAxiom(activeOnt, df.getOWLEquivalentClassesAxiom(and)));
        return changes;
    }

    private OWLClass getDefaultPatternRoot() {
        return entityCreator.createClass("ValuePartition");
    }

/////////////////////////////////////////////////////

    public OWLClass getBaseClass() {
        return baseClass;
    }

    public OWLObjectProperty getProperty() {
        return property;
    }

    public Set<OWLClass> getValues() {
        return values;
    }

    public void addValue(OWLClass newValue) {
        values.add(newValue);
        notifyChanged();
    }

    public void setFunctional(boolean func) {
        if (func != functional){
            functional = func;
            notifyChanged();
        }
    }

    public boolean isFunctional(){
        return functional;
    }

    public void removeValue(OWLClass oldValue) {
        if (values.contains(oldValue)){
            values.remove(oldValue);
            notifyChanged();
        }
    }

    public OWLObjectProperty setProperty(String name) throws OWLException {
        final OWLEntityRenderer entRen = getOWLEntityRenderer();
        final String currentName = entRen.render(property);
        if (name != null && name.length() > 0 && !name.equals(currentName)){
            if (entRen instanceof OWLEntityAnnotationValueRenderer){
                newPropName = name;
            }
            else{
                OWLObjectProperty temp = entityCreator.createProperty(name);
                if (oldProperty == null){
                    oldProperty = property;
                }
                property = temp;
            }
            notifyChanged();
            return property;
        }
        return null;
    }

    public OWLClass setName(String name) throws OWLException {
        final OWLEntityRenderer entRen = getOWLEntityRenderer();
        final String currentName = entRen.render(baseClass);
        if (name != null && name.length() > 0 && !name.equals(currentName)){
            if (entRen instanceof OWLEntityAnnotationValueRenderer){
                newBaseName = name;
            }
            else{
                OWLClass temp = entityCreator.createClass(name);
                if (oldBase == null){
                    oldBase = baseClass;
                }
                baseClass = temp;
            }
            notifyChanged();
            return baseClass;
        }
        return null;
    }
}
