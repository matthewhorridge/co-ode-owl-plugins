package org.coode.oppl.visitors;

import java.util.ArrayList;
import java.util.List;

import org.coode.oppl.variablemansyntax.PlainVariableVisitor;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;

public class GeneratedVariableCollector implements PlainVariableVisitor {
	private final List<GeneratedVariable<?>> toReturn;

	public GeneratedVariableCollector(List<GeneratedVariable<?>> toReturn) {
		this.toReturn = toReturn;
	}

	public GeneratedVariableCollector() {
		this(new ArrayList<GeneratedVariable<?>>());
	}

	public void visit(GeneratedVariable<?> v) {
		this.toReturn.add(v);
	}

	@SuppressWarnings("unused")
	public void visit(Variable v) {
		// empty, does not return input variables
	}

	public List<GeneratedVariable<?>> getCollectedVariables() {
		return this.toReturn;
	}
}