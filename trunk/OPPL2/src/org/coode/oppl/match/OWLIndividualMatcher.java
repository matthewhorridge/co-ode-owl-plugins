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
package org.coode.oppl.match;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.Assignment;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLEntityVisitorEx;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class OWLIndividualMatcher extends
		OWLObjectMatcher<OWLIndividual> implements OWLEntityVisitorEx<Boolean> {
	/**
	 * @param bindings
	 * @param constraintSystem
	 */
	public OWLIndividualMatcher(BindingNode bindings,
			ConstraintSystem constraintSystem) {
		super(bindings, constraintSystem);
	}

	@Override
	protected void replace(Variable variable, OWLIndividual value) {
		this.getBindings().addAssignment(new Assignment(variable, value));
	}

	public Boolean visit(OWLClass cls) {
		return false;
	}

	public Boolean visit(OWLObjectProperty property) {
		return false;
	}

	public Boolean visit(OWLDataProperty property) {
		return false;
	}

	public Boolean visit(OWLIndividual individual) {
		return false;
	}

	public Boolean visit(OWLDataType dataType) {
		return false;
	}

	public static OWLIndividualMatcher getMatcher(
			final OWLIndividual individual, BindingNode bindingNode,
			ConstraintSystem constraintSystem) {
		return new OWLIndividualMatcher(bindingNode, constraintSystem) {
			@Override
			public Boolean visit(OWLIndividual anotherIndividual) {
				boolean matches = individual.equals(anotherIndividual);
				if (!matches) {
					matches = this.getConstraintSystem().isVariable(individual)
							&& this.canReplace(this.getConstraintSystem()
									.getVariable(individual.getURI()),
									individual);
					if (matches) {
						this.replace(this.getConstraintSystem().getVariable(
								individual.getURI()), anotherIndividual);
					}
				}
				return matches;
			}
		};
	}
}
