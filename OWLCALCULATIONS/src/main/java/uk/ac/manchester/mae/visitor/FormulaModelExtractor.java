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

import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import uk.ac.manchester.mae.ConflictStrategyFactory;
import uk.ac.manchester.mae.evaluation.BindingModel;
import uk.ac.manchester.mae.evaluation.FormulaModel;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;
import uk.ac.manchester.mae.evaluation.StorageModel;
import uk.ac.manchester.mae.parser.ArithmeticsParserVisitor;
import uk.ac.manchester.mae.parser.MAEAdd;
import uk.ac.manchester.mae.parser.MAEBigSum;
import uk.ac.manchester.mae.parser.MAEBinding;
import uk.ac.manchester.mae.parser.MAEConflictStrategy;
import uk.ac.manchester.mae.parser.MAEIdentifier;
import uk.ac.manchester.mae.parser.MAEIntNode;
import uk.ac.manchester.mae.parser.MAEMult;
import uk.ac.manchester.mae.parser.MAEPower;
import uk.ac.manchester.mae.parser.MAEStart;
import uk.ac.manchester.mae.parser.MAEStoreTo;
import uk.ac.manchester.mae.parser.MAEmanSyntaxClassExpression;
import uk.ac.manchester.mae.parser.MAEpropertyChainExpression;
import uk.ac.manchester.mae.parser.Node;
import uk.ac.manchester.mae.parser.SimpleNode;

/**
 * @author Luigi Iannone The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 10, 2008
 */
public class FormulaModelExtractor implements ArithmeticsParserVisitor {

    protected FormulaModel formulaModel;
    private final OWLOntologyManager manager;
    private final Set<OWLOntology> ontologies;

    public FormulaModelExtractor(OWLOntologyManager manager,
            Set<OWLOntology> ontologies) {
        this.manager = manager;
        this.ontologies = ontologies;
        formulaModel = new FormulaModel();
    }

    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEStart node, Object data) {
        int childCount = node.jjtGetNumChildren();
        for (int i = 0; i < childCount; i++) {
            Node child = node.jjtGetChild(i);
            child.jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(MAEConflictStrategy node, Object data) {
        formulaModel.setConflictStrategy(
                ConflictStrategyFactory.getStrategy(node.getStrategyName()));
        return null;
    }

    @Override
    public Object visit(MAEmanSyntaxClassExpression node, Object data) {
        BidirectionalShortFormProviderAdapter adapter = new BidirectionalShortFormProviderAdapter(
                ontologies, new SimpleShortFormProvider());
        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
        parser.setOWLEntityChecker(new ShortFormEntityChecker(adapter));
        parser.setStringToParse(node.getContent());
        OWLClassExpression owlExpresion = parser.parseClassExpression();
        formulaModel.setAppliesTo(owlExpresion);
        return owlExpresion;
    }

    @Override
    public Object visit(MAEBinding node, Object data) {
        BindingModel bindingModel = new BindingModel(node.getIdentifier(),
                (PropertyChainModel) node.jjtGetChild(0).jjtAccept(this, data));
        formulaModel.getBindings().add(bindingModel);
        return null;
    }

    @Override
    public Object visit(MAEpropertyChainExpression node, Object data) {
        return MAEAdapter.toPropertyChainModel(node, ontologies, manager);
    }

    @Override
    public Object visit(MAEAdd node, Object data) {
        formulaModel.setFormulaBody(node.toString() + ";");
        return null;
    }

    @Override
    public Object visit(MAEMult node, Object data) {
        formulaModel.setFormulaBody(node.toString() + ";");
        return null;
    }

    @Override
    public Object visit(MAEPower node, Object data) {
        formulaModel.setFormulaBody(node.toString() + ";");
        return null;
    }

    @Override
    public Object visit(MAEIntNode node, Object data) {
        formulaModel.setFormulaBody(node.toString() + ";");
        return null;
    }

    @Override
    public Object visit(MAEIdentifier node, Object data) {
        formulaModel.setFormulaBody(node.toString() + ";");
        return null;
    }

    @Override
    public Object visit(MAEBigSum node, Object data) {
        formulaModel.setFormulaBody(node.toString() + ";");
        return null;
    }

    public FormulaModel getExtractedFormulaModel() {
        return formulaModel;
    }

    @Override
    public Object visit(MAEStoreTo node, Object data) {
        // PropertyChainModel propertyChainModel = (PropertyChainModel) node
        // .jjtGetChild(0).jjtAccept(this, data);
        MAEpropertyChainExpression propertyChain = (MAEpropertyChainExpression) node
                .jjtGetChild(0);
        if (propertyChain != null) {
            PropertyChainModel propertyChainModel = MAEAdapter
                    .toPropertyChainModel(propertyChain, ontologies, manager);
            StorageModel storageModel = new StorageModel(propertyChainModel);
            formulaModel.setStorageModel(storageModel);
        }
        return null;
    }
}
