package org.coode.oppl.variablemansyntax.generated;

import java.net.URI;

import org.coode.oppl.entity.OWLEntityCreationException;
import org.coode.oppl.entity.OWLEntityCreationSet;
import org.coode.oppl.entity.OWLEntityFactory;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.VariableType;
import org.semanticweb.owl.expression.OWLEntityChecker;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;

public class StringGeneratedVariable extends AbstractGeneratedVariable<String> {
	private final OWLOntology ontology;

	protected StringGeneratedVariable(String name, VariableType type,
			GeneratedValue<String> value, OWLOntology ontology) {
		super(name, type, value);
		this.ontology = ontology;
	}

	@Override
	protected OWLObject generateObject(String aValue) {
		OWLObject toReturn = null;
		OWLEntityChecker entityChecker = OPPLParser.getOPPLFactory()
				.getOWLEntityChecker();
		OWLEntityFactory ef = OPPLParser.getOPPLFactory().getOWLEntityFactory();
		OWLDataFactory df = OPPLParser.getOPPLFactory().getOWLDataFactory();
		switch (getType()) {
			case CLASS:
				try {
					toReturn = entityChecker.getOWLClass(aValue);
					if (toReturn == null) {
						toReturn = createUpdates(ef
								.createOWLClass(aValue, null));
					}
				} catch (OWLEntityCreationException e) {
					toReturn = df.getOWLClass(buildURI(aValue));
				} catch (OWLOntologyChangeException e) {
					throw new RuntimeException(e);
				}
				break;
			case OBJECTPROPERTY:
				try {
					toReturn = entityChecker.getOWLObjectProperty(aValue);
					if (toReturn == null) {
						toReturn = createUpdates(ef.createOWLObjectProperty(
								aValue, null));
					}
				} catch (OWLEntityCreationException e) {
					toReturn = df.getOWLObjectProperty(buildURI(aValue));
				} catch (OWLOntologyChangeException e) {
					throw new RuntimeException(e);
				}
				break;
			case DATAPROPERTY:
				try {
					toReturn = entityChecker.getOWLDataProperty(aValue);
					if (toReturn == null) {
						toReturn = createUpdates(ef.createOWLDataProperty(
								aValue, null));
					}
				} catch (OWLEntityCreationException e) {
					toReturn = df.getOWLDataProperty(buildURI(aValue));
				} catch (OWLOntologyChangeException e) {
					throw new RuntimeException(e);
				}
				break;
			case INDIVIDUAL:
				try {
					toReturn = entityChecker.getOWLIndividual(aValue);
					if (toReturn == null) {
						toReturn = createUpdates(ef.createOWLIndividual(aValue,
								null));
					}
				} catch (OWLEntityCreationException e) {
					toReturn = df.getOWLIndividual(buildURI(aValue));
				} catch (OWLOntologyChangeException e) {
					throw new RuntimeException(e);
				}
				break;
			case CONSTANT:
				toReturn = df.getOWLTypedConstant(aValue);
				break;
			default:
				break;
		}
		return toReturn;
	}

	private OWLObject createUpdates(
			OWLEntityCreationSet<? extends OWLEntity> set)
			throws OWLEntityCreationException, OWLOntologyChangeException {
		OPPLParser.getOWLOntologyManager().applyChanges(
				set.getOntologyChanges());
		return set.getOWLEntity();
	}

	/**
	 * @param aValue
	 * @return
	 */
	private URI buildURI(String aValue) {
		return URI.create(this.ontology.getURI().toString() + "#" + aValue);
	}

	public static StringGeneratedVariable buildGeneratedVariable(String name,
			VariableType type, GeneratedValue<String> value,
			OWLOntology ontology) {
		return new StringGeneratedVariable(name, type, value, ontology);
	}

	@Override
	protected GeneratedVariable<String> replace(GeneratedValue<String> v) {
		return buildGeneratedVariable(getName(), getType(), v, this.ontology);
	}

	public String getOPPLFunction() {
		return "create(" + getValue() + ")";
	}
}
