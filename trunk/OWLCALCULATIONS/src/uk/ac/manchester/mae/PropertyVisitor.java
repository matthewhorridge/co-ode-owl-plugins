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
package uk.ac.manchester.mae;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.coode.oae.utils.ParserFactory;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyInverse;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLPropertyExpressionVisitor;
import org.semanticweb.owl.util.NamespaceUtil;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 12, 2008
 */
public class PropertyVisitor implements OWLPropertyExpressionVisitor {
	protected Set<String> extractedFormulaStrings = new HashSet<String>();
	private Set<OWLOntology> ontologies;

	public PropertyVisitor(Set<OWLOntology> ontologies) {
		this.ontologies = ontologies;
	}

	/**
	 * No formula can be attached to an OWLObjectProperty. This visitor will not
	 * act on it
	 * 
	 * @see org.semanticweb.owl.model.OWLPropertyExpressionVisitor#visit(org.semanticweb.owl.model.OWLObjectProperty)
	 */
	public void visit(OWLObjectProperty property) {
	}

	/**
	 * No formula can be attached to an OWLObjectPropertyInverse. This visitor
	 * will not act on it
	 * 
	 * @see org.semanticweb.owl.model.OWLPropertyExpressionVisitor#visit(org.semanticweb.owl.model.OWLObjectProperty)
	 */
	public void visit(OWLObjectPropertyInverse property) {
	}

	/**
	 * Will extract the annotations containing the formulas
	 * 
	 * @see org.semanticweb.owl.model.OWLPropertyExpressionVisitor#visit(org.semanticweb.owl.model.OWLDataProperty)
	 */
	public void visit(OWLDataProperty property) {
		Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
		for (OWLOntology ontology : this.ontologies) {
			annotations.addAll(property.getAnnotations(ontology));
		}
		for (OWLAnnotation annotation : annotations) {
			NamespaceUtil nsUtil = new NamespaceUtil();
			URI annotationURI = annotation.getAnnotationURI();
			if (annotationURI != null) {
				String annotationNameSpace = nsUtil.split(annotationURI
						.toString(), new String[2])[0];
				if (annotationNameSpace
						.compareTo(Constants.FORMULA_NAMESPACE_URI_STRING) == 0) {
					String annotationBody = annotation
							.getAnnotationValueAsConstant().getLiteral();
					this.extractedFormulaStrings.add(annotationBody);
				}
			}
		}
	}

	@Deprecated
	/**
	 * @return the extractedFormulas
	 */
	public Set<String> getExtractedFormulaStrings() {
		return this.extractedFormulaStrings;
	}

	public Set<MAEStart> getExtractedFormulas() {
		Set<MAEStart> toReturn = new HashSet<MAEStart>(
				this.extractedFormulaStrings.size());
		for (String formulaBody : this.extractedFormulaStrings) {
			ParserFactory.initParser(formulaBody, this.ontologies);
			try {
				toReturn.add((MAEStart) ArithmeticsParser.Start());
			} catch (ParseException e) {
				Logger
						.getLogger(this.getClass().toString())
						.warn(
								"The formula body "
										+ formulaBody
										+ " could not be correctly parsed it will be skipped ",
								e);
			}
		}
		return toReturn;
	}
}
