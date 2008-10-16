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

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.ManchesterVariableSyntax;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.VariableScope;
import org.coode.oppl.variablemansyntax.VariableScopeChecker;
import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public class GeneratedVariable implements Variable {
	private final String name;
	private final VariableType type;
	private final GeneratedValue value;

	/**
	 * @param name
	 * @param type
	 * @param value
	 */
	public GeneratedVariable(String name, VariableType type,
			GeneratedValue value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	/**
	 * @see org.coode.oppl.variablemansyntax.Variable#getName()
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @see org.coode.oppl.variablemansyntax.Variable#getType()
	 */
	public VariableType getType() {
		return this.type;
	}

	/**
	 * @see org.coode.oppl.variablemansyntax.Variable#getURI()
	 */
	public URI getURI() {
		String fragment = this.name.substring(this.name.indexOf('?') + 1);
		return URI.create(ManchesterVariableSyntax.NAMESPACE + fragment);
	}

	/**
	 * @see org.coode.oppl.variablemansyntax.Variable#getVariableScope()
	 */
	public VariableScope getVariableScope() {
		return null;
	}

	/**
	 * @see org.coode.oppl.variablemansyntax.Variable#setVariableScope(org.coode.oppl.variablemansyntax.VariableScope,
	 *      org.coode.oppl.variablemansyntax.VariableScopeChecker)
	 */
	public void setVariableScope(VariableScope variableScope,
			VariableScopeChecker variableScopeChecker) {
	}

	public boolean addPossibleBinding(OWLObject object)
			throws OWLReasonerException {
		return false;
	}

	public void clearBindings() {
	}

	public Set<OWLObject> getPossibleBindings() {
		Set<OWLObject> toReturn = new HashSet<OWLObject>();
		List<String> generatedValues = this.value.getGeneratedValues();
		for (String value : generatedValues) {
			toReturn.add(this.generateObject(value));
		}
		return toReturn;
	}

	/**
	 * @param bindingNode
	 * @return the OWLObject that is generated as value for this Variable for
	 *         the input BindingNode, it may return null
	 */
	public OWLObject getGeneratedOWLObject(BindingNode bindingNode) {
		String generatedValue = this.value.getGeneratedValue(bindingNode);
		return generatedValue == null ? null : this
				.generateObject(generatedValue);
	}

	private OWLObject generateObject(String aValue) {
		OWLObject value = null;
		OWLEntityFactory entityFactory = OPPLParser.getOPPLFactory()
				.getOWLEntityFactory();
		switch (this.type) {
		case CLASS:
			try {
				value = entityFactory.createOWLClass(aValue, null)
						.getOWLEntity();
			} catch (OWLEntityCreationException e) {
				value = OPPLParser.getOWLDataFactory().getOWLClass(
						this.buildURI(aValue));
			}
			break;
		case OBJECTPROPERTY:
			try {
				value = entityFactory.createOWLObjectProperty(aValue, null)
						.getOWLEntity();
			} catch (OWLEntityCreationException e) {
				value = OPPLParser.getOWLDataFactory().getOWLObjectProperty(
						this.buildURI(aValue));
			}
			break;
		case DATAPROPERTY:
			try {
				value = entityFactory.createOWLDataProperty(aValue, null)
						.getOWLEntity();
			} catch (OWLEntityCreationException e) {
				value = OPPLParser.getOWLDataFactory().getOWLDataProperty(
						this.buildURI(aValue));
			}
			break;
		case INDIVIDUAL:
			try {
				value = entityFactory.createOWLIndividual(aValue, null)
						.getOWLEntity();
			} catch (OWLEntityCreationException e) {
				value = OPPLParser.getOWLDataFactory().getOWLIndividual(
						this.buildURI(aValue));
			}
			break;
		case CONSTANT:
			value = OPPLParser.getOWLDataFactory().getOWLTypedConstant(aValue);
		default:
			break;
		}
		return value;
	}

	/**
	 * @param aValue
	 * @return
	 */
	private URI buildURI(String aValue) {
		return URI.create(OPPLParser.getConstraintSystem().getOntology()
				.getURI().toString()
				+ "#" + aValue);
	}

	public boolean removePossibleBinding(OWLObject object) {
		return false;
	}

	public static GeneratedVariable buildGeneratedVariable(String name,
			VariableType type, GeneratedValue value) {
		return new GeneratedVariable(name, type, value);
	}

	@Override
	public String toString() {
		return this.name + ":" + this.type;
	}

	public <P> P accept(VariableVisitor<P> visitor) {
		return visitor.visit(this);
	}

	/**
	 * @return the value
	 */
	public GeneratedValue getValue() {
		return this.value;
	}

	/**
	 * @see org.coode.oppl.variablemansyntax.Variable#accept(org.coode.oppl.variablemansyntax.Variable.PlainVariableVisitor)
	 */
	public void accept(PlainVariableVisitor visitor) {
		visitor.visit(this);
	}
}
