import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owl.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Dec 4, 2007
 * Time: 3:47:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class IndividualAxiomCollectionVisitor extends OWLAxiomVisitorAdapter {
    private Set<OWLIndividual> markedIndividuals;
    private Set<OWLProperty> markedProperties;
    private Set<OWLClass> markedClasses;
    private OWLDataFactory factory; //factory for creating new axiom, if neceessary
    private HashSet<OWLIndividualAxiom> individualAxiomCollection = new HashSet<OWLIndividualAxiom>();  //collection of all the axioms to be copied
    private HashSet<OWLEntityAnnotationAxiom> annotationAxiomCollection = new HashSet<OWLEntityAnnotationAxiom>();  //collection of all the axioms to be copied
    private HashSet<OWLDeclarationAxiom> declarationAxiomCollection = new HashSet<OWLDeclarationAxiom>();

    public IndividualAxiomCollectionVisitor(Set<OWLIndividual> markedIndividuals, Set<OWLProperty> markedProperties, Set<OWLClass> markedClasses, OWLDataFactory factory) {
        this.markedIndividuals = markedIndividuals;   //store the list of properties to extract for comparison on certain visits
        this.markedProperties = markedProperties;
        this.markedClasses = markedClasses;
        this.factory = factory; //factory for creating cut down axioms, if not all axiom in property expressions are necessary
    }

    /** returns all the collected property axioms */
    public Set<OWLIndividualAxiom> getAxioms() {
        return individualAxiomCollection ;
    }

    /** returns all the collected entity annotation axioms */
    public Set<OWLEntityAnnotationAxiom> getAnnotations() {
        return annotationAxiomCollection;
    }

    public void visit(OWLEntityAnnotationAxiom axiom) {
        annotationAxiomCollection.add(axiom);
    }


    /** handle the different types of individual axioms */
    public void visit(OWLDifferentIndividualsAxiom axiom) {
        //copy only those indviduals in the axiom that are marked for inclusion
        HashSet<OWLIndividual> includedIndividuals = new HashSet<OWLIndividual>();

        Set<OWLIndividual> inds = axiom.getIndividuals();
        for(OWLIndividual ind: inds) {
            if (!ind.isAnonymous()) {
                if (markedIndividuals.contains(ind.asOWLIndividual())) {
                    includedIndividuals.add(ind);
                }
            }
        }
        OWLDifferentIndividualsAxiom newAxiom = factory.getOWLDifferentIndividualsAxiom(includedIndividuals);    //make a new disjoint axiom containing only those properties which are marked for copy
        individualAxiomCollection.add(newAxiom);
    }

    public void visit(OWLClassAssertionAxiom axiom) {
        if (!axiom.getDescription().isAnonymous()) {
            if (markedClasses.contains(axiom.getDescription().asOWLClass())) {
                individualAxiomCollection.add(axiom);
            }
        }
    }

    public void visit(OWLSameIndividualsAxiom axiom) {
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

    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        if (includeAssertionAxiom(axiom)) individualAxiomCollection.add(axiom);
    }
    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        if (includeAssertionAxiom(axiom)) individualAxiomCollection.add(axiom);
    }
    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        if (includeAssertionAxiom(axiom)) individualAxiomCollection.add(axiom);
    }
    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        if (includeAssertionAxiom(axiom)) individualAxiomCollection.add(axiom);
    }

}
