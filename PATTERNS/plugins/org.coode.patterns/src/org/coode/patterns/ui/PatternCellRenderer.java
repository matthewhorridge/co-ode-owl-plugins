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
package org.coode.patterns.ui;

import java.util.List;
import java.util.Set;

import org.coode.patterns.PatternExtractor;
import org.coode.patterns.PatternModel;
import org.coode.patterns.PatternOPPLScript;
import org.coode.patterns.syntax.PatternParser;
import org.coode.patterns.utils.Utils;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 *         Jun 19, 2008
 */
public class PatternCellRenderer extends OWLCellRenderer {
	private OWLEditorKit owlEditorKit;

	public PatternCellRenderer(OWLEditorKit owlEditorKit) {
		super(owlEditorKit);
		this.owlEditorKit = owlEditorKit;
		this.setWrap(true);
	}

	@Override
	protected String getRendering(Object object) {
		if (object instanceof PatternClassFrameSectionRow) {
			PatternExtractor patternExtractor = PatternParser
					.getPatternModelFactory().getPatternExtractor();
			List<? extends OWLObject> manipulatableObjects = ((PatternClassFrameSectionRow) object)
					.getManipulatableObjects();
			OWLAnnotationAxiom<? extends OWLObject> annotationAxAnnotation = (OWLAnnotationAxiom<? extends OWLObject>) manipulatableObjects.iterator().next();
			OWLAnnotation<?> annotation = annotationAxAnnotation
					.getAnnotation();
			PatternOPPLScript patternModel = annotation
					.accept(patternExtractor);
			if (patternModel != null) {
				return patternModel.getRendering();
			} else {
				return super.getRendering(object);
			}
		} else if (object instanceof PatternOntologyFrameSectionRow) {
			PatternExtractor patternExtractor = PatternParser
					.getPatternModelFactory().getPatternExtractor();
			OWLAnnotationAxiom<? extends OWLObject> annotationAxAnnotation = (OWLAnnotationAxiom<? extends OWLObject>) ((PatternOntologyFrameSectionRow) object)
					.getManipulatableObjects().iterator().next();
			OWLAnnotation<?> annotation = annotationAxAnnotation
					.getAnnotation();
			PatternModel patternModel = (PatternModel) annotation
					.accept(patternExtractor);
			if (patternModel != null) {
				return patternModel.getRendering();
			} else {
				return super.getRendering(object);
			}
		} else if (object instanceof PatternOWLEquivalentClassesAxiomFrameSectionRow
				|| object instanceof PatternOWLSubClassAxiomFrameSectionRow) {
			OWLAxiom annotationAxiom = object instanceof PatternOWLEquivalentClassesAxiomFrameSectionRow ? ((PatternOWLEquivalentClassesAxiomFrameSectionRow) object)
					.getAxiom()
					: ((PatternOWLSubClassAxiomFrameSectionRow) object)
							.getAxiom();
			String toReturn = "";
			Set<OWLAxiomAnnotationAxiom> annotationAxioms = annotationAxiom
					.getAnnotationAxioms(this.owlEditorKit.getModelManager()
							.getActiveOntology());
			if (Utils.isPatternGenerated(annotationAxioms)) {
				toReturn += Utils.getGeneratedPatternName(annotationAxioms)
						+ " ";
			}
			toReturn += super.getRendering(object);
			return toReturn;
		} else {
			return super.getRendering(object);
		}
	}
}
