import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Julian Seidenberg
 * Date: Dec 3, 2007
 * Time: 3:54:17 PM
 * Either copies a specific property axiom to the new ontology (by storing it for later writing to the new ontology)
 * or throws the specific axiom away, if it should not be copied.
 * No recursion is necessary, since a full recursive scan was made previously in the scanning phase.
 */
public class PropertyAxiomCollectionVisitor extends OWLAxiomVisitorAdapter {
    private Set<OWLProperty> markedProperties;
    private OWLDataFactory factory; //factory for creating new axiom, if neceessary
    private HashSet<OWLPropertyAxiom> propertyAxiomCollection = new HashSet<OWLPropertyAxiom>();  //collection of all the axioms to be copied
    private HashSet<OWLEntityAnnotationAxiom> annotationAxiomCollection = new HashSet<OWLEntityAnnotationAxiom>();  //collection of all the axioms to be copied
    private HashSet<OWLDeclarationAxiom> declarationAxiomCollection = new HashSet<OWLDeclarationAxiom>();

    public PropertyAxiomCollectionVisitor(Set<OWLProperty> markedProperties, OWLDataFactory factory) {
        this.markedProperties = markedProperties;   //store the list of properties to extract for comparison on certain visits
        this.factory = factory; //factory for creating cut down axioms, if not all axiom in property expressions are necessary
    }

    /** returns all the collected property axioms */
    public Set<OWLPropertyAxiom> getAxioms() {
        return propertyAxiomCollection;
    }

    /** returns all the collected declaration axioms */
    public Set<OWLDeclarationAxiom> getDeclarations() {
        return declarationAxiomCollection;
    }

    /** returns all the collected entity annotation axioms */
    public Set<OWLEntityAnnotationAxiom> getAnnotations() {
        return annotationAxiomCollection;
    }


    /** list of simple visits where the axioms are just collected for copying into the new ontology */
    public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }

    public void visit(OWLEntityAnnotationAxiom axiom) {
        annotationAxiomCollection.add(axiom);
    }


    /** list of complex visits where the axiom is only copied if all properties involved in the axiom are marked */
    public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
        //copy only if all properties in the chain are marked for inclusion
        ArrayList<OWLObjectProperty> includedChain = new ArrayList<OWLObjectProperty>();

        List<OWLObjectPropertyExpression> properties = axiom.getPropertyChain();
        for(OWLObjectPropertyExpression expression : properties) {
            if (!expression.isAnonymous()) {
                if (markedProperties.contains(expression.asOWLObjectProperty())) {
                    includedChain.add(expression.asOWLObjectProperty());
                }
            }
        }

        OWLObjectPropertyChainSubPropertyAxiom newAxiom = factory.getOWLObjectPropertyChainSubPropertyAxiom(includedChain, axiom.getSuperProperty());    //make a new disjoint axiom containing only those properties which are marked for copy
        propertyAxiomCollection.add(newAxiom);
    }
    public void visit(OWLObjectSubPropertyAxiom axiom) {
        //copy only if the axiom if the super-property is also marked for inclusion
        if (markedProperties.contains(axiom.getSuperProperty())) {
            propertyAxiomCollection.add(axiom);
        }
    }
    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        //copy only if the second property is also marked for inclusion
        if (markedProperties.contains(axiom.getSecondProperty())) {
            propertyAxiomCollection.add(axiom);
        }
    }
    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        //copy only if all properties in the axiom are marked for inclusion
        HashSet<OWLObjectProperty> includedProperties = new HashSet<OWLObjectProperty>();

        Set<OWLObjectPropertyExpression> properties = axiom.getProperties();
        for(OWLObjectPropertyExpression expression : properties) {
            if (!expression.isAnonymous()) {
                if (markedProperties.contains(expression.asOWLObjectProperty())) {
                    includedProperties.add(expression.asOWLObjectProperty());
                }
            }
        }
        OWLDisjointObjectPropertiesAxiom newAxiom = factory.getOWLDisjointObjectPropertiesAxiom(includedProperties);    //make a new disjoint axiom containing only those properties which are marked for copy
        propertyAxiomCollection.add(newAxiom);
    }


    /** handle axioms relevant to datatype property axioms */
    public void visit(OWLDataPropertyDomainAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }


    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        //copy only if all properties in the axiom are marked for inclusion
        HashSet<OWLDataProperty> includedProperties = new HashSet<OWLDataProperty>();

        Set<OWLDataPropertyExpression> properties = axiom.getProperties();
        for(OWLDataPropertyExpression expression : properties) {
            if (!expression.isAnonymous()) {
                if (markedProperties.contains(expression.asOWLDataProperty())) {
                    includedProperties.add(expression.asOWLDataProperty());
                }
            }
        }
        OWLDisjointDataPropertiesAxiom newAxiom = factory.getOWLDisjointDataPropertiesAxiom(includedProperties);    //make a new disjoint axiom containing only those properties which are marked for copy
        propertyAxiomCollection.add(newAxiom);
    }

    public void visit(OWLDataPropertyRangeAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }

    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }

    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }

    public void visit(OWLDataSubPropertyAxiom axiom) {
        if (markedProperties.contains(axiom.getSuperProperty())) {
            propertyAxiomCollection.add(axiom);
        }
    }

    /** special method to handle declaration axioms, which may be necessary for some applications */
    public void visit(OWLDeclarationAxiom axiom) {
        declarationAxiomCollection.add(axiom);
    }


}
