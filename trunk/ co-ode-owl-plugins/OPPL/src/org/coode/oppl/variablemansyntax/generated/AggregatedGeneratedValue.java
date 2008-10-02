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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;

/**
 * Aggregates several GeneratedValue instances into one
 * 
 * 
 * @author Luigi Iannone
 * 
 */
public abstract class AggregatedGeneratedValue implements GeneratedValue {
	private static class ValueTreeNode {
		private final List<List<String>> toAssign;
		private final List<String> assigned;

		private ValueTreeNode(List<List<String>> toAssign, List<String> assigned) {
			this.toAssign = toAssign;
			this.assigned = assigned;
		}

		/**
		 * @return the toAssign
		 */
		private List<List<String>> getToAssign() {
			return this.toAssign;
		}

		/**
		 * @return the assigned
		 */
		private List<String> getAssigned() {
			return this.assigned;
		}

		private boolean isLeaf() {
			return this.toAssign.isEmpty();
		}

		@Override
		public String toString() {
			return "assigned: " + this.assigned.toString() + " to assign: "
					+ this.toAssign;
		}
	}

	private static class ValueTree {
		private final ValueTreeNode rootNode;

		ValueTree(List<GeneratedValue> root) {
			List<List<String>> rootNode2Assign = new ArrayList<List<String>>();
			for (GeneratedValue generatedValue : root) {
				rootNode2Assign.add(generatedValue.getGeneratedValues());
			}
			this.rootNode = new ValueTreeNode(rootNode2Assign,
					new ArrayList<String>());
		}

		private List<ValueTreeNode> getLeaves() {
			List<ValueTreeNode> nodes = new ArrayList<ValueTreeNode>(
					Collections.singleton(this.rootNode));
			boolean allLeaves = this.rootNode.isLeaf();
			while (!allLeaves) {
				for (ValueTreeNode generatedChild : new ArrayList<ValueTreeNode>(
						nodes)) {
					if (!generatedChild.isLeaf()) {
						nodes.remove(generatedChild);
						List<ValueTreeNode> generatedChildren = this
								.generateChildren(generatedChild);
						nodes.addAll(generatedChildren);
					}
					allLeaves = this.allLeaves(nodes);
				}
			}
			return nodes;
		}

		private boolean allLeaves(List<ValueTreeNode> nodes) {
			Iterator<ValueTreeNode> it = nodes.iterator();
			ValueTreeNode aNode;
			boolean allLeaves = true;
			while (allLeaves && it.hasNext()) {
				aNode = it.next();
				allLeaves = aNode.isLeaf();
			}
			return allLeaves;
		}

		private List<ValueTreeNode> generateChildren(ValueTreeNode node) {
			List<ValueTreeNode> toReturn = new ArrayList<ValueTreeNode>();
			if (!node.isLeaf()) {
				List<List<String>> values2Assign = new ArrayList<List<String>>(
						node.getToAssign());
				List<String> head = values2Assign.remove(0);
				for (String value : head) {
					List<List<String>> childUnassignedVariables = new ArrayList<List<String>>(
							values2Assign);
					List<String> assigned = node.getAssigned();
					List<String> childAssignements = new ArrayList<String>(
							assigned);
					childAssignements.add(value);
					toReturn.add(new ValueTreeNode(childUnassignedVariables,
							childAssignements));
				}
			} else {
				toReturn.add(node);
			}
			return toReturn;
		}
	}

	private final List<GeneratedValue> values2Aggregate;

	/**
	 * @param values2Aggregate
	 */
	public AggregatedGeneratedValue(List<GeneratedValue> values2Aggregate) {
		this.values2Aggregate = values2Aggregate;
	}

	/**
	 * @see org.coode.oppl.variablemansyntax.generated.GeneratedValue#getGeneratedValues()
	 */
	public List<String> getGeneratedValues() {
		ValueTree tree = new ValueTree(this.values2Aggregate);
		List<ValueTreeNode> leaves = tree.getLeaves();
		List<String> toReturn = new ArrayList<String>();
		for (ValueTreeNode leaf : leaves) {
			List<String> assigned = leaf.getAssigned();
			String aggregation = this.aggregateValues(assigned);
			toReturn.add(aggregation);
		}
		return toReturn;
	}

	/**
	 * @return an aggregation of the values
	 * @see {@link AggregatedGeneratedValue#getValues2Aggregate()}
	 */
	protected abstract String aggregateValues(List<String> values);

	/**
	 * @return the values2Aggregate
	 */
	public List<GeneratedValue> getValues2Aggregate() {
		return this.values2Aggregate;
	}

	public String getGeneratedValue(BindingNode node) {
		List<String> toAggregate = new ArrayList<String>();
		for (GeneratedValue value : this.values2Aggregate) {
			toAggregate.add(value.getGeneratedValue(node));
		}
		return this.aggregateValues(toAggregate);
	}
}
