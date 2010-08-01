/**
 * 
 */
package uk.ac.manchester.cs.owl.lint.examples;

import java.util.Collection;
import java.util.Collections;

import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.lint.LintVisitor;
import org.semanticweb.owl.lint.LintVisitorEx;
import org.semanticweb.owl.lint.PatternBasedLint;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.cs.owl.lint.LintManagerFactory;

/**
 * @author Luigi Iannone
 * 
 */
public final class NonLeafNoInfoLint implements Lint<OWLClass> {
	private final PatternBasedLint<OWLClass> delegate;

	public NonLeafNoInfoLint() {
		this.delegate = LintManagerFactory.getInstance().getLintManager()
				.getLintFactory().createLint(
						Collections.singleton(new NonLeafNoInfoLintPattern()));
	}

	/**
	 * @param targets
	 * @return
	 * @throws LintException
	 * @see org.semanticweb.owl.lint.Lint#detected(java.util.Collection)
	 */
	public LintReport<OWLClass> detected(
			Collection<? extends OWLOntology> targets) throws LintException {
		return this.delegate.detected(targets);
	}

	/**
	 * @return
	 * @see org.semanticweb.owl.lint.Lint#getName()
	 */
	public String getName() {
		return this.delegate.getName();
	}

	/**
	 * @return
	 * @see org.semanticweb.owl.lint.Lint#getDescription()
	 */
	public String getDescription() {
		return "Detects all the classes that are not leaves in the asserted hierarchy and have no anonymous asserted super-classes or equivalent classes. They are, in other words, intermediate names used at most as synonyms of something else.";
	}

	public void accept(LintVisitor visitor) {
		this.delegate.accept(visitor);
	}

	public <P> P accept(LintVisitorEx<P> visitor) {
		return this.delegate.accept(visitor);
	}
}
