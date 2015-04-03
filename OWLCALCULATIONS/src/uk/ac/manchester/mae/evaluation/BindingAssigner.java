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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

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
    private OWLDataFactory df;
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
        df = ontologyManager.getOWLDataFactory();
		this.ontologies = ontologies;
		this.reasoner = reasoner;
	}

	/**
	 * @see uk.ac.manchester.mae.parser.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.parser.MAEConflictStrategy,
	 *      java.lang.Object)
	 */
	@Override
    public Object visit(MAEConflictStrategy node, Object data) {
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.parser.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.parser.MAEStoreTo,
	 *      java.lang.Object)
	 */
	@Override
    public Object visit(MAEStoreTo node, Object data) {
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.parser.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.parser.MAEmanSyntaxClassExpression,
	 *      java.lang.Object)
	 */
	@Override
    public Object visit(MAEmanSyntaxClassExpression node, Object data) {
		return null;
	}

	/**
	 * @see uk.ac.manchester.mae.parser.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.parser.MAEBinding,
	 *      java.lang.Object)
	 */
	@Override
    @SuppressWarnings("unchecked")
	public Object visit(MAEBinding node, Object data) {
		Set<OWLIndividual> chainStartingIndividuals = new HashSet<OWLIndividual>();
		chainStartingIndividuals.add(startingIndividual);
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			Node child = node.jjtGetChild(i);
			Collection<? extends Object> values = (Collection<? extends Object>) child
					.jjtAccept(this, chainStartingIndividuals);
			if (values != null && !values.isEmpty()) {
				BindingExtractor be = new BindingExtractor(
						ontologyManager, ontologies);
				BindingModel bindingModel = (BindingModel) node.jjtAccept(be,
						data);
				BindingAssignment anAssignment = new BindingAssignment(
						bindingModel, values);
				bindingAssignments.add(anAssignment);
			}
		}
		return null;
	}

    private OWLClassExpression parseFacet(String facet) {
		if (facet == null) {
			return null;
		}
        BidirectionalShortFormProviderAdapter adapter = new BidirectionalShortFormProviderAdapter(
                ontologies,
				new SimpleShortFormProvider());
        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
        parser.setOWLEntityChecker(new ShortFormEntityChecker(adapter));
        parser.setStringToParse(facet);
        return parser.parseClassExpression();
	}

	@Override
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
            OWLClassExpression facetDescription = parseFacet(facet);
			if (facetDescription == null) {
				facetDescription = ontologyManager.getOWLDataFactory()
						.getOWLThing();
			}
            IRI propertyURI = IRI.create(cell.getPropertyName());
				for (OWLIndividual individual : currentIndividuals) {
						newIndividuals.addAll(fetch(individual, propertyURI,
								facetDescription));
				}
			currentIndividuals = newIndividuals;
		}
		MAEpropertyChainCell cell = node.getCells().get(
				node.getCells().size() - 1);
			OWLDataProperty dataProperty = ontologyManager
					.getOWLDataFactory().getOWLDataProperty(
IRI.create(cell.getPropertyName()));
			for (OWLIndividual individual : currentIndividuals) {
				try {
					toReturn.addAll(fetchValues(individual, dataProperty));
				} catch (UnsupportedDataTypeException e) {
					e.printStackTrace();
				}
			}
		return toReturn;
	}

	private Collection<OWLIndividual> fetch(OWLIndividual currentIndividual,
            IRI propertyName, OWLClassExpression facetDescription) {
		Collection<OWLIndividual> toReturn = new HashSet<OWLIndividual>();
		for (OWLOntology ontology : ontologies) {
			OWLObjectProperty objectProperty = ontologyManager
					.getOWLDataFactory().getOWLObjectProperty(propertyName);
            Collection<OWLIndividual> fillers = EntitySearcher
                    .getObjectPropertyValues(currentIndividual, objectProperty,
                            ontology);
				for (OWLIndividual filler : fillers) {
                if (reasoner.isEntailed(df.getOWLClassAssertionAxiom(
                        facetDescription, filler))) {
						toReturn.add(filler);
					}
			}
		}
		return toReturn;
	}

	private Collection<Double> fetchValues(OWLIndividual ind,
			OWLDataProperty dataProperty) throws UnsupportedDataTypeException {
		Collection<Double> toReturn = new ArrayList<Double>();
		for (OWLOntology ontology : ontologies) {
            Collection<OWLLiteral> values = EntitySearcher
                    .getDataPropertyValues(ind, dataProperty, ontology);
            for (OWLLiteral value : values) {
                toReturn.add(convertValue(value));
				}
		}
		return toReturn;
	}

    private static final Set<IRI> supportedTypes = new HashSet<IRI>();
	static {
        supportedTypes.add(XSDVocabulary.INT.getIRI());
        supportedTypes.add(XSDVocabulary.INTEGER.getIRI());
        supportedTypes.add(XSDVocabulary.DOUBLE.getIRI());
        supportedTypes.add(XSDVocabulary.DECIMAL.getIRI());
        supportedTypes.add(XSDVocabulary.SHORT.getIRI());
	}

    private double convertValue(OWLLiteral typedConstant)
			throws UnsupportedDataTypeException {
		// Rough conversion big if
        if (!supportedTypes.contains(typedConstant.getDatatype().getIRI())) {
			throw new UnsupportedDataTypeException(
					"Currently unsuported data type - "
                            + typedConstant.getDatatype());
		}
		return Double.parseDouble(typedConstant.getLiteral());
	}

	public Set<BindingAssignment> getBindingAssignments() {
		return bindingAssignments;
	}

	@Override
	public String toString() {
		return bindingAssignments.toString();
	}
}
