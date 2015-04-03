import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

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
    //collection of all the axioms to be copied
    private HashSet<OWLAnnotationAssertionAxiom> annotationAxiomCollection = new HashSet<OWLAnnotationAssertionAxiom>();
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
    public Set<OWLAnnotationAssertionAxiom> getAnnotations() {
        return annotationAxiomCollection;
    }

    public ClassAxiomCollectionVisitor(Set<OWLClass> markedClasses, OWLDataFactory factory) {
        markedClasses = markedClasses;
        factory = factory;
    }

    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        //always include the superclass/restriction
        classAxiomCollection.add(axiom);
        /*if (!axiom.getSuperClass().isAnonymous()) {
            if (markedClasses.contains(axiom.getSuperClass())) {
            classAxiomCollection.add(axiom);
        } else {
            axiom.getSuperClass()

        }*/
    }

    @Override
    public void visit(OWLDisjointClassesAxiom axiom) {
        boolean containedInBoth = true;
        Set<OWLClassExpression> desc = axiom.getClassExpressions();
        for (OWLClassExpression d : desc) {
            if (!d.isAnonymous() && !markedClasses.contains(d)) {
                containedInBoth = false;
            }
        }

        if (containedInBoth) {
            classAxiomCollection.add(axiom);
        }
    }

    @Override
    public void visit(OWLDisjointUnionAxiom axiom) {
        Set<OWLClassExpression> newAxiomDescription = new HashSet<OWLClassExpression>();

        Set<OWLClassExpression> desc = axiom.getClassExpressions();
        for (OWLClassExpression d : desc) {
            if (!d.isAnonymous()) {
                if (markedClasses.contains(d.asOWLClass())) {
                    newAxiomDescription.add(d);
                }
            }
        }

        OWLDisjointUnionAxiom newAxiom = factory.getOWLDisjointUnionAxiom(axiom.getOWLClass(), newAxiomDescription);
        classAxiomCollection.add(newAxiom);
    }

    @Override
    public void visit(OWLAnnotationAssertionAxiom axiom) {
        annotationAxiomCollection.add(axiom);
    }


    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) {
        classAxiomCollection.add(axiom);
    }


    /** special method to handle declaration axioms, which may be necessary for some applications */
    @Override
    public void visit(OWLDeclarationAxiom axiom) {
        declarationAxiomCollection.add(axiom);
    }
}
