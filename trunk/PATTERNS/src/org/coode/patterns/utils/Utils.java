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
package org.coode.patterns.utils;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.coode.patterns.PatternExtractor;
import org.coode.patterns.PatternModel;
import org.coode.patterns.syntax.PatternParser;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.NamespaceUtil;

/**
 * @author Luigi Iannone
 * 
 * Jun 10, 2008
 */
public class Utils {
	@SuppressWarnings("unchecked")
	public static Set<PatternModel> getExistingPatterns(OWLOntology ontology) {
		Set<PatternModel> toReturn = new HashSet<PatternModel>();
		PatternExtractor patternExtractor = PatternParser
				.getPatternModelFactory().getPatternExtractor();
		for (OWLOntologyAnnotationAxiom anOntologyAnnotationAxiom : ontology
				.getOntologyAnnotationAxioms()) {
			PatternModel statementModel = (PatternModel) anOntologyAnnotationAxiom
					.getAnnotation().accept(patternExtractor);
			if (statementModel != null) {
				toReturn.add(statementModel);
			}
		}
		return toReturn;
	}

	public static PatternModel find(String patternName, OWLOntology ontology) {
		Iterator<OWLOntologyAnnotationAxiom> it = ontology
				.getOntologyAnnotationAxioms().iterator();
		boolean found = false;
		PatternModel extractedPattern = null;
		while (!found && it.hasNext()) {
			OWLOntologyAnnotationAxiom anOntologyAnnotationAxiom = it.next();
			OWLAnnotation<? extends OWLObject> annotation = anOntologyAnnotationAxiom
					.getAnnotation();
			URI annotationURI = annotation.getAnnotationURI();
			if (annotationURI.toString().compareTo(
					PatternModel.NAMESPACE + patternName) == 0) {
				PatternExtractor patternExtractor = PatternParser
						.getPatternModelFactory().getPatternExtractor();
				extractedPattern = (PatternModel) annotation
						.accept(patternExtractor);
				found = extractedPattern != null
						&& extractedPattern.getPatternLocalName().compareTo(
								patternName) == 0;
			}
		}
		return extractedPattern;
	}

	public static PatternModel find(String patternName,
			OWLOntologyManager ontologyManager) {
		Iterator<OWLOntology> it = ontologyManager.getOntologies().iterator();
		boolean found = false;
		PatternModel toReturn = null;
		while (!found && it.hasNext()) {
			toReturn = find(patternName, it.next());
			found = toReturn != null;
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public static Set<String> getExistingPatternNames(OWLOntology ontology) {
		Set<String> toReturn = new HashSet<String>();
		NamespaceUtil nsUtil = new NamespaceUtil();
		for (OWLOntologyAnnotationAxiom anOntologyAnnotationAxiom : ontology
				.getOntologyAnnotationAxioms()) {
			String[] split = nsUtil.split(anOntologyAnnotationAxiom
					.getAnnotation().getAnnotationURI().toString(), null);
			if (split != null && split.length == 2
					&& split[0].compareTo(PatternModel.NAMESPACE) == 0) {
				toReturn.add(split[1]);
			}
		}
		return toReturn;
	}

	public static Set<String> getExistingPatternNames(
			OWLOntologyManager ontologyManager) {
		Set<String> toReturn = new HashSet<String>();
		Set<OWLOntology> ontologies = ontologyManager.getOntologies();
		for (OWLOntology ontology : ontologies) {
			toReturn.addAll(getExistingPatternNames(ontology));
		}
		return toReturn;
	}

	public static Set<String> getExistingPatternNames(
			OWLOntologyManager ontologyManager, String prefix) {
		Set<String> toReturn = getExistingPatternNames(ontologyManager);
		Iterator<String> it = toReturn.iterator();
		while (it.hasNext()) {
			if (!it.next().startsWith(prefix)) {
				it.remove();
			}
		}
		return toReturn;
	}

	/**
	 * @param annotationAxioms
	 * @return
	 */
	public static boolean isPatternGenerated(
			Set<OWLAxiomAnnotationAxiom> annotationAxioms) {
		boolean isPatternGenerated = false;
		Iterator<OWLAxiomAnnotationAxiom> it = annotationAxioms.iterator();
		while (!isPatternGenerated && it.hasNext()) {
			OWLAxiomAnnotationAxiom axiomAnnotationAxiom = it.next();
			isPatternGenerated = axiomAnnotationAxiom.getAnnotation()
					.getAnnotationURI().toString().startsWith(
							PatternModel.NAMESPACE);
		}
		return isPatternGenerated;
	}

	public static boolean isPatternGenerated(String patternName,
			Set<OWLAxiomAnnotationAxiom> annotationAxioms) {
		boolean isPatternGenerated = false;
		Iterator<OWLAxiomAnnotationAxiom> it = annotationAxioms.iterator();
		while (!isPatternGenerated && it.hasNext()) {
			OWLAxiomAnnotationAxiom axiomAnnotationAxiom = it.next();
			OWLAnnotation<? extends OWLObject> annotation = axiomAnnotationAxiom
					.getAnnotation();
			isPatternGenerated = annotation.getAnnotationURI().toString()
					.startsWith(PatternModel.NAMESPACE)
					&& annotation.getAnnotationValueAsConstant().getLiteral()
							.endsWith(patternName + "PatternInstantiation");
		}
		return isPatternGenerated;
	}

	public static String getGeneratedPatternName(
			Set<OWLAxiomAnnotationAxiom> annotationAxioms) {
		boolean isPatternGenerated = false;
		String toReturn = null;
		Iterator<OWLAxiomAnnotationAxiom> it = annotationAxioms.iterator();
		OWLAxiomAnnotationAxiom axiomAnnotationAxiom = null;
		while (!isPatternGenerated && it.hasNext()) {
			axiomAnnotationAxiom = it.next();
			isPatternGenerated = axiomAnnotationAxiom.getAnnotation()
					.getAnnotationURI().toString().startsWith(
							PatternModel.NAMESPACE);
		}
		if (isPatternGenerated) {
			String value = axiomAnnotationAxiom.getAnnotation()
					.getAnnotationValueAsConstant().getLiteral();
			NamespaceUtil nsUtil = new NamespaceUtil();
			toReturn = nsUtil.split(value, null)[1];
		}
		return toReturn;
	}
}
