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
 *         Apr 7, 2008
 */
public class BindingPropertyChainExtractor implements ArithmeticsParserVisitor {

    private final MAEBinding binding;

    public BindingPropertyChainExtractor(MAEBinding binding) {
        this.binding = binding;
    }

    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEStart node, Object data) {
        MAEpropertyChainExpression toReturn = null;
        boolean found = false;
        for (int i = 0; !found && i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child.equals(binding)) {
                toReturn = (MAEpropertyChainExpression) child.jjtAccept(this,
                        null);
            }
        }
        return toReturn;
    }

    @Override
    public Object visit(MAEConflictStrategy node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEmanSyntaxClassExpression node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEBinding node, Object data) {
        MAEpropertyChainExpression toReturn = null;
        boolean found = false;
        for (int i = 0; !found && i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            toReturn = (MAEpropertyChainExpression) child.jjtAccept(this, data);
            found = toReturn != null;
        }
        return toReturn;
    }

    @Override
    public Object visit(MAEpropertyChainExpression node, Object data) {
        boolean isChild = false;
        for (int i = 0; i < binding.jjtGetNumChildren(); i++) {
            if (binding.jjtGetChild(i).equals(node)) {
                isChild = true;
            }
        }
        if (isChild) {
            return node;
        }
        return null;
        // return node.jjtGetParent().equals(this.binding) ? node : null;
    }

    @Override
    public Object visit(MAEAdd node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEMult node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEPower node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEIntNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEIdentifier node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEBigSum node, Object data) {
        return null;
    }

    @Override
    public Object visit(MAEStoreTo node, Object data) {
        return null;
    }
}
