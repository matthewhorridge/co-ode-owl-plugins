import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owl.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Dec 4, 2007
 * Time: 10:20:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClassAxiomCollectionVisitor extends OWLAxiomVisitorAdapter {
    private Set<OWLClass> markedClasses;
    private OWLDataFactory factory; //factory for creating new axiom, if neceessary
    private HashSet<OWLClassAxiom> classAxiomCollection = new HashSet<OWLClassAxiom>();  //collection of all the axioms to be copied
    private HashSet<OWLEntityAnnotationAxiom> annotationAxiomCollection = new HashSet<OWLEntityAnnotationAxiom>();  //collection of all the axioms to be copied
    private HashSet<OWLDeclarationAxiom> declarationAxiomCollection = new HashSet<OWLDeclarationAxiom>();

    /** returns all the collected class axioms */
    public Set<OWLClassAxiom> getAxioms() {
        return classAxiomCollection;
    }

    /** returns all the collected declaration axioms */
    public Set<OWLDeclarationAxiom> getDeclarations() {
        return declarationAxiomCollection;
    }

    /** returns all the collected entity annotation axioms */
    public Set<OWLEntityAnnotationAxiom> getAnnotations() {
        return annotationAxiomCollection;
    }

    public ClassAxiomCollectionVisitor(Set<OWLClass> markedClasses, OWLDataFactory factory) {
        this.markedClasses = markedClasses;
        this.factory = factory;
    }

    public void visit(OWLSubClassAxiom axiom) {
        //always include the superclass/restriction
        classAxiomCollection.add(axiom);
        /*if (!axiom.getSuperClass().isAnonymous()) {
            if (markedClasses.contains(axiom.getSuperClass())) {
            classAxiomCollection.add(axiom);
        } else {
            axiom.getSuperClass()

        }*/
    }

    public void visit(OWLDisjointClassesAxiom axiom) {
        boolean containedInBoth = true;
        Set<OWLDescription> desc = axiom.getDescriptions();
        for(OWLDescription d : desc) {
            if ((!d.isAnonymous()) && (!markedClasses.contains(d))) containedInBoth = false;
        }

        if (containedInBoth) classAxiomCollection.add(axiom);
    }

    public void visit(OWLDisjointUnionAxiom axiom) {
        Set<OWLDescription> newAxiomDescription = new HashSet<OWLDescription>();

        Set<OWLDescription> desc = axiom.getDescriptions();
        for(OWLDescription d : desc) {
            if (!d.isAnonymous()) {
                if (markedClasses.contains(d.asOWLClass())) {
                    newAxiomDescription.add(d);
                }
            }
        }

        OWLDisjointUnionAxiom newAxiom = factory.getOWLDisjointUnionAxiom(axiom.getOWLClass(), newAxiomDescription);
        classAxiomCollection.add(newAxiom);
    }

    public void visit(OWLEntityAnnotationAxiom axiom) {
        annotationAxiomCollection.add(axiom);
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
        classAxiomCollection.add(axiom);
    }


    /** special method to handle declaration axioms, which may be necessary for some applications */
    public void visit(OWLDeclarationAxiom axiom) {
        declarationAxiomCollection.add(axiom);
    }
}
