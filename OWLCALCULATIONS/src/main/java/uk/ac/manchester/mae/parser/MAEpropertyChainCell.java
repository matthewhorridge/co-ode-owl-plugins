package uk.ac.manchester.mae.parser;

public class MAEpropertyChainCell {

    private String propertyName;
    private String facet;
    private String content;

    public MAEpropertyChainCell(String propName, String f) {
        propertyName = propName;
        facet = f;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getFacet() {
        return facet;
    }

    public void setFacet(String facet) {
        this.facet = facet;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        if (facet != null) {
            return propertyName + "[" + facet + "]";
        }
        return propertyName;
    }
}
