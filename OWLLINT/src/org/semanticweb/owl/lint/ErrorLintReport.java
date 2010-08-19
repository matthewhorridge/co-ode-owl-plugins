package org.semanticweb.owl.lint;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

/**
 * This class should be used to report execution errors in a Lint
 * 
 * @author Luigi Iannone
 * 
 * @param <O>
 */
public final class ErrorLintReport<O extends OWLObject> implements LintReport<O> {
	private final Lint<O> lint;
	private final Throwable throwable;

	/**
	 * @param lint
	 * @param throwable
	 */
	private ErrorLintReport(Lint<O> lint, Throwable throwable) {
		if (lint == null) {
			throw new NullPointerException("The Lint cannot be null");
		}
		if (throwable == null) {
			throw new NullPointerException("The throwable cannot be null");
		}
		this.lint = lint;
		this.throwable = throwable;
	}

	public Set<O> getAffectedOWLObjects(OWLOntology ontology) {
		return Collections.emptySet();
	}

	public Set<OWLOntology> getAffectedOntologies() {
		return Collections.emptySet();
	}

	public boolean isAffected(OWLOntology ontology) {
		return false;
	}

	public Lint<O> getLint() {
		return this.lint;
	}

	public void add(O object, OWLOntology affectedOntology) {
	}

	public void add(O object, OWLOntology affectedOntology, String explanation) {
	}

	public String getExplanation(OWLObject object, OWLOntology affectedOntology) {
		return LintReport.NO_EXPLANATION_GIVEN;
	}

	public void accept(LintReportVisitor lintReportVisitor) {
		lintReportVisitor.visitErrorLintReport(this);
	}

	public <P> P accept(LintReportVisitorEx<P> lintReportVisitor) {
		return lintReportVisitor.visitErrorLintReport(this);
	}

	/**
	 * @return the throwable
	 */
	public Throwable getThrowable() {
		return this.throwable;
	}

	public static <P extends OWLObject> LintReport<P> buildErrorReport(Lint<P> lint, Throwable t) {
		return new ErrorLintReport<P>(lint, t);
	}
}
