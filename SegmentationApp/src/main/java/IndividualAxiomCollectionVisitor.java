import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Dec 4, 2007
 * Time: 3:47:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class IndividualAxiomCollectionVisitor extends OWLAxiomVisitorAdapter {
    private Set<OWLIndividual> markedIndividuals;
    private Set<OWLClass> markedClasses;
    private OWLDataFactory factory; //factory for creating new axiom, if neceessary
    private HashSet<OWLIndividualAxiom> individualAxiomCollection = new HashSet<>();  //collection of all the axioms to be copied
    //collection of all the axioms to be copied
    private HashSet<OWLAnnotationAssertionAxiom> annotationAxiomCollection = new HashSet<>();

    public IndividualAxiomCollectionVisitor(
            Set<OWLIndividual> markedIndividuals, Set<OWLClass> markedClasses,
            OWLDataFactory factory) {
        this.markedIndividuals = markedIndividuals;
        this.markedClasses = markedClasses;
        //factory for creating cut down axioms, if not all axiom in property expressions are necessary
        this.factory = factory;
    }

    /** returns all the collected property axioms */
    public Set<OWLIndividualAxiom> getAxioms() {
        return individualAxiomCollection ;
    }

    /** returns all the collected entity annotation axioms */
    public Set<OWLAnnotationAssertionAxiom> getAnnotations() {
        return annotationAxiomCollection;
    }

    @Override
    public void visit(OWLAnnotationAssertionAxiom axiom) {
        annotationAxiomCollection.add(axiom);
    }


    /** handle the different types of individual axioms */
    @Override
    public void visit(OWLDifferentIndividualsAxiom axiom) {
        //copy only those indviduals in the axiom that are marked for inclusion
        HashSet<OWLIndividual> includedIndividuals = new HashSet<>();

        Set<OWLIndividual> inds = axiom.getIndividuals();
        for(OWLIndividual ind: inds) {
            if (!ind.isAnonymous()) {
                if (markedIndividuals.contains(ind)) {
                    includedIndividuals.add(ind);
                }
            }
        }
        OWLDifferentIndividualsAxiom newAxiom = factory.getOWLDifferentIndividualsAxiom(includedIndividuals);    //make a new disjoint axiom containing only those properties which are marked for copy
        individualAxiomCollection.add(newAxiom);
    }

    @Override
    public void visit(OWLClassAssertionAxiom axiom) {
        if (!axiom.getClassExpression().isAnonymous()) {
            if (markedClasses.contains(axiom.getClassExpression().asOWLClass())) {
                individualAxiomCollection.add(axiom);
            }
        }
    }

    @Override
    public void visit(OWLSameIndividualAxiom axiom) {
        individualAxiomCollection.add(axiom);
    }


    /** Only copy the relations to other individuals, if those individuals' classes are already being included
     * in the new model. If they are not being included, the individual-to-class or individual-to-individual
     * relation is filtered out.
     */
    private boolean includeAssertionAxiom(OWLPropertyAssertionAxiom axiom) {
        boolean includeAxiom = true;
        //if (!markedProperties.contains(axiom.getProperty())) includeAxiom = false;

        if (axiom.getObject() instanceof OWLClass) {
            if (!markedClasses.contains(axiom.getObject())) includeAxiom = false;
        }
        if (axiom.getObject() instanceof OWLIndividual) {
            if (!markedIndividuals.contains(axiom.getObject())) includeAxiom = false;
        }

        return includeAxiom;
    }

    @Override
    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        if (includeAssertionAxiom(axiom)) individualAxiomCollection.add(axiom);
    }
    @Override
    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        if (includeAssertionAxiom(axiom)) individualAxiomCollection.add(axiom);
    }
    @Override
    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        if (includeAssertionAxiom(axiom)) individualAxiomCollection.add(axiom);
    }
    @Override
    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        if (includeAssertionAxiom(axiom)) individualAxiomCollection.add(axiom);
    }

}
