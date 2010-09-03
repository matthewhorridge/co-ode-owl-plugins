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
package org.semanticweb.owlapi.lint;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.lint.commons.Match;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 18, 2008
 */
public interface PatternReport<O extends OWLObject> extends Set<Match<O>> {
	/**
	 * @return the set of affected OWLOntology elements
	 */
	Set<OWLOntology> getAffectedOntologies();

	/**
	 * @param ontology
	 * @return the Set of affected OWLObject elements for the input ontology
	 */
	Set<O> getAffectedOWLObjects(OWLOntology ontology);

	/**
	 * @param ontology
	 * @return true is the input OWLOntology is affected by the LintPattern that
	 *         generated this report, false otherwise
	 */
	boolean isAffected(OWLOntology ontology);

	/**
	 * @return the LintPattern that generated this PatternReport
	 */
	public LintPattern<O> getLintPattern();
}
