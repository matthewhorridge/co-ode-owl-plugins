/**
 * 
 */
package org.semanticweb.owl.lint.protege;

import java.util.Collection;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintFactory;
import org.semanticweb.owl.lint.LintPattern;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.lint.PatternBasedLint;
import org.semanticweb.owl.lint.PatternReport;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.lint.LintFactoryImpl;

/**
 * @author Luigi Iannone
 * 
 */
public class ProtegeLintFactory implements LintFactory {
	private LintFactory delegate;

	/**
	 * @param modelManager
	 */
	public ProtegeLintFactory(OWLModelManager modelManager) {
		if (modelManager == null) {
			throw new NullPointerException("The model manager cannot be null");
		}
		this.delegate = new LintFactoryImpl(modelManager.getOWLOntologyManager(),
				modelManager.getReasoner());
	}

	/**
	 * @param lintPatterns
	 * @return
	 * @see org.semanticweb.owl.lint.LintFactory#createLint(org.semanticweb.owl.lint.LintPattern[])
	 */
	public <O extends OWLObject> PatternBasedLint<O> createLint(
			Collection<? extends LintPattern<O>> lintPatterns) {
		return this.delegate.createLint(lintPatterns);
	}

	/**
	 * @param pattern
	 * @return
	 * @see org.semanticweb.owl.lint.LintFactory#createPatternReport(org.semanticweb.owl.lint.LintPattern)
	 */
	public <O extends OWLObject> PatternReport<O> createPatternReport(LintPattern<O> pattern) {
		return this.delegate.createPatternReport(pattern);
	}

	/**
	 * @param lint
	 * @return
	 * @see org.semanticweb.owl.lint.LintFactory#createLintReport(org.semanticweb.owl.lint.Lint)
	 */
	public <O extends OWLObject> LintReport<O> createLintReport(Lint<O> lint) {
		return this.delegate.createLintReport(lint);
	}

	/**
	 * @return
	 * @see org.semanticweb.owl.lint.LintFactory#getOntologyManager()
	 */
	public OWLOntologyManager getOntologyManager() {
		return this.delegate.getOntologyManager();
	}

	/**
	 * @return
	 * @see org.semanticweb.owl.lint.LintFactory#getOWLReasoner()
	 */
	public OWLReasoner getOWLReasoner() {
		return this.delegate.getOWLReasoner();
	}
}
