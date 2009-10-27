/**
 *
 */
package org.coode.oppl.variablemansyntax.generated;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.coode.oppl.rendering.ManchesterSyntaxRenderer;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.OWLObjectInstantiator;
import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public class VariableExpressionGeneratedVariable extends
		GeneratedVariable<OWLObject> {
	private static final class VariableExpressionGeneratedValue implements
			GeneratedValue<OWLObject> {
		private final OWLObject owlObject;
		private final ConstraintSystem constraintSystem;

		/**
		 * @param owlObject
		 */
		protected VariableExpressionGeneratedValue(OWLObject owlObject,
				ConstraintSystem constraintSystem) {
			assert owlObject != null;
			assert constraintSystem != null;
			this.owlObject = owlObject;
			this.constraintSystem = constraintSystem;
		}

		public OWLObject getGeneratedValue(BindingNode node) {
			OWLObjectInstantiator instantiator = new OWLObjectInstantiator(
					node, this.constraintSystem);
			OWLObject instantiation = this.owlObject.accept(instantiator);
			return instantiation;
		}

		public List<OWLObject> getGeneratedValues() {
			Set<BindingNode> leaves = this.constraintSystem.getLeaves();
			List<OWLObject> toReturn = new ArrayList<OWLObject>(leaves.size());
			for (BindingNode bindingNode : leaves) {
				toReturn.add(getGeneratedValue(bindingNode));
			}
			return toReturn;
		}
	}

	private final ConstraintSystem constraintSystem;
	private OWLObject owlObject;

	/**
	 * Builds an instance of VariableExpressionGeneratedVariable from a name and
	 * an OWLObject.
	 * 
	 * @param name
	 *            the name for this variable. cannot be {@code null}.
	 * @param owlObject
	 *            the OWL object on which this variable will be based. Cannot be
	 *            {@code null}. It can depend on other variables.
	 * @param constraintSystem
	 *            the Constraint system containing all the variables necessary
	 *            to express this one. Cannot be {@code null}.
	 * @throws NullPointerException
	 *             when any of the inputs is {@code null}.
	 */
	public VariableExpressionGeneratedVariable(String name,
			OWLObject owlObject, ConstraintSystem constraintSystem) {
		super(name, VariableType.getVariableType(owlObject),
				new VariableExpressionGeneratedValue(owlObject,
						constraintSystem));
		if (constraintSystem == null) {
			throw new IllegalArgumentException(
					"The constraint system cannot be null");
		}
		if (owlObject == null) {
			throw new IllegalArgumentException("The OWL Object cannot be null");
		}
		this.owlObject = owlObject;
		this.constraintSystem = constraintSystem;
	}

	@Override
	protected OWLObject generateObject(OWLObject generatedValue) {
		return generatedValue;
	}

	@Override
	public String getOPPLFunction() {
		ManchesterSyntaxRenderer manchesterSyntaxRenderer = OPPLParser
				.getOPPLFactory().getManchesterSyntaxRenderer(
						this.constraintSystem);
		this.owlObject.accept(manchesterSyntaxRenderer);
		return manchesterSyntaxRenderer.toString();
	}

	@Override
	protected GeneratedVariable<OWLObject> replace(
			GeneratedValue<OWLObject> value) {
		return this;
	}
}
