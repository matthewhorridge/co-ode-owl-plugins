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
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.mae.MAEStart;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Apr 3, 2008
 */
@SuppressWarnings("unchecked")
public class OWLArithmeticsFormulaDataPropertyFrameSectionRow
		extends
		AbstractOWLFrameSectionRow<OWLDataProperty, OWLAnnotationAxiom, MAEStart> {
	protected OWLAnnotationAxiom<OWLDataProperty> axiom;

	protected OWLArithmeticsFormulaDataPropertyFrameSectionRow(
			OWLEditorKit owlEditorKit,
			OWLFrameSection<OWLDataProperty, OWLAnnotationAxiom, MAEStart> section,
			OWLOntology ontology, OWLDataProperty rootObject,
			OWLAnnotationAxiom<OWLDataProperty> axiom) {
		super(owlEditorKit, section, ontology, rootObject, axiom);
		this.axiom = axiom;
	}

	@Override
	protected OWLAnnotationAxiom createAxiom(MAEStart editedObject) {
		OWLAnnotationAxiom toReturn = this.getOWLDataFactory()
				.getOWLEntityAnnotationAxiom(
						this.getRootObject(),
						this.axiom.getAnnotation().getAnnotationURI(),
						this.getOWLDataFactory().getOWLTypedConstant(
								editedObject.toString()));
		return toReturn;
	}

	@Override
	protected OWLFrameSectionRowObjectEditor<MAEStart> getObjectEditor() {
		OWLArithmeticFormulaEditor arithmeticClassFormulaEditor = new OWLArithmeticFormulaEditor(
				this.getOWLEditorKit(), null, true);
		AnnotationFormulaExtractor extractor = new AnnotationFormulaExtractor(
				null, this.getOWLModelManager());
		this.axiom.getAnnotation().accept(extractor);
		MAEStart formula = extractor.getExtractedFormula();
		if (formula != null) {
			arithmeticClassFormulaEditor.setFormula(formula);
		}
		return arithmeticClassFormulaEditor;
	}

	public List<? extends OWLObject> getManipulatableObjects() {
		List<OWLAnnotationAxiom<OWLDataProperty>> toReturn = new ArrayList<OWLAnnotationAxiom<OWLDataProperty>>();
		toReturn.add(this.axiom);
		return toReturn;
	}
}
