package org.semanticweb.owl.lint;

public interface LintVisitor {
	public void visitPatternPasedLint(PatternBasedLint<?> lint);

	public void visitActingLint(ActingLint<?> actingLint);

	public void visitGenericLint(Lint<?> lint);
}
