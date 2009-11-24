/**
 *
 */
package org.coode.oppl.search;

import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLDescription;

import com.clarkparsia.explanation.SatisfiabilityConverter;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLInferredOWLAxiomSearchTree extends AbstractOPPLAxiomSearchTree {
	public OPPLInferredOWLAxiomSearchTree(ConstraintSystem constraintSystem) {
		super(constraintSystem);
	}

	/**
	 * @return {@code true} if the input {@link OPPLOWLAxiomSearchNode}
	 *         represents an OWLAxiom that can be inferred using the reasoner
	 *         exposed by the ConstraintSystem.
	 * @see org.coode.oppl.search.SearchTree#goalReached(java.lang.Object)
	 */
	@Override
	protected boolean goalReached(OPPLOWLAxiomSearchNode start) {
		SatisfiabilityConverter converter = new SatisfiabilityConverter(this
				.getConstraintSystem().getOntologyManager().getOWLDataFactory());
		OWLDescription conversion = converter.convert(start.getAxiom());
		try {
			return !this.getConstraintSystem().getReasoner().isSatisfiable(
					conversion);
		} catch (OWLReasonerException e) {
			e.printStackTrace();
			return false;
		}
	}
}
