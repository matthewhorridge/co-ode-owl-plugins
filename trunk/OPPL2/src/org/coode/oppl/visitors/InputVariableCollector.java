package org.coode.oppl.visitors;

import java.util.ArrayList;
import java.util.List;

import org.coode.oppl.variablemansyntax.PlainVariableVisitor;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;

public class InputVariableCollector implements PlainVariableVisitor {
	private final List<Variable> toReturn;

	public InputVariableCollector(List<Variable> toReturn) {
		this.toReturn = toReturn;
	}

	public InputVariableCollector() {
		this(new ArrayList<Variable>());
	}

	public void visit(GeneratedVariable<?> v) {
		// empty, does not return generated variables
	}

	public void visit(Variable v) {
		this.toReturn.add(v);
	}

	public List<Variable> getCollectedVariables() {
		return this.toReturn;
	}
}