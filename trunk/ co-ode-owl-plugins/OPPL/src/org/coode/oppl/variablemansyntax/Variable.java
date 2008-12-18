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
import java.util.Set;

import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLObject;

/**
 * This interface represents a generic Variable. When possible developers should
 * implement either {@link InputVariable} or extend {@link GeneratedVariable} in
 * order to use {@link Variable.VariableVisitor}
 * 
 * 
 * @author Luigi Iannone
 * 
 */
public interface Variable {
	public interface VariableVisitor<P extends Object> {
		P visit(InputVariable v);

		P visit(GeneratedVariable<?> v);
	}

	public interface PlainVariableVisitor {
		void visit(InputVariable v);

		void visit(GeneratedVariable<?> v);
	}

	/**
	 * @return the name of the Variable
	 */
	String getName();

	/**
	 * @return an URI for the Variable
	 */
	URI getURI();

	/**
	 * @return the type of the Variable
	 * @see VariableType
	 */
	VariableType getType();

	/**
	 * Adds a possible value (the input OWLObject) the Variable can assume
	 * 
	 * @param object
	 * @throws OWLReasonerException
	 */
	boolean addPossibleBinding(OWLObject object) throws OWLReasonerException;

	/**
	 * @return the currently possible values that a Variable can assume
	 */
	Set<OWLObject> getPossibleBindings();

	/**
	 * Removes the input OWLObject from the set of current possible values the
	 * Variable can assume
	 * 
	 * @param object
	 */
	boolean removePossibleBinding(OWLObject object);

	/**
	 * Empties the set of current possible values for the Variable
	 */
	void clearBindings();

	/**
	 * Sets the scope for the Variable that will be checked by means of the
	 * input VariableScopeChecker
	 * 
	 * @param variableScope
	 * @param variableScopeChecker
	 */
	void setVariableScope(VariableScope variableScope,
			VariableScopeChecker variableScopeChecker);

	/**
	 * @return the scope for the Variable (can be null if not previously
	 *         assigned)
	 */
	VariableScope getVariableScope();

	/**
	 * Visitor pattern interface method for visitors with return type
	 * 
	 * @param
	 * <P>
	 * @param visitor
	 * @return
	 */
	public <P> P accept(VariableVisitor<P> visitor);

	/**
	 * Visitor pattern interface method for visitors without return type
	 * 
	 * @param visitor
	 */
	void accept(PlainVariableVisitor visitor);
}
