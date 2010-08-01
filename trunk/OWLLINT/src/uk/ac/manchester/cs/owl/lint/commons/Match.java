/**
 * 
 */
package uk.ac.manchester.cs.owl.lint.commons;

import java.util.Formatter;

import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 */
public class Match<O extends OWLObject> {
	private OWLOntology ontology;
	private O owlObject;
	private String explanation = null;

	/**
	 * @param owlObject
	 * @param ontology
	 * @param explanation
	 */
	public Match(O owlObject, OWLOntology ontology, String explanation) {
		if (owlObject == null) {
			throw new NullPointerException("The owl object cannot be null");
		}
		if (ontology == null) {
			throw new NullPointerException("The ontology cannot be null");
		}
		this.owlObject = owlObject;
		this.ontology = ontology;
		this.explanation = explanation;
	}

	/**
	 * @param owlObject
	 * @param ontology
	 */
	public Match(O owlObject, OWLOntology ontology) {
		this(owlObject, ontology, LintReport.NO_EXPLANATION_GIVEN);
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
	public O getOWLObject() {
		return this.owlObject;
	}

	/**
	 * @return the explanation
	 */
	public String getExplanation() {
		return this.explanation;
	}

	@Override
	public String toString() {
		Formatter formatter = new Formatter();
		formatter.format("Match OWLObject %s in ontology %s explanation: ",
				this.getOWLObject(), this.getOntology().getURI(), this
						.getExplanation());
		return formatter.out().toString();
	}

	/**
	 * Only the ontology and the OWL object matter in this hashCode
	 * implementation.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (this.ontology == null ? 0 : this.ontology.hashCode());
		result = prime * result
				+ (this.owlObject == null ? 0 : this.owlObject.hashCode());
		return result;
	}

	/**
	 * Only the ontology and the OWL object matter in this equality check
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Match<?> other = (Match<?>) obj;
		if (this.ontology == null) {
			if (other.ontology != null) {
				return false;
			}
		} else if (!this.ontology.equals(other.ontology)) {
			return false;
		}
		if (this.owlObject == null) {
			if (other.owlObject != null) {
				return false;
			}
		} else if (!this.owlObject.equals(other.owlObject)) {
			return false;
		}
		return true;
	}
}
