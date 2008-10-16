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
package org.coode.oppl;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.OWLObjectInstantiator;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLObject;

/**
 * This ConstraintVisitor implementation verifies if the visited Constraint
 * holds
 * 
 * @author Luigi Iannone
 * 
 */
public class ConstraintChecker implements ConstraintVisitor<Boolean> {
	protected BindingNode bindingNode;
	protected ConstraintSystem constraintSystem;
	protected OWLDataFactory dataFactory;

	/**
	 * @param bindingNode
	 */
	public ConstraintChecker(BindingNode bindingNode, ConstraintSystem cs,
			OWLDataFactory dataFactory) {
		this.bindingNode = bindingNode;
		this.constraintSystem = cs;
		this.dataFactory = dataFactory;
	}

	/**
	 * @see org.coode.oppl.ConstraintVisitor#visit(org.coode.oppl.Constraint)
	 */
	public Boolean visit(Constraint c) {
		OWLObject expression = c.getExpression();
		OWLObjectInstantiator instantiator = new OWLObjectInstantiator(
				this.bindingNode, this.constraintSystem, this.dataFactory);
		OWLObject instantiatedObject = expression.accept(instantiator);
		Variable variable = c.getVariable();
		OWLObject assignedValue = this.bindingNode.getAssignmentValue(variable);
		// Hard coded check as there is only one kind of constraint at the
		// moment
		return !assignedValue.equals(instantiatedObject);
	}
}
