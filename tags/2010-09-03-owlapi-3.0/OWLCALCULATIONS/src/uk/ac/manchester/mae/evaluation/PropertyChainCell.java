package uk.ac.manchester.mae.evaluation;

import java.io.StringWriter;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.owl.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;

public class PropertyChainCell {
	@SuppressWarnings("unchecked")
	protected final OWLProperty property;
	protected OWLDescription facet = null;

	@SuppressWarnings("unchecked")
	public PropertyChainCell(OWLProperty p, OWLDescription f) {
		this.property = p;
		this.facet = f;
	}

	/**
	 * @return the property
	 */
	@SuppressWarnings("unchecked")
	public OWLProperty getProperty() {
		return this.property;
	}

	public OWLDescription getFacet() {
		return this.facet;
	}

	/**
	 * @param descriptionFacet
	 */
	public void setFacet(OWLDescription descriptionFacet) {
		this.facet = descriptionFacet;
	}

	public String render(OWLModelManager manager) {
		StringBuilder toReturn = new StringBuilder(manager
				.getRendering(getProperty()));
		if (this.facet != null) {
			String rendering = manager.getRendering(this.facet);
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
		toReturn.append(this.property.getURI().toString());
		if (this.facet != null) {
			StringWriter stringWriter = new StringWriter();
			ManchesterOWLSyntaxObjectRenderer renderer = new ManchesterOWLSyntaxObjectRenderer(
					stringWriter);
			renderer.setShortFormProvider(new SimpleShortFormProvider());
			this.facet.accept(renderer);
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
			return this.property.equals(p.property)
					&& ((this.facet == null && p.facet == null) || (this.facet != null && this.facet
							.equals(p.facet)));
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (this.facet != null) {
			return this.property.hashCode() + this.facet.hashCode();
		}
		return this.property.hashCode();
	}
}
