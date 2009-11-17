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

import java.util.Iterator;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDescriptionVisitorEx;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLNaryBooleanDescription;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectCardinalityRestriction;
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
public abstract class OWLDescriptionMatcher extends OWLObjectMatcher<OWLClass>
		implements OWLDescriptionVisitorEx<Boolean> {
	/**
	 * @param bindings
	 * @param constraintSystem
	 */
	public OWLDescriptionMatcher(BindingNode bindings,
			ConstraintSystem constraintSystem) {
		super(bindings, constraintSystem);
	}

	public Boolean visit(OWLClass desc) {
		return false;
	}

	public Boolean visit(OWLObjectIntersectionOf desc) {
		return false;
	}

	public Boolean visit(OWLObjectUnionOf desc) {
		return false;
	}

	public Boolean visit(OWLObjectComplementOf desc) {
		return false;
	}

	public Boolean visit(OWLObjectSomeRestriction desc) {
		return false;
	}

	public Boolean visit(OWLObjectAllRestriction desc) {
		return false;
	}

	public Boolean visit(OWLObjectValueRestriction desc) {
		return false;
	}

	public Boolean visit(OWLObjectMinCardinalityRestriction desc) {
		return false;
	}

	public Boolean visit(OWLObjectExactCardinalityRestriction desc) {
		return false;
	}

	public Boolean visit(OWLObjectMaxCardinalityRestriction desc) {
		return false;
	}

	public Boolean visit(OWLObjectSelfRestriction desc) {
		return false;
	}

	public Boolean visit(OWLObjectOneOf desc) {
		return false;
	}

	public Boolean visit(OWLDataSomeRestriction desc) {
		return false;
	}

	public Boolean visit(OWLDataAllRestriction desc) {
		return false;
	}

	public Boolean visit(OWLDataValueRestriction desc) {
		return false;
	}

	public Boolean visit(OWLDataMinCardinalityRestriction desc) {
		return false;
	}

	public Boolean visit(OWLDataExactCardinalityRestriction desc) {
		return false;
	}

	public Boolean visit(OWLDataMaxCardinalityRestriction desc) {
		return false;
	}

	public static OWLDescriptionMatcher getMatcher(OWLDescription description,
			final BindingNode bindings, final ConstraintSystem cs) {
		return description
				.accept(new OWLDescriptionVisitorEx<OWLDescriptionMatcher>() {
					public OWLDescriptionMatcher visit(final OWLClass desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(OWLClass owlClass) {
								boolean matches = desc.equals(owlClass);
								if (!matches) {
									matches = this.getConstraintSystem()
											.isVariable(desc)
											&& this.canReplace(this
													.getConstraintSystem()
													.getVariable(
															desc.asOWLClass()
																	.getURI()),
													desc.asOWLClass());
									if (matches) {
										this.replace(this.getConstraintSystem()
												.getVariable(
														desc.asOWLClass()
																.getURI()),
												owlClass);
									}
								}
								return matches;
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLObjectIntersectionOf desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLObjectIntersectionOf anotherDesc) {
								// For the time being we will not match in any
								// order
								boolean matches = super.matchOperands(desc,
										anotherDesc);
								return matches;
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLObjectUnionOf desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(OWLObjectUnionOf anotherDesc) {
								// For the time being we will not match in any
								// order
								boolean matches = super.matchOperands(desc,
										anotherDesc);
								return matches;
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLObjectComplementOf desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLObjectComplementOf anotherDesc) {
								OWLDescriptionMatcher matcher = getMatcher(desc
										.getOperand(), bindings, cs);
								return anotherDesc.getOperand().accept(matcher);
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLObjectSomeRestriction desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLObjectSomeRestriction anotherDesc) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(desc.getProperty(),
												bindings, cs);
								OWLDescriptionMatcher descriptionMatcher = getMatcher(
										desc.getFiller(), bindings, cs);
								return anotherDesc.getProperty().accept(
										propertyMatcher)
										&& anotherDesc.getFiller().accept(
												descriptionMatcher);
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLObjectAllRestriction desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLObjectAllRestriction anotherDesc) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(desc.getProperty(),
												bindings, cs);
								OWLDescriptionMatcher descriptionMatcher = getMatcher(
										desc.getFiller(), bindings, cs);
								return anotherDesc.getProperty().accept(
										propertyMatcher)
										&& anotherDesc.getFiller().accept(
												descriptionMatcher);
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLObjectValueRestriction desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLObjectValueRestriction anotherDesc) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(desc.getProperty(),
												bindings, cs);
								OWLIndividualMatcher individualMatcher = OWLIndividualMatcher
										.getMatcher(desc.getValue(), bindings,
												cs);
								return anotherDesc.getProperty().accept(
										propertyMatcher)
										&& anotherDesc.getValue().accept(
												individualMatcher);
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLObjectMinCardinalityRestriction desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLObjectMinCardinalityRestriction anotherDesc) {
								return this
										.matchOWLObjectCardinalityRestriction(
												desc, anotherDesc);
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLObjectExactCardinalityRestriction desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLObjectExactCardinalityRestriction anotherDesc) {
								return this
										.matchOWLObjectCardinalityRestriction(
												desc, anotherDesc);
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLObjectMaxCardinalityRestriction desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLObjectMaxCardinalityRestriction anotherDesc) {
								return this
										.matchOWLObjectCardinalityRestriction(
												desc, anotherDesc);
							}
						};
					}

					public OWLDescriptionMatcher visit(
							OWLObjectSelfRestriction desc) {
						return getNoDescriptionMatcher(bindings, cs);
					}

					public OWLDescriptionMatcher visit(final OWLObjectOneOf desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(OWLObjectOneOf anotherDesc) {
								// Different orderings will not be considered
								// for the time being
								Iterator<OWLIndividual> iterator = desc
										.getIndividuals().iterator();
								Iterator<OWLIndividual> anotherIterator = anotherDesc
										.getIndividuals().iterator();
								boolean matches = desc.getIndividuals().size() == anotherDesc
										.getIndividuals().size();
								while (matches && iterator.hasNext()) {
									OWLIndividual aDescIndividual = iterator
											.next();
									OWLIndividual anotherDescIndividual = anotherIterator
											.next();
									OWLIndividualMatcher matcher = OWLIndividualMatcher
											.getMatcher(aDescIndividual,
													bindings, cs);
									matches = anotherDescIndividual
											.accept(matcher);
								}
								return matches;
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLDataSomeRestriction desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLDataSomeRestriction anotherDesc) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(desc.getProperty(),
												bindings, cs);
								return desc.getFiller().equals(
										anotherDesc.getFiller())
										&& anotherDesc.getProperty().accept(
												propertyMatcher);
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLDataAllRestriction desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLDataAllRestriction anotherDesc) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(desc.getProperty(),
												bindings, cs);
								return desc.getFiller().equals(
										anotherDesc.getFiller())
										&& anotherDesc.getProperty().accept(
												propertyMatcher);
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLDataValueRestriction desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLDataValueRestriction anotherDesc) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(desc.getProperty(),
												bindings, cs);
								OWLConstantMatcher valueMatcher = OWLConstantMatcher
										.getMatcher(desc.getValue(), bindings,
												cs);
								return anotherDesc.getProperty().accept(
										propertyMatcher)
										&& anotherDesc.getValue().accept(
												valueMatcher);
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLDataMinCardinalityRestriction desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLDataMinCardinalityRestriction anotherDesc) {
								return this.matchOWLDataCardinalityRestriction(
										desc, anotherDesc);
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLDataExactCardinalityRestriction desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLDataExactCardinalityRestriction anotherDesc) {
								return this.matchOWLDataCardinalityRestriction(
										desc, anotherDesc);
							}
						};
					}

					public OWLDescriptionMatcher visit(
							final OWLDataMaxCardinalityRestriction desc) {
						return new OWLDescriptionMatcher(bindings, cs) {
							@Override
							public Boolean visit(
									OWLDataMaxCardinalityRestriction anotherDesc) {
								return this.matchOWLDataCardinalityRestriction(
										desc, anotherDesc);
							}
						};
					}
				});
	}

	/**
	 * @param desc
	 * @param anotherDesc
	 * @return
	 */
	protected <O extends OWLNaryBooleanDescription> boolean matchOperands(
			final O desc, O anotherDesc) {
		boolean matches = desc.getOperands().size() == anotherDesc
				.getOperands().size();
		if (matches) {
			Iterator<OWLDescription> descIterator = desc.getOperands()
					.iterator();
			Iterator<OWLDescription> anotherDescIterator = anotherDesc
					.getOperands().iterator();
			while (matches && descIterator.hasNext()) {
				OWLDescription aDescOperand = descIterator.next();
				OWLDescription anotherDescOperand = anotherDescIterator.next();
				OWLDescriptionMatcher operandMatcher = getMatcher(aDescOperand,
						this.getBindings(), this.getConstraintSystem());
				matches = anotherDescOperand.accept(operandMatcher);
			}
		}
		return matches;
	}

	protected <O extends OWLObjectCardinalityRestriction> boolean matchOWLObjectCardinalityRestriction(
			O aCardinalityRestriction, O anotherCardinalityRestriction) {
		OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
				.getMatcher(aCardinalityRestriction.getProperty(), this
						.getBindings(), this.getConstraintSystem());
		OWLDescriptionMatcher fillerMatcher = OWLDescriptionMatcher.getMatcher(
				aCardinalityRestriction.getFiller(), this.getBindings(), this
						.getConstraintSystem());
		boolean cardinalityMatch = aCardinalityRestriction.getCardinality() == anotherCardinalityRestriction
				.getCardinality();
		boolean propertyMatch = anotherCardinalityRestriction.getProperty()
				.accept(propertyMatcher);
		boolean fillerMatch = anotherCardinalityRestriction.getFiller().accept(
				fillerMatcher);
		return cardinalityMatch && propertyMatch && fillerMatch;
	}

	protected <O extends OWLDataCardinalityRestriction> boolean matchOWLDataCardinalityRestriction(
			O aCardinalityRestriction, O anotherCardinalityRestriction) {
		OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
				.getMatcher(aCardinalityRestriction.getProperty(), this
						.getBindings(), this.getConstraintSystem());
		return aCardinalityRestriction.getCardinality() == anotherCardinalityRestriction
				.getCardinality()
				&& aCardinalityRestriction.getFiller().equals(
						anotherCardinalityRestriction.getFiller())
				&& anotherCardinalityRestriction.getProperty().accept(
						propertyMatcher);
	}

	public static OWLDescriptionMatcher getNoDescriptionMatcher(
			BindingNode bindings, ConstraintSystem cs) {
		return new OWLDescriptionMatcher(bindings, cs) {
		};
	}
}
