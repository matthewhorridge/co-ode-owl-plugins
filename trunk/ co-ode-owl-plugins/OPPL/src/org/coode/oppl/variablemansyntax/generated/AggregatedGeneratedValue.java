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
public abstract class AggregatedGeneratedValue<N> implements GeneratedValue<N> {
	private static class ValueTreeNode<N> {
		private final List<List<N>> toAssign;
		private final List<N> assigned;

		private ValueTreeNode(List<List<N>> toAssign, List<N> assigned) {
			this.toAssign = toAssign;
			this.assigned = assigned;
		}

		/**
		 * @return the toAssign
		 */
		private List<List<N>> getToAssign() {
			return this.toAssign;
		}

		/**
		 * @return the assigned
		 */
		private List<N> getAssigned() {
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

	private static class ValueTree<N> {
		private final ValueTreeNode<N> rootNode;

		ValueTree(List<GeneratedValue<N>> root) {
			List<List<N>> rootNode2Assign = new ArrayList<List<N>>();
			for (GeneratedValue<N> generatedValue : root) {
				rootNode2Assign.add(generatedValue.getGeneratedValues());
			}
			this.rootNode = new ValueTreeNode(rootNode2Assign,
					new ArrayList<N>());
		}

		private List<ValueTreeNode<N>> getLeaves() {
			List<ValueTreeNode<N>> nodes = new ArrayList<ValueTreeNode<N>>(
					Collections.singleton(this.rootNode));
			boolean allLeaves = this.rootNode.isLeaf();
			while (!allLeaves) {
				for (ValueTreeNode<N> generatedChild : new ArrayList<ValueTreeNode<N>>(
						nodes)) {
					if (!generatedChild.isLeaf()) {
						nodes.remove(generatedChild);
						List<ValueTreeNode<N>> generatedChildren = this
								.generateChildren(generatedChild);
						nodes.addAll(generatedChildren);
					}
					allLeaves = this.allLeaves(nodes);
				}
			}
			return nodes;
		}

		private boolean allLeaves(List<ValueTreeNode<N>> nodes) {
			Iterator<ValueTreeNode<N>> it = nodes.iterator();
			ValueTreeNode<N> aNode;
			boolean allLeaves = true;
			while (allLeaves && it.hasNext()) {
				aNode = it.next();
				allLeaves = aNode.isLeaf();
			}
			return allLeaves;
		}

		private List<ValueTreeNode<N>> generateChildren(ValueTreeNode<N> node) {
			List<ValueTreeNode<N>> toReturn = new ArrayList<ValueTreeNode<N>>();
			if (!node.isLeaf()) {
				List<List<N>> values2Assign = new ArrayList<List<N>>(node
						.getToAssign());
				List<N> head = values2Assign.remove(0);
				for (N value : head) {
					List<List<N>> childUnassignedVariables = new ArrayList<List<N>>(
							values2Assign);
					List<N> assigned = node.getAssigned();
					List<N> childAssignements = new ArrayList<N>(assigned);
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

	private final List<GeneratedValue<N>> values2Aggregate;

	/**
	 * @param values2Aggregate
	 */
	public AggregatedGeneratedValue(List<GeneratedValue<N>> values2Aggregate) {
		this.values2Aggregate = values2Aggregate;
	}

	/**
	 * @see org.coode.oppl.variablemansyntax.generated.GeneratedValue#getGeneratedValues()
	 */
	public List<N> getGeneratedValues() {
		ValueTree<N> tree = new ValueTree(this.values2Aggregate);
		List<ValueTreeNode<N>> leaves = tree.getLeaves();
		List<N> toReturn = new ArrayList<N>();
		for (ValueTreeNode<N> leaf : leaves) {
			List<N> assigned = leaf.getAssigned();
			N aggregation = this.aggregateValues(assigned);
			toReturn.add(aggregation);
		}
		return toReturn;
	}

	/**
	 * @return an aggregation of the values
	 * @see {@link AggregatedGeneratedValue#getValues2Aggregate()}
	 */
	protected abstract N aggregateValues(List<N> values);

	/**
	 * @return the values2Aggregate
	 */
	public List<GeneratedValue<N>> getValues2Aggregate() {
		return this.values2Aggregate;
	}

	public N getGeneratedValue(BindingNode node) {
		List<N> toAggregate = new ArrayList<N>();
		for (GeneratedValue<N> value : this.values2Aggregate) {
			toAggregate.add(value.getGeneratedValue(node));
		}
		return this.aggregateValues(toAggregate);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for (GeneratedValue<N> value2Aggregate : this.getValues2Aggregate()) {
			String aggregator = first ? "" : this.getAggregatorSymbol() + " ";
			buffer.append(aggregator);
			buffer.append(value2Aggregate.toString());
		}
		return buffer.toString();
	}

	protected abstract String getAggregatorSymbol();
}
