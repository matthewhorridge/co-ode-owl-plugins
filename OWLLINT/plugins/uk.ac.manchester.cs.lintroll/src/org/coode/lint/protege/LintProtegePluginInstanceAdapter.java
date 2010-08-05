package org.coode.lint.protege;

import java.util.Collection;

import org.coode.lint.protege.configuration.LintClassBasedLintConfigurationInitializer;
import org.coode.lint.protege.configuration.LintConfigurationInitializer;
import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.PluginProperties;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.lint.LintVisitor;
import org.semanticweb.owl.lint.LintVisitorEx;
import org.semanticweb.owl.lint.configuration.LintConfiguration;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

public class LintProtegePluginInstanceAdapter<O extends OWLObject> implements
		LintProtegePluginInstance<O> {
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
		return this.getDelegate().detected(targets);
	}

	/**
	 * @return
	 * @see org.semanticweb.owl.lint.Lint#getName()
	 */
	public String getName() {
		return PluginProperties.getParameterValue(
				this.getExtension(),
				NAME_PARAM,
				this.delegate.getName());
	}

	@Override
	public String toString() {
		return this.getName();
	}

	/**
	 * @return
	 * @see org.semanticweb.owl.lint.Lint#getDescription()
	 */
	public String getDescription() {
		return this.getDelegate().getDescription();
	}

	@Override
	public boolean equals(Object obj) {
		return this.getDelegate().equals(obj);
	}

	public void accept(LintVisitor visitor) {
		this.getDelegate().accept(visitor);
	}

	public <P> P accept(LintVisitorEx<P> visitor) {
		return this.getDelegate().accept(visitor);
	}

	@Override
	public int hashCode() {
		return this.getDelegate().hashCode();
	}

	public void dispose() throws Exception {
	}

	public void initialise() throws Exception {
	}

	/**
	 * @return the delegate
	 */
	public Lint<O> getDelegate() {
		return this.delegate;
	}

	public LintConfiguration getLintConfiguration() {
		return this.getDelegate().getLintConfiguration();
	}

	/**
	 * @return the extension
	 */
	public IExtension getExtension() {
		return this.extension;
	}

	public Lint<?> getOriginatingLint() {
		return this.getDelegate();
	}

	public LintConfigurationInitializer getLintConfigurationInitializer() {
		return new LintClassBasedLintConfigurationInitializer(this.delegate);
	}
}
