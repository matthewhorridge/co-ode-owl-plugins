package org.coode.oppl.variablemansyntax;

import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;

public interface PlainVariableVisitor {
	void visit(InputVariable v);

	void visit(GeneratedVariable<?> v);
}