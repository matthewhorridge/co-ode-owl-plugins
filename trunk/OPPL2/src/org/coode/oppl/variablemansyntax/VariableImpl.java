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

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public final class VariableImpl implements Variable {
	private final String name;
	private final VariableType type;
	private final URI uri;
	protected Set<OWLObject> possibleBindings = new HashSet<OWLObject>();
	private VariableScope variableScope = null;
	private VariableScopeChecker variableScopeChecker = null;

	public VariableImpl(String name, VariableType type) {
		if (name == null) {
			throw new NullPointerException("The name cannot be null");
		}
		if (type == null) {
			throw new NullPointerException("The type cannot be null");
		}
		this.name = name;
		this.type = type;
		String fragment = this.name.substring(this.name.indexOf('?') + 1);
		this.uri = URI.create(ManchesterVariableSyntax.NAMESPACE + fragment);
	}

	public String getName() {
		return this.name;
	}

	public URI getURI() {
		// String fragment = this.name.substring(this.name.indexOf('?') + 1);
		// return URI.create(ManchesterVariableSyntax.NAMESPACE + fragment);
		return this.uri;
	}

	/**
	 * @return the type
	 */
	public VariableType getType() {
		return this.type;
	}

	public boolean addPossibleBinding(OWLObject owlObject)
			throws OWLReasonerException {
		if (owlObject != null
				&& (this.variableScopeChecker == null
						|| this.variableScope == null || this.variableScope
						.check(owlObject, this.variableScopeChecker))) {
			this.possibleBindings.add(owlObject);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return this.name + ":" + this.getType();
	}

	public Set<OWLObject> getPossibleBindings() {
		return this.possibleBindings;
	}

	public boolean removePossibleBinding(OWLObject uri) {
		return this.possibleBindings.remove(uri);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Variable
				&& this.name.equals(((Variable) obj).getName());
	}

	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}

	public void clearBindings() {
		this.possibleBindings.clear();
	}

	public VariableScope getVariableScope() {
		return this.variableScope;
	}

	public void setVariableScope(VariableScope variableScope,
			VariableScopeChecker variableScopeChecker) {
		this.variableScope = variableScope;
		this.variableScopeChecker = variableScopeChecker;
	}

	public <P> P accept(VariableVisitor<P> visitor) {
		return visitor.visit(this);
	}

	public void accept(PlainVariableVisitor visitor) {
		visitor.visit(this);
	}
}
