package org.coode.oppl.test;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLSubClassAxiom;

public class Test {
	public static void main(String[] args) {
		OWLOntologyManager ontologyManager = OWLManager
				.createOWLOntologyManager();
		try {
			URI ontologyURI = URI
					.create("http://www.cs.man.ac.uk/~iannonel/oppl/ontologies/mikelsFamily.owl");
			OWLOntology loadedOntology = ontologyManager
					.loadOntology(ontologyURI);
			OWLClass genderOWLClass = ontologyManager.getOWLDataFactory()
					.getOWLClass(URI.create(ontologyURI + "#gender"));
			Set<OWLClass> subClasses = new HashSet<OWLClass>();
			List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
			for (OWLOntology ontology : ontologyManager.getOntologies()) {
				Set<OWLSubClassAxiom> subClassAxioms = ontology
						.getSubClassAxiomsForLHS(genderOWLClass);
				for (OWLSubClassAxiom subClassAxiom : subClassAxioms) {
					if (!subClassAxiom.getSubClass().isAnonymous()) {
						subClasses
								.add(subClassAxiom.getSubClass().asOWLClass());
					}
				}
			}
			for (OWLClass aSubClass : subClasses) {
				for (OWLClass anotherSubClass : subClasses) {
					if (!aSubClass.equals(anotherSubClass)) {
						changes.add(new AddAxiom(loadedOntology,
								ontologyManager.getOWLDataFactory()
										.getOWLDisjointClassesAxiom(aSubClass,
												anotherSubClass)));
					}
				}
			}
			ontologyManager.applyChanges(changes);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLOntologyChangeException e) {
			e.printStackTrace();
		}
	}
}
