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

import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLEntity;

/**
 * @author Luigi Iannone
 * 
 */
public class VariableIndexGeneratedValue implements GeneratedValue<String> {
	private final RegExpGeneratedVariable variable;
	private final RegExpGeneratedValue value;
	private final int index;
	private OWLEntity entity;

	/**
	 * @param variable
	 * @param attribute
	 */
	public VariableIndexGeneratedValue(RegExpGeneratedVariable variable, int i,
			OWLEntity e) {
		this.variable = variable;
		this.value = variable.getValue();
		this.index = i;
		this.entity = e;
	}

	@Override
	public String toString() {
		return this.variable.getName() + ".GROUPS(" + this.index + ")";
	}

	/**
	 * @return the attribute
	 */
	public final int getIndex() {
		return this.index;
	}

	/**
	 * @return the variable
	 */
	public final Variable getVariable() {
		return this.variable;
	}

	public List<String> computePossibleValues() {
		return this.entity.accept(this.variable.getValue());
	}

	public String getGeneratedValue(BindingNode node) {
		this.value.getGeneratedValue(node);
		List<String> l = this.entity.accept(this.value);
		if (l.size() > this.index) {
			return l.get(this.index);
		}
		return null;
	}
}
