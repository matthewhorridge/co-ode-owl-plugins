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

import org.coode.oae.utils.ParserFactory;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLAnnotationVisitor;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstantAnnotation;
import org.semanticweb.owl.model.OWLObjectAnnotation;
import org.semanticweb.owl.util.NamespaceUtil;

import uk.ac.manchester.mae.ArithmeticsParser;
import uk.ac.manchester.mae.MAEStart;
import uk.ac.manchester.mae.ParseException;
import uk.ac.manchester.mae.visitor.protege.ProtegeClassExtractor;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Apr 4, 2008
 */
public class AnnotationFormulaExtractor implements OWLAnnotationVisitor {
	private static final String FORMULA_NAMESPACE_URI_STRING = "http://www.cs.manchester.ac.uk/owlarithmetics/formula#";
	protected OWLClass owlClass = null;
	private OWLModelManager modelManager;
	private MAEStart extractedFormula;

	/**
	 * @param owlClass
	 * @param modelManager
	 */
	public AnnotationFormulaExtractor(OWLClass owlClass,
			OWLModelManager modelManager) {
		this.owlClass = owlClass;
		this.modelManager = modelManager;
	}

	public AnnotationFormulaExtractor() {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAnnotationVisitor#visit(org.semanticweb.owl.model.OWLObjectAnnotation)
	 */
	public void visit(OWLObjectAnnotation annotation) {
	}

	/**
	 * @see org.semanticweb.owl.model.OWLAnnotationVisitor#visit(org.semanticweb.owl.model.OWLConstantAnnotation)
	 */
	public void visit(OWLConstantAnnotation annotation) {
		NamespaceUtil namespaceUtil = new NamespaceUtil();
		String[] namespaceSplitURString = new String[2];
		String uriString = annotation.getAnnotationURI().toString();
		if (uriString != null) {
			namespaceUtil.split(uriString, namespaceSplitURString);
			String namespace = namespaceSplitURString[0];
			if (namespace != null
					&& namespace.compareTo(FORMULA_NAMESPACE_URI_STRING) == 0) {
				String formulaString = annotation.getAnnotationValue()
						.getLiteral().toString();
				ParserFactory.initParser(formulaString);
				try {
					MAEStart extractedFormula = (MAEStart) ArithmeticsParser
							.Start();
					ProtegeClassExtractor classExtractor = new ProtegeClassExtractor(
							this.modelManager);
					this.extractedFormula = this.owlClass == null
							|| this.owlClass != null
							&& this.owlClass.equals(extractedFormula.jjtAccept(
									classExtractor, null)) ? extractedFormula
							: null;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return the extractedFormula
	 */
	public MAEStart getExtractedFormula() {
		return this.extractedFormula;
	}
}
