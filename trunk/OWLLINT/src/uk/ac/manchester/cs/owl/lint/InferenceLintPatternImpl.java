/**
 * 
 */
package uk.ac.manchester.cs.owl.lint;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.lint.InferenceLintPattern;
import org.semanticweb.owl.lint.LintException;

/**
 * @author Luigi Iannone
 * 
 */
public class InferenceLintPatternImpl implements InferenceLintPattern {
	protected OWLReasoner reasoner;

	/**
	 * @param reasoner
	 * 
	 */
	public InferenceLintPatternImpl(OWLReasoner reasoner) {
		this.reasoner = reasoner;
	}

	/**
	 * @see org.semanticweb.owl.lint.InferenceLintPattern#getOWLReasoner()
	 */
	public OWLReasoner getOWLReasoner() throws LintException {
		if (this.reasoner != null) {
			return this.reasoner;
		} else {
			throw new LintException("Null reasoner for this pattern");
		}
	}
}
