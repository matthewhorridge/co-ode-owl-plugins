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

import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Feb 15, 2008
 */
public interface Lint {
	/**
	 * @param targets
	 * @return the {@link LintReport} after the execution of this {@link Lint}
	 *         on the input Set of OWLOntology
	 * @throws LintException
	 */
	public LintReport detected(Set<OWLOntology> targets) throws LintException;

	/**
	 * @return this Lint name
	 */
	public String getName();

	/**
	 * Sets this Lint name
	 * 
	 * @param name
	 */
	public void setName(String name);

	/**
	 * @return a human readable description for this Lint
	 */
	public String getDescription();

	/**
	 * @return the ontology manager
	 */
	public OWLOntologyManager getOntologyManager();
}