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
package org.coode.patterns;

import org.coode.oppl.syntax.OPPLAction;
import org.coode.oppl.syntax.OPPLActions;
import org.coode.oppl.syntax.OPPLClause;
import org.coode.oppl.syntax.OPPLConstraint;
import org.coode.oppl.syntax.OPPLOWLExpression;
import org.coode.oppl.syntax.OPPLParserVisitor;
import org.coode.oppl.syntax.OPPLQuery;
import org.coode.oppl.syntax.OPPLStart;
import org.coode.oppl.syntax.OPPLVariableDefinition;
import org.coode.oppl.syntax.OPPLparseScope;
import org.coode.oppl.syntax.OPPLtypeScope;
import org.coode.oppl.syntax.SimpleNode;

/**
 * @author Luigi Iannone
 * 
 * Jun 10, 2008
 */
public class PatternChecker implements OPPLParserVisitor {
	protected boolean atLeastOneVariable = false;
	protected boolean atLeastOneQuery = false;

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.SimpleNode,
	 *      java.lang.Object)
	 */
	public Object visit(SimpleNode node, Object data) {
		return null;
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLStart,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLStart node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLActions,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLActions node, Object data) {
		return null;
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLVariableDefinition,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLVariableDefinition node, Object data) {
		this.atLeastOneVariable = true;
		return null;
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLConstraint,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLConstraint node, Object data) {
		this.atLeastOneQuery = true;
		return null;
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLOWLExpression,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLOWLExpression node, Object data) {
		return null;
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLQuery,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLQuery node, Object data) {
		this.atLeastOneQuery = true;
		return null;
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLAction,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLAction node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLClause,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLClause node, Object data) {
		return null;
	}

	public boolean isPattern() {
		return this.atLeastOneVariable && !this.atLeastOneQuery;
	}

	public Object visit(OPPLtypeScope node, Object data) {
		return null;
	}

	public Object visit(OPPLparseScope node, Object data) {
		return null;
	}
}
