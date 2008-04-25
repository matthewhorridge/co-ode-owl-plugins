/**
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.coode.oae.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLProperty;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Apr 10, 2008
 */
@SuppressWarnings("unchecked")
public class PropertyChainModel {
	protected OWLProperty property;
	protected PropertyChainModel child = null;
	protected OWLDescription facet = null;
	private OWLEditorKit owlEditorKit;

	/**
	 * @param property
	 * @param index
	 */
	public PropertyChainModel(OWLProperty property, OWLDescription facet,
			OWLEditorKit owlEditorKit) {
		this.property = property;
		this.facet = facet;
		this.owlEditorKit = owlEditorKit;
	}

	/**
	 * @return the property
	 */
	public OWLProperty getProperty() {
		return this.property;
	}

	public PropertyChainModel getChild() {
		return this.child;
	}

	public void setChild(PropertyChainModel childPropertyChainModel) {
		this.child = childPropertyChainModel;
	}

	@Override
	public String toString() {
		String toReturn = "";
		PropertyChainModel propertyChainModel = this;
		toReturn += propertyChainModel.getProperty().getURI().toString();
		if (this.facet != null) {
			toReturn += "["
					+ this.owlEditorKit.getModelManager().getRendering(
							this.facet) + "]";
		}
		boolean endReached = propertyChainModel.getChild() == null;
		while (!endReached) {
			propertyChainModel = propertyChainModel.getChild();
			toReturn += "!"
					+ propertyChainModel.getProperty().getURI().toString();
			endReached = propertyChainModel.getChild() == null;
		}
		return toReturn;
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
}
