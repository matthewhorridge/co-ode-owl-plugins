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
package org.coode.oppl.variablemansyntax.generated;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public class VariableGeneratedValue implements GeneratedValue {
	public enum Attribute {
		RENDERING("RENDERING") {
			@Override
			protected String getValue(OWLObject object) {
				String toReturn = null;
				if (object instanceof OWLEntity) {
					toReturn = entityRenderer.render((OWLEntity) object);
				} else {
					toReturn = object.toString();
				}
				return toReturn;
			}

			@Override
			protected List<String> getValues(Variable variable) {
				List<String> toReturn = new ArrayList<String>();
				Set<OWLObject> possibleBindings = variable
						.getPossibleBindings();
				for (OWLObject object : possibleBindings) {
					if (object instanceof OWLEntity) {
						toReturn.add(entityRenderer.render((OWLEntity) object));
					} else {
						toReturn.add(object.toString());
					}
				}
				return toReturn;
			}
		};
		private final String attribute;
		final static OWLEntityRenderer entityRenderer = OPPLParser
				.getOPPLFactory().getOWLEntityRenderer();

		private Attribute(String s) {
			this.attribute = s;
		}

		/**
		 * @param variable
		 * @return the possible values given the input Variable
		 */
		protected abstract List<String> getValues(Variable variable);

		@Override
		public String toString() {
			return this.attribute;
		}

		public static Attribute parse(String s)
				throws InvalidAttributeException {
			Attribute toReturn = null;
			EnumSet<Attribute> attributes = EnumSet.allOf(Attribute.class);
			Iterator<Attribute> it = attributes.iterator();
			boolean found = false;
			Attribute attribute = null;
			while (!found && it.hasNext()) {
				attribute = it.next();
				found = attribute.toString().compareTo(s) == 0;
			}
			if (found) {
				toReturn = attribute;
			} else {
				throw new InvalidAttributeException(s);
			}
			return toReturn;
		}

		/**
		 * @param object
		 * @return the value resulting from a Variable assuming as value the
		 *         input object
		 */
		protected abstract String getValue(OWLObject object);
	}

	private final Variable variable;
	private final Attribute attribute;

	/**
	 * @param variable
	 * @param attribute
	 */
	public VariableGeneratedValue(Variable variable, Attribute attribute) {
		this.variable = variable;
		this.attribute = attribute;
	}

	/**
	 * @see org.coode.oppl.variablemansyntax.generated.GeneratedValue#getGeneratedValues()
	 */
	public List<String> getGeneratedValues() {
		return this.attribute.getValues(this.variable);
	}

	@Override
	public String toString() {
		return this.variable.getName() + "." + this.attribute;
	}

	public String getGeneratedValue(BindingNode node) {
		OWLObject assignmentValue = node.getAssignmentValue(this.variable);
		String toReturn = null;
		if (assignmentValue != null) {
			toReturn = this.attribute.getValue(assignmentValue);
		}
		return toReturn;
	}
}
