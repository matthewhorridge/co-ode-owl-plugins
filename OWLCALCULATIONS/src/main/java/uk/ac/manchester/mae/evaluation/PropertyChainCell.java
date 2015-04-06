package uk.ac.manchester.mae.evaluation;

import java.io.StringWriter;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxObjectRenderer;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class PropertyChainCell {

    protected final OWLProperty property;
    protected OWLClassExpression facet = null;

    public PropertyChainCell(OWLProperty p, OWLClassExpression f) {
        property = p;
        facet = f;
    }

    /**
     * @return the property
     */
    public OWLProperty getProperty() {
        return property;
    }

    public OWLClassExpression getFacet() {
        return facet;
    }

    /**
     * @param descriptionFacet
     */
    public void setFacet(OWLClassExpression descriptionFacet) {
        facet = descriptionFacet;
    }

    public String render(OWLModelManager manager) {
        StringBuilder toReturn = new StringBuilder(
                manager.getRendering(getProperty()));
        if (facet != null) {
            String rendering = manager.getRendering(facet);
            toReturn.append("[");
            toReturn.append(rendering);
            toReturn.append("]");
        }
        return toReturn.toString();
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder();
        // PropertyChainModel propertyChainModel = this;
        toReturn.append(property.getIRI().toString());
        if (facet != null) {
            StringWriter stringWriter = new StringWriter();
            ManchesterOWLSyntaxObjectRenderer renderer = new ManchesterOWLSyntaxObjectRenderer(
                    stringWriter, new SimpleShortFormProvider());
            facet.accept(renderer);
            String rendering = stringWriter.toString();
            toReturn.append("[");
            toReturn.append(rendering);
            toReturn.append("]");
        }
        return toReturn.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj.getClass().equals(this.getClass())) {
            PropertyChainCell p = (PropertyChainCell) obj;
            return property.equals(p.property)
                    && (facet == null && p.facet == null
                            || facet != null && facet.equals(p.facet));
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (facet != null) {
            return property.hashCode() + facet.hashCode();
        }
        return property.hashCode();
    }
}
