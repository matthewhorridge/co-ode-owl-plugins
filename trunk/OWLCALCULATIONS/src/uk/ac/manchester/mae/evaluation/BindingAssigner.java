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
import java.util.List;
import java.util.Set;

import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxDescriptionParser;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.expression.ShortFormEntityChecker;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owl.util.OWLEntitySetProvider;
import org.semanticweb.owl.util.ReferencedEntitySetProvider;
import org.semanticweb.owl.util.SimpleShortFormProvider;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.manchester.mae.UnsupportedDataTypeException;
import uk.ac.manchester.mae.parser.MAEBinding;
import uk.ac.manchester.mae.parser.MAEConflictStrategy;
import uk.ac.manchester.mae.parser.MAEStoreTo;
import uk.ac.manchester.mae.parser.MAEmanSyntaxClassExpression;
import uk.ac.manchester.mae.parser.MAEpropertyChainCell;
import uk.ac.manchester.mae.parser.MAEpropertyChainExpression;
import uk.ac.manchester.mae.parser.Node;
import uk.ac.manchester.mae.visitor.BindingExtractor;
import uk.ac.manchester.mae.visitor.FormulaSetupVisitor;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Apr 29, 2008
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
	 * @see uk.ac.manchester.mae.parser.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.parser.MAEConflictStrategy,
	 *      java.lang.Object)
	 */
	public Object visit(MAEConflictStrategy node, Object data) {
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.parser.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.parser.MAEStoreTo,
	 *      java.lang.Object)
	 */
	public Object visit(MAEStoreTo node, Object data) {
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.parser.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.parser.MAEmanSyntaxClassExpression,
	 *      java.lang.Object)
	 */
	public Object visit(MAEmanSyntaxClassExpression node, Object data) {
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.parser.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.parser.MAEBinding,
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

	private OWLDescription parseFacet(String facet) {
		if (facet == null) {
			return null;
		}
		BidirectionalShortFormProviderAdapter adapter = new BidirectionalShortFormProviderAdapter(
				new SimpleShortFormProvider());
		OWLEntitySetProvider<OWLEntity> owlEntitySetProvider = new ReferencedEntitySetProvider(
				this.ontologies);
		adapter.rebuild(owlEntitySetProvider);
		ManchesterOWLSyntaxDescriptionParser parser = new ManchesterOWLSyntaxDescriptionParser(
				this.ontologyManager.getOWLDataFactory(),
				new ShortFormEntityChecker(adapter));
		try {
			return parser.parse(facet);
		} catch (ParserException e) {
			return null;
		}
	}

	/**
	 * 
	 * @see uk.ac.manchester.mae.parser.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.parser.MAEPropertyChain,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object visit(MAEpropertyChainExpression node, Object data) {
		// XXX I have my doubts this method actually gets called
		// XXX bogus behaviour: only the first property and first facet get used
		List<Object> toReturn = new ArrayList<Object>();
		Set<OWLIndividual> currentIndividuals = (Set<OWLIndividual>) data;
		for (int index = 0; index < node.getCells().size() - 1; index++) {
			// all the cells explored here contain objectproperties
			MAEpropertyChainCell cell = node.getCells().get(index);
			Set<OWLIndividual> newIndividuals = new HashSet<OWLIndividual>();
			String facet = cell.getFacet();
			OWLDescription facetDescription = parseFacet(facet);
			if (facetDescription == null) {
				facetDescription = this.ontologyManager.getOWLDataFactory()
						.getOWLThing();
			}
			URI propertyURI;
			try {
				propertyURI = new URI(cell.getPropertyName());
				for (OWLIndividual individual : currentIndividuals) {
					try {
						newIndividuals.addAll(fetch(individual, propertyURI,
								facetDescription));
					} catch (OWLReasonerException e) {
						e.printStackTrace();
					}
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			currentIndividuals = newIndividuals;
		}
		MAEpropertyChainCell cell = node.getCells().get(
				node.getCells().size() - 1);
		try {
			OWLDataProperty dataProperty = this.ontologyManager
					.getOWLDataFactory().getOWLDataProperty(
							new URI(cell.getPropertyName()));
			for (OWLIndividual individual : currentIndividuals) {
				try {
					toReturn.addAll(fetchValues(individual, dataProperty));
				} catch (UnsupportedDataTypeException e) {
					e.printStackTrace();
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	private Collection<OWLIndividual> fetch(OWLIndividual currentIndividual,
			URI propertyName, OWLDescription facetDescription)
			throws OWLReasonerException {
		Collection<OWLIndividual> toReturn = new HashSet<OWLIndividual>();
		for (OWLOntology ontology : this.ontologies) {
			OWLObjectProperty objectProperty = this.ontologyManager
					.getOWLDataFactory().getOWLObjectProperty(propertyName);
			Set<OWLIndividual> fillers = currentIndividual
					.getObjectPropertyValues(ontology).get(objectProperty);
			if (!(fillers == null || fillers.isEmpty())) {
				for (OWLIndividual filler : fillers) {
					if (this.reasoner.hasType(filler, facetDescription, false)) {
						toReturn.add(filler);
					}
				}
			}
		}
		return toReturn;
	}

	private Collection<Double> fetchValues(OWLIndividual ind,
			OWLDataProperty dataProperty) throws UnsupportedDataTypeException {
		Collection<Double> toReturn = new ArrayList<Double>();
		for (OWLOntology ontology : this.ontologies) {
			Set<OWLConstant> values = ind.getDataPropertyValues(ontology).get(
					dataProperty);
			if (values != null) {
				for (OWLConstant value : values) {
					toReturn.add(convertValue(value.asOWLTypedConstant()));
				}
			}
		}
		return toReturn;
	}

	private static final Set<URI> supportedTypes = new HashSet<URI>();
	static {
		supportedTypes.add(XSDVocabulary.INT.getURI());
		supportedTypes.add(XSDVocabulary.INTEGER.getURI());
		supportedTypes.add(XSDVocabulary.DOUBLE.getURI());
		supportedTypes.add(XSDVocabulary.DECIMAL.getURI());
		supportedTypes.add(XSDVocabulary.SHORT.getURI());
	}

	private double convertValue(OWLTypedConstant typedConstant)
			throws UnsupportedDataTypeException {
		// Rough conversion big if
		if (!supportedTypes.contains(typedConstant.getDataType().getURI())) {
			throw new UnsupportedDataTypeException(
					"Currently unsuported data type - "
							+ typedConstant.getDataType().getURI().toString());
		}
		return Double.parseDouble(typedConstant.getLiteral());
	}

	public Set<BindingAssignment> getBindingAssignments() {
		return this.bindingAssignments;
	}

	@Override
	public String toString() {
		return this.bindingAssignments.toString();
	}
}
