/**
 * 
 */
package uk.ac.manchester.cs.owl.lint.examples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.lint.ActingLint;
import org.semanticweb.owl.lint.LintActionException;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.lint.LintVisitor;
import org.semanticweb.owl.lint.LintVisitorEx;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.RemoveAxiom;

import uk.ac.manchester.cs.owl.lint.LintManagerFactory;
import uk.ac.manchester.cs.owl.lint.commons.SimpleMatchBasedLintReport;

/**
 * @author Luigi Iannone
 * 
 */
public final class TransitiveObjectPropertyLint implements
		ActingLint<OWLObjectProperty> {
	public LintReport<OWLObjectProperty> detected(
			Collection<? extends OWLOntology> targets) throws LintException {
		SimpleMatchBasedLintReport<OWLObjectProperty> report = new SimpleMatchBasedLintReport<OWLObjectProperty>(
				this);
		for (OWLOntology ontology : targets) {
			for (OWLObjectProperty objectProperty : ontology
					.getReferencedObjectProperties()) {
				if (objectProperty.isTransitive(ontology)) {
					Set<OWLObjectPropertyExpression> superProperties = objectProperty
							.getSuperProperties(ontology);
					for (OWLObjectPropertyExpression objectPropertyExpression : superProperties) {
						if (objectPropertyExpression.isTransitive(ontology)) {
							report
									.add(
											objectProperty,
											ontology,
											"The property "
													+ objectProperty.toString()
													+ " is transitive and has a transitive super property");
						}
					}
				}
			}
		}
		return report;
	}

	public void executeActions(Collection<? extends OWLOntology> ontologies)
			throws LintActionException {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		OWLOntologyManager ontologyManager = LintManagerFactory.getInstance()
				.getOntologyManager();
		OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();
		LintReport<OWLObjectProperty> report;
		try {
			report = this.detected(ontologies);
			for (OWLOntology owlOntology : ontologies) {
				for (OWLObjectProperty objectProperty : report
						.getAffectedOWLObjects(owlOntology)) {
					changes
							.add(new RemoveAxiom(
									owlOntology,
									dataFactory
											.getOWLTransitiveObjectPropertyAxiom(objectProperty)));
				}
			}
			ontologyManager.applyChanges(changes);
		} catch (LintException e) {
			throw new LintActionException(e);
		} catch (OWLOntologyChangeException e) {
			throw new LintActionException(e);
		}
	}

	public String getDescription() {
		return "Detects all the transitive object properties which have a super property that is transitive";
	}

	public String getName() {
		return "Transitive Object Property Lint";
	}

	public void accept(LintVisitor visitor) {
		visitor.visitActingLint(this);
	}

	public <P> P accept(LintVisitorEx<P> visitor) {
		return visitor.visitActingLint(this);
	}
}
