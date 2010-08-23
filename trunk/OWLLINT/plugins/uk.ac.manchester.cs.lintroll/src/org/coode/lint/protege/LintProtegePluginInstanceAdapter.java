package org.coode.lint.protege;

import java.util.Collection;
import java.util.Formatter;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.PluginProperties;
import org.semanticweb.owlapi.lint.Lint;
import org.semanticweb.owlapi.lint.LintException;
import org.semanticweb.owlapi.lint.LintReport;
import org.semanticweb.owlapi.lint.LintVisitor;
import org.semanticweb.owlapi.lint.LintVisitorEx;
import org.semanticweb.owlapi.lint.configuration.LintConfiguration;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.lint.commons.SimpleMatchBasedLintReport;

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
	 * @see org.semanticweb.owlapi.lint.Lint#detected(java.util.Collection)
	 */
	public LintReport<O> detected(Collection<? extends OWLOntology> targets) throws LintException {
		LintReport<O> detected = this.getDelegate().detected(targets);
		SimpleMatchBasedLintReport<O> toReturn = new SimpleMatchBasedLintReport<O>(this, detected);
		return toReturn;
	}

	/**
	 * @return
	 * @see org.semanticweb.owlapi.lint.Lint#getName()
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
	 * @see org.semanticweb.owlapi.lint.Lint#getDescription()
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

	public boolean isInferenceRequired() {
		return this.delegate.isInferenceRequired();
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

	public String getId() {
		Formatter formatter = new Formatter();
		formatter.format("lint.%s", this.extension.getSimpleIdentifier());
		String string = formatter.out().toString();
		return string;
	}
}
