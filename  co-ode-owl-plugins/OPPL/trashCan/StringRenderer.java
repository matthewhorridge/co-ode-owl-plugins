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
package org.coode.oppl;

import java.io.StringWriter;

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
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.VariableScope;
import org.coode.oppl.variablemansyntax.VariableScopes.Direction;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.OWLEntityRendererListener;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;

import uk.ac.manchester.cs.owl.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;

/**
 * @author Luigi Iannone
 * 
 */
public class StringRenderer implements OPPLParserVisitor {
	protected ConstraintSystem constraintSystem;
	protected OWLModelManager modelManager = null;

	public StringRenderer() {
	}

	/**
	 * @param modelManager
	 */
	public StringRenderer(OWLModelManager modelManager) {
		this.modelManager = modelManager;
	}

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
		if (this.constraintSystem == null) {
			this.constraintSystem = node.getConstraintSystem();
		}
		String toReturn = "";
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0 && node.jjtGetChild(i) instanceof OPPLVariableDefinition) {
				toReturn += ", ";
			}
			if (node.jjtGetChild(i) instanceof OPPLConstraint
					&& node.jjtGetChild(i - 1) instanceof OPPLQuery) {
				toReturn += "WHERE ";
			}
			toReturn += node.jjtGetChild(i).jjtAccept(this, data);
		}
		return toReturn;
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLActions,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLActions node, Object data) {
		String toReturn = "BEGIN ";
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				toReturn += ", ";
			}
			toReturn += node.jjtGetChild(i).jjtAccept(this, data);
		}
		return toReturn + " END; ";
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLVariableDefinition,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLVariableDefinition node, Object data) {
		Variable variable = node.getVariable();
		String toReturn = variable.getName() + ": " + variable.getType();
		if (variable.getVariableScope() != null) {
			VariableScope scope = variable.getVariableScope();
			Direction direction = scope.getDirection();
			OWLObject scopingObject = scope.getScopingObject();
			toReturn += "[" + direction + this.render(scopingObject) + "]";
		}
		return toReturn;
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLConstraint,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLConstraint node, Object data) {
		return node.getVariable().getName() + "!="
				+ this.render(node.getExpression());
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
		boolean first = true;
		String toReturn = "SELECT ";
		for (OWLAxiom axiom : node.getAxioms()) {
			if (first) {
				first = false;
			} else {
				toReturn += ",";
			}
			toReturn += this.render(axiom);
		}
		return toReturn;
	}

	/**
	 * 
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLAction,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLAction node, Object data) {
		OWLAxiom axiom = node.getAxiom();
		String axiomRendering = this.render(axiom);
		return node.getAction() + " " + axiomRendering;
	}

	/**
	 * @param axiom
	 * @return
	 */
	private String render(OWLObject axiom) {
		String toReturn = null;
		if (this.modelManager != null) {
			OWLModelManagerEntityRenderer oldEntityRenderer = this.modelManager
					.getOWLEntityRenderer();
			OWLModelManagerEntityRenderer variableEntityRenderer = new OWLModelManagerEntityRenderer() {
				public void addListener(OWLEntityRendererListener listener) {
					// TODO Auto-generated method stub
				}

				public void dispose() {
					// TODO Auto-generated method stub
				}

				public void initialise() {
					// TODO Auto-generated method stub
				}

				public void removeListener(OWLEntityRendererListener listener) {
					// TODO Auto-generated method stub
				}

				public String render(OWLEntity entity) {
					SimpleVariableShortFormProvider simpleVariableShortFormProvider = new SimpleVariableShortFormProvider(
							StringRenderer.this.constraintSystem);
					return simpleVariableShortFormProvider.getShortForm(entity);
				}

				public void setup(OWLModelManager owlModelManager) {
					// TODO Auto-generated method stub
				}
			};
			this.modelManager.setOWLEntityRenderer(variableEntityRenderer);
			toReturn = this.modelManager.getRendering(axiom);
			this.modelManager.setOWLEntityRenderer(oldEntityRenderer);
		} else {
			StringWriter axiomRenderingWriter = new StringWriter();
			ManchesterOWLSyntaxObjectRenderer renderer = this
					.getRenderer(axiomRenderingWriter);
			axiom.accept(renderer);
			toReturn = axiomRenderingWriter.toString();
		}
		return toReturn;
	}

	/**
	 * @param writer
	 * @return
	 */
	public ManchesterOWLSyntaxObjectRenderer getRenderer(StringWriter writer) {
		ManchesterOWLSyntaxObjectRenderer renderer = new ManchesterOWLSyntaxObjectRenderer(
				writer);
		renderer.setShortFormProvider(new SimpleVariableShortFormProvider(
				this.constraintSystem));
		return renderer;
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLClause,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLClause node, Object data) {
		return null;
	}

	/**
	 * @param constraintSystem
	 *            the constraintSystem to set
	 */
	public void setConstraintSystem(ConstraintSystem constraintSystem) {
		this.constraintSystem = constraintSystem;
	}

	public Object visit(OPPLtypeScope node, Object data) {
		return null;
	}

	/**
	 * @see org.coode.oppl.syntax.OPPLParserVisitor#visit(org.coode.oppl.syntax.OPPLparseScope,
	 *      java.lang.Object)
	 */
	public Object visit(OPPLparseScope node, Object data) {
		return null;
	}
}
