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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.coode.oppl.entity.OWLEntityRenderer;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class VariableGeneratedValue<N> implements GeneratedValue<N> {
	private static class RenderingAttributeGenerator implements
			AttributeGenerator<String> {
		private OWLEntityRenderer entityRenderer = OPPLParser.getOPPLFactory()
				.getOWLEntityRenderer(this.constraintSystem);
		private static RenderingAttributeGenerator instance = null;
		private ConstraintSystem constraintSystem;

		private RenderingAttributeGenerator(ConstraintSystem cs) {
			assert cs != null;
			this.constraintSystem = cs;
		}

		public static RenderingAttributeGenerator getInstance(
				ConstraintSystem cs) {
			if (instance == null) {
				instance = new RenderingAttributeGenerator(cs);
			}
			return instance;
		}

		public String getValue(OWLObject object) {
			String toReturn = null;
			if (object instanceof OWLEntity) {
				toReturn = this.entityRenderer.render((OWLEntity) object);
			} else {
				toReturn = object.toString();
			}
			return toReturn;
		}

		public List<String> getValues(Variable variable) {
			List<String> toReturn = new ArrayList<String>();
			Set<OWLObject> possibleBindings = variable.getPossibleBindings();
			for (OWLObject object : possibleBindings) {
				if (object instanceof OWLEntity) {
					toReturn
							.add(this.entityRenderer.render((OWLEntity) object));
				} else {
					toReturn.add(object.toString());
				}
			}
			return toReturn;
		}
	}

	public interface AttributeGenerator<N> {
		/**
		 * @author Luigi Iannone
		 * 
		 */
		List<N> getValues(Variable aVariable);

		N getValue(OWLObject object);
	}

	public enum Attribute {
		RENDERING("RENDERING") {
			@Override
			public VariableGeneratedValue<?> getVariableGeneratedValue(
					Variable variable, final ConstraintSystem constraintSystem) {
				return new VariableGeneratedValue<String>(variable, this) {
					@Override
					public String getGeneratedValue(BindingNode node) {
						return RenderingAttributeGenerator.getInstance(
								constraintSystem).getValue(
								node.getAssignmentValue(this.getVariable()));
					}

					@Override
					public List<String> getGeneratedValues() {
						return RenderingAttributeGenerator.getInstance(
								constraintSystem).getValues(this.getVariable());
					}
				};
			}
		},
		VALUES("VALUES") {
			@Override
			public VariableGeneratedValue<?> getVariableGeneratedValue(
					Variable variable, final ConstraintSystem constraintSystem) {
				switch (variable.getType()) {
				case CLASS:
					return new CollectionGeneratedValue<OWLDescription>(
							variable, this, constraintSystem) {
						@Override
						public Collection<OWLDescription> getGeneratedValue(
								BindingNode node) {
							Set<OWLDescription> toReturn = new HashSet<OWLDescription>();
							Set<BindingNode> leaves = constraintSystem
									.getLeaves();
							if (leaves != null && !leaves.isEmpty()) {
								for (BindingNode bindingNode : leaves) {
									OWLDescription assignmentValue = (OWLDescription) bindingNode
											.getAssignmentValue(this
													.getVariable());
									if (assignmentValue != null) {
										toReturn.add(assignmentValue);
									}
								}
							} else {
								toReturn = null;
							}
							return toReturn == null || toReturn.isEmpty() ? null
									: toReturn;
						}

						@Override
						public List<Collection<OWLDescription>> getGeneratedValues() {
							Collection<OWLDescription> generatedValue = this
									.getGeneratedValue(null);
							return generatedValue == null ? new ArrayList<Collection<OWLDescription>>()
									: new ArrayList<Collection<OWLDescription>>(
											Collections
													.singleton(generatedValue));
						}
					};
				case OBJECTPROPERTY:
					return new CollectionGeneratedValue<OWLObjectProperty>(
							variable, this, constraintSystem) {
						@Override
						public Collection<OWLObjectProperty> getGeneratedValue(
								BindingNode node) {
							Set<OWLObjectProperty> toReturn = new HashSet<OWLObjectProperty>();
							Set<BindingNode> leaves = constraintSystem
									.getLeaves();
							if (leaves != null && !leaves.isEmpty()) {
								for (BindingNode bindingNode : leaves) {
									OWLObjectProperty assignmentValue = (OWLObjectProperty) bindingNode
											.getAssignmentValue(this
													.getVariable());
									if (assignmentValue != null) {
										toReturn.add(assignmentValue);
									}
								}
							} else {
								toReturn = null;
							}
							return toReturn == null || toReturn.isEmpty() ? null
									: toReturn;
						}

						@Override
						public List<Collection<OWLObjectProperty>> getGeneratedValues() {
							Collection<OWLObjectProperty> generatedValue = this
									.getGeneratedValue(null);
							return generatedValue == null ? new ArrayList<Collection<OWLObjectProperty>>()
									: new ArrayList<Collection<OWLObjectProperty>>(
											Collections
													.singleton(generatedValue));
						}
					};
				case DATAPROPERTY:
					return new CollectionGeneratedValue<OWLDataProperty>(
							variable, this, constraintSystem) {
						@Override
						public Collection<OWLDataProperty> getGeneratedValue(
								BindingNode node) {
							Set<OWLDataProperty> toReturn = new HashSet<OWLDataProperty>();
							Set<BindingNode> leaves = constraintSystem
									.getLeaves();
							if (leaves != null && !leaves.isEmpty()) {
								for (BindingNode bindingNode : leaves) {
									OWLDataProperty assignmentValue = (OWLDataProperty) bindingNode
											.getAssignmentValue(this
													.getVariable());
									if (assignmentValue != null) {
										toReturn.add(assignmentValue);
									}
								}
							} else {
								toReturn = null;
							}
							return toReturn == null || toReturn.isEmpty() ? null
									: toReturn;
						}

						@Override
						public List<Collection<OWLDataProperty>> getGeneratedValues() {
							Collection<OWLDataProperty> generatedValue = this
									.getGeneratedValue(null);
							return generatedValue == null ? new ArrayList<Collection<OWLDataProperty>>()
									: new ArrayList<Collection<OWLDataProperty>>(
											Collections
													.singleton(generatedValue));
						}
					};
				case INDIVIDUAL:
					return new CollectionGeneratedValue<OWLIndividual>(
							variable, this, constraintSystem) {
						@Override
						public Collection<OWLIndividual> getGeneratedValue(
								BindingNode node) {
							Set<OWLIndividual> toReturn = new HashSet<OWLIndividual>();
							Set<BindingNode> leaves = constraintSystem
									.getLeaves();
							if (leaves != null && !leaves.isEmpty()) {
								for (BindingNode bindingNode : leaves) {
									OWLIndividual assignmentValue = (OWLIndividual) bindingNode
											.getAssignmentValue(this
													.getVariable());
									if (assignmentValue != null) {
										toReturn.add(assignmentValue);
									}
								}
							} else {
								toReturn = null;
							}
							return toReturn == null || toReturn.isEmpty() ? null
									: toReturn;
						}

						@Override
						public List<Collection<OWLIndividual>> getGeneratedValues() {
							Collection<OWLIndividual> generatedValue = this
									.getGeneratedValue(null);
							return generatedValue == null ? new ArrayList<Collection<OWLIndividual>>()
									: new ArrayList<Collection<OWLIndividual>>(
											Collections
													.singleton(generatedValue));
						}
					};
				case CONSTANT:
					return new CollectionGeneratedValue<OWLConstant>(variable,
							this, constraintSystem) {
						@Override
						public Collection<OWLConstant> getGeneratedValue(
								BindingNode node) {
							Set<OWLConstant> toReturn = new HashSet<OWLConstant>();
							Set<BindingNode> leaves = constraintSystem
									.getLeaves();
							if (leaves != null && !leaves.isEmpty()) {
								for (BindingNode bindingNode : leaves) {
									OWLConstant assignmentValue = (OWLConstant) bindingNode
											.getAssignmentValue(this
													.getVariable());
									if (assignmentValue != null) {
										toReturn.add(assignmentValue);
									}
								}
							} else {
								toReturn = null;
							}
							return toReturn == null || toReturn.isEmpty() ? null
									: toReturn;
						}

						@Override
						public List<Collection<OWLConstant>> getGeneratedValues() {
							Collection<OWLConstant> generatedValue = this
									.getGeneratedValue(null);
							return generatedValue == null ? new ArrayList<Collection<OWLConstant>>()
									: new ArrayList<Collection<OWLConstant>>(
											Collections
													.singleton(generatedValue));
						}
					};
				default:
					break;
				}
				return null;
			}
		};
		private final String attribute;

		private Attribute(String s) {
			this.attribute = s;
		}

		@Override
		public String toString() {
			return this.attribute;
		}

		public static Attribute parse(String s)
				throws InvalidAttributeException {
			Attribute toReturn = null;
			EnumSet<Attribute> attributes = EnumSet.allOf(Attribute.class);
			Iterator<Attribute> it = attributes.iterator();
			boolean found = false;
			Attribute attribute = null;
			while (!found && it.hasNext()) {
				attribute = it.next();
				found = attribute.toString().compareTo(s) == 0;
			}
			if (found) {
				toReturn = attribute;
			} else {
				throw new InvalidAttributeException(s);
			}
			return toReturn;
		}

		public abstract VariableGeneratedValue<?> getVariableGeneratedValue(
				Variable variable, ConstraintSystem constraintSystem);
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
		return this.attribute.getVariableGeneratedValue(v, constraintSystem);
	}
}
