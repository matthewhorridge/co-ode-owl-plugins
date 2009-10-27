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
package org.coode.oppl.variablemansyntax.bindingtree;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.coode.oppl.entity.OWLEntityRenderer;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.InputVariable;
import org.coode.oppl.variablemansyntax.PlainVariableVisitor;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.VariableVisitor;
import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public class BindingNode implements VariableVisitor<OWLObject> {
	private class VariableInspector implements PlainVariableVisitor {
		public VariableInspector() {
		}

		public void visit(InputVariable v) {
			BindingNode.this.unassignedVariables.add(v);
		}

		public void visit(GeneratedVariable<?> v) {
		}
	}

	private final Set<Assignment> assignments;
	protected final Set<Variable> unassignedVariables;

	/**
	 * @param assignments
	 * @param unassignedVariables
	 */
	public BindingNode(Set<Assignment> assignments,
			Set<Variable> unassignedVariables) {
		this.assignments = assignments;
		this.unassignedVariables = new HashSet<Variable>(unassignedVariables);
	}

	public void accept(BindingVisitor visitor) {
		visitor.visit(this);
	}

	public <O> O accept(BindingVistorEx<O> visitor) {
		return visitor.visit(this);
	}

	public boolean isLeaf() {
		boolean found = false;
		Iterator<Variable> it = this.unassignedVariables.iterator();
		while (!found && it.hasNext()) {
			found = !it.next().getPossibleBindings().isEmpty();
		}
		return this.unassignedVariables.isEmpty() || !found;
	}

	@Override
	public String toString() {
		return this.assignments
				+ "\n"
				+ (this.unassignedVariables.isEmpty() ? ""
						: this.unassignedVariables);
	}

	public String render(ConstraintSystem cs) {
		boolean first = true;
		StringWriter stringWriter = new StringWriter();
		OWLEntityRenderer entityRenderer = OPPLParser.getOPPLFactory()
				.getOWLEntityRenderer(cs);
		for (Assignment assignment : this.assignments) {
			OWLObject value = assignment.getAssignment();
			String assignmentRendering = value instanceof OWLEntity ? entityRenderer
					.render((OWLEntity) value)
					: value.toString();
			String commaString = first ? "" : ", ";
			stringWriter.append(commaString);
			first = false;
			stringWriter.append(assignment.getAssignedVariable().getName()
					+ " = " + assignmentRendering);
		}
		if (!this.unassignedVariables.isEmpty()) {
			stringWriter.append(" ");
			stringWriter.append(this.unassignedVariables.toString());
		}
		return stringWriter.toString();
	}

	public OWLObject getAssignmentValue(Variable variable) {
		return variable.accept(this);
	}

	public void addAssignment(Assignment assignment) {
		this.assignments.add(assignment);
		this.unassignedVariables.remove(assignment.getAssignedVariable());
	}

	public Set<Variable> getAssignedVariables() {
		Set<Variable> toReturn = new HashSet<Variable>();
		for (Assignment assignment : this.assignments) {
			toReturn.add(assignment.getAssignedVariable());
		}
		return toReturn;
	}

	/**
	 * @return the assignments
	 */
	public Set<Assignment> getAssignments() {
		return this.assignments;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (Variable unassignedVariable : this.unassignedVariables) {
			hashCode *= unassignedVariable.hashCode();
		}
		for (Assignment assignment : this.assignments) {
			hashCode *= assignment.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		boolean toReturn = obj instanceof BindingNode;
		if (toReturn) {
			BindingNode toCompare = (BindingNode) obj;
			toReturn = this.assignments.equals(toCompare.assignments)
					&& this.unassignedVariables
							.equals(toCompare.unassignedVariables);
		}
		return toReturn;
	}

	public Set<Variable> getUnassignedVariables() {
		return this.unassignedVariables;
	}

	/**
	 * Adds a variable to the set of the unassigned ones
	 * 
	 * @param v
	 */
	public void addUnassignedVariable(Variable v) {
		v.accept(new VariableInspector());
	}

	public OWLObject visit(InputVariable v) {
		for (Assignment assignment : this.assignments) {
			if (assignment.getAssignedVariable().equals(v)) {
				return assignment.getAssignment();
			}
		}
		return null;
	}

	public OWLObject visit(GeneratedVariable<?> v) {
		Iterator<Assignment> it = this.assignments.iterator();
		boolean found = false;
		OWLObject toReturn = null;
		while (!found && it.hasNext()) {
			Assignment assignment = it.next();
			found = assignment.getAssignedVariable().getName().compareTo(
					v.getName()) == 0;
			toReturn = found ? assignment.getAssignment() : toReturn;
		}
		if (!found) {
			toReturn = v.getGeneratedOWLObject(this);
		}
		return toReturn;
	}

	/**
	 * @return true if the Binding node has got no assigned variable nor to
	 *         assign variables
	 */
	public boolean isEmpty() {
		return this.assignments.isEmpty() && this.unassignedVariables.isEmpty();
	}
}
