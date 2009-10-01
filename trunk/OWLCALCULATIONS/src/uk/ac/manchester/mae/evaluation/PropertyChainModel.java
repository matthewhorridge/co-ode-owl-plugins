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
package uk.ac.manchester.mae.evaluation;

import java.util.List;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLProperty;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 10, 2008
 */
@SuppressWarnings("unchecked")
public class PropertyChainModel {
	protected final PropertyChainCell cell;
	protected PropertyChainModel child;

	/**
	 * @param property
	 * @param index
	 */
	public PropertyChainModel(OWLProperty property, OWLDescription facet) {
		this.cell = new PropertyChainCell(property, facet);
	}

	protected PropertyChainModel(List<PropertyChainCell> cells, int i) {
		this.cell = cells.get(i);
		if (i < cells.size() - 1) {
			this.child = new PropertyChainModel(cells, i + 1);
		}
	}

	public PropertyChainModel(List<PropertyChainCell> cells) {
		this(cells, 0);
	}

	/**
	 * @return the property
	 */
	public OWLProperty getProperty() {
		return this.cell.getProperty();
	}

	public OWLDescription getFacet() {
		return this.cell.getFacet();
	}

	/**
	 * @param descriptionFacet
	 */
	public void setFacet(OWLDescription descriptionFacet) {
		this.cell.setFacet(descriptionFacet);
	}

	public PropertyChainModel getChild() {
		return this.child;
	}

	public void setChild(PropertyChainModel childPropertyChainModel) {
		this.child = childPropertyChainModel;
	}

	@Override
	public String toString() {
		String toReturn = this.cell.toString();
		if (this.child != null) {
			toReturn += "!" + this.child.toString();
		}
		return toReturn;
	}

	public PropertyChainCell getCell() {
		return this.cell;
	}

	public String render(OWLModelManager manager) {
		String toReturn = this.cell.render(manager);
		if (this.child != null) {
			toReturn += "!" + this.child.render(manager);
		}
		return toReturn;
	}
}
