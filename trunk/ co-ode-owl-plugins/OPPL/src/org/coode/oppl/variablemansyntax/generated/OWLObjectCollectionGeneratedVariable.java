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

import java.util.Collection;
import java.util.HashSet;

import org.coode.oppl.variablemansyntax.VariableType;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectUnionOf;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class OWLObjectCollectionGeneratedVariable<P extends OWLObject>
		extends GeneratedVariable<Collection<P>> {
	private final OWLDataFactory dataFactory;

	OWLObjectCollectionGeneratedVariable(String name, VariableType type,
			GeneratedValue<Collection<P>> value, OWLDataFactory dataFactory) {
		super(name, type, value);
		this.dataFactory = dataFactory;
	}

	@Override
	protected abstract OWLObject generateObject(Collection<P> generatedValue);

	public static GeneratedVariable<Collection<OWLClass>> getConjunction(
			String name, VariableType type,
			GeneratedValue<Collection<OWLClass>> value,
			OWLDataFactory dataFactory) {
		return new OWLObjectCollectionGeneratedVariable<OWLClass>(name, type,
				value, dataFactory) {
			@Override
			protected OWLObjectIntersectionOf generateObject(
					Collection<OWLClass> generatedValue) {
				return generatedValue == null ? null : this.getDataFactory()
						.getOWLObjectIntersectionOf(
								new HashSet<OWLClass>(generatedValue));
			}

			@Override
			public String toString() {
				return this.getName() + ":" + this.getType() + " = "
						+ this.getOPPLFunction();
			}

			@Override
			protected GeneratedVariable<Collection<OWLClass>> replace(
					GeneratedValue<Collection<OWLClass>> value) {
				return this.replaceValue(false, value);
			}

			@Override
			public String getOPPLFunction() {
				return "createIntersection(" + this.getValue().toString() + ")";
			}
		};
	}

	public static GeneratedVariable<Collection<OWLClass>> getDisjunction(
			String name, VariableType type,
			GeneratedValue<Collection<OWLClass>> value,
			OWLDataFactory dataFactory) {
		return new OWLObjectCollectionGeneratedVariable<OWLClass>(name, type,
				value, dataFactory) {
			@Override
			protected OWLObjectUnionOf generateObject(
					Collection<OWLClass> generatedValue) {
				return generatedValue == null ? null : this.getDataFactory()
						.getOWLObjectUnionOf(
								new HashSet<OWLClass>(generatedValue));
			}

			@Override
			public String toString() {
				return this.getName() + ":" + this.getType() + " = "
						+ this.getOPPLFunction();
			}

			@Override
			protected GeneratedVariable<Collection<OWLClass>> replace(
					GeneratedValue<Collection<OWLClass>> value) {
				return this.replaceValue(true, value);
			}

			@Override
			public String getOPPLFunction() {
				return "createUnion(" + this.getValue().toString() + ")";
			}
		};
	}

	/**
	 * @return the dataFactory
	 */
	public final OWLDataFactory getDataFactory() {
		return this.dataFactory;
	}

	protected GeneratedVariable<Collection<OWLClass>> replaceValue(
			boolean isUnion, GeneratedValue<Collection<OWLClass>> value) {
		return isUnion ? getDisjunction(this.getName(), this.getType(), value,
				this.getDataFactory()) : getConjunction(this.getName(), this
				.getType(), value, this.getDataFactory());
	}
}
