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

import java.net.URI;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.coode.oae.utils.ParserFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLProperty;

import uk.ac.manchester.mae.ArithmeticsParser;
import uk.ac.manchester.mae.BindingPropertyChainExtractor;
import uk.ac.manchester.mae.ConflictStrategy;
import uk.ac.manchester.mae.FormulaModelExtractor;
import uk.ac.manchester.mae.MAEBinding;
import uk.ac.manchester.mae.MAEPropertyChain;
import uk.ac.manchester.mae.MAEStart;
import uk.ac.manchester.mae.ParseException;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Apr 7, 2008
 */
public class MAENodeAdapter {
	public static MutableTreeNode toTreeNode(MAEBinding binding,
			OWLModelManager manager) {
		DefaultMutableTreeNode toReturn = new DefaultMutableTreeNode(binding
				.getIdentifier());
		BindingPropertyChainExtractor propertyExtractor = new BindingPropertyChainExtractor(
				binding);
		MAEPropertyChain propertyChain = (MAEPropertyChain) binding.jjtAccept(
				propertyExtractor, null);
		if (propertyChain != null) {
			DefaultMutableTreeNode propertyChainNode = toTreeNode(
					propertyChain, manager);
			toReturn.insert(propertyChainNode, 0);
		}
		return toReturn;
	}

	public static DefaultMutableTreeNode toTreeNode(
			MAEPropertyChain propertyChain, OWLModelManager modelManager) {
		DefaultMutableTreeNode toReturn;
		if (!propertyChain.isEnd()) {
			toReturn = new DefaultMutableTreeNode(modelManager
					.getOWLDataFactory().getOWLObjectProperty(
							URI.create(propertyChain.getPropertyName())));
			MAEPropertyChain innerPropertyChain = (MAEPropertyChain) propertyChain
					.jjtGetChild(0);
			DefaultMutableTreeNode innerPropertyChainNode = toTreeNode(
					innerPropertyChain, modelManager);
			toReturn.insert(innerPropertyChainNode, 0);
		} else {
			toReturn = new DefaultMutableTreeNode(modelManager
					.getOWLDataFactory().getOWLDataProperty(
							URI.create(propertyChain.getPropertyName())));
		}
		return toReturn;
	}

	public static BindingModel toBindingModel(DefaultMutableTreeNode node) {
		String identifier = node.getUserObject().toString();
		BindingModel toReturn = new BindingModel(identifier, null);
		if (!node.isLeaf()) {
			DefaultMutableTreeNode propertyChainNode = (DefaultMutableTreeNode) node
					.getChildAt(0);
			PropertyChainModel propertyChainModel = toPropertyChainModel(propertyChainNode);
			toReturn.setPropertyChainModel(propertyChainModel);
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	private static PropertyChainModel toPropertyChainModel(
			DefaultMutableTreeNode propertyChainNode) {
		PropertyChainModel toReturn = new PropertyChainModel(
				(OWLProperty) propertyChainNode.getUserObject());
		if (!propertyChainNode.isLeaf()) {
			toReturn
					.setChild(toPropertyChainModel((DefaultMutableTreeNode) propertyChainNode
							.getChildAt(0)));
		}
		return toReturn;
	}

	public static MAEStart toFormula(FormulaModel formulaModel)
			throws ParseException {
		String formulaString = "";
		OWLEditorKit owlEditorKit = formulaModel.getOwlEditorKit();
		;
		ConflictStrategy conflictStrategy = formulaModel.getConflictStrategy();
		if (conflictStrategy != null) {
			formulaString += "$" + conflictStrategy.toString() + "$ ";
		}
		OWLDescription appliesTo = formulaModel.getAppliesTo();
		if (appliesTo != null) {
			String rendering = owlEditorKit.getOWLModelManager()
					.getOWLObjectRenderer().render(
							appliesTo,
							owlEditorKit.getOWLModelManager()
									.getOWLEntityRenderer());
			formulaString += "APPLIESTO <" + rendering + "> ";
		}
		Set<BindingModel> bindings = formulaModel.getBindings();
		for (BindingModel bindingModel : bindings) {
			formulaString += "{" + bindingModel.getIdentifier() + "=";
			PropertyChainModel propertyChainModel = bindingModel
					.getPropertyChainModel();
			if (propertyChainModel != null) {
				formulaString += propertyChainModel.getProperty().getURI()
						.toString();
				boolean endReached = propertyChainModel.getChild() == null;
				while (!endReached) {
					propertyChainModel = propertyChainModel.getChild();
					formulaString += "!"
							+ propertyChainModel.getProperty().getURI()
									.toString();
					endReached = propertyChainModel.getChild() == null;
				}
			}
			formulaString += "}";
		}
		formulaString += "->" + formulaModel.getFormulaBody();
		ParserFactory.initParser(formulaString);
		return (MAEStart) ArithmeticsParser.Start();
	}

	public static FormulaModel toFormulaModel(MAEStart formula,
			OWLEditorKit owlEditorKit) {
		FormulaModelExtractor fme = new FormulaModelExtractor(owlEditorKit);
		formula.jjtAccept(fme, null);
		return fme.getExtractedFormulaModel();
	}
}
