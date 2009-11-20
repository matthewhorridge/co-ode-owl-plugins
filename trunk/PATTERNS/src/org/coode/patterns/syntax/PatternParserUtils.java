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
package org.coode.patterns.syntax;

import org.coode.oppl.OPPLAbstractFactory;
import org.coode.oppl.entity.OWLEntityCreationException;
import org.coode.oppl.entity.OWLEntityCreationSet;
import org.coode.oppl.entity.OWLEntityFactory;
import org.coode.oppl.utils.ParserFactory;
import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.patterns.PatternException;
import org.coode.patterns.PatternSignature;
import org.semanticweb.owl.expression.OWLEntityChecker;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 *         Sep 29, 2008
 */
public class PatternParserUtils {
	public static String create(OWLOntologyManager manager, String toCreate,
			int i, PatternSignature signature, OPPLAbstractFactory factory)
			throws PatternException, OWLEntityCreationException,
			OWLOntologyChangeException {
		VariableType ithVariableType = signature.getIthVariableType(i);
		OWLEntityFactory entityFactory = factory.getOWLEntityFactory();
		OWLEntity createdEntity = null;
		OWLEntityChecker entityChecker = factory.getOWLEntityChecker();
		OWLEntityCreationSet<? extends OWLEntity> owlCreationSet = null;
		switch (ithVariableType) {
			case CLASS:
				createdEntity = entityChecker.getOWLClass(toCreate);
				if (createdEntity == null) {
					owlCreationSet = entityFactory.createOWLClass(toCreate,
							null);
				}
				break;
			case OBJECTPROPERTY:
				createdEntity = entityChecker.getOWLObjectProperty(toCreate);
				if (createdEntity == null) {
					owlCreationSet = entityFactory.createOWLObjectProperty(
							toCreate, null);
				}
				break;
			case DATAPROPERTY:
				createdEntity = entityChecker.getOWLDataProperty(toCreate);
				if (createdEntity == null) {
					owlCreationSet = entityFactory.createOWLDataProperty(
							toCreate, null);
				}
				break;
			case INDIVIDUAL:
				createdEntity = entityChecker.getOWLIndividual(toCreate);
				if (createdEntity == null) {
					owlCreationSet = entityFactory.createOWLIndividual(
							toCreate, null);
				}
				break;
			default:
				break;
		}
		if (owlCreationSet != null) {
			manager.applyChanges(owlCreationSet.getOntologyChanges());
			createdEntity = owlCreationSet.getOWLEntity();
		}
		return createdEntity != null ? ParserFactory.getInstance()
				.getOPPLFactory().getOWLEntityRenderer(
						signature.getPattern().getConstraintSystem()).render(
						createdEntity) : null;
	}
}
