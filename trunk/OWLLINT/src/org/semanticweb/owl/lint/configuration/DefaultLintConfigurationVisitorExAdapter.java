package org.semanticweb.owl.lint.configuration;

import uk.ac.manchester.cs.owl.lint.commons.NonConfigurableLintConfiguration;

public abstract class DefaultLintConfigurationVisitorExAdapter<O> implements
		LintConfigurationVisitorEx<O> {
	protected abstract O doDefault(LintConfiguration lintConfiguration);

	public O visitNonConfigurableLintConfiguration(
			NonConfigurableLintConfiguration nonConfigurableLintConfiguration) {
		return this.doDefault(nonConfigurableLintConfiguration);
	}

	public O visitPropertiesBasedLintConfiguration(
			PropertyBasedLintConfiguration propertiesBasedLintConfiguration) {
		return this.doDefault(propertiesBasedLintConfiguration);
	}
}
