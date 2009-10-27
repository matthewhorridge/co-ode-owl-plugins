package org.coode.oppl.variablemansyntax.generated;

import java.net.URI;

import org.coode.oppl.entity.OWLEntityCreationException;
import org.coode.oppl.entity.OWLEntityCreationSet;
import org.coode.oppl.entity.OWLEntityFactory;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.VariableType;
import org.semanticweb.owl.expression.OWLEntityChecker;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;

public class StringGeneratedVariable extends GeneratedVariable<String> {
	private final OWLOntology ontology;

	public StringGeneratedVariable(String name, VariableType type,
			GeneratedValue<String> value, OWLOntology ontology) {
		super(name, type, value);
		this.ontology = ontology;
	}

	@Override
	protected OWLObject generateObject(String aValue) {
		OWLObject value = null;
		OWLEntityChecker entityChecker = OPPLParser.getOPPLFactory()
				.getOWLEntityChecker();
		OWLOntologyManager ontologyManager = OPPLParser.getOPPLFactory()
				.getOntologyManager();
		OWLEntityFactory entityFactory = OPPLParser.getOPPLFactory()
				.getOWLEntityFactory();
		switch (getType()) {
			case CLASS:
				try {
					value = entityChecker.getOWLClass(aValue);
					if (value == null) {
						OWLEntityCreationSet<OWLClass> creationEntitySet = entityFactory
								.createOWLClass(aValue, null);
						value = creationEntitySet.getOWLEntity();
						ontologyManager.applyChanges(creationEntitySet
								.getOntologyChanges());
					}
				} catch (OWLEntityCreationException e) {
					value = OPPLParser.getOPPLFactory().getOWLDataFactory()
							.getOWLClass(buildURI(aValue));
				} catch (OWLOntologyChangeException e) {
					throw new RuntimeException(e);
				}
				break;
			case OBJECTPROPERTY:
				try {
					value = entityChecker.getOWLObjectProperty(aValue);
					if (value == null) {
						OWLEntityCreationSet<OWLObjectProperty> creationEntitySet = entityFactory
								.createOWLObjectProperty(aValue, null);
						value = creationEntitySet.getOWLEntity();
						ontologyManager.applyChanges(creationEntitySet
								.getOntologyChanges());
					}
				} catch (OWLEntityCreationException e) {
					value = OPPLParser.getOPPLFactory().getOWLDataFactory()
							.getOWLObjectProperty(buildURI(aValue));
				} catch (OWLOntologyChangeException e) {
					throw new RuntimeException(e);
				}
				break;
			case DATAPROPERTY:
				try {
					value = entityChecker.getOWLDataProperty(aValue);
					if (value == null) {
						OWLEntityCreationSet<OWLDataProperty> creationEntitySet = entityFactory
								.createOWLDataProperty(aValue, null);
						value = creationEntitySet.getOWLEntity();
						ontologyManager.applyChanges(creationEntitySet
								.getOntologyChanges());
					}
				} catch (OWLEntityCreationException e) {
					value = OPPLParser.getOPPLFactory().getOWLDataFactory()
							.getOWLDataProperty(buildURI(aValue));
				} catch (OWLOntologyChangeException e) {
					throw new RuntimeException(e);
				}
				break;
			case INDIVIDUAL:
				try {
					value = entityChecker.getOWLIndividual(aValue);
					if (value == null) {
						OWLEntityCreationSet<OWLIndividual> creationEntitySet = entityFactory
								.createOWLIndividual(aValue, null);
						value = creationEntitySet.getOWLEntity();
						ontologyManager.applyChanges(creationEntitySet
								.getOntologyChanges());
					}
				} catch (OWLEntityCreationException e) {
					value = OPPLParser.getOPPLFactory().getOWLDataFactory()
							.getOWLIndividual(buildURI(aValue));
				} catch (OWLOntologyChangeException e) {
					throw new RuntimeException(e);
				}
				break;
			case CONSTANT:
				value = OPPLParser.getOPPLFactory().getOWLDataFactory()
						.getOWLTypedConstant(aValue);
				//$FALL-THROUGH$
			default:
				break;
		}
		return value;
	}

	/**
	 * @param aValue
	 * @return
	 */
	private URI buildURI(String aValue) {
		return URI.create(getOntology().getURI().toString() + "#" + aValue);
	}

	public static StringGeneratedVariable buildGeneratedVariable(String name,
			VariableType type, GeneratedValue<String> value,
			OWLOntology ontology) {
		return new StringGeneratedVariable(name, type, value, ontology);
	}

	@Override
	protected GeneratedVariable<String> replace(GeneratedValue<String> value) {
		return buildGeneratedVariable(getName(), getType(), value,
				getOntology());
	}

	/**
	 * @return the ontology
	 */
	public OWLOntology getOntology() {
		return this.ontology;
	}

	@Override
	public String getOPPLFunction() {
		return "create(" + getValue() + ")";
	}
}
