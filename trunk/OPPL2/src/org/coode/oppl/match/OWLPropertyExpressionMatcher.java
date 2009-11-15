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
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyInverse;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyExpression;
import org.semanticweb.owl.model.OWLPropertyExpressionVisitor;
import org.semanticweb.owl.model.OWLPropertyExpressionVisitorEx;

/**
 * @author Luigi Iannone
 * 
 */
public class OWLPropertyExpressionMatcher extends
		OWLObjectMatcher<OWLPropertyExpression<?, ?>> implements
		OWLPropertyExpressionVisitorEx<Boolean> {
	/**
	 * @param bindings
	 * @param constraintSystem
	 */
	public OWLPropertyExpressionMatcher(BindingNode bindings,
			ConstraintSystem constraintSystem) {
		super(bindings, constraintSystem);
	}

	/**
	 * @see org.semanticweb.owl.model.OWLPropertyExpressionVisitorEx#visit(org.semanticweb.owl.model.OWLObjectProperty)
	 */
	public Boolean visit(OWLObjectProperty property) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLPropertyExpressionVisitorEx#visit(org.semanticweb.owl.model.OWLObjectPropertyInverse)
	 */
	public Boolean visit(OWLObjectPropertyInverse property) {
		return false;
	}

	/**
	 * @see org.semanticweb.owl.model.OWLPropertyExpressionVisitorEx#visit(org.semanticweb.owl.model.OWLDataProperty)
	 */
	public Boolean visit(OWLDataProperty property) {
		return false;
	}

	public static OWLPropertyExpressionMatcher getMatcher(
			OWLPropertyExpression<?, ?> propertyExpression,
			final BindingNode bindings, final ConstraintSystem cs) {
		return propertyExpression
				.accept(new OWLPropertyExpressionVisitorEx<OWLPropertyExpressionMatcher>() {
					public OWLPropertyExpressionMatcher visit(
							final OWLObjectProperty property) {
						return new OWLPropertyExpressionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLObjectProperty anotherProperty) {
								return this.matchProperties(property,
										anotherProperty);
							}
						};
					}

					public OWLPropertyExpressionMatcher visit(
							final OWLObjectPropertyInverse property) {
						return new OWLPropertyExpressionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLObjectPropertyInverse anotherProperty) {
								OWLPropertyExpressionMatcher matcher = getMatcher(
										property.getInverse(), bindings, cs);
								return anotherProperty.accept(matcher);
							}
						};
					}

					public OWLPropertyExpressionMatcher visit(
							final OWLDataProperty property) {
						return new OWLPropertyExpressionMatcher(bindings, cs) {
							@Override
							public Boolean visit(OWLDataProperty anotherProperty) {
								return this.matchProperties(property,
										anotherProperty);
							}
						};
					}
				});
	}

	@Override
	protected void replace(final Variable variable,
			OWLPropertyExpression<?, ?> value) {
		value.accept(new OWLPropertyExpressionVisitor() {
			public void visit(OWLDataProperty property) {
				OWLPropertyExpressionMatcher.this.getBindings().addAssignment(
						new Assignment(variable, property));
			}

			public void visit(OWLObjectPropertyInverse property) {
			}

			public void visit(OWLObjectProperty property) {
				OWLPropertyExpressionMatcher.this.getBindings().addAssignment(
						new Assignment(variable, property));
			}
		});
	}

	protected <O extends OWLProperty<?, ?>> boolean matchProperties(O property,
			O anotherProperty) {
		boolean matches = property.equals(anotherProperty);
		if (!matches) {
			matches = this.getConstraintSystem().getVariable(property.getURI()) != null
					&& this.canReplace(this.getConstraintSystem().getVariable(
							property.getURI()), anotherProperty);
			if (matches) {
				this.replace(this.getConstraintSystem().getVariable(
						property.getURI()), anotherProperty);
			}
		}
		return matches;
	}
}
