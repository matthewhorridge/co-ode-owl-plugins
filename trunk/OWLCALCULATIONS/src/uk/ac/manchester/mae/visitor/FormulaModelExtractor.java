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
package uk.ac.manchester.mae.visitor;

import java.net.URI;

import org.coode.oae.ui.BindingModel;
import org.coode.oae.ui.FormulaModel;
import org.coode.oae.ui.PropertyChainModel;
import org.coode.oae.ui.StorageModel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLProperty;

import uk.ac.manchester.mae.ArithmeticsParserVisitor;
import uk.ac.manchester.mae.ConflictStrategyFactory;
import uk.ac.manchester.mae.MAEAdd;
import uk.ac.manchester.mae.MAEBigSum;
import uk.ac.manchester.mae.MAEBinding;
import uk.ac.manchester.mae.MAEConflictStrategy;
import uk.ac.manchester.mae.MAEIdentifier;
import uk.ac.manchester.mae.MAEIntNode;
import uk.ac.manchester.mae.MAEMult;
import uk.ac.manchester.mae.MAEPower;
import uk.ac.manchester.mae.MAEPropertyChain;
import uk.ac.manchester.mae.MAEPropertyFacet;
import uk.ac.manchester.mae.MAEStart;
import uk.ac.manchester.mae.MAEStoreTo;
import uk.ac.manchester.mae.MAEmanSyntaxClassExpression;
import uk.ac.manchester.mae.Node;
import uk.ac.manchester.mae.SimpleNode;
import uk.ac.manchester.mae.visitor.protege.ProtegeDescriptionFacetExtractor;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Apr 10, 2008
 */
public class FormulaModelExtractor implements ArithmeticsParserVisitor {
	protected FormulaModel formulaModel;

	public FormulaModelExtractor(OWLEditorKit owlEditorKit) {
		this.formulaModel = new FormulaModel(owlEditorKit);
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.SimpleNode,
	 *      java.lang.Object)
	 */
	public Object visit(SimpleNode node, Object data) {
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEStart,
	 *      java.lang.Object)
	 */
	public Object visit(MAEStart node, Object data) {
		int childCount = node.jjtGetNumChildren();
		for (int i = 0; i < childCount; i++) {
			Node child = node.jjtGetChild(i);
			child.jjtAccept(this, data);
		}
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEConflictStrategy,
	 *      java.lang.Object)
	 */
	public Object visit(MAEConflictStrategy node, Object data) {
		this.formulaModel.setConflictStrategy(ConflictStrategyFactory
				.getStrategy(node.getStrategyName()));
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEmanSyntaxClassExpression,
	 *      java.lang.Object)
	 */
	public Object visit(MAEmanSyntaxClassExpression node, Object data) {
		OWLEditorKit owlEditorKit = this.formulaModel.getOwlEditorKit();
		try {
			OWLDescription owlExpresion = owlEditorKit.getOWLModelManager()
					.getOWLDescriptionParser().createOWLDescription(
							node.getContent());
			this.formulaModel.setAppliesTo(owlExpresion);
		} catch (OWLExpressionParserException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEBinding,
	 *      java.lang.Object)
	 */
	public Object visit(MAEBinding node, Object data) {
		BindingModel bindingModel = new BindingModel(node.getIdentifier(),
				(PropertyChainModel) node.jjtGetChild(0).jjtAccept(this, data));
		this.formulaModel.getBindings().add(bindingModel);
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEPropertyChain,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object visit(MAEPropertyChain node, Object data) {
		OWLEditorKit owlEditorKit = this.formulaModel.getOwlEditorKit();
		URI propertyURI = URI.create(node.getPropertyName());
		OWLProperty property = node.isEnd() ? owlEditorKit.getOWLModelManager()
				.getOWLDataFactory().getOWLDataProperty(propertyURI)
				: owlEditorKit.getOWLModelManager().getOWLDataFactory()
						.getOWLObjectProperty(propertyURI);
		ProtegeDescriptionFacetExtractor pdfExtractor = new ProtegeDescriptionFacetExtractor(
				this.formulaModel.getOwlEditorKit().getOWLModelManager());
		node.jjtAccept(pdfExtractor, data);
		OWLDescription facet = pdfExtractor.getExtractedDescription();
		PropertyChainModel toReturn = new PropertyChainModel(property, facet,
				this.formulaModel.getOwlEditorKit());
		if (!node.isEnd() && node.jjtGetNumChildren() > 0) {
			boolean found = false;
			for (int i = 0; !found && i < node.jjtGetNumChildren(); i++) {
				Node child = node.jjtGetChild(i);
				if (child instanceof MAEPropertyChain) {
					found = true;
					toReturn.setChild((PropertyChainModel) node.jjtGetChild(i)
							.jjtAccept(this, data));
				}
			}
		}
		return toReturn;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEAdd,
	 *      java.lang.Object)
	 */
	public Object visit(MAEAdd node, Object data) {
		this.formulaModel.setFormulaBody(node.toString() + ";");
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEMult,
	 *      java.lang.Object)
	 */
	public Object visit(MAEMult node, Object data) {
		this.formulaModel.setFormulaBody(node.toString() + ";");
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEPower,
	 *      java.lang.Object)
	 */
	public Object visit(MAEPower node, Object data) {
		this.formulaModel.setFormulaBody(node.toString() + ";");
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEIntNode,
	 *      java.lang.Object)
	 */
	public Object visit(MAEIntNode node, Object data) {
		this.formulaModel.setFormulaBody(node.toString() + ";");
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEIdentifier,
	 *      java.lang.Object)
	 */
	public Object visit(MAEIdentifier node, Object data) {
		this.formulaModel.setFormulaBody(node.toString() + ";");
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEBigSum,
	 *      java.lang.Object)
	 */
	public Object visit(MAEBigSum node, Object data) {
		this.formulaModel.setFormulaBody(node.toString() + ";");
		return null;
	}

	public FormulaModel getExtractedFormulaModel() {
		return this.formulaModel;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEStoreTo,
	 *      java.lang.Object)
	 */
	public Object visit(MAEStoreTo node, Object data) {
		// PropertyChainModel propertyChainModel = (PropertyChainModel) node
		// .jjtGetChild(0).jjtAccept(this, data);
		MAEPropertyChain propertyChain = (MAEPropertyChain) node.jjtGetChild(0);
		if (propertyChain != null) {
			PropertyChainModel propertyChainModel = this
					.toPropertyChainModel(propertyChain);
			StorageModel storageModel = new StorageModel(propertyChainModel);
			this.formulaModel.setStorageModel(storageModel);
		}
		return null;
	}

	private PropertyChainModel toPropertyChainModel(
			MAEPropertyChain propertyChain) {
		String propertyName = propertyChain.getPropertyName();
		ProtegeDescriptionFacetExtractor descriptionFacetExatrctor = new ProtegeDescriptionFacetExtractor(
				this.formulaModel.getOwlEditorKit().getModelManager());
		PropertyChainModel toReturn = new PropertyChainModel(this.formulaModel
				.getOwlEditorKit().getOWLModelManager().getOWLDataFactory()
				.getOWLObjectProperty(URI.create(propertyName)),
				descriptionFacetExatrctor.getExtractedDescription(),
				this.formulaModel.getOwlEditorKit());
		if (!propertyChain.isEnd()) {
			toReturn.setChild(this
					.toPropertyChainModel((MAEPropertyChain) propertyChain
							.jjtGetChild(0)));
		}
		return toReturn;
	}

	public Object visit(MAEPropertyFacet node, Object data) {
		return null;
	}
}
