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

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.mae.evaluation.FormulaModel;
import uk.ac.manchester.mae.parser.MAEStart;
import uk.ac.manchester.mae.parser.ParseException;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 3, 2008
 */
public class OWLCalculationsFormulaDataPropertyFrameSectionRow
		extends
        AbstractOWLFrameSectionRow<OWLDataProperty, OWLAnnotationAssertionAxiom, FormulaModel> {

    protected OWLAnnotationAssertionAxiom axiom;

	protected OWLCalculationsFormulaDataPropertyFrameSectionRow(
			OWLEditorKit owlEditorKit,
            OWLFrameSection<OWLDataProperty, OWLAnnotationAssertionAxiom, FormulaModel> section,
            OWLOntology ontology, OWLDataProperty rootObject,
            OWLAnnotationAssertionAxiom axiom) {
		super(owlEditorKit, section, ontology, rootObject, axiom);
		this.axiom = axiom;
	}

	@Override
	@SuppressWarnings("unchecked")
    protected OWLAnnotationAssertionAxiom
            createAxiom(
			FormulaModel editedObject) {
        OWLAnnotationAssertionAxiom toReturn = null;
        OWLDataProperty dataProperty = getRootObject();
		OWLDataFactory odf = getOWLDataFactory();
		if (dataProperty != null) {
            IRI uri = editedObject.getFormulaURI();
			if (uri != null) {
				try {
                    toReturn = odf.getOWLAnnotationAssertionAxiom(odf
                            .getOWLAnnotationProperty(uri), dataProperty
                            .getIRI(), odf.getOWLLiteral(MAENodeAdapter
                            .toFormula(editedObject, getOWLModelManager())
                                    .toString()));
				} catch (ParseException e) {
					// Impossible
					e.printStackTrace();
				}
			}
		}
		return toReturn;
	}
	@Override
    protected OWLObjectEditor<FormulaModel> getObjectEditor() {
        OWLCalculationsFormulaEditor toReturn = new OWLCalculationsFormulaEditor(
				getOWLEditorKit());
		AnnotationFormulaExtractor extractor = new AnnotationFormulaExtractor(
				null, getOWLModelManager());
        MAEStart formula = extractor.visit(axiom.getAnnotation());
		if (formula != null) {
            toReturn.setFormula(MAENodeAdapter.toFormulaModel(formula, axiom
                    .getAnnotation().getProperty().getIRI(), getOWLEditorKit()));
		}
		return toReturn;
	}

	@Override
    public List<? extends OWLObject> getManipulatableObjects() {
        List<OWLAnnotationAxiom> toReturn = new ArrayList<OWLAnnotationAxiom>();
		toReturn.add(axiom);
		return toReturn;
	}
}
