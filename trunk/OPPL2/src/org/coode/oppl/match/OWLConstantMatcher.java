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
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataComplementOf;
import org.semanticweb.owl.model.OWLDataOneOf;
import org.semanticweb.owl.model.OWLDataRangeFacetRestriction;
import org.semanticweb.owl.model.OWLDataRangeRestriction;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataVisitorEx;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.model.OWLUntypedConstant;

/**
 * @author Luigi Iannone
 * 
 */
public class OWLConstantMatcher extends OWLObjectMatcher<OWLConstant> implements
		OWLDataVisitorEx<Boolean> {
	/**
	 * @param bindings
	 * @param constraintSystem
	 */
	public OWLConstantMatcher(BindingNode bindings,
			ConstraintSystem constraintSystem) {
		super(bindings, constraintSystem);
	}

	public Boolean visit(OWLDataType node) {
		return false;
	}

	public Boolean visit(OWLDataComplementOf node) {
		return false;
	}

	public Boolean visit(OWLDataOneOf node) {
		return false;
	}

	public Boolean visit(OWLDataRangeRestriction node) {
		return false;
	}

	public Boolean visit(OWLTypedConstant node) {
		return false;
	}

	public Boolean visit(OWLUntypedConstant node) {
		return false;
	}

	public Boolean visit(OWLDataRangeFacetRestriction node) {
		return false;
	}

	public static OWLConstantMatcher getMatcher(OWLConstant owlConstant,
			final BindingNode bindings, final ConstraintSystem cs) {
		return owlConstant.accept(new OWLDataVisitorEx<OWLConstantMatcher>() {
			public OWLConstantMatcher visit(OWLDataComplementOf node) {
				throw new RuntimeException(
						"Impossible to get here (OWLConstantMatcher.getMatcher(...).new OWLDataVisitorEx<OWLConstantMatcher>() {...}.visit(OWLDataComplementOf)) when visitng a constant!");
			}

			public OWLConstantMatcher visit(OWLDataOneOf node) {
				throw new RuntimeException(
						"Impossible to get here (OWLConstantMatcher.getMatcher(...).new OWLDataVisitorEx<OWLConstantMatcher>() {...}.visit(OWLDataOneOf)) when visitng a constant!");
			}

			public OWLConstantMatcher visit(OWLDataRangeFacetRestriction node) {
				throw new RuntimeException(
						"Impossible to get here (OWLConstantMatcher.getMatcher(...).new OWLDataVisitorEx<OWLConstantMatcher>() {...}.visit(OWLDataRangeFacetRestriction)) when visitng a constant!");
			}

			public OWLConstantMatcher visit(OWLDataRangeRestriction node) {
				throw new RuntimeException(
						"Impossible to get here (OWLConstantMatcher.getMatcher(...).new OWLDataVisitorEx<OWLDataRangeFacetRestriction>() {...}.visit(OWLDataRangeRestriction)) when visitng a constant!");
			}

			public OWLConstantMatcher visit(OWLDataType node) {
				throw new RuntimeException(
						"Impossible to get here (OWLConstantMatcher.getMatcher(...).new OWLDataVisitorEx<OWLDataRangeFacetRestriction>() {...}.visit(OWLDataType)) when visitng a constant!");
			}

			public OWLConstantMatcher visit(final OWLTypedConstant node) {
				return new OWLConstantMatcher(bindings, cs) {
					@Override
					public Boolean visit(OWLTypedConstant anotherNode) {
						return this.matchConstants(node, anotherNode);
					}
				};
			}

			public OWLConstantMatcher visit(final OWLUntypedConstant node) {
				return new OWLConstantMatcher(bindings, cs) {
					@Override
					public Boolean visit(OWLUntypedConstant anotherNode) {
						return this.matchConstants(node, anotherNode);
					}
				};
			}
		});
	}

	protected <O extends OWLConstant> boolean matchConstants(O aConstant,
			O anotherConstant) {
		boolean matches = aConstant.equals(anotherConstant);
		if (!matches) {
			matches = this.getConstraintSystem().isVariable(aConstant)
					&& this.canReplace(this.getConstraintSystem().getVariable(
							aConstant.getLiteral()), anotherConstant);
			if (matches) {
				this.replace(this.getConstraintSystem().getVariable(
						aConstant.getLiteral()), anotherConstant);
			}
		}
		return matches;
	}
}
