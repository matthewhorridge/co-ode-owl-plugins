package org.coode.oppl.variablemansyntax.generated.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.coode.oppl.variablemansyntax.generated.Attribute;
import org.coode.oppl.variablemansyntax.generated.CollectionGeneratedValue;
import org.semanticweb.owl.model.OWLObject;

public class OWLObjectCollectionGeneratedValue extends
		CollectionGeneratedValue<OWLObject> {
	private final ConstraintSystem constraintSystem;

	public OWLObjectCollectionGeneratedValue(Variable variable,
			Attribute attribute, ConstraintSystem constraintSystem,
			ConstraintSystem constraintSystem2) {
		super(variable, attribute, constraintSystem);
		this.constraintSystem = constraintSystem2;
	}

	@Override
	public Collection<OWLObject> getGeneratedValue(BindingNode node) {
		Set<OWLObject> toReturn = null;
		Set<BindingNode> leaves = this.constraintSystem.getLeaves();
		if (leaves != null && !leaves.isEmpty()) {
			toReturn = new HashSet<OWLObject>();
			for (BindingNode bindingNode : leaves) {
				OWLObject assignmentValue = bindingNode
						.getAssignmentValue(getVariable());
				if (assignmentValue != null) {
					toReturn.add(assignmentValue);
				}
			}
		}
		return toReturn;
	}

	@Override
	public List<Collection<OWLObject>> getGeneratedValues() {
		List<Collection<OWLObject>> toReturn = new ArrayList<Collection<OWLObject>>();
		Collection<OWLObject> generatedValue = getGeneratedValue(null);
		if (generatedValue != null) {
			toReturn.add(Collections.unmodifiableCollection(generatedValue));
		}
		return toReturn;
	}
}