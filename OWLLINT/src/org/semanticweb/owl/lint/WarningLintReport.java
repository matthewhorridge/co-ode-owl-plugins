/**
 * 
 */
package org.semanticweb.owl.lint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

/**
 * LintReport implementation that allows to report warnings.
 * 
 * @author Luigi Iannone
 * 
 */
public final class WarningLintReport<O extends OWLObject> implements LintReport<O> {
	private final LintReport<O> delegate;
	private final Set<String> warnings = new HashSet<String>();

	/**
	 * @param delegate
	 */
	private WarningLintReport(LintReport<O> report, Collection<? extends String> warnings) {
		assert report != null;
		assert warnings != null;
		assert !warnings.isEmpty();
		this.delegate = report;
		this.warnings.addAll(warnings);
	}

	public static <P extends OWLObject> WarningLintReport<?> buildWarningReport(
			LintReport<P> report, final Collection<? extends String> warnings) {
		if (report == null) {
			throw new NullPointerException("The Lint report cannot be null");
		}
		if (warnings == null) {
			throw new NullPointerException("The warnings cannot be null");
		}
		if (warnings.isEmpty()) {
			throw new IllegalArgumentException("The warnings cannot be empty");
		}
		// If the report is already a warning report the warning should be added
		// together
		WarningLintReport<?> toReturn = report.accept(new DefaultLintReportVisitorExAdapter<WarningLintReport<?>>() {
			@Override
			protected WarningLintReport<?> doDefault(LintReport<?> lintReport) {
				return build(lintReport, warnings);
			}

			@Override
			public WarningLintReport<?> visitWarningLintReport(
					WarningLintReport<?> warningLintReport) {
				Set<String> allWarnings = warningLintReport.getWarnings();
				allWarnings.addAll(warnings);
				return build(warningLintReport, warnings);
			}
		});
		return toReturn;
	}

	private static <P extends OWLObject> WarningLintReport<?> build(LintReport<P> report,
			Collection<? extends String> warnings) {
		assert report != null;
		assert warnings != null;
		return new WarningLintReport<P>(report, warnings);
	}

	/**
	 * @param ontology
	 * @return
	 * @see org.semanticweb.owl.lint.LintReport#getAffectedOWLObjects(org.semanticweb.owl.model.OWLOntology)
	 */
	public Set<O> getAffectedOWLObjects(OWLOntology ontology) {
		return this.delegate.getAffectedOWLObjects(ontology);
	}

	/**
	 * @return
	 * @see org.semanticweb.owl.lint.LintReport#getAffectedOntologies()
	 */
	public Set<OWLOntology> getAffectedOntologies() {
		return this.delegate.getAffectedOntologies();
	}

	/**
	 * @param ontology
	 * @return
	 * @see org.semanticweb.owl.lint.LintReport#isAffected(org.semanticweb.owl.model.OWLOntology)
	 */
	public boolean isAffected(OWLOntology ontology) {
		return this.delegate.isAffected(ontology);
	}

	/**
	 * @return
	 * @see org.semanticweb.owl.lint.LintReport#getLint()
	 */
	public Lint<O> getLint() {
		return this.delegate.getLint();
	}

	/**
	 * @param object
	 * @param affectedOntology
	 * @see org.semanticweb.owl.lint.LintReport#add(org.semanticweb.owl.model.OWLObject,
	 *      org.semanticweb.owl.model.OWLOntology)
	 */
	public void add(O object, OWLOntology affectedOntology) {
		this.delegate.add(object, affectedOntology);
	}

	/**
	 * @param object
	 * @param affectedOntology
	 * @param explanation
	 * @see org.semanticweb.owl.lint.LintReport#add(org.semanticweb.owl.model.OWLObject,
	 *      org.semanticweb.owl.model.OWLOntology, java.lang.String)
	 */
	public void add(O object, OWLOntology affectedOntology, String explanation) {
		this.delegate.add(object, affectedOntology, explanation);
	}

	/**
	 * @param object
	 * @param affectedOntology
	 * @return
	 * @see org.semanticweb.owl.lint.LintReport#getExplanation(org.semanticweb.owl.model.OWLObject,
	 *      org.semanticweb.owl.model.OWLOntology)
	 */
	public String getExplanation(OWLObject object, OWLOntology affectedOntology) {
		return this.delegate.getExplanation(object, affectedOntology);
	}

	/**
	 * @return the warnings
	 */
	public Set<String> getWarnings() {
		return new HashSet<String>(this.warnings);
	}

	public void accept(LintReportVisitor lintReportVisitor) {
		lintReportVisitor.visitWarningLintReport(this);
	}

	public <P> P accept(LintReportVisitorEx<P> lintReportVisitor) {
		return lintReportVisitor.visitWarningLintReport(this);
	}
}
