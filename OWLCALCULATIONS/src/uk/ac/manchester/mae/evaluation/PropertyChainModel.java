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
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLProperty;

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
    public PropertyChainModel(OWLProperty property, OWLClassExpression facet) {
		cell = new PropertyChainCell(property, facet);
	}

	protected PropertyChainModel(List<PropertyChainCell> cells, int i) {
		cell = cells.get(i);
		if (i < cells.size() - 1) {
			child = new PropertyChainModel(cells, i + 1);
		}
	}

	public PropertyChainModel(List<PropertyChainCell> cells) {
		this(cells, 0);
	}

	/**
	 * @return the property
	 */
	public OWLProperty getProperty() {
		return cell.getProperty();
	}

    public OWLClassExpression getFacet() {
		return cell.getFacet();
	}

	/**
	 * @param descriptionFacet
	 */
    public void setFacet(OWLClassExpression descriptionFacet) {
		cell.setFacet(descriptionFacet);
	}

	public PropertyChainModel getChild() {
		return child;
	}

	public void setChild(PropertyChainModel childPropertyChainModel) {
		child = childPropertyChainModel;
	}

	@Override
	public String toString() {
		String toReturn = cell.toString();
		if (child != null) {
			toReturn += " o " + child.toString();
		}
		return toReturn;
	}

	public PropertyChainCell getCell() {
		return cell;
	}

	public String render(OWLModelManager manager) {
		String toReturn = cell.render(manager);
		if (child != null) {
			toReturn += " o " + child.render(manager);
		}
		return toReturn;
	}
}
