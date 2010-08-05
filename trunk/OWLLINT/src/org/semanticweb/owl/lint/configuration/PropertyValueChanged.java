/**
 * 
 */
package org.semanticweb.owl.lint.configuration;


/**
 * @author Luigi Iannone
 * 
 */
public final class PropertyValueChanged extends LintConfigurationChangeEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7319339966802294938L;
	private final String propertyName;
	private final String oldValue;
	private final String newValue;

	public PropertyValueChanged(Object source, String propertyName, String oldValue, String newValue) {
		super(source);
		if (propertyName == null) {
			throw new NullPointerException("The property name cannot be null");
		}
		this.propertyName = propertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 * @see org.semanticweb.owl.lint.configuration.LintConfigurationChangeEvent#accept
	 *      (org.semanticweb.owl.lint.configuration.LintConfigurationChangeEventVisitor)
	 */
	@Override
	public void accept(LintConfigurationChangeEventVisitor visitor) {
		visitor.visitPropertyValueChanged(this);
	}

	/**
	 * @see org.semanticweb.owl.lint.configuration.LintConfigurationChangeEvent#accept
	 *      (org.semanticweb.owl.lint.configuration.LintConfigurationChangeEventVisitorEx)
	 */
	@Override
	public <O> O accept(LintConfigurationChangeEventVisitorEx<O> visitor) {
		return visitor.visitPropertyValueChanged(this);
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	/**
	 * @return the oldValue
	 */
	public String getOldValue() {
		return this.oldValue;
	}

	/**
	 * @return the newValue
	 */
	public String getNewValue() {
		return this.newValue;
	}
}
