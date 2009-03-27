package org.coode.oppl.variablemansyntax.generated;

import java.net.URI;

import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.VariableType;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.semanticweb.owl.expression.OWLEntityChecker;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

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
		OWLEntityFactory entityFactory = OPPLParser.getOPPLFactory()
				.getOWLEntityFactory();
		switch (this.getType()) {
		case CLASS:
			try {
				value = entityChecker.getOWLClass(aValue);
				if (value == null) {
					value = entityFactory.createOWLClass(aValue, null)
							.getOWLEntity();
				}
			} catch (OWLEntityCreationException e) {
				value = OPPLParser.getOPPLFactory().getOWLDataFactory()
						.getOWLClass(this.buildURI(aValue));
			}
			break;
		case OBJECTPROPERTY:
			try {
				value = entityChecker.getOWLObjectProperty(aValue);
				if (value == null) {
					value = entityFactory.createOWLObjectProperty(aValue, null)
							.getOWLEntity();
				}
			} catch (OWLEntityCreationException e) {
				value = OPPLParser.getOPPLFactory().getOWLDataFactory()
						.getOWLObjectProperty(this.buildURI(aValue));
			}
			break;
		case DATAPROPERTY:
			try {
				value = entityChecker.getOWLDataProperty(aValue);
				if (value == null) {
					value = entityFactory.createOWLDataProperty(aValue, null)
							.getOWLEntity();
				}
			} catch (OWLEntityCreationException e) {
				value = OPPLParser.getOPPLFactory().getOWLDataFactory()
						.getOWLDataProperty(this.buildURI(aValue));
			}
			break;
		case INDIVIDUAL:
			try {
				value = entityChecker.getOWLIndividual(aValue);
				if (value == null) {
					value = entityFactory.createOWLIndividual(aValue, null)
							.getOWLEntity();
				}
			} catch (OWLEntityCreationException e) {
				value = OPPLParser.getOPPLFactory().getOWLDataFactory()
						.getOWLIndividual(this.buildURI(aValue));
			}
			break;
		case CONSTANT:
			value = OPPLParser.getOPPLFactory().getOWLDataFactory()
					.getOWLTypedConstant(aValue);
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
		return URI
				.create(this.getOntology().getURI().toString() + "#" + aValue);
	}

	public static StringGeneratedVariable buildGeneratedVariable(String name,
			VariableType type, GeneratedValue<String> value,
			OWLOntology ontology) {
		return new StringGeneratedVariable(name, type, value, ontology);
	}

	@Override
	public String toString() {
		return this.getName() + ":" + this.getType() + this.getOPPLFunction();
	}

	@Override
	protected GeneratedVariable<String> replace(GeneratedValue<String> value) {
		return buildGeneratedVariable(this.getName(), this.getType(), value,
				this.getOntology());
	}

	/**
	 * @return the ontology
	 */
	public OWLOntology getOntology() {
		return this.ontology;
	}

	@Override
	public String getOPPLFunction() {
		return " = create(" + this.getValue() + ")";
	}
}
