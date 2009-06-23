/**
 * 
 */
package org.coode.lint.examples;

import java.util.Set;

import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.lint.LintManagerFactory;

/**
 * @author Luigi Iannone
 * 
 */
public class TransitiveObjectPropertyLint implements Lint {
	protected OWLOntologyManager manager;

	public TransitiveObjectPropertyLint(OWLOntologyManager manager) {
		this.manager = manager;
	}

	public LintReport detected(Set<OWLOntology> targets) throws LintException {
		LintReport report = LintManagerFactory.getLintManager(this.manager)
				.getLintFactory().createLintReport(this);
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

	public String getDescription() {
		return "Detects all the transitive object properties which have a super property that is transitive";
	}

	public String getName() {
		return "Transitive Object Property Lint";
	}

	public OWLOntologyManager getOntologyManager() {
		return this.manager;
	}

	public void setName(String name) {
	}
}
