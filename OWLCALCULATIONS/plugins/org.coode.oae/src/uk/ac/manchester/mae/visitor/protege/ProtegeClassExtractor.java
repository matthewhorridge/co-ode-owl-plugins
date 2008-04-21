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
package uk.ac.manchester.mae.visitor.protege;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.semanticweb.owl.model.OWLDescription;

import uk.ac.manchester.mae.ArithmeticsParserVisitor;
import uk.ac.manchester.mae.MAEAdd;
import uk.ac.manchester.mae.MAEBigSum;
import uk.ac.manchester.mae.MAEBinding;
import uk.ac.manchester.mae.MAEConflictStrategy;
import uk.ac.manchester.mae.MAEIdentifier;
import uk.ac.manchester.mae.MAEIntNode;
import uk.ac.manchester.mae.MAEMult;
import uk.ac.manchester.mae.MAEPower;
import uk.ac.manchester.mae.MAEPropertyChain;
import uk.ac.manchester.mae.MAEStart;
import uk.ac.manchester.mae.MAEStoreTo;
import uk.ac.manchester.mae.MAEmanSyntaxClassExpression;
import uk.ac.manchester.mae.Node;
import uk.ac.manchester.mae.SimpleNode;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Mar 12, 2008
 */
public class ProtegeClassExtractor implements ArithmeticsParserVisitor {
	private OWLModelManager manager;

	/**
	 * @param ontologies
	 * @param shortFormProvider
	 * @param manager
	 */
	public ProtegeClassExtractor(OWLModelManager manager) {
		this.manager = manager;
	}

	public Object visit(SimpleNode node, Object data) {
		return null;
	}

	public Object visit(MAEStart node, Object data) {
		boolean assigned = false;
		Object toReturn = null;
		for (int i = 0; i < node.jjtGetNumChildren() && !assigned; i++) {
			Node child = node.jjtGetChild(i);
			toReturn = child.jjtAccept(this, data);
			assigned = toReturn != null;
		}
		return toReturn;
	}

	public Object visit(MAEmanSyntaxClassExpression node, Object data) {
		OWLDescription classDescription;
		try {
			classDescription = this.manager.getOWLDescriptionParser()
					.createOWLDescription(node.getContent());
			data = classDescription;
		} catch (OWLExpressionParserException e) {
			e.printStackTrace();
			return null;
		}
		return data;
	}

	public Object visit(MAEBinding node, Object data) {
		Object toReturn = data;
		if (data == null) {
			data = this.manager.getOWLDataFactory().getOWLThing();
			toReturn = data;
		}
		return toReturn;
	}

	public Object visit(MAEPropertyChain node, Object data) {
		Object toReturn = data;
		if (data == null) {
			data = this.manager.getOWLDataFactory().getOWLThing();
			toReturn = data;
		}
		return toReturn;
	}

	public Object visit(MAEAdd node, Object data) {
		Object toReturn = data;
		if (data == null) {
			data = this.manager.getOWLDataFactory().getOWLThing();
			toReturn = data;
		}
		return toReturn;
	}

	public Object visit(MAEMult node, Object data) {
		Object toReturn = data;
		if (data == null) {
			data = this.manager.getOWLDataFactory().getOWLThing();
			toReturn = data;
		}
		return toReturn;
	}

	public Object visit(MAEPower node, Object data) {
		Object toReturn = data;
		if (data == null) {
			data = this.manager.getOWLDataFactory().getOWLThing();
			toReturn = data;
		}
		return toReturn;
	}

	public Object visit(MAEIntNode node, Object data) {
		Object toReturn = data;
		if (data == null) {
			data = this.manager.getOWLDataFactory().getOWLThing();
			toReturn = data;
		}
		return toReturn;
	}

	public Object visit(MAEIdentifier node, Object data) {
		Object toReturn = data;
		if (data == null) {
			data = this.manager.getOWLDataFactory().getOWLThing();
			toReturn = data;
		}
		return toReturn;
	}

	public Object visit(MAEBigSum node, Object data) {
		Object toReturn = data;
		if (data == null) {
			data = this.manager.getOWLDataFactory().getOWLThing();
			toReturn = data;
		}
		return toReturn;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEConflictStrategy,
	 *      java.lang.Object)
	 */
	public Object visit(MAEConflictStrategy node, Object data) {
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEStoreTo,
	 *      java.lang.Object)
	 */
	public Object visit(MAEStoreTo node, Object data) {
		return null;
	}
}
