/**
 * 
 */
package uk.ac.manchester.cs.owl.lint.examples;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.lint.LintVisitor;
import org.semanticweb.owl.lint.LintVisitorEx;
import org.semanticweb.owl.lint.configuration.LintConfiguration;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.cs.owl.lint.commons.NonConfigurableLintConfiguration;
import uk.ac.manchester.cs.owl.lint.commons.SimpleMatchBasedLintReport;

/**
 * @author Luigi Iannone
 * 
 */
public final class WaitFor20secondsLint implements Lint<OWLObject> {
	/**
	 * @see org.semanticweb.owl.lint.Lint#detected(java.util.Collection)
	 */
	public LintReport<OWLObject> detected(Collection<? extends OWLOntology> targets)
			throws LintException {
		LintReport<OWLObject> empty = new SimpleMatchBasedLintReport<OWLObject>(this);
		try {
			Thread.sleep(20000);
			return empty;
		} catch (InterruptedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Could not wait anymore");
			return empty;
		}
	}

	/**
	 * @see org.semanticweb.owl.lint.Lint#getName()
	 */
	public String getName() {
		return "wait20Secs";
	}

	/**
	 * @see org.semanticweb.owl.lint.Lint#getDescription()
	 */
	public String getDescription() {
		return "Dummy lint that just waits for 20 seconds and the returns an empty report";
	}

	public void accept(LintVisitor visitor) {
		visitor.visitGenericLint(this);
	}

	public <P> P accept(LintVisitorEx<P> visitor) {
		return visitor.visitGenericLint(this);
	}

	public LintConfiguration getLintConfiguration() {
		return NonConfigurableLintConfiguration.getInstance();
	}
}
