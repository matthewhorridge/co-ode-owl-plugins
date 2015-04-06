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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;

import uk.ac.manchester.mae.ConflictStrategy;

/**
 * @author Luigi Iannone The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 10, 2008
 */
public class FormulaModel {

    protected IRI formulaURI;
    protected ConflictStrategy conflictStrategy = null;
    protected OWLClassExpression appliesTo = null;
    protected Set<BindingModel> bindings = new HashSet<>();
    protected String formulaBody = null;
    protected StorageModel storageModel = null;

    /**
     * @return the conflictStrategy
     */
    public ConflictStrategy getConflictStrategy() {
        return conflictStrategy;
    }

    /**
     * @param conflictStrategy
     *        the conflictStrategy to set
     */
    public void setConflictStrategy(ConflictStrategy conflictStrategy) {
        this.conflictStrategy = conflictStrategy;
    }

    /**
     * @return the formulaBody
     */
    public String getFormulaBody() {
        return formulaBody;
    }

    /**
     * @param formulaBody
     *        the formulaBody to set
     */
    public void setFormulaBody(String formulaBody) {
        this.formulaBody = formulaBody;
    }

    /**
     * @return the bindings
     */
    public Set<BindingModel> getBindings() {
        return bindings;
    }

    /**
     * @param bindings
     *        the bindings to set
     */
    public void setBindings(Set<BindingModel> bindings) {
        this.bindings = bindings;
    }

    public OWLClassExpression getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(OWLClassExpression appliesTo) {
        this.appliesTo = appliesTo;
    }

    /**
     * @param storageModel
     */
    public void setStorageModel(StorageModel storageModel) {
        this.storageModel = storageModel;
    }

    /**
     * @return the storageModel
     */
    public StorageModel getStorageModel() {
        return storageModel;
    }

    public IRI getFormulaURI() {
        return formulaURI;
    }

    public void setFormulaURI(IRI formulaURI) {
        this.formulaURI = formulaURI;
    }

    public String render(OWLModelManager manager) {
        StringBuilder toReturn = new StringBuilder();
        if (conflictStrategy != null) {
            toReturn.append("$");
            toReturn.append(conflictStrategy.toString());
            toReturn.append("$ ");
        }
        if (appliesTo != null) {
            String rendering = manager.getOWLObjectRenderer().render(appliesTo);
            toReturn.append("APPLIESTO <");
            toReturn.append(rendering);
            toReturn.append("> ");
        }
        if (storageModel != null) {
            toReturn.append("STORETO <");
            toReturn.append(
                    storageModel.getPropertyChainModel().render(manager));
            toReturn.append(">");
        }
        if (!bindings.isEmpty()) {
            toReturn.append("{");
            List<BindingModel> listedBindings = new ArrayList<>(
                    getBindings());
            for (int i = 0; i < listedBindings.size(); i++) {
                BindingModel bindingModel = listedBindings.get(i);
                toReturn.append(bindingModel.getIdentifier());
                toReturn.append("=");
                PropertyChainModel propertyChainModel = bindingModel
                        .getPropertyChainModel();
                if (propertyChainModel != null) {
                    toReturn.append(propertyChainModel.render(manager));
                }
                if (i < listedBindings.size() - 1) {
                    toReturn.append(",");
                }
            }
            toReturn.append("}->");
        }
        toReturn.append(getFormulaBody());
        return toReturn.toString();
    }
}
