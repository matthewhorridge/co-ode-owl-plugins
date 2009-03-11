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

import uk.ac.manchester.mae.ArithmeticsParser;
import uk.ac.manchester.mae.ConflictStrategy;
import uk.ac.manchester.mae.MAEBinding;
import uk.ac.manchester.mae.MAEPropertyChain;
import uk.ac.manchester.mae.MAEStart;
import uk.ac.manchester.mae.ParseException;
import uk.ac.manchester.mae.evaluation.BindingModel;
import uk.ac.manchester.mae.evaluation.FormulaModel;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;
import uk.ac.manchester.mae.evaluation.StorageModel;
import uk.ac.manchester.mae.visitor.BindingPropertyChainExtractor;
import uk.ac.manchester.mae.visitor.protege.ProtegeFormulaModelExtractor;

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
			toReturn = new DefaultMutableTreeNode(propertyChain);
			MAEPropertyChain innerPropertyChain = (MAEPropertyChain) propertyChain
					.jjtGetChild(0);
			DefaultMutableTreeNode innerPropertyChainNode = toTreeNode(
					innerPropertyChain, modelManager);
			toReturn.insert(innerPropertyChainNode, 0);
		} else {
			toReturn = new DefaultMutableTreeNode(propertyChain);
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
		PropertyChainModel toReturn = (PropertyChainModel) propertyChainNode
				.getUserObject();
		if (!propertyChainNode.isLeaf()) {
			toReturn
					.setChild(toPropertyChainModel((DefaultMutableTreeNode) propertyChainNode
							.getChildAt(0)));
		}
		return toReturn;
	}

	public static MAEStart toFormula(FormulaModel formulaModel,
			OWLModelManager modelManager) throws ParseException {
		String formulaString = "";
		ConflictStrategy conflictStrategy = formulaModel.getConflictStrategy();
		if (conflictStrategy != null) {
			formulaString += "$" + conflictStrategy.toString() + "$ ";
		}
		OWLDescription appliesTo = formulaModel.getAppliesTo();
		if (appliesTo != null) {
			String rendering = modelManager.getOWLObjectRenderer().render(
					appliesTo, modelManager.getOWLEntityRenderer());
			formulaString += "APPLIESTO <" + rendering + "> ";
		}
		StorageModel storageModel = formulaModel.getStorageModel();
		if (storageModel != null) {
			formulaString += "STORETO <"
					+ storageModel.getPropertyChainModel().toString() + ">";
		}
		Set<BindingModel> bindings = formulaModel.getBindings();
		for (BindingModel bindingModel : bindings) {
			formulaString += "{" + bindingModel.getIdentifier() + "=";
			PropertyChainModel propertyChainModel = bindingModel
					.getPropertyChainModel();
			if (propertyChainModel != null) {
				formulaString += propertyChainModel.toString();
			}
			formulaString += "}";
		}
		if (!bindings.isEmpty()) {
			formulaString += "->";
		}
		formulaString += formulaModel.getFormulaBody();
		ParserFactory.initParser(formulaString, modelManager);
		return (MAEStart) ArithmeticsParser.Start();
	}

	public static FormulaModel toFormulaModel(MAEStart formula, URI formulaURI,
			OWLEditorKit owlEditorKit) {
		ProtegeFormulaModelExtractor fme = new ProtegeFormulaModelExtractor(
				owlEditorKit.getModelManager());
		formula.jjtAccept(fme, null);
		FormulaModel extractedFormulaModel = fme.getExtractedFormulaModel();
		if (extractedFormulaModel != null) {
			extractedFormulaModel.setFormulaURI(formulaURI);
		}
		return extractedFormulaModel;
	}

	public static StorageModel toStorageModel(
			DefaultMutableTreeNode storageSubTreeRoot) {
		return new StorageModel(toPropertyChainModel(storageSubTreeRoot));
	}

	public static MutableTreeNode toTreeNode(StorageModel storageModel,
			OWLModelManager modelManager) {
		DefaultMutableTreeNode toReturn = new DefaultMutableTreeNode("Store to");
		PropertyChainModel propertyChainModel = storageModel
				.getPropertyChainModel();
		if (propertyChainModel != null) {
			toReturn.insert(toTreeNode(propertyChainModel, modelManager), 0);
		}
		return toReturn;
	}

	public static MutableTreeNode toTreeNode(
			PropertyChainModel propertyChainModel, OWLModelManager modelManager) {
		DefaultMutableTreeNode toReturn = new DefaultMutableTreeNode(
				propertyChainModel);
		if (propertyChainModel.getChild() != null) {
			toReturn.insert(toTreeNode(propertyChainModel.getChild(),
					modelManager), 0);
		}
		return toReturn;
	}

	public static MutableTreeNode toTreeNode(BindingModel binding,
			OWLModelManager modelManager) {
		DefaultMutableTreeNode toReturn = new DefaultMutableTreeNode(binding
				.getIdentifier());
		PropertyChainModel propertyChainModel = binding.getPropertyChainModel();
		if (propertyChainModel != null) {
			toReturn.insert(toTreeNode(propertyChainModel, modelManager), 0);
		}
		return toReturn;
	}
}
