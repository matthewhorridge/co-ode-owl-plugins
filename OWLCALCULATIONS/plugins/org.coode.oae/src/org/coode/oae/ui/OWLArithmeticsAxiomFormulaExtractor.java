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
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

import uk.ac.manchester.mae.parser.MAEStart;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Apr 4, 2008
 */
public class OWLArithmeticsAxiomFormulaExtractor extends OWLAxiomVisitorAdapter
        implements OWLAxiomVisitor {
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

    @Override
    public void visit(OWLAnnotationAssertionAxiom axiom) {
		AnnotationFormulaExtractor formulaExtractor = new AnnotationFormulaExtractor(
				owlClass, modelManager);
        MAEStart extractedF = formulaExtractor.visit(axiom.getAnnotation());
		if (extractedF != null) {
			extractedFormula = extractedF;
		}
	}

	/**
	 * @return the extractedFormula
	 */
	public MAEStart getExtractedFormula() {
		return extractedFormula;
	}
}
