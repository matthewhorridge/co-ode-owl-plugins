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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.mae.MAEAdd;
import uk.ac.manchester.mae.MAEBigSum;
import uk.ac.manchester.mae.MAEIdentifier;
import uk.ac.manchester.mae.MAEIntNode;
import uk.ac.manchester.mae.MAEMult;
import uk.ac.manchester.mae.MAEPower;
import uk.ac.manchester.mae.MAEStart;
import uk.ac.manchester.mae.Node;
import uk.ac.manchester.mae.visitor.FormulaBodyVisitor;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Apr 29, 2008
 */
public class SimpleFormulaEvaluator extends FormulaBodyVisitor {
	protected Set<BindingAssignment> bindingAssignments;
	private EvaluationResult evaluationResults = null;

	/**
	 * @param bindingAssignments
	 */
	public SimpleFormulaEvaluator(Set<BindingAssignment> bindingAssignments) {
		this.bindingAssignments = bindingAssignments;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object visit(MAEStart node, Object data) {
		Object results = null;
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			Node child = node.jjtGetChild(i);
			if (results == null) {
				results = child.jjtAccept(this, data);
			}
		}
		if (results != null && results instanceof Collection) {
			this.evaluationResults = new EvaluationResult(
					(Collection<? extends Object>) results);
		} else if (results != null) {
			List<Object> resultList = new ArrayList<Object>();
			resultList.add(results);
			this.evaluationResults = new EvaluationResult(resultList);
		}
		return results;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEAdd,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object visit(MAEAdd node, Object data) {
		List<Double> firstValues = (List<Double>) node.jjtGetChild(0)
				.jjtAccept(this, data);
		List<Double> secondValues = (List<Double>) node.jjtGetChild(1)
				.jjtAccept(this, data);
		List<Double> result = null;
		if (firstValues == null || secondValues == null) {
			node.setSymbolic(true);
		} else {
			result = new ArrayList<Double>();
			for (Double aValue : firstValues) {
				for (Double anotherValue : secondValues) {
					result.add(node.isSum() ? aValue + anotherValue : aValue
							- anotherValue);
				}
			}
		}
		return result;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEMult,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object visit(MAEMult node, Object data) {
		List<Double> result = null;
		List<Double> firstValues = (List<Double>) node.jjtGetChild(0)
				.jjtAccept(this, data);
		List<Double> secondValues = (List<Double>) node.jjtGetChild(1)
				.jjtAccept(this, data);
		if (firstValues == null || secondValues == null) {
			node.setSymbolic(true);
		} else {
			for (Double aValue : firstValues) {
				for (Double anotherValue : secondValues) {
					result = new ArrayList<Double>();
					if (node.isMultiplication()) {
						result.add(aValue * anotherValue);
					} else if (node.isPercentage()) {
						result.add(aValue * anotherValue / 100);
					} else {
						result.add(aValue / anotherValue);
					}
				}
			}
		}
		return result;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEPower,
	 *      java.lang.Object)
	 */
	public Object visit(MAEPower node, Object data) {
		List<Double> result = null;
		if (node.getBaseIdentifier() != null) {
			Collection<? extends Object> values = this.findAssignement(
					node.getBaseIdentifier()).getValues();
			if (!values.isEmpty()) {
				result = new ArrayList<Double>();
				for (Object value : values) {
					if (value != null) {
						result.add(Math.pow(Double
								.parseDouble(value.toString()), node.getExp()));
					}
				}
			}
		} else {
			result = new ArrayList<Double>();
			result.add(Math.pow(node.getBase(), node.getExp()));
		}
		return result;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEInteger,
	 *      java.lang.Object)
	 */
	public Object visit(MAEIntNode node, Object data) {
		List<Double> toReturn = null;
		if (!node.isSymbolic()) {
			toReturn = new ArrayList<Double>();
			toReturn.add(node.getValue());
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public Object visit(MAEBigSum node, Object data) {
		List<Double> childValues = (List<Double>) node.jjtGetChild(0)
				.jjtAccept(this, data);
		Double result = null;
		if (childValues != null) {
			result = 0d;
			for (Double value : childValues) {
				result += value;
			}
		} else {
			node.setSymbolic(true);
		}
		return result;
	}

	public Object visit(MAEIdentifier node, Object data) {
		Object toReturn = null;
		BindingAssignment bindingAssignment = this.findAssignement(node
				.getIdentifierName());
		if (bindingAssignment != null) {
			toReturn = bindingAssignment.getValues();
		}
		return toReturn;
	}

	private BindingAssignment findAssignement(String identifierName) {
		BindingAssignment toReturn = null;
		boolean found = false;
		Iterator<BindingAssignment> it = this.bindingAssignments.iterator();
		while (!found && it.hasNext()) {
			BindingAssignment aBindingAssignment = it.next();
			found = aBindingAssignment.getBindingModel().getIdentifier()
					.compareTo(identifierName) == 0;
			if (found) {
				toReturn = aBindingAssignment;
			}
		}
		return toReturn;
	}

	public EvaluationResult getEvaluationResults() {
		return this.evaluationResults;
	}
}
