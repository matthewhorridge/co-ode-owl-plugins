package uk.ac.manchester.cs.owl.lint.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.configuration.LintConfiguration;
import org.semanticweb.owl.lint.configuration.LintConfigurationVisitor;

/**
 * Abstract implementation of a PropertiesBasedLint that provides a default
 * location for properties. i.e.: the file called
 * {@literal <classname>.properties}
 * 
 * @author Luigi Iannone
 * 
 * 
 */
public abstract class AbstractPropertiesBasedLintConfiguration implements LintConfiguration {
	private final Lint<?> lint;

	/**
	 * @param lint
	 */
	public AbstractPropertiesBasedLintConfiguration(Lint<?> lint) {
		if (lint == null) {
			throw new NullPointerException("The lint cannot be null");
		}
		this.lint = lint;
	}

	public Properties getProperties() {
		Properties toReturn = new Properties();
		String fileName = this.getClass().getName() + ".properties";
		InputStream in = this.getLint().getClass().getClassLoader().getResourceAsStream(fileName);
		try {
			toReturn.load(in);
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(
					Level.WARNING,
					"The properties could not be loaded from the file " + fileName);
		}
		return toReturn;
	}

	public void accept(LintConfigurationVisitor visitor) {
		visitor.visitPropertiesBasedLintConfiguration(this);
	}

	/**
	 * @return the lint
	 */
	public Lint<?> getLint() {
		return this.lint;
	}
}
