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

import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxDescriptionParser;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.expression.ShortFormEntityChecker;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owl.util.OWLEntitySetProvider;
import org.semanticweb.owl.util.ReferencedEntitySetProvider;
import org.semanticweb.owl.util.SimpleShortFormProvider;

import uk.ac.manchester.mae.parser.MAEBinding;
import uk.ac.manchester.mae.parser.MAEConflictStrategy;
import uk.ac.manchester.mae.parser.MAEStoreTo;
import uk.ac.manchester.mae.parser.MAEmanSyntaxClassExpression;
import uk.ac.manchester.mae.parser.MAEpropertyChainExpression;
import uk.ac.manchester.mae.parser.SimpleNode;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Mar 12, 2008
 */
public class ClassExtractor extends FormulaSetupVisitor {
	private OWLOntologyManager manager;
	private Set<OWLOntology> ontologies;
	private OWLDescription classDescription = null;

	/**
	 * @param ontologies
	 * @param shortFormProvider
	 * @param manager
	 */
	public ClassExtractor(Set<OWLOntology> ontologies,
			OWLOntologyManager manager) {
		this.ontologies = ontologies;
		this.manager = manager;
	}

	@Override
	public Object visit(SimpleNode node, Object data) {
		return null;
	}

	public Object visit(MAEmanSyntaxClassExpression node, Object data) {
		BidirectionalShortFormProviderAdapter adapter = new BidirectionalShortFormProviderAdapter(
				new SimpleShortFormProvider());
		OWLEntitySetProvider<OWLEntity> owlEntitySetProvider = new ReferencedEntitySetProvider(
				this.ontologies);
		adapter.rebuild(owlEntitySetProvider);
		ManchesterOWLSyntaxDescriptionParser parser = new ManchesterOWLSyntaxDescriptionParser(
				this.manager.getOWLDataFactory(), new ShortFormEntityChecker(
						adapter));
		try {
			this.classDescription = parser.parse(node.getContent());
			data = this.classDescription;
			return data;
		} catch (ParserException e) {
			return null;
		}
	}

	public Object visit(MAEBinding node, Object data) {
		Object toReturn = data;
		if (data == null) {
			data = this.manager.getOWLDataFactory().getOWLThing();
			toReturn = data;
		}
		return toReturn;
	}

	public Object visit(MAEpropertyChainExpression node, Object data) {
		Object toReturn = data;
		if (data == null) {
			data = this.manager.getOWLDataFactory().getOWLThing();
			toReturn = data;
		}
		return toReturn;
	}

	public Object visit(MAEConflictStrategy node, Object data) {
		Object toReturn = data;
		if (data == null) {
			data = this.manager.getOWLDataFactory().getOWLThing();
			toReturn = data;
		}
		return toReturn;
	}

	/**
	 * @see uk.ac.manchester.mae.parser.ArithmeticsParserVisitor#visit(uk.ac.manchester.mae.parser.MAEStoreTo,
	 *      java.lang.Object)
	 */
	public Object visit(MAEStoreTo node, Object data) {
		return null;
	}

	public OWLDescription getClassDescription() {
		return this.classDescription;
	}
}
