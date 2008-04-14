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
package uk.ac.manchester.cs.owl.lint.examples;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.lint.InferenceLintPatter;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.lint.ReasonerCreationImpossibleException;
import uk.ac.manchester.cs.owl.lint.commons.OntologyWiseLintPattern;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Feb 13, 2008
 */
public class SingleSubClassLintPattern extends OntologyWiseLintPattern
		implements InferenceLintPatter {
	/**
	 * @return the set of {@link OWLClass} that have just one <b>asserted</b>
	 *         subclass in the input {@link OWLOntology}
	 * @throws LintException
	 * @see org.semanticweb.owl.lint.LintPattern#matches(org.semanticweb.owl.model.OWLOntology)
	 */
	@Override
	public Set<OWLObject> matches(OWLOntology ontology) throws LintException {
		Set<OWLObject> toReturn = new HashSet<OWLObject>();
		Set<OWLClass> nothing = new HashSet<OWLClass>();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			OWLReasoner reasoner = this.getOWLReasoner();
			reasoner.loadOntologies(manager.getImportsClosure(ontology));
			if (!reasoner.isClassified()) {
				reasoner.classify();
			}
			nothing.add(manager.getOWLDataFactory().getOWLNothing());
			nothing.addAll(reasoner.getEquivalentClasses(manager
					.getOWLDataFactory().getOWLNothing()));
			// Subclasses are equivalence classes rather than OWLClass therefore
			// they are presented in sets
			for (OWLClass cls : ontology.getReferencedClasses()) {
				Set<Set<OWLClass>> subClasses = reasoner.getSubClasses(cls);
				subClasses.remove(nothing);
				if (subClasses.size() == 1) {
					Set<OWLClass> subclassEquivalenceClass = subClasses
							.iterator().next();
					if (subclassEquivalenceClass.size() == 1) {
						toReturn.add(cls);
					}
				}
			}
		} catch (LintException e) {
			Logger logger = Logger.getLogger(this.getClass().getName());
			logger
					.warn("Unable to create reasoner... only asserted taxonomy will be used");
			for (OWLClass cls : ontology.getReferencedClasses()) {
				if (cls.getSubClasses(ontology).size() == 1) {
					toReturn.add(cls);
				}
			}
		} catch (OWLReasonerException e) {
			throw new LintException(e);
		}
		return toReturn;
	}

	/**
	 * @see org.semanticweb.owl.lint.InferenceLintPatter#getOWLReasoner()
	 */
	@SuppressWarnings("unchecked")
	public OWLReasoner getOWLReasoner() throws LintException {
		String reasonerClassName = "org.mindswap.pellet.owlapi.Reasoner";
		Class reasonerClass;
		try {
			reasonerClass = Class.forName(reasonerClassName);
			Constructor<OWLReasoner> con = reasonerClass
					.getConstructor(OWLOntologyManager.class);
			return con.newInstance(OWLManager.createOWLOntologyManager());
		} catch (Exception e) {
			throw new ReasonerCreationImpossibleException(e);
		}
	}
}
