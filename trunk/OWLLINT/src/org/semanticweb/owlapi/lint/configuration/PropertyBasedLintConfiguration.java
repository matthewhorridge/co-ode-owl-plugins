package org.semanticweb.owlapi.lint.configuration;

import java.io.IOException;
import java.util.Properties;

public interface PropertyBasedLintConfiguration extends LintConfiguration {
	/**
	 * Retrieves the Properties upon which this LintConfiguration is based.
	 * 
	 * @return an instance of Properties.
	 */
	public Properties getProperties();

	/**
	 * Stores the current configuration.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs during the storage.
	 */
	public void store() throws IOException;
}
