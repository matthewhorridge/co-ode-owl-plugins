/**
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.coode.oae.ui;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLAxiomVisitor;
import org.semanticweb.owl.model.OWLClass;
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
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.SWRLRule;

import uk.ac.manchester.mae.MAEStart;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Apr 4, 2008
 */
public class OWLArithmeticsAxiomFormulaExtractor implements OWLAxiomVisitor {
	protected OWLClass owlClass;
	protected MAEStart extractedFormula;
	private OWLModelManager modelManager;

	/**
	 * @param owlClass
	 * @param modelManager
	 */
	public OWLArithmeticsAxiomFormulaExtractor(OWLClass owlClass,
			OWLModelManager modelManager) {
		this.owlClass = owlClass;
		this.modelManager = modelManager;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLSubClassAxiom)
	 */
	public void visit(OWLSubClassAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom)
	 */
	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom)
	 */
	public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom)
	 */
	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLDisjointClassesAxiom)
	 */
	public void visit(OWLDisjointClassesAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLDataPropertyDomainAxiom)
	 */
	public void visit(OWLDataPropertyDomainAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLImportsDeclaration)
	 */
	public void visit(OWLImportsDeclaration axiom) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLAxiomAnnotationAxiom)
	 */
	public void visit(OWLAxiomAnnotationAxiom axiom) {
		AnnotationFormulaExtractor formulaExtractor = new AnnotationFormulaExtractor(
				this.owlClass, this.modelManager);
		axiom.getAnnotation().accept(formulaExtractor);
		MAEStart extractedF = formulaExtractor.getExtractedFormula();
		if (extractedF != null) {
			this.extractedFormula = extractedF;
		}
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom)
	 */
	public void visit(OWLObjectPropertyDomainAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom)
	 */
	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom)
	 */
	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLDifferentIndividualsAxiom)
	 */
	public void visit(OWLDifferentIndividualsAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom)
	 */
	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom)
	 */
	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom)
	 */
	public void visit(OWLObjectPropertyRangeAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom)
	 */
	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom)
	 */
	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLObjectSubPropertyAxiom)
	 */
	public void visit(OWLObjectSubPropertyAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLDisjointUnionAxiom)
	 */
	public void visit(OWLDisjointUnionAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLDeclarationAxiom)
	 */
	public void visit(OWLDeclarationAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLEntityAnnotationAxiom)
	 */
	public void visit(OWLEntityAnnotationAxiom axiom) {
		AnnotationFormulaExtractor formulaExtractor = new AnnotationFormulaExtractor(
				this.owlClass, this.modelManager);
		axiom.getAnnotation().accept(formulaExtractor);
		MAEStart extractedF = formulaExtractor.getExtractedFormula();
		if (extractedF != null) {
			this.extractedFormula = extractedF;
		}
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLOntologyAnnotationAxiom)
	 */
	public void visit(OWLOntologyAnnotationAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom)
	 */
	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLDataPropertyRangeAxiom)
	 */
	public void visit(OWLDataPropertyRangeAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom)
	 */
	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom)
	 */
	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLClassAssertionAxiom)
	 */
	public void visit(OWLClassAssertionAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLEquivalentClassesAxiom)
	 */
	public void visit(OWLEquivalentClassesAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom)
	 */
	public void visit(OWLDataPropertyAssertionAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom)
	 */
	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom)
	 */
	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLDataSubPropertyAxiom)
	 */
	public void visit(OWLDataSubPropertyAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom)
	 */
	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLSameIndividualsAxiom)
	 */
	public void visit(OWLSameIndividualsAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom)
	 */
	public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom)
	 */
	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAxiomVisitor#visit(org.semanticweb.owl.model.SWRLRule)
	 */
	public void visit(SWRLRule rule) {
	}

	/**
	 * @return the extractedFormula
	 */
	public MAEStart getExtractedFormula() {
		return this.extractedFormula;
	}
}
