package uk.ac.manchester.mae.parser;

public class MAEpropertyChainCell {
	private String propertyName;
	private String facet;
	private String content;

	public MAEpropertyChainCell(String propName, String f) {
		this.propertyName = propName;
		this.facet = f;
	}

	public String getPropertyName() {
		return this.propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getFacet() {
		return this.facet;
	}

	public void setFacet(String facet) {
		this.facet = facet;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		if (this.facet != null) {
			return this.propertyName + "[" + this.facet + "]";
		}
		return this.propertyName;
	}
}
