package org.coode.oppl.lint.protege;

import java.util.Collection;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.PluginProperties;
import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.lint.LintVisitor;
import org.semanticweb.owl.lint.LintVisitorEx;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

public final class LintProtegePluginInstanceAdapter<O extends OWLObject> implements
		ProtegePluginInstance, Lint<O> {
	private static final String NAME_PARAM = "label";
	private final Lint<O> delegate;
	private final IExtension extension;

	/**
	 * @param delegate
	 */
	private LintProtegePluginInstanceAdapter(Lint<O> delegate, IExtension extension) {
		if (delegate == null) {
			throw new NullPointerException("The lint cannot be null");
		}
		if (extension == null) {
			throw new NullPointerException("The extension cannot be null");
		}
		this.delegate = delegate;
		this.extension = extension;
	}

	public static <P extends OWLObject> LintProtegePluginInstanceAdapter<P> buildLintProtegePluginInstanceAdapter(
			Lint<P> delegate, IExtension extension) {
		return new LintProtegePluginInstanceAdapter<P>(delegate, extension);
	}

	/**
	 * @param targets
	 * @return
	 * @throws LintException
	 * @see org.semanticweb.owl.lint.Lint#detected(java.util.Collection)
	 */
	public LintReport<O> detected(Collection<? extends OWLOntology> targets) throws LintException {
		return this.delegate.detected(targets);
	}

	/**
	 * @return
	 * @see org.semanticweb.owl.lint.Lint#getName()
	 */
	public String getName() {
		return PluginProperties.getParameterValue(
				this.extension,
				NAME_PARAM,
				this.delegate.getName());
	}

	/**
	 * @return
	 * @see org.semanticweb.owl.lint.Lint#getDescription()
	 */
	public String getDescription() {
		return this.delegate.getDescription();
	}

	@Override
	public boolean equals(Object obj) {
		return this.delegate.equals(obj);
	}

	public void accept(LintVisitor visitor) {
		this.delegate.accept(visitor);
	}

	public <P> P accept(LintVisitorEx<P> visitor) {
		return this.delegate.accept(visitor);
	}

	@Override
	public int hashCode() {
		return this.delegate.hashCode();
	}

	public void dispose() throws Exception {
	}

	public void initialise() throws Exception {
	}
}
