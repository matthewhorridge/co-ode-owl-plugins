package org.coode.oppl.variablemansyntax.generated;

import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLObject;

public interface GeneratedVariable<N> extends Variable {
	/**
	 * @return the OPPL function String serialisation
	 */
	public String getOPPLFunction();

	public OWLObject getGeneratedOWLObject(BindingNode bindingNode);

	public GeneratedValue<N> getValue();

	public void setValue(GeneratedValue<N> value);

	public <P> GeneratedVariable<P> replaceValue(GeneratedValue<P> replaceValue);
}