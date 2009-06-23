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
package uk.ac.manchester.cs.owl.lint;

import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintException;

/**
 * This {@link LintException} is thrown whenever the creation of an OWLReasoner
 * is impossible in a {@link Lint} setup or matching process
 * 
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Feb 15, 2008
 */
public class ReasonerCreationImpossibleException extends LintException {
	public ReasonerCreationImpossibleException(String message) {
		super(message);
	}

	public ReasonerCreationImpossibleException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReasonerCreationImpossibleException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4311762201067665838L;
}
