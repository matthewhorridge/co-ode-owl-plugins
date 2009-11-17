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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.oppl.utils.CollectionPermutation;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLAxiomVisitorEx;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointUnionAxiom;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.SWRLRule;

/**
 * @author Luigi Iannone
 * 
 */
public class MatcherGenerator {
	public static Set<? extends AxiomMatcher> getAxiomMatchers(OWLAxiom axiom,
			final BindingNode bindingNode,
			final ConstraintSystem constraintSystem) {
		return axiom
				.accept(new OWLAxiomVisitorEx<Set<? extends AxiomMatcher>>() {
					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLSubClassAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLSubClassAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(OWLSubClassAxiom anotherAxiom) {
								OWLDescriptionMatcher subClassMatcher = OWLDescriptionMatcher
										.getMatcher(axiom.getSubClass(),
												bindingNode, constraintSystem);
								OWLDescriptionMatcher superClassMatcher = OWLDescriptionMatcher
										.getMatcher(axiom.getSuperClass(),
												bindingNode, constraintSystem);
								return anotherAxiom.getSubClass().accept(
										subClassMatcher)
										&& anotherAxiom.getSuperClass().accept(
												superClassMatcher);
							};
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLNegativeObjectPropertyAssertionAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLNegativeObjectPropertyAssertionAxiom anotherAxiom) {
								OWLIndividualMatcher subjectMatcher = OWLIndividualMatcher
										.getMatcher(axiom.getSubject(),
												bindingNode, constraintSystem);
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getProperty(),
												bindingNode, constraintSystem);
								OWLIndividualMatcher objectMatcher = OWLIndividualMatcher
										.getMatcher(axiom.getObject(),
												bindingNode, constraintSystem);
								return anotherAxiom.getProperty().accept(
										propertyMatcher)
										&& anotherAxiom.getSubject().accept(
												subjectMatcher)
										&& anotherAxiom.getObject().accept(
												objectMatcher);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLAntiSymmetricObjectPropertyAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLAntiSymmetricObjectPropertyAxiom anotherAxiom) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getProperty(),
												bindingNode, constraintSystem);
								return anotherAxiom.getProperty().accept(
										propertyMatcher);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLReflexiveObjectPropertyAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLReflexiveObjectPropertyAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLReflexiveObjectPropertyAxiom anotherAxiom) {
								return this.matchUnaryPropertyAxioms(axiom,
										anotherAxiom);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLDisjointClassesAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLDisjointClassesAxiom axiom) {
						Set<OWLDescription> axiomDescriptions = axiom
								.getDescriptions();
						Set<List<OWLDescription>> allPermutations = CollectionPermutation
								.getAllPermutations(axiomDescriptions);
						Set<AxiomMatcher> toReturn = new HashSet<AxiomMatcher>(
								allPermutations.size());
						for (final List<OWLDescription> aPermutation : allPermutations) {
							toReturn.add(new AxiomMatcher(new BindingNode(
									bindingNode), constraintSystem) {
								@Override
								public Boolean visit(
										OWLDisjointClassesAxiom anotherAxiom) {
									return this.matchDescriptionCollections(
											aPermutation, anotherAxiom
													.getDescriptions());
								}
							});
						}
						return toReturn;
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLDataPropertyDomainAxiom
					 *      )
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLDataPropertyDomainAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLDataPropertyDomainAxiom anotherAxiom) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getProperty(),
												bindingNode, constraintSystem);
								OWLDescriptionMatcher domainMatcher = OWLDescriptionMatcher
										.getMatcher(axiom.getDomain(),
												bindingNode, constraintSystem);
								return anotherAxiom.getDomain().accept(
										domainMatcher)
										&& anotherAxiom.getProperty().accept(
												propertyMatcher);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLImportsDeclaration)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLImportsDeclaration axiom) {
						return Collections.singleton(AxiomMatcher
								.getNoMatchAxiomMatcher(bindingNode,
										constraintSystem));
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLAxiomAnnotationAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLAxiomAnnotationAxiom axiom) {
						return Collections.singleton(AxiomMatcher
								.getNoMatchAxiomMatcher(bindingNode,
										constraintSystem));
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom
					 *      )
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLObjectPropertyDomainAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLObjectPropertyDomainAxiom anotherAxiom) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getProperty(),
												bindingNode, constraintSystem);
								OWLDescriptionMatcher descriptionMatcher = OWLDescriptionMatcher
										.getMatcher(axiom.getDomain(),
												bindingNode, constraintSystem);
								return anotherAxiom.getProperty().accept(
										propertyMatcher)
										&& anotherAxiom.getDomain().accept(
												descriptionMatcher);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org. semanticweb.owl.model.
					 *      OWLEquivalentObjectPropertiesAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLEquivalentObjectPropertiesAxiom axiom) {
						Set<OWLObjectPropertyExpression> properties = axiom
								.getProperties();
						Set<List<OWLObjectPropertyExpression>> allPermutations = CollectionPermutation
								.getAllPermutations(properties);
						Set<AxiomMatcher> toReturn = new HashSet<AxiomMatcher>(
								allPermutations.size());
						for (final List<OWLObjectPropertyExpression> aPermutation : allPermutations) {
							toReturn.add(new AxiomMatcher(new BindingNode(
									bindingNode.getAssignments(), bindingNode
											.getUnassignedVariables()),
									constraintSystem) {
								@Override
								public Boolean visit(
										OWLEquivalentObjectPropertiesAxiom anotherAxiom) {
									return this.matchPropertyCollections(
											aPermutation, anotherAxiom
													.getProperties());
								}
							});
						}
						return toReturn;
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org. semanticweb.owl.model.
					 *      OWLNegativeDataPropertyAssertionAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLNegativeDataPropertyAssertionAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLNegativeDataPropertyAssertionAxiom anotherAxiom) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getProperty(),
												bindingNode, constraintSystem);
								OWLIndividualMatcher subJectMatcher = OWLIndividualMatcher
										.getMatcher(axiom.getSubject(),
												bindingNode, constraintSystem);
								OWLConstantMatcher objectMatcher = OWLConstantMatcher
										.getMatcher(axiom.getObject(),
												bindingNode, constraintSystem);
								return anotherAxiom.getProperty().accept(
										propertyMatcher)
										&& anotherAxiom.getSubject().accept(
												subJectMatcher)
										&& anotherAxiom.getObject().accept(
												objectMatcher);
							};
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLDifferentIndividualsAxiom
					 *      )
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLDifferentIndividualsAxiom axiom) {
						Set<List<OWLIndividual>> allPermutations = CollectionPermutation
								.getAllPermutations(axiom.getIndividuals());
						Set<AxiomMatcher> toReturn = new HashSet<AxiomMatcher>();
						for (final List<OWLIndividual> aPermutation : allPermutations) {
							toReturn.add(new AxiomMatcher(new BindingNode(
									bindingNode), constraintSystem) {
								@Override
								public Boolean visit(
										OWLDifferentIndividualsAxiom anotherAxiom) {
									return this.matchIndividualCollections(
											aPermutation, anotherAxiom
													.getIndividuals());
								}
							});
						}
						return toReturn;
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLDisjointDataPropertiesAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLDisjointDataPropertiesAxiom axiom) {
						Set<OWLDataPropertyExpression> properties = axiom
								.getProperties();
						Set<List<OWLDataPropertyExpression>> allPermutations = CollectionPermutation
								.getAllPermutations(properties);
						Set<AxiomMatcher> toReturn = new HashSet<AxiomMatcher>(
								allPermutations.size());
						for (final List<OWLDataPropertyExpression> aPermutation : allPermutations) {
							toReturn.add(new AxiomMatcher(new BindingNode(
									bindingNode), constraintSystem) {
								@Override
								public Boolean visit(
										OWLDisjointDataPropertiesAxiom anotherAxiom) {
									return this.matchPropertyCollections(
											aPermutation, anotherAxiom
													.getProperties());
								}
							});
						}
						return toReturn;
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLDisjointObjectPropertiesAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLDisjointObjectPropertiesAxiom axiom) {
						Set<OWLObjectPropertyExpression> properties = axiom
								.getProperties();
						Set<List<OWLObjectPropertyExpression>> allPermutations = CollectionPermutation
								.getAllPermutations(properties);
						Set<AxiomMatcher> toReturn = new HashSet<AxiomMatcher>(
								allPermutations.size());
						for (final List<OWLObjectPropertyExpression> aPermutation : allPermutations) {
							toReturn.add(new AxiomMatcher(new BindingNode(
									bindingNode.getAssignments(), bindingNode
											.getUnassignedVariables()),
									constraintSystem) {
								@Override
								public Boolean visit(
										OWLDisjointObjectPropertiesAxiom anotherAxiom) {
									return this.matchPropertyCollections(
											aPermutation, anotherAxiom
													.getProperties());
								}
							});
						}
						return toReturn;
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom
					 *      )
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLObjectPropertyRangeAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLObjectPropertyRangeAxiom anotherAxiom) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getProperty(),
												bindingNode, constraintSystem);
								OWLDescriptionMatcher descriptionMatcher = OWLDescriptionMatcher
										.getMatcher(axiom.getRange(),
												bindingNode, constraintSystem);
								return anotherAxiom.getProperty().accept(
										propertyMatcher)
										&& anotherAxiom.getRange().accept(
												descriptionMatcher);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLObjectPropertyAssertionAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLObjectPropertyAssertionAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLObjectPropertyAssertionAxiom anotherAxiom) {
								OWLIndividualMatcher subjectMatcher = OWLIndividualMatcher
										.getMatcher(axiom.getSubject(),
												bindingNode, constraintSystem);
								OWLIndividualMatcher objectMatcher = OWLIndividualMatcher
										.getMatcher(axiom.getObject(),
												bindingNode, constraintSystem);
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getProperty(),
												bindingNode, constraintSystem);
								return anotherAxiom.getProperty().accept(
										propertyMatcher)
										&& anotherAxiom.getSubject().accept(
												subjectMatcher)
										&& anotherAxiom.getObject().accept(
												objectMatcher);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLFunctionalObjectPropertyAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLFunctionalObjectPropertyAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLFunctionalObjectPropertyAxiom anotherAxiom) {
								return this.matchUnaryPropertyAxioms(axiom,
										anotherAxiom);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLObjectSubPropertyAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLObjectSubPropertyAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLObjectSubPropertyAxiom anotherAxiom) {
								OWLPropertyExpressionMatcher subPropertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getSuperProperty(),
												bindingNode, constraintSystem);
								OWLPropertyExpressionMatcher superPropertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getSubProperty(),
												bindingNode, constraintSystem);
								return anotherAxiom.getSubProperty().accept(
										subPropertyMatcher)
										&& anotherAxiom.getSuperProperty()
												.accept(superPropertyMatcher);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLDisjointUnionAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLDisjointUnionAxiom axiom) {
						return Collections.singleton(AxiomMatcher
								.getNoMatchAxiomMatcher(bindingNode,
										constraintSystem));
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.OWLDeclarationAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLDeclarationAxiom axiom) {
						return Collections.singleton(AxiomMatcher
								.getNoMatchAxiomMatcher(bindingNode,
										constraintSystem));
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLEntityAnnotationAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLEntityAnnotationAxiom axiom) {
						return Collections.singleton(AxiomMatcher
								.getNoMatchAxiomMatcher(bindingNode,
										constraintSystem));
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLOntologyAnnotationAxiom
					 *      )
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLOntologyAnnotationAxiom axiom) {
						return Collections.singleton(AxiomMatcher
								.getNoMatchAxiomMatcher(bindingNode,
										constraintSystem));
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLSymmetricObjectPropertyAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLSymmetricObjectPropertyAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLSymmetricObjectPropertyAxiom anotherAxiom) {
								return this.matchUnaryPropertyAxioms(axiom,
										anotherAxiom);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLDataPropertyRangeAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLDataPropertyRangeAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLDataPropertyRangeAxiom anotherAxiom) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getProperty(),
												bindingNode, constraintSystem);
								return anotherAxiom.getProperty().accept(
										propertyMatcher)
										&& axiom.getRange().equals(
												anotherAxiom.getRange());
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLFunctionalDataPropertyAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLFunctionalDataPropertyAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLFunctionalDataPropertyAxiom anotherAxiom) {
								return this.matchUnaryPropertyAxioms(axiom,
										anotherAxiom);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLEquivalentDataPropertiesAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLEquivalentDataPropertiesAxiom axiom) {
						Set<OWLDataPropertyExpression> properties = axiom
								.getProperties();
						Set<List<OWLDataPropertyExpression>> allPermutations = CollectionPermutation
								.getAllPermutations(properties);
						Set<AxiomMatcher> toReturn = new HashSet<AxiomMatcher>(
								allPermutations.size());
						for (final List<OWLDataPropertyExpression> aPermutation : allPermutations) {
							toReturn.add(new AxiomMatcher(new BindingNode(
									bindingNode), constraintSystem) {
								@Override
								public Boolean visit(
										OWLEquivalentDataPropertiesAxiom anotherAxiom) {
									return this.matchPropertyCollections(
											aPermutation, anotherAxiom
													.getProperties());
								}
							});
						}
						return toReturn;
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLClassAssertionAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLClassAssertionAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLClassAssertionAxiom anotherAxiom) {
								OWLDescriptionMatcher descriptionMatcher = OWLDescriptionMatcher
										.getMatcher(axiom.getDescription(),
												bindingNode, constraintSystem);
								OWLIndividualMatcher individualMatcher = OWLIndividualMatcher
										.getMatcher(axiom.getIndividual(),
												bindingNode, constraintSystem);
								return anotherAxiom.getDescription().accept(
										descriptionMatcher)
										&& anotherAxiom.getIndividual().accept(
												individualMatcher);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLEquivalentClassesAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLEquivalentClassesAxiom axiom) {
						Set<OWLDescription> axiomDescriptions = axiom
								.getDescriptions();
						Set<List<OWLDescription>> allPermutations = CollectionPermutation
								.getAllPermutations(axiomDescriptions);
						Set<AxiomMatcher> toReturn = new HashSet<AxiomMatcher>(
								allPermutations.size());
						for (final List<OWLDescription> aPermutation : allPermutations) {
							toReturn.add(new AxiomMatcher(new BindingNode(
									bindingNode), constraintSystem) {
								@Override
								public Boolean visit(
										OWLEquivalentClassesAxiom anotherAxiom) {
									return this.matchDescriptionCollections(
											aPermutation, anotherAxiom
													.getDescriptions());
								}
							});
						}
						return toReturn;
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom
					 *      )
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLDataPropertyAssertionAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLDataPropertyAssertionAxiom anotherAxiom) {
								OWLPropertyExpressionMatcher propertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getProperty(),
												bindingNode, constraintSystem);
								OWLIndividualMatcher subjectMatcher = OWLIndividualMatcher
										.getMatcher(axiom.getSubject(),
												bindingNode, constraintSystem);
								OWLConstantMatcher objectMatcher = OWLConstantMatcher
										.getMatcher(axiom.getObject(),
												bindingNode, constraintSystem);
								return anotherAxiom.getProperty().accept(
										propertyMatcher)
										&& anotherAxiom.getSubject().accept(
												subjectMatcher)
										&& anotherAxiom.getObject().accept(
												objectMatcher);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLTransitiveObjectPropertyAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLTransitiveObjectPropertyAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLTransitiveObjectPropertyAxiom anotherAxiom) {
								return this.matchUnaryPropertyAxioms(axiom,
										anotherAxiom);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLIrreflexiveObjectPropertyAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLIrreflexiveObjectPropertyAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLIrreflexiveObjectPropertyAxiom anotherAxiom) {
								return this.matchUnaryPropertyAxioms(axiom,
										anotherAxiom);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLDataSubPropertyAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLDataSubPropertyAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLDataSubPropertyAxiom anotherAxiom) {
								OWLPropertyExpressionMatcher subPropertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getSubProperty(),
												bindingNode, constraintSystem);
								OWLPropertyExpressionMatcher superPropertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getSuperProperty(),
												bindingNode, constraintSystem);
								return anotherAxiom.getSubProperty().accept(
										subPropertyMatcher)
										&& anotherAxiom.getSuperProperty()
												.accept(superPropertyMatcher);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLInverseFunctionalObjectPropertyAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLInverseFunctionalObjectPropertyAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLInverseFunctionalObjectPropertyAxiom anotherAxiom) {
								return this.matchUnaryPropertyAxioms(axiom,
										anotherAxiom);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final
					 *      org.semanticweb.owl.model.OWLSameIndividualsAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLSameIndividualsAxiom axiom) {
						Set<List<OWLIndividual>> allPermutations = CollectionPermutation
								.getAllPermutations(axiom.getIndividuals());
						Set<AxiomMatcher> toReturn = new HashSet<AxiomMatcher>();
						for (final List<OWLIndividual> aPermutation : allPermutations) {
							toReturn.add(new AxiomMatcher(new BindingNode(
									bindingNode), constraintSystem) {
								@Override
								public Boolean visit(
										OWLSameIndividualsAxiom anotherAxiom) {
									return this.matchIndividualCollections(
											aPermutation, anotherAxiom
													.getIndividuals());
								}
							});
						}
						return toReturn;
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLObjectPropertyChainSubPropertyAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLObjectPropertyChainSubPropertyAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLObjectPropertyChainSubPropertyAxiom anotherAxiom) {
								OWLPropertyExpressionMatcher superPropertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getSuperProperty(),
												bindingNode, constraintSystem);
								return anotherAxiom.getSuperProperty().accept(
										superPropertyMatcher)
										&& this
												.matchPropertyCollections(
														axiom
																.getPropertyChain(),
														anotherAxiom
																.getPropertyChain());
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.
					 *      OWLInverseObjectPropertiesAxiom)
					 */
					public Set<? extends AxiomMatcher> visit(
							final OWLInverseObjectPropertiesAxiom axiom) {
						return Collections.singleton(new AxiomMatcher(
								bindingNode, constraintSystem) {
							@Override
							public Boolean visit(
									OWLInverseObjectPropertiesAxiom anotherAxiom) {
								OWLPropertyExpressionMatcher firstPropertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getFirstProperty(),
												bindingNode, constraintSystem);
								OWLPropertyExpressionMatcher secondPropertyMatcher = OWLPropertyExpressionMatcher
										.getMatcher(axiom.getSecondProperty(),
												bindingNode, constraintSystem);
								return anotherAxiom.getFirstProperty().accept(
										firstPropertyMatcher)
										&& anotherAxiom.getSecondProperty()
												.accept(secondPropertyMatcher);
							}
						});
					}

					/**
					 * @see org.semanticweb.owl.model.OWLAxiomVisitorEx#visit(
					 *      final org.semanticweb.owl.model.SWRLRule)
					 */
					public Set<? extends AxiomMatcher> visit(final SWRLRule rule) {
						return Collections.singleton(AxiomMatcher
								.getNoMatchAxiomMatcher(bindingNode,
										constraintSystem));
					}
				});
	}
}
