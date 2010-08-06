/**
 * 
 */
package uk.ac.manchester.cs.owl.lint.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Luigi Iannone
 * 
 */
public final class SimplePropertyBasedLintConfiguration extends
		AbstractPropertiesBasedLintConfiguration {
	private final Class<?> aClass;
	private final boolean samePackage;

	public SimplePropertyBasedLintConfiguration(Class<?> aClass) {
		this(aClass, true);
	}

	public SimplePropertyBasedLintConfiguration(Class<?> aClass,
			boolean samePackage) {
		if (aClass == null) {
			throw new NullPointerException("The class cannot be null");
		}
		this.aClass = aClass;
		this.samePackage = samePackage;
	}

	@Override
	protected Properties initProperties() {
		Properties properties = new Properties();
		try {
			InputStream in = this.samePackage ? this.aClass
					.getResourceAsStream(this.aClass.getName() + ".properties")
					: this.aClass.getClassLoader().getResourceAsStream(
							this.aClass.getName() + ".properties");
			properties.load(in);
		} catch (IOException e) {
			Formatter formatter = new Formatter();
			formatter.format(
					"Error: %s in loading property file %s for properties of ",
					e.getMessage(), this.aClass);
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					formatter.out().toString());
		}
		return properties;
	}

	public void store() throws IOException {
	}
}
