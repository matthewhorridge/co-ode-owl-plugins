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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.manchester.mae.MAEBinding;
import uk.ac.manchester.mae.MAEConflictStrategy;
import uk.ac.manchester.mae.MAEPropertyChain;
import uk.ac.manchester.mae.MAEPropertyFacet;
import uk.ac.manchester.mae.MAEStoreTo;
import uk.ac.manchester.mae.MAEmanSyntaxClassExpression;
import uk.ac.manchester.mae.Node;
import uk.ac.manchester.mae.UnsupportedDataTypeException;
import uk.ac.manchester.mae.visitor.BindingExtractor;
import uk.ac.manchester.mae.visitor.DescriptionFacetExtractor;
import uk.ac.manchester.mae.visitor.FormulaSetupVisitor;

/**
 * @author Luigi Iannone
 * 
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Apr 29, 2008
 */
public class BindingAssigner extends FormulaSetupVisitor {
	protected OWLIndividual startingIndividual;
	protected Set<BindingAssignment> bindingAssignments = new HashSet<BindingAssignment>();
	private OWLOntologyManager ontologyManager;
	private Set<OWLOntology> ontologies;
	private OWLReasoner reasoner;

	/**
	 * @param startingIndividual
	 * @param ontologyManager
	 * @param ontologies
	 * @param reasoner
	 * @param shortFormProvider
	 */
	public BindingAssigner(OWLIndividual startingIndividual,
			OWLOntologyManager ontologyManager, Set<OWLOntology> ontologies,
			OWLReasoner reasoner) {
		this.startingIndividual = startingIndividual;
		this.ontologyManager = ontologyManager;
		this.ontologies = ontologies;
		this.reasoner = reasoner;
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

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEmanSyntaxClassExpression,
	 *      java.lang.Object)
	 */
	public Object visit(MAEmanSyntaxClassExpression node, Object data) {
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEBinding,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object visit(MAEBinding node, Object data) {
		Set<OWLIndividual> chainStartingIndividuals = new HashSet<OWLIndividual>();
		chainStartingIndividuals.add(this.startingIndividual);
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			Node child = node.jjtGetChild(i);
			Collection<? extends Object> values = (Collection<? extends Object>) child
					.jjtAccept(this, chainStartingIndividuals);
			if (values != null && !values.isEmpty()) {
				BindingExtractor be = new BindingExtractor(
						this.ontologyManager, this.ontologies);
				BindingModel bindingModel = (BindingModel) node.jjtAccept(be,
						data);
				BindingAssignment anAssignment = new BindingAssignment(
						bindingModel, values);
				this.bindingAssignments.add(anAssignment);
			}
		}
		return null;
	}

	/**
	 * 
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEPropertyChain,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object visit(MAEPropertyChain node, Object data) {
		List<Object> toReturn = null;
		Set<OWLIndividual> individuals = (Set<OWLIndividual>) data;
		String propertyName = node.getPropertyName();
		DescriptionFacetExtractor descriptionExtractor = new DescriptionFacetExtractor(
				this.ontologyManager, this.ontologies);
		node.jjtAccept(descriptionExtractor, data);
		OWLDescription facetDescription = descriptionExtractor
				.getExtractedDescription() == null ? this.ontologyManager
				.getOWLDataFactory().getOWLThing() : descriptionExtractor
				.getExtractedDescription();
		toReturn = new ArrayList<Object>();
		if (node.isEnd()) {
			for (OWLIndividual individual : individuals) {
				try {
					toReturn.addAll(this.fetch(individual, propertyName, true,
							facetDescription));
				} catch (UnsupportedDataTypeException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (OWLReasonerException e) {
					e.printStackTrace();
				}
			}
		} else {
			for (OWLIndividual individual : individuals) {
				for (int i = 0; i < node.jjtGetNumChildren(); i++) {
					Node child = node.jjtGetChild(i);
					Set<Object> fillers;
					try {
						if (child instanceof MAEPropertyChain) {
							fillers = (Set<Object>) this.fetch(individual,
									propertyName, false, facetDescription);
							toReturn.addAll((List<Object>) child.jjtAccept(
									this, fillers));
						}
					} catch (UnsupportedDataTypeException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					} catch (OWLReasonerException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return toReturn;
	}

	/**
	 * @see uk.ac.manchester.mae.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.MAEPropertyFacet,
	 *      java.lang.Object)
	 */
	public Object visit(MAEPropertyFacet node, Object data) {
		return null;
	}

	private Collection<Object> fetch(OWLIndividual currentIndividual,
			String propertyName, boolean isDatatype,
			OWLDescription facetDescription) throws URISyntaxException,
			UnsupportedDataTypeException, OWLReasonerException {
		Collection<Object> toReturn = null;
		Iterator<OWLOntology> it = this.ontologies.iterator();
		boolean found = false;
		OWLOntology ontology;
		while (!found && it.hasNext()) {
			ontology = it.next();
			if (isDatatype) {
				OWLDataProperty dataProperty = this.ontologyManager
						.getOWLDataFactory().getOWLDataProperty(
								new URI(propertyName));
				Set<OWLConstant> values = currentIndividual
						.getDataPropertyValues(ontology).get(dataProperty);
				toReturn = new ArrayList<Object>();
				if (!(values == null || values.isEmpty())) {
					for (OWLConstant value : values) {
						toReturn.add(this.convertValue(value
								.asOWLTypedConstant()));
					}
				}
			} else {
				OWLObjectProperty objectProperty = this.ontologyManager
						.getOWLDataFactory().getOWLObjectProperty(
								new URI(propertyName));
				Set<OWLIndividual> fillers = currentIndividual
						.getObjectPropertyValues(ontology).get(objectProperty);
				toReturn = new HashSet<Object>();
				if (!(fillers == null || fillers.isEmpty())) {
					for (OWLIndividual filler : fillers) {
						if (this.reasoner.hasType(filler, facetDescription,
								false)) {
							toReturn.add(filler);
						}
					}
				}
			}
		}
		return toReturn;
	}

	private Object convertValue(OWLTypedConstant typedConstant)
			throws UnsupportedDataTypeException {
		OWLDataType type = typedConstant.getDataType();
		Object toReturn = null;
		// Rough conversion big if
		if (type.getURI().equals(XSDVocabulary.INT.getURI())
				|| type.getURI().equals(XSDVocabulary.INTEGER.getURI())
				|| type.getURI().equals(XSDVocabulary.DOUBLE.getURI())
				|| type.getURI().equals(XSDVocabulary.DECIMAL.getURI())
				|| type.getURI().equals(XSDVocabulary.SHORT.getURI())) {
			toReturn = new Double(Double
					.parseDouble(typedConstant.getLiteral()));
		} else {
			throw new UnsupportedDataTypeException(
					"Currently unsuported data type - "
							+ type.getURI().toString());
		}
		return toReturn;
	}

	public Set<BindingAssignment> getBindingAssignments() {
		return this.bindingAssignments;
	}

	@Override
	public String toString() {
		return this.bindingAssignments.toString();
	}
}
