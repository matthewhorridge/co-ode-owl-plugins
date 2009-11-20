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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.coode.oppl.variablemansyntax.VariableScopes.Direction;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityVisitorEx;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 */
@SuppressWarnings("unused")
public enum VariableType implements OWLEntityVisitorEx<VariableType> {
	CLASS("CLASS", EnumSet.of(Direction.SUBCLASSOF, Direction.SUPERCLASSOF)) {
		@Override
		public Set<OWLClass> getReferencedValues(OWLOntology ontology) {
			return ontology.getReferencedClasses();
		}
	},
	DATAPROPERTY("DATAPROPERTY", EnumSet.of(Direction.SUBPROPERTYOF,
			Direction.SUPERPROPERTYOF)) {
		@Override
		public Set<OWLDataProperty> getReferencedValues(OWLOntology ontology) {
			return ontology.getReferencedDataProperties();
		}
	},
	OBJECTPROPERTY("OBJECTPROPERTY", EnumSet.of(Direction.SUBPROPERTYOF,
			Direction.SUPERPROPERTYOF)) {
		@Override
		public Set<OWLObjectProperty> getReferencedValues(OWLOntology ontology) {
			return ontology.getReferencedObjectProperties();
		}
	},
	INDIVIDUAL("INDIVIDUAL", EnumSet.of(Direction.INSTANCEOF)) {
		@Override
		public Set<OWLIndividual> getReferencedValues(OWLOntology ontology) {
			return ontology.getReferencedIndividuals();
		}
	},
	CONSTANT("CONSTANT", EnumSet.noneOf(Direction.class)) {
		@Override
		public Set<OWLConstant> getReferencedValues(OWLOntology ontology) {
			return Collections.emptySet();
		}
	};
	private final String rendering;
	private final EnumSet<Direction> allowedDirections;

	private VariableType(String rendering, EnumSet<Direction> directions) {
		this.rendering = rendering;
		this.allowedDirections = directions;
	}

	@Override
	public String toString() {
		return this.rendering;
	}

	public EnumSet<Direction> getAllowedDirections() {
		return this.allowedDirections;
	}

	public abstract Set<? extends OWLObject> getReferencedValues(
			OWLOntology ontology);

	public <O> O accept(VariableTypeVisitorEx<O> visitor) {
		return visitor.visit(this);
	}

	// public static VariableType getVariableType(String rendering) {
	// VariableType toReturn = null;
	// if (rendering.equals(CLASS.rendering)) {
	// toReturn = CLASS;
	// } else if (rendering.equals(OBJECTPROPERTY.rendering)) {
	// toReturn = OBJECTPROPERTY;
	// } else if (rendering.equals(DATAPROPERTY.rendering)) {
	// toReturn = DATAPROPERTY;
	// } else if (rendering.equals(INDIVIDUAL.rendering)) {
	// toReturn = INDIVIDUAL;
	// } else if (rendering.equals(CONSTANT.rendering)) {
	// toReturn = CONSTANT;
	// }
	// return toReturn;
	// }
	public static VariableType valueOfIgnoreCase(String s) {
		for (VariableType t : values()) {
			if (t.rendering.equalsIgnoreCase(s.trim())) {
				return t;
			}
		}
		return null;
	}

	public boolean isCompatibleWith(OWLObject o) {
		return o instanceof OWLEntity ? this.isCompatibleWith((OWLEntity) o)
				: this.isCompatibleWith((OWLConstant) o);
	}

	protected boolean isCompatibleWith(OWLEntity entity) {
		CompatibilityChecker checker = new CompatibilityChecker(this);
		return entity.accept(checker);
	}

	protected boolean isCompatibleWith(OWLConstant constant) {
		return this.equals(CONSTANT);
	}

	public VariableType visit(OWLClass owlClass) {
		return CLASS;
	}

	public VariableType visit(OWLDataProperty owlDataProperty) {
		return DATAPROPERTY;
	}

	public VariableType visit(OWLDataType owlDataType) {
		return null;
	}

	public VariableType visit(OWLIndividual owlIndividual) {
		return INDIVIDUAL;
	}

	public VariableType visit(OWLObjectProperty owlObjectProperty) {
		return OBJECTPROPERTY;
	}

	public static VariableType getVariableType(OWLObject owlObject) {
		// Shortened for brevity, a visitor is more appropriate, but there you
		// go
		if (owlObject instanceof OWLEntity) {
			OWLEntity entity = (OWLEntity) owlObject;
			VariableType aVariableType = EnumSet.allOf(VariableType.class)
					.iterator().next();
			return entity.accept(aVariableType);
		} else if (owlObject instanceof OWLDescription) {
			return CLASS;
		} else {
			return CONSTANT;
		}
	}

	static class CompatibilityChecker implements OWLEntityVisitorEx<Boolean> {
		private final VariableType variableType;

		/**
		 * @param variableType
		 */
		CompatibilityChecker(VariableType variableType) {
			this.variableType = variableType;
		}

		public Boolean visit(OWLClass cls) {
			return this.variableType.equals(VariableType.CLASS);
		}

		public Boolean visit(OWLObjectProperty property) {
			return this.variableType.equals(VariableType.OBJECTPROPERTY);
		}

		public Boolean visit(OWLDataProperty property) {
			return this.variableType.equals(VariableType.DATAPROPERTY);
		}

		public Boolean visit(OWLIndividual individual) {
			return this.variableType.equals(VariableType.INDIVIDUAL);
		}

		public Boolean visit(OWLDataType dataType) {
			return false;
		}
	}
}
