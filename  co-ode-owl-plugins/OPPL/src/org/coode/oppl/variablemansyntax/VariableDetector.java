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
package org.coode.oppl.variablemansyntax;

import java.util.Iterator;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDescriptionVisitorEx;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectOneOf;
import org.semanticweb.owl.model.OWLObjectSelfRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLObjectValueRestriction;

/**
 * @author Luigi Iannone
 * 
 */
public class VariableDetector implements OWLDescriptionVisitorEx<Boolean> {
	protected ConstraintSystem constraintSystem;

	/**
	 * @param constraintSystem
	 */
	public VariableDetector(ConstraintSystem constraintSystem) {
		this.constraintSystem = constraintSystem;
	}

	public Boolean visit(OWLClass desc) {
		return this.constraintSystem.isVariableURI(desc.getURI());
	}

	public Boolean visit(OWLObjectIntersectionOf desc) {
		boolean found = false;
		Iterator<OWLDescription> it = desc.getOperands().iterator();
		OWLDescription operand;
		while (!found && it.hasNext()) {
			operand = it.next();
			found = operand.accept(this);
		}
		return found;
	}

	public Boolean visit(OWLObjectUnionOf desc) {
		boolean found = false;
		Iterator<OWLDescription> it = desc.getOperands().iterator();
		OWLDescription operand;
		while (!found && it.hasNext()) {
			operand = it.next();
			found = operand.accept(this);
		}
		return found;
	}

	public Boolean visit(OWLObjectComplementOf desc) {
		return desc.getOperand().accept(this);
	}

	public Boolean visit(OWLObjectSomeRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty())
				|| desc.getFiller().accept(this);
	}

	public Boolean visit(OWLObjectAllRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty())
				|| desc.getFiller().accept(this);
	}

	public Boolean visit(OWLObjectValueRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty())
				|| this.constraintSystem.isVariable(desc.getValue());
	}

	public Boolean visit(OWLObjectMinCardinalityRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty())
				|| (desc.getFiller() == null ? false : desc.getFiller().accept(
						this));
	}

	public Boolean visit(OWLObjectExactCardinalityRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty())
				|| (desc.getFiller() == null ? false : desc.getFiller().accept(
						this));
	}

	public Boolean visit(OWLObjectMaxCardinalityRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty())
				|| (desc.getFiller() == null ? false : desc.getFiller().accept(
						this));
	}

	public Boolean visit(OWLObjectSelfRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLObjectProperty());
	}

	public Boolean visit(OWLObjectOneOf desc) {
		boolean found = false;
		Iterator<OWLIndividual> it = desc.getIndividuals().iterator();
		OWLIndividual individual;
		while (!found && it.hasNext()) {
			individual = it.next();
			found = this.constraintSystem.isVariable(individual);
		}
		return found;
	}

	public Boolean visit(OWLDataSomeRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLDataProperty());
	}

	public Boolean visit(OWLDataAllRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLDataProperty());
	}

	public Boolean visit(OWLDataValueRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLDataProperty());
	}

	public Boolean visit(OWLDataMinCardinalityRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLDataProperty());
	}

	public Boolean visit(OWLDataExactCardinalityRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLDataProperty());
	}

	public Boolean visit(OWLDataMaxCardinalityRestriction desc) {
		return this.constraintSystem.isVariable(desc.getProperty()
				.asOWLDataProperty());
	}
}
