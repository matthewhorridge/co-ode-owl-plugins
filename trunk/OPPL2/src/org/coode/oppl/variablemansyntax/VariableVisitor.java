package org.coode.oppl.variablemansyntax;

import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;

public interface VariableVisitor<P extends Object> {
	P visit(Variable v);

	P visit(GeneratedVariable<?> v);
}