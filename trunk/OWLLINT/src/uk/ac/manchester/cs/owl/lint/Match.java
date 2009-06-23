/**
 * 
 */
package uk.ac.manchester.cs.owl.lint;

import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 */
public class Match {
	OWLOntology ontology;
	OWLObject owlObject;
	String explanation = null;

	/**
	 * @param owlObject
	 * @param ontology
	 * @param explanation
	 */
	public Match(OWLObject owlObject, OWLOntology ontology, String explanation) {
		this.owlObject = owlObject;
		this.ontology = ontology;
		this.explanation = explanation;
	}

	/**
	 * @param owlObject
	 * @param ontology
	 */
	public Match(OWLObject owlObject, OWLOntology ontology) {
		this.owlObject = owlObject;
		this.ontology = ontology;
	}

	/**
	 * @return the ontology
	 */
	public OWLOntology getOntology() {
		return this.ontology;
	}

	/**
	 * @return the owlObject
	 */
	public OWLObject getOWLObject() {
		return this.owlObject;
	}

	/**
	 * @return the explanation
	 */
	public String getExplanation() {
		return this.explanation;
	}

	@Override
	public boolean equals(Object obj) {
		boolean toReturn = false;
		if (obj instanceof Match) {
			Match anotherMatch = (Match) obj;
			toReturn = this.owlObject.equals(anotherMatch.owlObject)
					&& this.ontology.equals(anotherMatch.ontology)
					&& this.explanation.equals(anotherMatch.explanation);
		}
		return toReturn;
	}

	@Override
	public int hashCode() {
		int stringHashCode = this.explanation == null ? 0 : this.explanation
				.hashCode();
		return this.owlObject.hashCode() + this.ontology.hashCode()
				+ stringHashCode;
	}
}
