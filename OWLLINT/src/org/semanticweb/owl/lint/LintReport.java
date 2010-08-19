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
package org.semanticweb.owl.lint;

import java.util.Set;

import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 15, 2008
 */
public interface LintReport<O extends OWLObject> {
	public static final String NO_EXPLANATION_GIVEN = "No explanation given";

	/**
	 * @param ontology
	 * @return the Set of affected OWLObject elements for the input ontology
	 */
	Set<O> getAffectedOWLObjects(OWLOntology ontology);

	/**
	 * @return the set of affected OWLOntology elements
	 */
	Set<OWLOntology> getAffectedOntologies();

	/**
	 * @param ontology
	 * @return true is the input {@link OWLOntology} is affected by the Lint
	 *         that generated this report, false otherwise
	 */
	boolean isAffected(OWLOntology ontology);

	/**
	 * @return the Lint that generated this Report
	 */
	Lint<O> getLint();

	/**
	 * Adds the input OWLObject to the this LintReport for the input ontology
	 * 
	 * @param object
	 * @param affectedOntology
	 */
	void add(O object, OWLOntology affectedOntology);

	/**
	 * Adds the input OWLObject to the this LintReport for the input ontology
	 * with an explanation
	 * 
	 * @param object
	 * @param affectedOntology
	 * @param explanation
	 */
	void add(O object, OWLOntology affectedOntology, String explanation);

	/**
	 * 
	 * 
	 * 
	 * @param object
	 * @param affectedOntology
	 * @return the explanation for the input object to be in the report for the
	 *         input ontology, it can be null
	 */
	String getExplanation(OWLObject object, OWLOntology affectedOntology);

	/**
	 * Visitor pattern for LintReport
	 * 
	 * @param lintReportVisitor
	 */
	public void accept(LintReportVisitor lintReportVisitor);

	/**
	 * Visitor pattern with return value for LintReport
	 * 
	 * @param <P>
	 *            the type of the return value
	 * @param lintReportVisitor
	 * @return
	 */
	public <P> P accept(LintReportVisitorEx<P> lintReportVisitor);
}
