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
package org.coode.oppl.variablemansyntax;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.manchesterowlsyntax.ManchesterOWLSyntax;
import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxTokenizer;
import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxTokenizer.Token;
import org.coode.oppl.syntax.OPPLParser;
import org.semanticweb.owl.expression.OWLEntityChecker;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;

/**
 * @author Luigi Iannone
 * 
 */
public class VariableManchesterOWLSyntaxParser extends
		ManchesterOWLSyntaxEditorParser {
	protected class ManchesterOWLSyntaxVariableTokeizer extends
			ManchesterOWLSyntaxTokenizer {
		public ManchesterOWLSyntaxVariableTokeizer(String s) {
			super(s);
			this.delims.remove('?');
		}
	}

	private ConstraintSystem constraintSystem;
	@SuppressWarnings("unchecked")
	private Map<String, Set<AxiomType>> tokenAxiomTypesMap = new HashMap<String, Set<AxiomType>>();
	private VariableShortFormEntityChecker owlEntityChecker;

	public VariableManchesterOWLSyntaxParser(String s, ConstraintSystem cs) {
		super(cs.getDataFactory(), s);
		this.constraintSystem = cs;
		this.owlEntityChecker = new VariableShortFormEntityChecker(OPPLParser
				.getOPPLFactory().getOWLEntityChecker(), cs);
		super.setOWLEntityChecker(this.owlEntityChecker);
		this.setupTokenAximoTypeMap();
	}

	@Override
	protected ManchesterOWLSyntaxTokenizer getTokenizer(String s) {
		return new ManchesterOWLSyntaxVariableTokeizer(s);
	}

	@SuppressWarnings("unchecked")
	private void setupTokenAximoTypeMap() {
		Set<AxiomType> subClassAxiomTypes = new HashSet<AxiomType>();
		subClassAxiomTypes.add(AxiomType.SUBCLASS);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.SUBCLASS_OF.toString()
				.toLowerCase(), subClassAxiomTypes);
		Set<AxiomType> equivalentToAxiomTypes = new HashSet<AxiomType>();
		equivalentToAxiomTypes.add(AxiomType.EQUIVALENT_CLASSES);
		equivalentToAxiomTypes.add(AxiomType.EQUIVALENT_DATA_PROPERTIES);
		equivalentToAxiomTypes.add(AxiomType.EQUIVALENT_OBJECT_PROPERTIES);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.EQUIVALENT_TO
				.toString().toLowerCase(), equivalentToAxiomTypes);
		Set<AxiomType> disjointWithAxiomTypes = new HashSet<AxiomType>();
		disjointWithAxiomTypes.add(AxiomType.DISJOINT_CLASSES);
		disjointWithAxiomTypes.add(AxiomType.DISJOINT_DATA_PROPERTIES);
		disjointWithAxiomTypes.add(AxiomType.DISJOINT_OBJECT_PROPERTIES);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.DISJOINT_WITH
				.toString().toLowerCase(), disjointWithAxiomTypes);
		Set<AxiomType> antiSymmetricAxiomTypes = new HashSet<AxiomType>();
		antiSymmetricAxiomTypes.add(AxiomType.ANTI_SYMMETRIC_OBJECT_PROPERTY);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.ANTI_SYMMETRIC
				.toString().toLowerCase(), antiSymmetricAxiomTypes);
		Set<AxiomType> functionalAxiomTypes = new HashSet<AxiomType>();
		functionalAxiomTypes.add(AxiomType.FUNCTIONAL_DATA_PROPERTY);
		functionalAxiomTypes.add(AxiomType.FUNCTIONAL_OBJECT_PROPERTY);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.FUNCTIONAL.toString()
				.toLowerCase(), functionalAxiomTypes);
		Set<AxiomType> symmetricAxiomTypes = new HashSet<AxiomType>();
		symmetricAxiomTypes.add(AxiomType.SYMMETRIC_OBJECT_PROPERTY);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.SYMMETRIC.toString()
				.toLowerCase(), symmetricAxiomTypes);
		Set<AxiomType> subPropertyAxiomTypes = new HashSet<AxiomType>();
		subPropertyAxiomTypes.add(AxiomType.SUB_DATA_PROPERTY);
		subPropertyAxiomTypes.add(AxiomType.SUB_OBJECT_PROPERTY);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.SUB_PROPERTY_OF
				.toString().toLowerCase(), subPropertyAxiomTypes);
		Set<AxiomType> domainAxiomTypes = new HashSet<AxiomType>();
		domainAxiomTypes.add(AxiomType.DATA_PROPERTY_DOMAIN);
		domainAxiomTypes.add(AxiomType.OBJECT_PROPERTY_DOMAIN);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.DOMAIN.toString()
				.toLowerCase(), domainAxiomTypes);
		Set<AxiomType> rangeAxiomTypes = new HashSet<AxiomType>();
		rangeAxiomTypes.add(AxiomType.OBJECT_PROPERTY_RANGE);
		rangeAxiomTypes.add(AxiomType.DATA_PROPERTY_RANGE);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.RANGE.toString()
				.toLowerCase(), rangeAxiomTypes);
		Set<AxiomType> inverseFunctionalAxiomTypes = new HashSet<AxiomType>();
		inverseFunctionalAxiomTypes
				.add(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.INVERSE_FUNCTIONAL
				.toString().toLowerCase(), inverseFunctionalAxiomTypes);
		Set<AxiomType> inverseAxiomTypes = new HashSet<AxiomType>();
		inverseAxiomTypes.add(AxiomType.INVERSE_OBJECT_PROPERTIES);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.INVERSE_OF.toString()
				.toLowerCase(), inverseAxiomTypes);
		Set<AxiomType> irreflexiveAxiomTypes = new HashSet<AxiomType>();
		irreflexiveAxiomTypes.add(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.IRREFLEXIVE.toString()
				.toLowerCase(), irreflexiveAxiomTypes);
		Set<AxiomType> reflexiveAxiomTypes = new HashSet<AxiomType>();
		reflexiveAxiomTypes.add(AxiomType.REFLEXIVE_OBJECT_PROPERTY);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.REFLEXIVE.toString()
				.toLowerCase(), reflexiveAxiomTypes);
		Set<AxiomType> transitiveAxiomTypes = new HashSet<AxiomType>();
		transitiveAxiomTypes.add(AxiomType.TRANSITIVE_OBJECT_PROPERTY);
		this.tokenAxiomTypesMap.put(ManchesterOWLSyntax.TRANSITIVE.toString()
				.toLowerCase(), transitiveAxiomTypes);
	}

	@Override
	public OWLConstant parseConstant() throws ParserException {
		OWLConstant toReturn = null;
		Token tok = this.getToken();
		Variable variable = this.constraintSystem.getVariable(tok.getToken());
		if (variable != null
				&& variable.getType().equals(VariableType.CONSTANT)) {
			toReturn = this.getDataFactory().getOWLUntypedConstant(
					variable.getName());
		}
		if (toReturn == null) {
			toReturn = super.parseConstant();
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public OWLAxiom parseAxiom() throws ParserException {
		OWLAxiom axiom = null;
		String tok;
		Set<AxiomType> axiomTypes = new HashSet<AxiomType>();
		Set<AxiomType> tokenAxiomTypes;
		Set<String> generatedElements = new HashSet<String>();
		List<Token> axiomTokens = this.getTokens();
		Iterator<Token> tokenIt = axiomTokens.iterator();
		StringBuilder debugStringBuilder = new StringBuilder();
		while (tokenIt.hasNext()) {
			tok = tokenIt.next().getToken();
			debugStringBuilder.append(tok + " ");
			String lowerCase = tok.toLowerCase();
			tokenAxiomTypes = this.tokenAxiomTypesMap.get(lowerCase);
			if (tokenAxiomTypes != null) {
				axiomTypes.addAll(tokenAxiomTypes);
			} else if (lowerCase.startsWith("!")) {
				generatedElements.add(tok);
			}
		}
		this.reset();
		if (axiomTypes.isEmpty()) {
			axiomTypes.add(AxiomType.CLASS_ASSERTION);
			axiomTypes.add(AxiomType.DATA_PROPERTY_ASSERTION);
			axiomTypes.add(AxiomType.OBJECT_PROPERTY_ASSERTION);
			axiomTypes.add(AxiomType.DATA_PROPERTY_ASSERTION);
			axiomTypes.add(AxiomType.SAME_INDIVIDUAL);
			axiomTypes.add(AxiomType.DIFFERENT_INDIVIDUALS);
			axiomTypes.add(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION);
			axiomTypes.add(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION);
		}
		Iterator<AxiomType> it = axiomTypes.iterator();
		boolean found = false;
		AxiomType axiomType;
		Comparator<ParserException> peComparator = new Comparator<ParserException>() {
			public int compare(ParserException anException,
					ParserException anotherException) {
				return anException.getColumnNumber()
						- anotherException.getColumnNumber();
			}
		};
		Set<ParserException> exceptions = new HashSet<ParserException>();
		while (!found && it.hasNext()) {
			axiomType = it.next();
			this.owlEntityChecker.generate(generatedElements);
			do {
				try {
					this.reset();
					axiom = this.parseAxiom(axiomType);
					found = axiom != null;
					if (!found) {
						this.owlEntityChecker.discard();
					}
				} catch (ParserException e) {
					exceptions.add(e);
					this.owlEntityChecker.discard();
				}
			} while (!found && this.owlEntityChecker.hasAlternatives());
		}
		if (!found) {
			ParserException bestTryExcpetion = exceptions.isEmpty() ? new ParserException(
					this.getToken().getToken(), this.getTokenPos(), this
							.getTokenRow(), this.getTokenCol())
					: Collections.max(exceptions, peComparator);
			throw bestTryExcpetion;
		}
		return axiom;
	}

	/**
	 * @param axiom
	 * @param axiomType
	 * @return
	 * @throws ParserException
	 */
	@SuppressWarnings("unchecked")
	private OWLAxiom parseAxiom(AxiomType axiomType) throws ParserException {
		OWLAxiom axiom = null;
		if (axiomType.equals(AxiomType.SUBCLASS)
				|| axiomType.equals(AxiomType.EQUIVALENT_CLASSES)
				|| axiomType.equals(AxiomType.DISJOINT_CLASSES)) {
			axiom = this.parseClassAxiom();
			if (this.getToken().getToken().compareTo("<EOF>") != 0) {
				this.throwException("<EOF>");
			}
		} else if (axiomType.equals(AxiomType.ANTI_SYMMETRIC_OBJECT_PROPERTY)
				|| axiomType.equals(AxiomType.DISJOINT_OBJECT_PROPERTIES)
				|| axiomType.equals(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)
				|| axiomType.equals(AxiomType.SYMMETRIC_OBJECT_PROPERTY)
				|| axiomType.equals(AxiomType.SUB_OBJECT_PROPERTY)
				|| axiomType.equals(AxiomType.OBJECT_PROPERTY_DOMAIN)
				|| axiomType.equals(AxiomType.OBJECT_PROPERTY_RANGE)
				|| axiomType
						.equals(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY)
				|| axiomType.equals(AxiomType.INVERSE_OBJECT_PROPERTIES)
				|| axiomType.equals(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY)
				|| axiomType.equals(AxiomType.REFLEXIVE_OBJECT_PROPERTY)
				|| axiomType.equals(AxiomType.TRANSITIVE_OBJECT_PROPERTY)
				|| axiomType.equals(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
			axiom = this.parseObjectPropertyAxiom();
			if (this.getToken().getToken().compareTo("<EOF>") != 0) {
				this.throwException("<EOF>");
			}
		} else if (axiomType.equals(AxiomType.DISJOINT_DATA_PROPERTIES)
				|| axiomType.equals(AxiomType.FUNCTIONAL_DATA_PROPERTY)
				|| axiomType.equals(AxiomType.SUB_DATA_PROPERTY)
				|| axiomType.equals(AxiomType.DATA_PROPERTY_DOMAIN)
				|| axiomType.equals(AxiomType.DATA_PROPERTY_RANGE)
				|| axiomType.equals(AxiomType.EQUIVALENT_DATA_PROPERTIES)) {
			axiom = this.parseDataPropertyAxiom();
			if (this.getToken().getToken().compareTo("<EOF>") != 0) {
				this.throwException("<EOF>");
			}
		} else if (axiomType.equals(AxiomType.CLASS_ASSERTION)
				|| axiomType.equals(AxiomType.DATA_PROPERTY_ASSERTION)
				|| axiomType.equals(AxiomType.OBJECT_PROPERTY_ASSERTION)
				|| axiomType.equals(AxiomType.SAME_INDIVIDUAL)
				|| axiomType.equals(AxiomType.DIFFERENT_INDIVIDUALS)
				|| axiomType.equals(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION)
				|| axiomType
						.equals(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION)) {
			axiom = this.parseAssertion();
			if (this.getToken().getToken().compareTo("<EOF>") != 0) {
				this.throwException("<EOF>");
			}
		}
		return axiom;
	}

	@Override
	public OWLObjectPropertyAxiom parseObjectPropertyAxiom()
			throws ParserException {
		String tok = this.getToken().getToken();
		if (tok.compareToIgnoreCase(ManchesterOWLSyntax.FUNCTIONAL.toString()) == 0
				|| tok.compareToIgnoreCase(ManchesterOWLSyntax.FUNCTIONAL
						.toString()
						+ ":") == 0) {
			this.consumeToken();
			String open = this.consumeToken();
			if (!open.equals("(")) {
				this.throwException("(");
			}
			OWLObjectPropertyExpression prop = this
					.parseObjectPropertyExpression();
			String close = this.consumeToken();
			if (!close.equals(")")) {
				this.throwException(")");
			}
			return this.getDataFactory().getOWLFunctionalObjectPropertyAxiom(
					prop);
		} else if (tok.compareToIgnoreCase(ManchesterOWLSyntax.TRANSITIVE
				.toString()) == 0
				|| tok.compareToIgnoreCase(ManchesterOWLSyntax.TRANSITIVE
						.toString()
						+ ":") == 0) {
			this.consumeToken();
			String colon = this.getToken().getToken();
			if (colon.equals(":")) {
				this.consumeToken();
			}
			OWLObjectPropertyExpression prop = this
					.parseObjectPropertyExpression();
			return this.getDataFactory().getOWLTransitiveObjectPropertyAxiom(
					prop);
		} else if (tok.compareToIgnoreCase(ManchesterOWLSyntax.SYMMETRIC
				.toString()) == 0
				|| tok.compareToIgnoreCase(ManchesterOWLSyntax.SYMMETRIC
						.toString()
						+ ":") == 0) {
			this.consumeToken();
			String colon = this.getToken().getToken();
			if (colon.equals(":")) {
				this.consumeToken();
			}
			OWLObjectPropertyExpression prop = this
					.parseObjectPropertyExpression();
			return this.getDataFactory().getOWLSymmetricObjectPropertyAxiom(
					prop);
		} else if (tok.compareToIgnoreCase(ManchesterOWLSyntax.REFLEXIVE
				.toString()) == 0
				|| tok.compareToIgnoreCase(ManchesterOWLSyntax.REFLEXIVE
						.toString()
						+ ":") == 0) {
			this.consumeToken();
			String colon = this.getToken().getToken();
			if (colon.equals(":")) {
				this.consumeToken();
			}
			OWLObjectPropertyExpression prop = this
					.parseObjectPropertyExpression();
			return this.getDataFactory().getOWLReflexiveObjectPropertyAxiom(
					prop);
		} else if (tok.compareToIgnoreCase(ManchesterOWLSyntax.IRREFLEXIVE
				.toString()) == 0
				|| tok.compareToIgnoreCase(ManchesterOWLSyntax.IRREFLEXIVE
						.toString()
						+ ":") == 0) {
			this.consumeToken();
			String colon = this.getToken().getToken();
			if (colon.equals(":")) {
				this.consumeToken();
			}
			OWLObjectPropertyExpression prop = this
					.parseObjectPropertyExpression();
			return this.getDataFactory().getOWLIrreflexiveObjectPropertyAxiom(
					prop);
		} else if (tok.compareToIgnoreCase(ManchesterOWLSyntax.ANTI_SYMMETRIC
				.toString()) == 0
				|| tok.compareToIgnoreCase(ManchesterOWLSyntax.TRANSITIVE
						.toString()
						+ ":") == 0) {
			this.consumeToken();
			String colon = this.getToken().getToken();
			if (colon.equals(":")) {
				this.consumeToken();
			}
			OWLObjectPropertyExpression prop = this
					.parseObjectPropertyExpression();
			return this.getDataFactory()
					.getOWLAntiSymmetricObjectPropertyAxiom(prop);
		} else if (this.isObjectPropertyName(tok)) {
			OWLObjectPropertyExpression property = this
					.parseObjectPropertyExpression();
			tok = this.consumeToken();
			if (tok.compareToIgnoreCase(ManchesterOWLSyntax.EQUIVALENT_TO
					.toString()) == 0) {
				Set<OWLObjectPropertyExpression> equivalentProperties = this
						.parseObjectPropertyList();
				equivalentProperties.add(property);
				return this.getDataFactory()
						.getOWLEquivalentObjectPropertiesAxiom(
								equivalentProperties);
			} else if (tok
					.compareToIgnoreCase(ManchesterOWLSyntax.DISJOINT_WITH
							.toString()) == 0) {
				Set<OWLObjectPropertyExpression> disjointProperties = this
						.parseObjectPropertyList();
				disjointProperties.add(property);
				return this
						.getDataFactory()
						.getOWLDisjointObjectPropertiesAxiom(disjointProperties);
			} else if (tok
					.compareToIgnoreCase(ManchesterOWLSyntax.SUB_PROPERTY_OF
							.toString()) == 0) {
				OWLObjectPropertyExpression superProperty = this
						.parseObjectPropertyExpression();
				return this.getDataFactory().getOWLSubObjectPropertyAxiom(
						property, superProperty);
			} else if (tok.compareToIgnoreCase(ManchesterOWLSyntax.DOMAIN
					.toString()) == 0) {
				OWLDescription domain = this.parseDescription();
				return this.getDataFactory().getOWLObjectPropertyDomainAxiom(
						property, domain);
			} else if (tok.compareToIgnoreCase(ManchesterOWLSyntax.RANGE
					.toString()) == 0) {
				OWLDescription range = this.parseDescription();
				return this.getDataFactory().getOWLObjectPropertyRangeAxiom(
						property, range);
			} else if (tok
					.compareToIgnoreCase(ManchesterOWLSyntax.INVERSE_FUNCTIONAL
							.toString()) == 0) {
				String open = this.consumeToken();
				if (!open.equals("(")) {
					this.throwException("(");
				}
				OWLObjectPropertyExpression prop = this
						.parseObjectPropertyExpression();
				String close = this.consumeToken();
				if (!close.equals(")")) {
					this.throwException(")");
				}
				return this.getDataFactory()
						.getOWLInverseFunctionalObjectPropertyAxiom(prop);
			} else if (tok.compareToIgnoreCase(ManchesterOWLSyntax.INVERSE_OF
					.toString()) == 0) {
				String open = this.consumeToken();
				if (!open.equals("(")) {
					this.throwException("(");
				}
				OWLObjectPropertyExpression prop = this
						.parseObjectPropertyExpression();
				String close = this.consumeToken();
				if (!close.equals(")")) {
					this.throwException(")");
				}
				return this.getDataFactory()
						.getOWLInverseObjectPropertiesAxiom(property, prop);
			} else {
				this.throwException(ManchesterOWLSyntax.EQUIVALENT_TO
						.toString(), ManchesterOWLSyntax.SUB_PROPERTY_OF
						.toString(), ManchesterOWLSyntax.INVERSE.toString(),
						ManchesterOWLSyntax.INVERSE_FUNCTIONAL.toString());
			}
		}
		return null;
	}

	private OWLAxiom parseAssertion() throws ParserException {
		String tok = this.getToken().getToken();
		if (tok.equals(ManchesterOWLSyntax.NOT.toString())) {
			this.consumeToken();
			this.getToken().getToken();
			OWLAxiom negatedTargetAssertion = this.parseAssertion();
			if (negatedTargetAssertion instanceof OWLDataPropertyAssertionAxiom) {
				OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom = (OWLDataPropertyAssertionAxiom) negatedTargetAssertion;
				return this.getDataFactory()
						.getOWLNegativeDataPropertyAssertionAxiom(
								dataPropertyAssertionAxiom.getSubject(),
								dataPropertyAssertionAxiom.getProperty(),
								dataPropertyAssertionAxiom.getObject());
			} else if (negatedTargetAssertion instanceof OWLObjectPropertyAssertionAxiom) {
				OWLObjectPropertyAssertionAxiom objectPropertyAssertionAxiom = (OWLObjectPropertyAssertionAxiom) negatedTargetAssertion;
				return this.getDataFactory()
						.getOWLNegativeObjectPropertyAssertionAxiom(
								objectPropertyAssertionAxiom.getSubject(),
								objectPropertyAssertionAxiom.getProperty(),
								objectPropertyAssertionAxiom.getObject());
			} else {
				this.throwException("object property assertion",
						"data property assertion");
			}
		} else if (this.isIndividualName(tok)) {
			OWLIndividual individual = this.parseIndividual();
			tok = this.getToken().getToken();
			if (tok.equals(ManchesterOWLSyntax.SAME_AS.toString())) {
				Set<OWLIndividual> sameIndividuals = this.parseIndividualList();
				sameIndividuals.add(individual);
				return this.getDataFactory().getOWLSameIndividualsAxiom(
						sameIndividuals);
			} else if (tok
					.equals(ManchesterOWLSyntax.DIFFERENT_FROM.toString())) {
				Set<OWLIndividual> differentIndividuals = this
						.parseIndividualList();
				differentIndividuals.add(individual);
				return this.getDataFactory().getOWLDifferentIndividualsAxiom(
						differentIndividuals);
			} else if (this.isDataPropertyName(tok)) {
				OWLDataPropertyExpression dataProperty = this
						.parseDataProperty();
				OWLConstant filler = this.parseConstant();
				return this.getDataFactory().getOWLDataPropertyAssertionAxiom(
						individual, dataProperty, filler);
			} else if (this.isObjectPropertyName(tok)) {
				OWLObjectPropertyExpression objectProperty = this
						.parseObjectPropertyExpression();
				OWLIndividual filler = this.parseIndividual();
				return this.getDataFactory()
						.getOWLObjectPropertyAssertionAxiom(individual,
								objectProperty, filler);
			} else if (tok.compareToIgnoreCase("InstanceOf") == 0) {
				this.consumeToken();
				OWLDescription description = this.parseDescription();
				return this.getDataFactory().getOWLClassAssertionAxiom(
						individual, description);
			}
		}
		return null;
	}

	@Override
	public OWLDescription parseDescription() throws ParserException {
		OWLDescription desc = this.parseIntersection();
		return desc;
	}

	public OWLAxiom parseDataPropertyAxiom() throws ParserException {
		String tok = this.getToken().getToken();
		if (tok.compareTo(ManchesterOWLSyntax.FUNCTIONAL.toString()) == 0) {
			this.consumeToken();
			String open = this.consumeToken();
			if (!open.equals("(")) {
				this.throwException("(");
			}
			OWLDataPropertyExpression prop = this.parseDataProperty();
			String close = this.consumeToken();
			if (!close.equals(")")) {
				this.throwException(")");
			}
			return this.getDataFactory()
					.getOWLFunctionalDataPropertyAxiom(prop);
		} else if (this.isDataPropertyName(tok)) {
			OWLDataPropertyExpression dataProperty = this
					.getOWLDataProperty(tok);
			tok = this.consumeToken();
			if (tok.equalsIgnoreCase(ManchesterOWLSyntax.SUB_PROPERTY_OF
					.toString())) {
				OWLDataProperty anotherDataProperty = this.parseDataProperty();
				return this.getDataFactory().getOWLSubDataPropertyAxiom(
						dataProperty, anotherDataProperty);
			} else if (tok.equalsIgnoreCase(ManchesterOWLSyntax.DISJOINT_WITH
					.toString())) {
				OWLDataProperty anotherDataProperty = this.parseDataProperty();
				Set<OWLDataPropertyExpression> properties = new HashSet<OWLDataPropertyExpression>();
				properties.add(dataProperty);
				properties.add(anotherDataProperty);
				return this.getDataFactory().getOWLDisjointDataPropertiesAxiom(
						properties);
			} else if (tok.equalsIgnoreCase(ManchesterOWLSyntax.EQUIVALENT_TO
					.toString())) {
				OWLDataProperty anotherDataProperty = this.parseDataProperty();
				Set<OWLDataPropertyExpression> properties = new HashSet<OWLDataPropertyExpression>();
				properties.add(dataProperty);
				properties.add(anotherDataProperty);
				return this.getDataFactory()
						.getOWLEquivalentDataPropertiesAxiom(properties);
			} else if (tok.equalsIgnoreCase(ManchesterOWLSyntax.DOMAIN
					.toString())) {
				OWLDescription domain = this.parseDescription();
				return this.getDataFactory().getOWLDataPropertyDomainAxiom(
						dataProperty, domain);
			} else if (tok.equalsIgnoreCase(ManchesterOWLSyntax.RANGE
					.toString())) {
				OWLDataRange range = this.parseDataRange(true);
				return this.getDataFactory().getOWLDataPropertyRangeAxiom(
						dataProperty, range);
			}
			this.throwException(SUB_PROPERTY_OF, EQUIVALENT_TO, DISJOINT_WITH,
					DOMAIN, RANGE);
		}
		return null;
	}

	@Override
	public void setOWLEntityChecker(OWLEntityChecker owlEntityChecker) {
		this.owlEntityChecker = new VariableShortFormEntityChecker(
				owlEntityChecker, this.constraintSystem);
		super.setOWLEntityChecker(this.owlEntityChecker);
	}
}
