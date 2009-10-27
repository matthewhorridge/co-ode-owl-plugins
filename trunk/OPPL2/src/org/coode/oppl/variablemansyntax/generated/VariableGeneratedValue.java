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
package org.coode.oppl.variablemansyntax.generated;

import java.util.List;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.coode.oppl.variablemansyntax.generated.factory.AttributeFactory;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class VariableGeneratedValue<N> implements GeneratedValue<N>,
		GeneratedValues<N> {
	public interface AttributeGenerator<N> {
		/**
		 * @author Luigi Iannone
		 * 
		 */
		List<N> getValues(Variable aVariable);

		N getValue(OWLObject object);
	}

	private final Variable variable;
	private final Attribute attribute;

	/**
	 * @param variable
	 * @param attribute
	 */
	public VariableGeneratedValue(Variable variable, Attribute attribute) {
		this.variable = variable;
		this.attribute = attribute;
	}

	public abstract List<N> getGeneratedValues();

	public abstract N getGeneratedValue(BindingNode node);

	@Override
	public String toString() {
		return this.variable.getName() + "." + this.attribute;
	}

	/**
	 * @return the attribute
	 */
	public final Attribute getAttribute() {
		return this.attribute;
	}

	/**
	 * @return the variable
	 */
	public final Variable getVariable() {
		return this.variable;
	}

	public VariableGeneratedValue<?> replaceVariable(Variable v,
			ConstraintSystem constraintSystem) {
		return AttributeFactory.getVariableGeneratedValue(this.attribute,
				this.variable, constraintSystem);
	}
}
