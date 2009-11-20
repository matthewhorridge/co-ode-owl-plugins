/**
 * 
 */
package org.coode.oppl.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDescriptionVisitor;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;

/**
 * Retrieves all the object used to build an axiom
 * 
 * @author Luigi Iannone
 * 
 */
public class OWLObjectExtractor {
	public static Collection<? extends OWLClass> getAllClasses(OWLAxiom axiom) {
		final Set<OWLClass> toReturn = new HashSet<OWLClass>();
		for (OWLEntity entity : axiom.getReferencedEntities()) {
			entity.accept(new OWLEntityVisitor() {
				public void visit(OWLDataType dataType) {
				}

				public void visit(OWLIndividual individual) {
				}

				public void visit(OWLDataProperty property) {
				}

				public void visit(OWLObjectProperty property) {
				}

				public void visit(OWLClass cls) {
					toReturn.add(cls);
				}
			});
		}
		return toReturn;
	}

	public static Collection<? extends OWLObjectProperty> getAllOWLObjectProperties(
			OWLAxiom axiom) {
		final Set<OWLObjectProperty> toReturn = new HashSet<OWLObjectProperty>();
		for (OWLEntity entity : axiom.getReferencedEntities()) {
			entity.accept(new OWLEntityVisitor() {
				public void visit(OWLDataType dataType) {
				}

				public void visit(OWLIndividual individual) {
				}

				public void visit(OWLDataProperty property) {
				}

				public void visit(OWLObjectProperty property) {
					toReturn.add(property);
				}

				public void visit(OWLClass cls) {
				}
			});
		}
		return toReturn;
	}

	public static Collection<? extends OWLDataProperty> getAllOWLDataProperties(
			OWLAxiom axiom) {
		final Set<OWLDataProperty> toReturn = new HashSet<OWLDataProperty>();
		for (OWLEntity entity : axiom.getReferencedEntities()) {
			entity.accept(new OWLEntityVisitor() {
				public void visit(OWLDataType dataType) {
				}

				public void visit(OWLIndividual individual) {
				}

				public void visit(OWLDataProperty property) {
					toReturn.add(property);
				}

				public void visit(OWLObjectProperty property) {
				}

				public void visit(OWLClass cls) {
				}
			});
		}
		return toReturn;
	}

	public static Collection<? extends OWLIndividual> getAllOWLIndividuals(
			OWLAxiom axiom) {
		final Set<OWLIndividual> toReturn = new HashSet<OWLIndividual>();
		for (OWLEntity entity : axiom.getReferencedEntities()) {
			entity.accept(new OWLEntityVisitor() {
				public void visit(OWLDataType dataType) {
				}

				public void visit(OWLIndividual individual) {
					toReturn.add(individual);
				}

				public void visit(OWLDataProperty property) {
				}

				public void visit(OWLObjectProperty property) {
				}

				public void visit(OWLClass cls) {
				}
			});
		}
		return toReturn;
	}

	public static Collection<? extends OWLConstant> getAllOWLConstants(
			OWLAxiom axiomToVisit) {
		final Set<OWLConstant> toReturn = new HashSet<OWLConstant>();
		final OWLDescriptionVisitor constantExtractor = new OWLDescriptionVisitorAdapter() {
			@Override
			public void visit(OWLDataValueRestriction desc) {
				toReturn.add(desc.getValue());
			}
		};
		axiomToVisit.accept(new OWLAxiomVisitorAdapter() {
			@Override
			public void visit(OWLClassAssertionAxiom axiom) {
				axiom.getDescription().accept(constantExtractor);
			}

			@Override
			public void visit(OWLDataPropertyAssertionAxiom axiom) {
				toReturn.add(axiom.getObject());
			}

			@Override
			public void visit(OWLDisjointClassesAxiom axiom) {
				for (OWLDescription description : axiom.getDescriptions()) {
					description.accept(constantExtractor);
				}
			}

			@Override
			public void visit(OWLEquivalentClassesAxiom axiom) {
				for (OWLDescription description : axiom.getDescriptions()) {
					description.accept(constantExtractor);
				}
			}

			@Override
			public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
				toReturn.add(axiom.getObject());
			}

			@Override
			public void visit(OWLSubClassAxiom axiom) {
				axiom.getSubClass().accept(constantExtractor);
				axiom.getSuperClass().accept(constantExtractor);
			}
		});
		return toReturn;
	}

	public static Collection<? extends OWLObject> getAllOWLObjects(
			OWLAxiom axiom) {
		Set<OWLObject> toReturn = new HashSet<OWLObject>();
		toReturn.addAll(getAllClasses(axiom));
		toReturn.addAll(getAllOWLObjectProperties(axiom));
		toReturn.addAll(getAllOWLDataProperties(axiom));
		toReturn.addAll(getAllOWLIndividuals(axiom));
		toReturn.addAll(getAllOWLConstants(axiom));
		return toReturn;
	}
}
