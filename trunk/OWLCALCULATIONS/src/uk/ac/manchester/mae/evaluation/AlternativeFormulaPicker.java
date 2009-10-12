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
package uk.ac.manchester.mae.evaluation;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyInverse;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLPropertyExpressionVisitor;

import uk.ac.manchester.mae.MoreThanOneFormulaPerIndividualException;
import uk.ac.manchester.mae.PropertyVisitor;
import uk.ac.manchester.mae.parser.MAEStart;
import uk.ac.manchester.mae.visitor.ClassExtractor;

/**
 * Looks for possible alternatives to a formula for a data property
 * 
 * 
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         May 1, 2008
 */
public class AlternativeFormulaPicker implements OWLPropertyExpressionVisitor {
	private Set<OWLOntology> ontologies;
	private OWLIndividual individual;
	private MAEStart formula;
	private Set<MAEStart> formulas = null;
	private OWLReasoner reasoner;
	private OWLOntologyManager ontologyManager;

	/**
	 * @param ontologies
	 * @param individual
	 * @param reasoner
	 * @param ontologyManager
	 */
	public AlternativeFormulaPicker(OWLIndividual individual, MAEStart formula,
			Set<OWLOntology> ontologies, OWLReasoner reasoner,
			OWLOntologyManager ontologyManager) {
		this.ontologies = ontologies;
		this.individual = individual;
		this.formula = formula;
		this.reasoner = reasoner;
		this.ontologyManager = ontologyManager;
	}

	public void visit(OWLObjectProperty property) {
	}

	public void visit(OWLObjectPropertyInverse property) {
	}

	public void visit(OWLDataProperty property) {
		PropertyVisitor propertyVisitor = new PropertyVisitor(this.ontologies);
		property.accept(propertyVisitor);
		this.formulas = propertyVisitor.getExtractedFormulas();
		this.formulas.remove(this.formula);
	}

	/**
	 * @return null if there are not alternatives, a possible alternative
	 *         otherwise
	 * @throws OWLReasonerException
	 * @throws MoreThanOneFormulaPerIndividualException
	 *             if there is more than one alternative
	 */
	public MAEStart pickFormula() throws OWLReasonerException,
			MoreThanOneFormulaPerIndividualException {
		ClassExtractor classExtractor = new ClassExtractor(this.ontologies,
				this.ontologyManager);
		this.formula.jjtAccept(classExtractor, null);
		OWLDescription classDescription = classExtractor.getClassDescription() == null ? this.ontologyManager
				.getOWLDataFactory().getOWLThing()
				: classExtractor.getClassDescription();
		MAEStart toReturn = null;
		if (!this.reasoner.isClassified()) {
			this.reasoner.classify();
		}
		for (MAEStart anotherFormula : new HashSet<MAEStart>(this.formulas)) {
			anotherFormula.jjtAccept(classExtractor, null);
			OWLDescription anotherFormulaClassDescription = classExtractor
					.getClassDescription() == null ? this.ontologyManager
					.getOWLDataFactory().getOWLThing() : classExtractor
					.getClassDescription();
			if (!this.reasoner.hasType(this.individual,
					anotherFormulaClassDescription, false)
					|| this.reasoner.isSubClassOf(classDescription,
							anotherFormulaClassDescription)
					&& !this.reasoner.isSubClassOf(
							anotherFormulaClassDescription, classDescription)) {
				this.formulas.remove(anotherFormula);
			}
		}
		// There must be at most one applicable formula per individual
		if (this.formulas.size() > 1) {
			throw new MoreThanOneFormulaPerIndividualException(
					"There is more than one formula for individual "
							+ this.individual);
		} else {
			toReturn = this.formulas.size() > 0 ? this.formulas.iterator()
					.next() : null;
		}
		return toReturn;
	}
}
