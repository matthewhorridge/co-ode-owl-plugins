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

import java.util.Collection;

import org.semanticweb.owl.lint.configuration.LintConfiguration;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 15, 2008
 */
public interface Lint<O extends OWLObject> {
	/**
	 * @param targets
	 * @return the {@link LintReport} after the execution of this {@link Lint}
	 *         on the input Set of OWLOntology
	 * @throws LintException
	 */
	public LintReport<O> detected(Collection<? extends OWLOntology> targets) throws LintException;

	/**
	 * @return this Lint name
	 */
	public String getName();

	/**
	 * @return a human readable description for this Lint
	 */
	public String getDescription();

	public void accept(LintVisitor visitor);

	public <P> P accept(LintVisitorEx<P> visitor);

	/**
	 * Retrieves the configuration strategy for this Lint.
	 * 
	 * @return a LintConfiguration
	 */
	LintConfiguration getLintConfiguration();

	/**
	 * Determines weather this Lint requires inference.
	 * 
	 * @return {@code true} if a reasoner is required for this Lint to work
	 *         properly <code>false</code> otherwise.
	 */
	public boolean isInferenceRequired();
}