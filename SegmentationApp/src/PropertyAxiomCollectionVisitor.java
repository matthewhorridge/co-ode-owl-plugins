import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLPropertyAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

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
    private Set<OWLObjectPropertyExpression> markedObjectProperties;
    private Set<OWLDataPropertyExpression> markedDataProperties;

    private OWLDataFactory factory; //factory for creating new axiom, if neceessary
    private HashSet<OWLPropertyAxiom> propertyAxiomCollection = new HashSet<OWLPropertyAxiom>();  //collection of all the axioms to be copied
    private HashSet<OWLAnnotationAssertionAxiom> annotationAxiomCollection = new HashSet<OWLAnnotationAssertionAxiom>();  //collection of all the axioms to be copied
    private HashSet<OWLDeclarationAxiom> declarationAxiomCollection = new HashSet<OWLDeclarationAxiom>();

    public PropertyAxiomCollectionVisitor(Set<OWLObjectPropertyExpression> markedObjectProperties,
            Set<OWLDataPropertyExpression> markedDataProperties,
            OWLDataFactory factory) {
        this.markedObjectProperties = markedObjectProperties;   //store the list of properties to extract for comparison on certain visits
        this.markedDataProperties = markedDataProperties;   //store the list of properties to extract for comparison on certain visits
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
    public Set<OWLAnnotationAssertionAxiom> getAnnotations() {
        return annotationAxiomCollection;
    }


    /** list of simple visits where the axioms are just collected for copying into the new ontology */
    @Override
    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    @Override
    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    @Override
    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    @Override
    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    @Override
    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    @Override
    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    @Override
    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    @Override
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    @Override
    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }
    @Override
    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }

    @Override
    public void visit(OWLAnnotationAssertionAxiom axiom) {
        annotationAxiomCollection.add(axiom);
    }


    /** list of complex visits where the axiom is only copied if all properties involved in the axiom are marked */
    @Override
    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        //copy only if all properties in the chain are marked for inclusion
        ArrayList<OWLObjectProperty> includedChain = new ArrayList<OWLObjectProperty>();

        List<OWLObjectPropertyExpression> properties = axiom.getPropertyChain();
        for(OWLObjectPropertyExpression expression : properties) {
            if (!expression.isAnonymous()) {
                if (markedObjectProperties.contains(expression
                        .asOWLObjectProperty())) {
                    includedChain.add(expression.asOWLObjectProperty());
                }
            }
        }

        OWLSubPropertyChainOfAxiom newAxiom = factory
                .getOWLSubPropertyChainOfAxiom(includedChain,
                        axiom.getSuperProperty());   //make a new disjoint axiom containing only those properties which are marked for copy
        propertyAxiomCollection.add(newAxiom);
    }

    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        //copy only if the axiom if the super-property is also marked for inclusion
        if (markedObjectProperties.contains(axiom.getSuperProperty())) {
            propertyAxiomCollection.add(axiom);
        }
    }
    @Override
    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        //copy only if the second property is also marked for inclusion
        if (markedObjectProperties.contains(axiom.getSecondProperty())) {
            propertyAxiomCollection.add(axiom);
        }
    }
    @Override
    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        //copy only if all properties in the axiom are marked for inclusion
        HashSet<OWLObjectProperty> includedProperties = new HashSet<OWLObjectProperty>();

        Set<OWLObjectPropertyExpression> properties = axiom.getProperties();
        for(OWLObjectPropertyExpression expression : properties) {
            if (!expression.isAnonymous()) {
                if (markedObjectProperties.contains(expression
                        .asOWLObjectProperty())) {
                    includedProperties.add(expression.asOWLObjectProperty());
                }
            }
        }
        OWLDisjointObjectPropertiesAxiom newAxiom = factory.getOWLDisjointObjectPropertiesAxiom(includedProperties);    //make a new disjoint axiom containing only those properties which are marked for copy
        propertyAxiomCollection.add(newAxiom);
    }


    /** handle axioms relevant to datatype property axioms */
    @Override
    public void visit(OWLDataPropertyDomainAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }


    @Override
    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        //copy only if all properties in the axiom are marked for inclusion
        HashSet<OWLDataProperty> includedProperties = new HashSet<OWLDataProperty>();

        Set<OWLDataPropertyExpression> properties = axiom.getProperties();
        for(OWLDataPropertyExpression expression : properties) {
            if (!expression.isAnonymous()) {
                if (markedDataProperties.contains(expression
                        .asOWLDataProperty())) {
                    includedProperties.add(expression.asOWLDataProperty());
                }
            }
        }
        OWLDisjointDataPropertiesAxiom newAxiom = factory.getOWLDisjointDataPropertiesAxiom(includedProperties);    //make a new disjoint axiom containing only those properties which are marked for copy
        propertyAxiomCollection.add(newAxiom);
    }

    @Override
    public void visit(OWLDataPropertyRangeAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }

    @Override
    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }

    @Override
    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        propertyAxiomCollection.add(axiom);
    }

    @Override
    public void visit(OWLSubDataPropertyOfAxiom axiom) {
        if (markedDataProperties.contains(axiom.getSuperProperty())) {
            propertyAxiomCollection.add(axiom);
        }
    }

    /** special method to handle declaration axioms, which may be necessary for some applications */
    @Override
    public void visit(OWLDeclarationAxiom axiom) {
        declarationAxiomCollection.add(axiom);
    }


}
