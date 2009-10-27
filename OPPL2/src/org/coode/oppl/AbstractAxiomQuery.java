package org.coode.oppl;

import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointUnionAxiom;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.SWRLRule;

abstract class AbstractAxiomQuery implements AxiomQuery {
	protected abstract void match(OWLAxiom axiom);

	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLDisjointClassesAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLDataPropertyDomainAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLImportsDeclaration axiom) {
		match(axiom);
	}

	public void visit(OWLAxiomAnnotationAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLDifferentIndividualsAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLObjectSubPropertyAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLDisjointUnionAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLDeclarationAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLEntityAnnotationAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLOntologyAnnotationAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLDataPropertyRangeAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLClassAssertionAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLEquivalentClassesAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLDataSubPropertyAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLSameIndividualsAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
		match(axiom);
	}

	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		match(axiom);
	}

	public void visit(SWRLRule rule) {
		match(rule);
	}
}
